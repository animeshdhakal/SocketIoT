package app.socketiot.server.handlers.device;

import app.socketiot.server.core.dao.DeviceDao;
import app.socketiot.server.core.model.StatusMsg;
import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.core.model.enums.MsgType;
import app.socketiot.server.core.model.json.JsonParser;
import app.socketiot.server.core.model.message.InternalMessage;
import io.netty.channel.ChannelHandlerContext;

public class RemoveDeviceHandler {
    public static void handleMessage(DeviceDao deviceDao, ChannelHandlerContext ctx, User user,
            InternalMessage message) {
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

        if (user.dash.removeDeviceByToken(device.token)) {
            deviceDao.removeUserDeviceByToken(device.token);
            user.lastModified = System.currentTimeMillis();

            ctx.writeAndFlush(new InternalMessage(MsgType.REMOVE_DEVICE, StatusMsg.success("Device Removed")));

        } else {
            ctx.writeAndFlush(new InternalMessage(MsgType.FAILED, "Device Not Found"));
        }

    }
}
