package app.socketiot.server.handlers.blueprint;

import app.socketiot.server.core.dao.BluePrintDao;
import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.blueprint.BluePrint;
import app.socketiot.server.core.model.enums.MsgType;
import app.socketiot.server.core.model.json.JsonParser;
import app.socketiot.server.core.model.message.InternalMessage;
import app.socketiot.utils.TokenUtil;
import io.netty.channel.ChannelHandlerContext;

public class CreateBluePrintHandler {
    public static void handleMessage(BluePrintDao bluePrintDao, ChannelHandlerContext ctx, User user,
            InternalMessage message) {
        String bluePrintJson = message.body[0];

        if (bluePrintJson == null || bluePrintJson.isEmpty()) {
            ctx.writeAndFlush(new InternalMessage(MsgType.FAILED, "Invalid BluePrint"));
            return;
        }

        BluePrint bluePrint = JsonParser.parseProtectedJson(bluePrintJson, BluePrint.class);

        if (bluePrint == null || bluePrint.isInvalid()) {
            ctx.writeAndFlush(new InternalMessage(MsgType.FAILED, "Invalid BluePrint"));
            return;
        }

        bluePrint.id = TokenUtil.generate(8);

        user.dash.addBluePrint(bluePrint);
        bluePrintDao.addBluePrint(bluePrint);

        user.lastModified = System.currentTimeMillis();

        ctx.writeAndFlush(new InternalMessage(MsgType.CREATE_BLUEPRINT, JsonParser.toPrivateJson(bluePrint)));
    }
}
