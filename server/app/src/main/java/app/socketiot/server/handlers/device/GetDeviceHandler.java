package app.socketiot.server.handlers.device;

import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.core.model.enums.MsgType;
import app.socketiot.server.core.model.json.JsonParser;
import app.socketiot.server.core.model.message.InternalMessage;
import io.netty.channel.ChannelHandlerContext;

public class GetDeviceHandler {
    public static void handleMessage(ChannelHandlerContext ctx, User user, InternalMessage message) {
        String deviceJson = message.body[0];

        if (deviceJson == null || deviceJson.isEmpty()) {
            ctx.writeAndFlush(new InternalMessage(MsgType.FAILED, "Invalid Device"));
            return;
        }

        Device device = JsonParser.parseProtectedJson(deviceJson, Device.class);

        if (device.token == null || device.token.isEmpty()) {
            ctx.writeAndFlush(new InternalMessage(MsgType.FAILED, "Invalid Device"));
            return;
        }

        device = user.dash.getDeviceByToken(device.token);

        if (device == null) {
            ctx.writeAndFlush(new InternalMessage(MsgType.FAILED, "Invalid Device"));
            return;
        }

        ctx.writeAndFlush(new InternalMessage(MsgType.GET_DEVICE, JsonParser.toProtectedJson(device)));
    }
}
