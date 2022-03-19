package app.socketiot.server.hardware;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.exceptions.ExceptionHandler;
import app.socketiot.server.core.model.HardwareInfo;
import app.socketiot.server.core.model.HardwareMessage;
import app.socketiot.server.core.model.MsgType;
import app.socketiot.server.core.model.device.UserDevice;
import app.socketiot.server.core.statebase.HardwareStateBase;
import app.socketiot.server.hardware.handler.HardwareLogicHandler;
import app.socketiot.server.utils.NumberUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class HardwareHandler extends HardwareStateBase {
    private static final Logger log = LogManager.getLogger(HardwareHandler.class);
    public final UserDevice userDevice;
    private final HardwareLogicHandler hardware;

    public HardwareHandler(Holder holder, UserDevice userDevice) {
        this.userDevice = userDevice;
        this.hardware = new HardwareLogicHandler(userDevice);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                log.trace("Device Closed Due to InActivity {}", userDevice.device);
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

    public void handleInfo(ChannelHandlerContext ctx, String[] body) {
        HardwareInfo info = new HardwareInfo(body);
        if (info.heartbeat > 0) {
            log.trace("Changing Hearbeat for {} with value {}", ctx.channel(), info.heartbeat);
            ctx.pipeline().replace(IdleStateHandler.class, "IdleStateHandler",
                    new IdleStateHandler(NumberUtil.calculateHeartBeat(info.heartbeat), 0, 0));
        }
        userDevice.device.info = info;
    }

    public void process(ChannelHandlerContext ctx, HardwareMessage msg) {
        switch (msg.type) {
            case MsgType.WRITE:
                hardware.handleWrite(ctx, msg);
                break;
            case MsgType.SYNC:
                hardware.handleSync(ctx);
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
        userDevice.user.json.removeHardChannel(ctx.channel());
        if (userDevice.user.json.hardChannels.size() == 0) {
            userDevice.device.online = false;
            userDevice.device.lastOnline = System.currentTimeMillis();
            userDevice.user.json.sendToApps(ctx,
                    new HardwareMessage(MsgType.DEVICE_STATUS, String.valueOf(userDevice.device.id), "0"));
        }
    }

    public UserDevice getUserDevice() {
        return userDevice;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ExceptionHandler.handleException(ctx, cause);
    }

}
