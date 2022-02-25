package app.socketiot.server.hardware;

import app.socketiot.server.core.Holder;
import app.socketiot.server.core.exceptions.ExceptionHandler;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.hardware.message.HardwareMessage;
import app.socketiot.server.hardware.message.MsgType;
import app.socketiot.server.utils.IPUtil;
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
    private final static AttributeKey<String> tokenKey = AttributeKey.valueOf("token");
    private boolean isDash = false;

    public HardwareHandler(Holder holder) {
        this.holder = holder;
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
        if (msg instanceof HardwareMessage) {
            process(ctx, (HardwareMessage) msg);
        }
    }

    public void broadCastMessage(ChannelHandlerContext ctx, HardwareMessage msg, String token) {
        Device device = holder.deviceDao.getDeviceByToken(token);
        for (Channel c : device.hardGroup) {
            if (!c.equals(ctx.channel())) {
                c.writeAndFlush(msg);
            }
        }
        for (Channel c : device.dashGroup) {
            if (!c.equals(ctx.channel())) {
                c.writeAndFlush(msg);
            }
        }

    }

    public void broadCastMessage(ChannelHandlerContext ctx, HardwareMessage msg) {
        String token = ctx.channel().attr(tokenKey).get();
        if (token != null)
            broadCastMessage(ctx, msg, token);
    }

    public void handleAuth(ChannelHandlerContext ctx, String token, boolean isHardware) {
        Device device = holder.deviceDao.getDeviceByToken(token);
        if (device != null) {
            ctx.channel().attr(tokenKey).set(token);

            if (isHardware) {
                device.hardGroup.add(ctx.channel());
                device.online = true;
                device.lastIP = IPUtil.getIP(ctx.channel().remoteAddress());
            } else {
                device.dashGroup.add(ctx.channel());
                isDash = true;
            }

            holder.deviceDao.updateDevice(device);

            ctx.writeAndFlush(new HardwareMessage(MsgType.AUTH, "1"));
        } else {
            ctx.writeAndFlush(new HardwareMessage(MsgType.AUTH, "0"));
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
                    broadCastMessage(ctx, new HardwareMessage(MsgType.WRITE, params[0], params[1]));
                }
            }
        }
    }

    public void handleSync(ChannelHandlerContext ctx) {
        String token = ctx.channel().attr(tokenKey).get();
        if (token != null) {
            Device device = holder.deviceDao.getDeviceByToken(token);
            for (String key : device.json.pins.keySet()) {
                ctx.writeAndFlush(new HardwareMessage(MsgType.WRITE, key, device.json.pins.get(key)));
            }
        }
    }

    public void process(ChannelHandlerContext ctx, HardwareMessage msg) {
        switch (msg.type) {
            case MsgType.AUTH:
                handleAuth(ctx, msg.body[0], msg.body.length == 1);
                break;
            case MsgType.WRITE:
                handleWrite(ctx, msg.body);
                break;
            case MsgType.SYNC:
                handleSync(ctx);
                break;
            case MsgType.PING:
                ctx.writeAndFlush(msg);
                break;
            default:
                break;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String token = ctx.channel().attr(tokenKey).get();
        if (token != null) {
            Device device = holder.deviceDao.getDeviceByToken(token);
            if (isDash) {
                device.dashGroup.remove(ctx.channel());
            } else {
                device.hardGroup.remove(ctx.channel());
            }
            if (device != null && !isDash && device.hardGroup.size() == 0) {
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
