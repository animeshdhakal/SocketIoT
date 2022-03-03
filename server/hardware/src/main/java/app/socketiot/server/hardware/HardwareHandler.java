package app.socketiot.server.hardware;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.exceptions.ExceptionHandler;
import app.socketiot.server.core.model.HardwareInfo;
import app.socketiot.server.core.model.HardwareMessage;
import app.socketiot.server.core.model.MsgType;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.utils.NumberUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class HardwareHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LogManager.getLogger(HardwareHandler.class);
    private final Device device;
    private final boolean isHardware;
    private final Holder holder;

    public HardwareHandler(Holder holder, Device device, boolean isHardware) {
        this.device = device;
        this.isHardware = isHardware;
        this.holder = holder;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                log.trace("Device Closed Due to InActivity {}", device);
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

    public void broadCastMessage(ChannelHandlerContext ctx, HardwareMessage msg) {
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

    public void handleWrite(ChannelHandlerContext ctx, String[] params) {
        if (params.length < 2)
            return;

        if (device != null) {
            if (device.json != null && device.json.pins.get(params[0]) != null) {
                device.json.pins.put(params[0], params[1]);
                broadCastMessage(ctx, new HardwareMessage(MsgType.WRITE, params[0], params[1]));
            }
        }

    }

    public void handleSync(ChannelHandlerContext ctx) {
        for (String key : device.json.pins.keySet()) {
            ctx.writeAndFlush(new HardwareMessage(MsgType.WRITE, key, device.json.pins.get(key)));
        }
    }

    public void handleInfo(ChannelHandlerContext ctx, String[] body) {
        HardwareInfo info = new HardwareInfo(body);
        if (info.heartbeat > 0) {
            log.trace("Changing Hearbeat for {} with value {}", ctx.channel(), info.heartbeat);
            ctx.pipeline().replace(IdleStateHandler.class, "IdleStateHandler",
                    new IdleStateHandler(NumberUtil.calculateHeartBeat(info.heartbeat), 0, 0));
        }

    }

    public void process(ChannelHandlerContext ctx, HardwareMessage msg) {
        switch (msg.type) {
            case MsgType.WRITE:
                handleWrite(ctx, msg.body);
                break;
            case MsgType.SYNC:
                handleSync(ctx);
                break;
            case MsgType.PING:
                ctx.writeAndFlush(msg);
                break;
            case MsgType.INFO:
                handleInfo(ctx, msg.body);
                break;
            default:
                break;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (isHardware) {
            device.hardGroup.remove(ctx.channel());
        } else {
            device.dashGroup.remove(ctx.channel());
        }
        if (device != null && isHardware && device.hardGroup.size() == 0) {
            device.online = false;
            holder.deviceDao.updateDevice(device);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ExceptionHandler.handleException(ctx, cause);
    }

}
