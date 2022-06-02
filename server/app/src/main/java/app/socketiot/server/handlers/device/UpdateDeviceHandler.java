package app.socketiot.server.handlers.device;

import app.socketiot.server.core.model.StatusMsg;
import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.core.model.enums.MsgType;
import app.socketiot.server.core.model.json.JsonParser;
import app.socketiot.server.core.model.message.InternalMessage;
import io.netty.channel.ChannelHandlerContext;

public class UpdateDeviceHandler {
    public static void handleMessage(ChannelHandlerContext ctx, User user, InternalMessage message) {
        if (message.body.length < 1) {
            ctx.writeAndFlush(new InternalMessage(MsgType.FAILED, "Invalid Command"));
            return;
        }

        String deviceJson = message.body[0];

        if (deviceJson == null || deviceJson.isEmpty()) {
            ctx.writeAndFlush(new InternalMessage(MsgType.FAILED, "Invalid Device"));
            return;
        }

        Device device = JsonParser.parseProtectedJson(deviceJson, Device.class);

        if (device.token == null || device.token.isEmpty() || device.isInvalid()) {
            ctx.writeAndFlush(new InternalMessage(MsgType.FAILED, "Invalid Device"));
            return;
        }

        Device dbDevice = user.dash.getDeviceByToken(device.token);

        if (dbDevice == null) {
            ctx.writeAndFlush(new InternalMessage(MsgType.FAILED, "Invalid Device"));
            return;
        }

        dbDevice.name = device.name;

        user.lastModified = System.currentTimeMillis();

        ctx.writeAndFlush(new InternalMessage(MsgType.UPDATE_DEVICE, StatusMsg.success("Device updated")));
    }
}
