package app.socketiot.server.handlers;

import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.core.model.enums.MsgType;
import app.socketiot.server.core.model.message.InternalMessage;
import app.socketiot.server.core.model.storage.PinStorage;
import io.netty.channel.ChannelHandlerContext;

public class SyncHandler {
    public static void handleMessage(ChannelHandlerContext ctx, User user, InternalMessage message) {
        if (message.body.length < 1) {
            return;
        }

        String deviceID = message.body[0];

        if (deviceID == null) {
            return;
        }

        int iDeviceID = Integer.parseInt(deviceID);

        Device device = user.dash.getDeviceByID(iDeviceID);

        if (device == null) {
            return;
        }

        if (device.pinStorage.size() == 0) {
            ctx.writeAndFlush(message);
            return;
        }

        for (Short pin : device.pinStorage.keySet()) {
            StringBuilder builder = new StringBuilder();

            builder.append(deviceID).append("\0").append(pin);

            PinStorage store = device.pinStorage.get(pin);

            for (String value : store.getValues()) {
                builder.append("\0").append(value);
            }

            ctx.writeAndFlush(new InternalMessage(MsgType.WRITE, builder.toString()));
        }
    }
}
