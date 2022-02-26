package app.socketiot.server.api;

import java.util.ArrayList;
import java.util.List;

import app.socketiot.server.api.model.BluePrintList;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.http.JwtHttpHandler;
import app.socketiot.server.core.http.annotations.POST;
import app.socketiot.server.core.http.annotations.Path;
import app.socketiot.server.core.http.handlers.HttpReq;
import app.socketiot.server.core.http.handlers.HttpRes;
import app.socketiot.server.core.http.handlers.StatusMsg;
import app.socketiot.server.core.json.JsonParser;
import app.socketiot.server.core.json.model.BluePrintJson;
import app.socketiot.server.core.model.blueprint.BluePrint;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.core.model.widgets.Widget;
import app.socketiot.server.utils.RandomUtil;
import io.netty.channel.ChannelHandler;

@Path("/api/blueprint")
@ChannelHandler.Sharable
public class BluePrintApiHandler extends JwtHttpHandler {
    public BluePrintApiHandler(Holder holder) {
        super(holder);
    }

    @Path("/create")
    @POST
    public HttpRes add(HttpReq req) {
        BluePrint blueprint = req.getContentAs(BluePrint.class);

        if (blueprint == null || blueprint.name == null) {
            return StatusMsg.badRequest("Incomplete Fields");
        }

        BluePrint bluePrint = holder.bluePrintDao.getBluePrintByEmail(req.user.email);

        if (bluePrint != null && bluePrint.name.equals(blueprint.name)) {
            return StatusMsg.badRequest("Name should be unique");
        }

        blueprint.id = RandomUtil.unique(8);
        blueprint.email = req.user.email;
        blueprint.json = new BluePrintJson();
        blueprint.json.widgets = new ArrayList<Widget>();

        holder.bluePrintDao.addBluePrint(blueprint);

        return new HttpRes(new BluePrint(blueprint.id));
    }

    @Path("/delete")
    @POST
    public HttpRes delete(HttpReq req) {
        BluePrint blueprint = req.getContentAs(BluePrint.class);

        if (blueprint == null || blueprint.id == null) {
            return StatusMsg.badRequest("Incomplete Fields");
        }

        BluePrint dbBluePrint = holder.bluePrintDao.getBluePrintByEmailAndID(req.user.email, blueprint.id);

        if (dbBluePrint == null) {
            return StatusMsg.badRequest("BluePrint Not Found");
        }

        holder.db.removeBluePrint(blueprint.id);

        List<Device> bluePrintDevices = holder.deviceDao.getAllDevicesByBluePrint(blueprint.id);

        for (Device device : bluePrintDevices) {
            holder.db.removeDevice(device.token);
        }

        return StatusMsg.ok("BluePrint Deleted Successfully");
    }

    @Path("/all")
    @POST
    public HttpRes all(HttpReq req) {
        BluePrintList bluePrintList = new BluePrintList(
                holder.bluePrintDao.getAllBluePrintsByEmail(req.user.email));

        return new HttpRes(JsonParser.toString(bluePrintList, "BluePrintJsonFilter", "json"));
    }

    @Path("/get")
    @POST
    public HttpRes get(HttpReq req) {
        BluePrint bluePrint = req.getContentAs(BluePrint.class);

        if (bluePrint == null || bluePrint.id == null) {
            return StatusMsg.badRequest("Incomplete Fields");
        }

        bluePrint = holder.bluePrintDao.getBluePrint(bluePrint.id);

        if (bluePrint == null) {
            return StatusMsg.badRequest("BluePrint Not Found");
        }

        if (bluePrint.json == null) {
            return StatusMsg.badRequest("Invalid Blueprint");
        }

        return new HttpRes(bluePrint.json);
    }

}
