package app.socketiot.server.handlers.blueprint;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import app.socketiot.server.core.dao.DeviceDao;
import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.blueprint.BluePrint;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.core.model.enums.MsgType;
import app.socketiot.server.core.model.json.JsonParser;
import app.socketiot.server.core.model.message.InternalMessage;
import app.socketiot.server.core.model.storage.PinStorage;
import app.socketiot.server.core.model.storage.SingleValuePinStorage;
import app.socketiot.server.core.model.widgets.Widget;
import io.netty.channel.ChannelHandlerContext;

public class AddWidgetsBluePrintHandler {
    public static void handleMessage(DeviceDao deviceDao, ChannelHandlerContext ctx, User user,
            InternalMessage message) {
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

        BluePrint dbBluePrint = user.dash.getBluePrintByID(bluePrint.id);

        if (dbBluePrint == null) {
            ctx.writeAndFlush(new InternalMessage(MsgType.FAILED, "BluePrint Not Found"));
            return;
        }

        List<Device> devices = deviceDao.getAllDevicesByBluePrintID(bluePrint.id);

        dbBluePrint.widgets = bluePrint.widgets;

        for (Device device : devices) {
            ConcurrentHashMap<Short, PinStorage> pinStorages = new ConcurrentHashMap<>();
            for (Widget widget : bluePrint.widgets) {
                if (device.pinStorage.get(widget.pin) != null) {
                    pinStorages.put(widget.pin, device.pinStorage.get(widget.pin));
                } else {
                    pinStorages.put(widget.pin, new SingleValuePinStorage(null));
                }
            }
        }

        user.lastModified = System.currentTimeMillis();

        ctx.writeAndFlush(new InternalMessage(MsgType.ADD_WIDGETS_BLUEPRINT, JsonParser.toPrivateJson(dbBluePrint)));
    }
}
