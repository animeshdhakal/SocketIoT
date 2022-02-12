package app.socketiot.server.hardware;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.db.model.Device;
import app.socketiot.server.core.exceptions.ExceptionHandler;
import app.socketiot.server.core.metrics.QuotaLimitChecker;
import app.socketiot.server.hardware.message.MsgType;
import app.socketiot.server.utils.IPUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class HardwareHandler extends ChannelInboundHandlerAdapter {
    private final Holder holder;
    private final static int HEADER_SIZE = 4;
    private final static AttributeKey<String> tokenKey = AttributeKey.valueOf("token");
    private static ConcurrentHashMap<String, Set<Channel>> groups = new ConcurrentHashMap<>();
    private final QuotaLimitChecker limitChecker;

    public HardwareHandler(Holder holder) {
        this.holder = holder;
        this.limitChecker = new QuotaLimitChecker(10);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (limitChecker.quotaExceeded()) {
            return;
        }

        ByteBuf buf = (ByteBuf) msg;

        if (buf.readableBytes() < HEADER_SIZE)
            return;

        short msg_len = buf.readShort();
        short msg_type = buf.readShort();

        if (buf.readableBytes() < msg_len)
            return;

        byte[] msg_body = new byte[msg_len];
        buf.readBytes(msg_body);
        String msg_str = new String(msg_body);
        process(ctx, msg_type, msg_str.split("\0"));

        if (buf.readableBytes() >= HEADER_SIZE) {
            channelRead(ctx, buf);
        }
    }

    public ByteBuf createMessage(short msg_type, String... args) {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer();

        String msg_str = String.join("\0", args);

        buf.writeShort(msg_str.length());
        buf.writeShort(msg_type);

        if (msg_str.length() > 0) {
            buf.writeBytes(msg_str.getBytes());
        }

        return buf;
    }

    public void sendMessage(ChannelHandlerContext ctx, ByteBuf msg) {
        ctx.writeAndFlush(msg);
    }

    public void broadCastMessage(ChannelHandlerContext ctx, ByteBuf msg, String token) {
        var group = groups.get(token);
        if (group != null) {
            for (Channel c : group) {
                if (!c.equals(ctx.channel())) {
                    c.writeAndFlush(msg.retainedDuplicate());
                }
            }
        }
    }

    public void broadCastMessage(ChannelHandlerContext ctx, ByteBuf msg) {
        String token = ctx.channel().attr(tokenKey).get();
        if (token != null)
            broadCastMessage(ctx, msg, token);
    }

    public void handleAuth(ChannelHandlerContext ctx, String token, boolean isHardware) {
        Device device = holder.deviceDao.getDeviceByToken(token);
        if (device != null) {
            ctx.channel().attr(tokenKey).set(token);
            var group = groups.get(token);
            if (group == null) {
                group = new HashSet<>();
                group.add(ctx.channel());
                groups.put(token, group);
            } else {
                group.add(ctx.channel());
            }

            if (isHardware) {
                device.online = true;
                device.lastIP = IPUtil.getIP(ctx.channel().remoteAddress());
            }

            holder.deviceDao.updateDevice(device);

            sendMessage(ctx, createMessage(MsgType.AUTH, "1"));
        } else {
            sendMessage(ctx, createMessage(MsgType.AUTH, "0"));
        }
    }

    public void handleWrite(ChannelHandlerContext ctx, String[] params) {
        if (params.length < 2)
            return;
        String token = ctx.channel().attr(tokenKey).get();
        if (token != null) {
            Device device = holder.deviceDao.getDeviceByToken(token);
            if (device != null) {
                if (device.json != null && device.json.pins.get(params[0]) != null) {
                    device.json.pins.put(params[0], params[1]);
                    holder.deviceDao.updateDevice(device);
                    broadCastMessage(ctx, createMessage(MsgType.WRITE, params[0], params[1]));
                }
            }
        }
    }

    public void handleSync(ChannelHandlerContext ctx) {
        String token = ctx.channel().attr(tokenKey).get();
        if (token != null) {
            Device device = holder.deviceDao.getDeviceByToken(token);
            for (String key : device.json.pins.keySet()) {
                sendMessage(ctx, createMessage(MsgType.WRITE, key, device.json.pins.get(key)));
            }
        }
    }

    public void process(ChannelHandlerContext ctx, int msg_type, String[] params) {
        switch (msg_type) {
            case MsgType.AUTH:
                handleAuth(ctx, params[0], params.length == 1);
                break;
            case MsgType.WRITE:
                handleWrite(ctx, params);
                break;
            case MsgType.SYNC:
                handleSync(ctx);
                break;
            case MsgType.PING:
                break;
            default:
                break;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String token = ctx.channel().attr(tokenKey).get();
        if (token != null) {
            var group = groups.get(token);
            Device device = holder.deviceDao.getDeviceByToken(token);
            if (group != null) {
                if (group.size() == 1) {
                    groups.remove(token);
                } else {
                    group.remove(ctx.channel());
                }
            }
            if (device != null) {
                device.online = false;
                holder.deviceDao.updateDevice(device);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ExceptionHandler.handleException(ctx, cause);
    }

}
