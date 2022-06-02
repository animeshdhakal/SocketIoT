package app.socketiot.server.hardware.handlers;

import app.socketiot.server.core.model.device.UserDevice;
import app.socketiot.server.core.model.enums.MsgType;
import app.socketiot.server.core.model.message.InternalMessage;
import app.socketiot.server.core.model.storage.PinStorage;
import io.netty.channel.ChannelHandlerContext;

public class HardwareSyncHandler {
    public static void handleMessage(ChannelHandlerContext ctx, UserDevice userDevice, InternalMessage message) {
        if (userDevice.device.pinStorage.size() == 0) {
            ctx.writeAndFlush(message);
            return;
        }

        for (Short pin : userDevice.device.pinStorage.keySet()) {
            StringBuilder builder = new StringBuilder();
            PinStorage storage = userDevice.device.pinStorage.get(pin);

            builder.append(pin);

            for (String value : storage.getValues()) {
                builder.append("\0").append(value);
            }

            ctx.writeAndFlush(new InternalMessage(MsgType.WRITE, builder.toString()));
        }
    }
}
