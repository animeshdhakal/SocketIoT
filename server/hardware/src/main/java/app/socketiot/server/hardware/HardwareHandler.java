package app.socketiot.server.hardware;

import java.util.concurrent.ConcurrentHashMap;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.db.model.Device;
import app.socketiot.server.core.exceptions.ExceptionHandler;
import app.socketiot.server.core.json.JsonParser;
import app.socketiot.server.core.json.model.DeviceJson;
import app.socketiot.server.hardware.message.MsgType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;

public class HardwareHandler extends ChannelInboundHandlerAdapter{
    private final Holder holder;
    private final static int HEADER_SIZE = 4;
    private final static AttributeKey<String> tokenKey = AttributeKey.valueOf("token");
    private static ConcurrentHashMap<String, ChannelGroup> groups = new ConcurrentHashMap<>();

    public HardwareHandler(Holder holder) {
        this.holder = holder;
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE){
                ctx.close();
            }
        }else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        short msg_len = buf.readShort();
        short msg_type = buf.readShort();
        byte[] msg_body = new byte[msg_len];
        buf.readBytes(msg_body);
        String msg_str = new String(msg_body);
        process(ctx, msg_type,  msg_str.split("\0"));

        if(buf.readableBytes() >= HEADER_SIZE) {
            channelRead(ctx, buf);
        }
    }


    public ByteBuf createMessage(short msg_type, String ...args){
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer();
        buf.writeShort(HEADER_SIZE);
        buf.writeShort(msg_type);
        for(String arg : args){
            buf.writeBytes(arg.getBytes());
            buf.writeByte(0);
        }
        return buf;
    }

    public void sendMessage(ChannelHandlerContext ctx, ByteBuf msg){
        ctx.writeAndFlush(msg);
    }

    public void broadCastMessage(ChannelHandlerContext ctx, ByteBuf msg, String token){
        ChannelGroup group = groups.get(token);
        if(group != null){
            for(Channel c : group){
                if(!c.equals(ctx.channel())){
                    c.writeAndFlush(msg);
                }
            }
        }
    }


    public void broadCastMessage(ChannelHandlerContext ctx, ByteBuf msg){
        String token = ctx.channel().attr(tokenKey).get();
        if (token != null) broadCastMessage(ctx, msg, token);
    }

    public void handleAuth(ChannelHandlerContext ctx, String token){
        if(holder.deviceDao.getDeviceByToken(token) != null){
            ctx.channel().attr(tokenKey).set(token);
            ChannelGroup group = groups.get(token);
            if(group == null){
                group = new DefaultChannelGroup(ctx.executor());
                group.add(ctx.channel());
                groups.put(token, group);
            }else 
            {
                group.add(ctx.channel());
            }
            sendMessage(ctx, createMessage(MsgType.AUTH, "1"));
        }else{
            sendMessage(ctx, createMessage(MsgType.AUTH, "0"));
        }
    }


    public void handleWrite(ChannelHandlerContext ctx, String[] params){
        if (params.length < 2) return;
        String token = ctx.channel().attr(tokenKey).get();
        if(token != null){
            Device device = holder.deviceDao.getDeviceByToken(token);
            if (device != null) {
                DeviceJson deviceJson = JsonParser.parse(DeviceJson.class, device.json);
                if (deviceJson != null && deviceJson.pins.get(params[0]) != null) {
                    deviceJson.pins.put(params[0], params[1]);
                    device.json = JsonParser.toString(deviceJson);
                    holder.deviceDao.updateDevice(device);
                    broadCastMessage(ctx, createMessage(MsgType.WRITE, params[0], params[1]));
                }
            }
        }
    }

    public void process(ChannelHandlerContext ctx, int msg_type, String[] params){
        switch(msg_type){
            case MsgType.AUTH:
                handleAuth(ctx, params[0]);
                break;
            case MsgType.WRITE:
                System.out.println("Writing");
                handleWrite(ctx, params);
                break;
            case MsgType.PING:
                break;
            case MsgType.SYNC:
                break;
            default:
                break;
        }
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String token = ctx.channel().attr(tokenKey).get();
        if(token != null){
            ChannelGroup group = groups.get(token);
            if(group != null){
                group.remove(ctx.channel());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ExceptionHandler.handleException(ctx, cause);
    }

}
