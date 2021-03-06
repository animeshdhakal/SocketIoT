package app.socketiot.server.handlers;

import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.core.model.enums.MsgType;
import app.socketiot.server.core.model.message.InternalMessage;
import app.socketiot.server.core.model.storage.PinStorage;
import io.netty.channel.ChannelHandlerContext;

public class WriteHandler {
    public static void handleMessage(ChannelHandlerContext ctx, User user, InternalMessage message) {
        if (message.body.length < 3) {
            return;
        }

        String deviceID = message.body[0];
        String pin = message.body[1];

        if (deviceID == null || pin == null) {
            return;
        }

        int iDeviceID = Integer.parseInt(deviceID);
        short sPin = Short.parseShort(pin);

        Device device = user.dash.getDeviceByID(iDeviceID);

        if (device == null) {
            return;
        }

        PinStorage pinStorage = device.pinStorage.get(sPin);

        if (pinStorage == null) {
            return;
        }

        StringBuilder builder = new StringBuilder();

        builder.append(pin);

        for (int i = 2; i < message.body.length; i++) {
            pinStorage.updateValue(message.body[i]);
            builder.append("\0").append(message.body[i]);
        }

        user.dash.sendToHardwares(ctx.channel(), iDeviceID, new InternalMessage(MsgType.WRITE, builder.toString()));
        user.dash.sendToApps(ctx.channel(), message);

        user.lastModified = System.currentTimeMillis();
    }
}
