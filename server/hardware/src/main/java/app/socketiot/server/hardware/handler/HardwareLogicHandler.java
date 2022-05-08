package app.socketiot.server.hardware.handler;

import app.socketiot.server.core.model.HardwareMessage;
import app.socketiot.server.core.model.device.UserDevice;
import app.socketiot.server.core.pinstore.MultiValuePinStore;
import app.socketiot.server.core.pinstore.PinStore;
import app.socketiot.server.core.pinstore.SingleValuePinStore;
import app.socketiot.server.utils.NumberUtil;
import io.netty.channel.ChannelHandlerContext;

public class HardwareLogicHandler {
    public final UserDevice userDevice;

    public HardwareLogicHandler(UserDevice userDevice) {
        this.userDevice = userDevice;
    }

    public void handleWrite(ChannelHandlerContext ctx, HardwareMessage msg) {
        if (msg.body.length < 2)
            return;

        short pin = NumberUtil.parsePin(msg.body[0]);
        PinStore store = userDevice.device.pins.get(pin);

        if (store == null) {
            return;
        }

        if (store instanceof MultiValuePinStore) {
            for (int i = 1; i < msg.body.length; i++) {
                store.updateValue(msg.body[i]);
            }
        } else if (store instanceof SingleValuePinStore) {
            store.updateValue(msg.body[1]);
        }

        userDevice.user.json.broadCastWriteMessage(ctx.channel(), userDevice.device.id, pin, store);

        userDevice.user.isUpdated = true;
    }

    public void handleSync(ChannelHandlerContext ctx) {
        for (short key : userDevice.device.pins.keySet()) {
            PinStore store = userDevice.device.pins.get(key);
            store.sendSync(ctx.channel(), userDevice.device.id, key);
        }
    }

}
