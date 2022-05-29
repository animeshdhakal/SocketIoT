package app.socketiot.server.handlers.device;

import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.core.model.enums.MsgType;
import app.socketiot.server.core.model.json.JsonParser;
import app.socketiot.server.core.model.message.InternalMessage;
import app.socketiot.utils.TokenUtil;
import io.netty.channel.ChannelHandlerContext;

public class AddDeviceHandler {
    public static void handleMessage(ChannelHandlerContext ctx, User user, InternalMessage message) {
        String deviceJson = message.body[0];

        if (deviceJson == null || deviceJson.isEmpty()) {
            ctx.writeAndFlush(new InternalMessage(MsgType.FAILED, "Invalid Device"));
            return;
        }

        Device device = JsonParser.parseProtectedJson(deviceJson, Device.class);

        if (device.isInvalid()) {
            ctx.writeAndFlush(new InternalMessage(MsgType.FAILED, "Invalid Device"));
            return;
        }

        device.token = TokenUtil.generate();

        Device lastDevice = user.dash.getLastDevice();

        device.id = lastDevice == null ? 1 : lastDevice.id + 1;

        user.dash.addDevice(device);

        user.lastModified = System.currentTimeMillis();

        ctx.writeAndFlush(new InternalMessage(MsgType.ADD_DEVICE, JsonParser.toPrivateJson(device)));
    }
}
