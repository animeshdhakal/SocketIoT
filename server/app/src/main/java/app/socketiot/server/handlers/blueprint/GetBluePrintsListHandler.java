package app.socketiot.server.handlers.blueprint;

import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.enums.MsgType;
import app.socketiot.server.core.model.json.JsonParser;
import app.socketiot.server.core.model.message.InternalMessage;
import io.netty.channel.ChannelHandlerContext;

public class GetBluePrintsListHandler {
    public static void handleMessage(ChannelHandlerContext ctx, User user, InternalMessage message) {

        ctx.writeAndFlush(
                new InternalMessage(MsgType.GET_BLUEPRINTS_LIST, JsonParser.toProtectedJson(user.dash.bluePrints)));
    }
}
