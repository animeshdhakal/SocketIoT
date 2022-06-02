package app.socketiot.server.handlers.blueprint;

import app.socketiot.server.core.dao.BluePrintDao;
import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.blueprint.BluePrint;
import app.socketiot.server.core.model.enums.MsgType;
import app.socketiot.server.core.model.json.JsonParser;
import app.socketiot.server.core.model.message.InternalMessage;
import io.netty.channel.ChannelHandlerContext;

public class DeleteBluePrintHandler {
    public static void handleMessage(BluePrintDao bluePrintDao, ChannelHandlerContext ctx, User user,
            InternalMessage message) {

        if (message.body.length < 1) {
            ctx.writeAndFlush(new InternalMessage(MsgType.FAILED, "Invalid Command"));
            return;
        }

        String bluePrintJson = message.body[0];

        if (bluePrintJson == null || bluePrintJson.isEmpty()) {
            ctx.writeAndFlush(new InternalMessage(MsgType.FAILED, "Invalid BluePrint"));
            return;
        }

        BluePrint bluePrint = JsonParser.parseProtectedJson(bluePrintJson, BluePrint.class);

        if (bluePrint == null || bluePrint.id == null) {
            ctx.writeAndFlush(new InternalMessage(MsgType.FAILED, "Invalid BluePrint"));
            return;
        }

        if (user.dash.deleteBluePrintByID(bluePrint.id)) {
            bluePrintDao.deleteBluePrintByID(bluePrint.id);
            user.lastModified = System.currentTimeMillis();

            // TODO: Remove All User By BluePrintID

            ctx.writeAndFlush(new InternalMessage(MsgType.DELETE_BLUEPRINT, JsonParser.toPrivateJson(bluePrint)));
        } else {
            ctx.writeAndFlush(new InternalMessage(MsgType.FAILED, "Invalid BluePrint"));
        }

    }
}
