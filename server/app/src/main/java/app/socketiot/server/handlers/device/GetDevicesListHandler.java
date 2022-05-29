package app.socketiot.server.handlers.device;

import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.enums.MsgType;
import app.socketiot.server.core.model.json.JsonParser;
import app.socketiot.server.core.model.message.InternalMessage;
import io.netty.channel.ChannelHandlerContext;

public class GetDevicesListHandler {
    public static void handleMessage(ChannelHandlerContext ctx, User user, InternalMessage message) {
        ctx.writeAndFlush(
                new InternalMessage(MsgType.GET_DEVICES_LIST, JsonParser.toProtectedJson(user.dash.devices)));
    }
}
