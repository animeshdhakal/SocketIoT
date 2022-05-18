package app.socketiot.server.app;

import app.socketiot.server.core.Holder;
import app.socketiot.server.core.exceptions.ExceptionHandler;
import app.socketiot.server.core.model.HardwareMessage;
import app.socketiot.server.core.model.MsgType;
import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.core.pinstore.MultiValuePinStore;
import app.socketiot.server.core.pinstore.PinStore;
import app.socketiot.server.core.pinstore.SingleValuePinStore;
import app.socketiot.server.utils.NumberUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class AppHandler extends ChannelInboundHandlerAdapter {
    private final User user;

    public AppHandler(Holder holder, User user) {
        this.user = user;
    }

    public void handleSync(ChannelHandlerContext ctx, int deviceID) {
        Device device = user.json.getDevice(deviceID);
        if (device == null) {
            return;
        }
        for (Short key : device.pins.keySet()) {
            PinStore store = device.pins.get(key);
            store.sendSync(ctx.channel(), deviceID, key);
        }
    }

    public void handleWrite(ChannelHandlerContext ctx, HardwareMessage msg) {
        if (msg.body.length > 2) {
            short pin = Short.valueOf(msg.body[0]);

            Device device = user.json.getDevice(pin);
            if (device == null) {
                return;
            }

            PinStore store = device.pins.get(Short.valueOf(msg.body[1]));

            if (store instanceof MultiValuePinStore) {
                for (int i = 2; i < msg.body.length; i++) {
                    store.updateValue(msg.body[i]);
                }
            } else if (store instanceof SingleValuePinStore) {
                store.updateValue(msg.body[2]);
            }

            user.json.broadCastWriteMessage(ctx.channel(), device.id, pin, store);

            user.isUpdated = true;
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HardwareMessage) {
            HardwareMessage message = (HardwareMessage) msg;

            switch (message.type) {
                case MsgType.WRITE:
                    handleWrite(ctx, message);
                    break;
                case MsgType.SYNC:
                    handleSync(ctx, NumberUtil.parsePin(message.body[0]));
                    break;
                case MsgType.PING:
                    ctx.writeAndFlush(msg);
                    break;
            }
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        user.json.removeAppChannel(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ExceptionHandler.handleException(ctx, cause);
    }
}
