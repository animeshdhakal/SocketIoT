package app.socketiot.server.handlers.blueprint;

import app.socketiot.server.core.dao.BluePrintDao;
import app.socketiot.server.core.model.blueprint.BluePrint;
import app.socketiot.server.core.model.enums.MsgType;
import app.socketiot.server.core.model.json.JsonParser;
import app.socketiot.server.core.model.message.InternalMessage;
import io.netty.channel.ChannelHandlerContext;

public class GetBluePrintHandler {
    public static void handleMessage(BluePrintDao bluePrintDao, ChannelHandlerContext ctx, InternalMessage message) {
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

        BluePrint bluePrintFromDb = bluePrintDao.getBluePrintByID(bluePrint.id);

        if (bluePrintFromDb == null) {
            ctx.writeAndFlush(new InternalMessage(MsgType.FAILED, "Invalid BluePrint"));
            return;
        }

        ctx.writeAndFlush(new InternalMessage(MsgType.GET_BLUEPRINT, JsonParser.toPrivateJson(bluePrintFromDb)));
    }
}
