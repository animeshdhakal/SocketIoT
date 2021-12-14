package animesh.app.server;

import java.util.concurrent.ConcurrentHashMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    static ObjectMapper objMapper = new ObjectMapper();

    boolean isWebSocket;
    static int HEADER_SIZE = 4;
    int loss_time = 0;

    final static AttributeKey<String> tokenKey = AttributeKey.valueOf("token");

    static ConcurrentHashMap<String, ChannelGroup> tcpGroups = new ConcurrentHashMap<>();
    static ConcurrentHashMap<String, ChannelGroup> webSocketGroups = new ConcurrentHashMap<>();
    static ConcurrentHashMap<String, JsonModel> jsonDatas = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ChannelGroup> groups;

    ClientHandler(boolean isWebSocket) {
        this.isWebSocket = isWebSocket;
        if (isWebSocket) {
            groups = webSocketGroups;
        } else {
            groups = tcpGroups;
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                loss_time++;
                if (loss_time >= 5) {
                    ctx.channel().close();
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    static boolean checkAuth(String auth) {
        return auth.equals("animesh");
    }

    public static void broadCastMessage(ChannelHandlerContext ctx, ByteBuf buff, String token) {
        Channel incoming = ctx.channel();
        ChannelGroup webSocketGroup = webSocketGroups.get(token);
        ChannelGroup tcpGroup = tcpGroups.get(token);

        if (webSocketGroup != null) {
            for (Channel channel : webSocketGroup) {
                if (!channel.equals(incoming)) {
                    channel.writeAndFlush(new BinaryWebSocketFrame(buff.retainedDuplicate()));
                }
            }
        }

        if (tcpGroup != null) {
            for (Channel channel : tcpGroup) {
                if (!channel.equals(incoming)) {
                    channel.writeAndFlush(buff.retainedDuplicate());
                }
            }
        }

    }

    public void sendMessage(ChannelHandlerContext ctx, ByteBuf msg) {
        if (isWebSocket) {
            ctx.writeAndFlush(new BinaryWebSocketFrame(msg));
        } else {
            ctx.writeAndFlush(msg);
        }
    }

    public static ByteBuf createMessage(short msg_type, String... args) {
        String msg = String.join("\0", args);
        ByteBuf buff = Unpooled.buffer(HEADER_SIZE + msg.length());
        buff.writeShort(msg_type);
        buff.writeShort(msg.length());
        buff.writeBytes(msg.getBytes());
        return buff;
    }

    public static String getPinVal(String token, String pin) {
        JsonModel jsonData = jsonDatas.get(token);
        if (jsonData != null) {
            return jsonData.pins.get(pin);
        }
        return null;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf rcvd = (ByteBuf) msg;
        loss_time = 0;

        short msg_len = rcvd.readShort();
        short msg_type = rcvd.readShort();

        byte[] buff = new byte[msg_len];

        rcvd.readBytes(buff);

        String[] msg_args = new String(buff).split("\0");

        switch (msg_type) {
            case MsgType.AUTH:
                if (checkAuth(msg_args[0])) {
                    ctx.channel().attr(tokenKey).set(msg_args[0]);
                    System.out.println("Authenticated");
                    ChannelGroup group = groups.get(msg_args[0]);
                    if (group == null) {
                        group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
                        group.add(ctx.channel());
                        groups.put(msg_args[0], group);
                    } else {
                        group.add(ctx.channel());
                    }
                    JsonModel jsonData = jsonDatas.get(msg_args[0]);
                    if (jsonData == null) {
                        jsonDatas.put(msg_args[0], JsonModel.createJsonModelObj());
                    }

                    sendMessage(ctx, createMessage(MsgType.AUTH, "1"));
                } else {
                    sendMessage(ctx, createMessage(MsgType.AUTH, "0"));
                }
                break;

            case MsgType.WRITE:
                String token = ctx.channel().attr(tokenKey).get();
                if (token != null) {
                    ByteBuf tmp = Unpooled.buffer(msg_len + HEADER_SIZE);
                    JsonModel jsonData = jsonDatas.get(token);
                    if (jsonData != null) {
                        jsonData.pins.put(msg_args[0], msg_args[1]);
                        String prettyStaff1 = objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonData);
                        System.out.println(prettyStaff1);
                    }

                    tmp.writeShort(msg_len);
                    tmp.writeShort(msg_type);
                    tmp.writeBytes(buff);
                    broadCastMessage(ctx, tmp, token);
                }
                break;

            case MsgType.SYNC:
                System.out.println("SYNC MESSAGE");
                JsonModel jsonData = jsonDatas.get(ctx.channel().attr(tokenKey).get());
                if (jsonData != null) {
                    for (String key : jsonData.pins.keySet()) {
                        System.out.println(key + " " + jsonData.pins.get(key));
                        sendMessage(ctx, createMessage(MsgType.WRITE, key, jsonData.pins.get(key)));
                    }
                } else {
                    System.out.println("SYNC FAILED");
                }
                break;

            case MsgType.PING:
                break;

            default:
                System.out.println("Invalid MsgType " + msg_type);

        }

        if (rcvd.readableBytes() >= HEADER_SIZE) {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LoggerUtil.logger.debug("Client Connected");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LoggerUtil.logger.debug("Client Disconnected");

        String token = ctx.channel().attr(tokenKey).get();
        if (token != null) {
            ChannelGroup group = groups.get(token);
            if (group != null) {
                if (group.size() == 1) {
                    groups.remove(token);
                } else {
                    group.remove(ctx.channel());
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LoggerUtil.logger.error(cause);
        ctx.close();
    }

}
