package app.socketiot.server.hardware.handlers;

import app.socketiot.server.core.model.device.UserDevice;
import app.socketiot.server.core.model.enums.MsgType;
import app.socketiot.server.core.model.message.InternalMessage;
import app.socketiot.server.core.model.storage.PinStorage;
import io.netty.channel.ChannelHandlerContext;

public class HardwareWriteHandler {
    public static void handleMessage(ChannelHandlerContext ctx, UserDevice userDevice, InternalMessage message) {
        if (message.body.length < 2) {
            return;
        }

        String pin = message.body[0];

        short sPin = Short.parseShort(pin);

        PinStorage storage = userDevice.device.pinStorage.get(sPin);

        if (storage == null) {
            return;
        }

        StringBuilder builder = new StringBuilder();

        builder.append(userDevice.device.id);

        for (int i = 1; i < message.body.length; i++) {
            builder.append("\0").append(message.body[i]);
            storage.updateValue(message.body[i]);
        }

        userDevice.user.lastModified = System.currentTimeMillis();

        userDevice.user.dash.sendToApps(ctx.channel(), new InternalMessage(MsgType.WRITE, builder.toString()));
        userDevice.user.dash.sendToHardwares(ctx.channel(), userDevice.device.id, message);
    }
}
