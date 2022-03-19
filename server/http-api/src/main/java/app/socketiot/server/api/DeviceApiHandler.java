package app.socketiot.server.api;

import java.util.concurrent.ConcurrentHashMap;
import app.socketiot.server.api.model.WidgetReq;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.http.JwtHttpHandler;
import app.socketiot.server.core.http.annotations.POST;
import app.socketiot.server.core.http.annotations.Path;
import app.socketiot.server.core.http.handlers.HttpReq;
import app.socketiot.server.core.http.handlers.HttpRes;
import app.socketiot.server.core.http.handlers.StatusMsg;
import app.socketiot.server.core.json.JsonParser;
import app.socketiot.server.core.model.blueprint.BluePrint;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.core.model.widgets.Widget;
import app.socketiot.server.utils.RandomUtil;
import io.netty.channel.ChannelHandler;

@Path("/api/device")
@ChannelHandler.Sharable
public class DeviceApiHandler extends JwtHttpHandler {
    public DeviceApiHandler(Holder holder) {
        super(holder);
    }

    @Path("/add")
    @POST
    public HttpRes add(HttpReq req) {
        Device device = req.getContentAs(Device.class);

        if (device == null || device.name == null || device.blueprint_id == null || device.id == -1) {
            return StatusMsg.badRequest("Incomplete Fields");
        }

        Device dbDevice = req.user.json.getLastDevice();

        if (dbDevice != null && dbDevice.name.equals(device.name)) {
            return StatusMsg.badRequest("Name should be unique");
        }

        device.token = RandomUtil.unique();

        BluePrint bluePrint = holder.bluePrintDao.getBluePrint(device.blueprint_id);

        if (bluePrint == null) {
            return StatusMsg.badRequest("BluePrint Not Found");
        }

        if (bluePrint.json == null) {
            return StatusMsg.badRequest("Invalid Blueprint");
        }

        device.pins = new ConcurrentHashMap<>();

        for (Widget widget : bluePrint.json.widgets) {
            device.pins.put(widget.pin, "");
        }

        holder.deviceDao.addDevice(req.user, device);
        req.user.json.addDevice(device);

        Device resDevice = new Device(device.token);
        req.user.isUpdated = true;

        return new HttpRes(resDevice);
    }

    @POST
    @Path("/remove")
    public HttpRes remove(HttpReq req) {
        Device device = req.getContentAs(Device.class);

        if (device == null || device.token == null) {
            return StatusMsg.badRequest("Incomplete Fields");
        }

        if (!req.user.json.removeDevice(device.token)) {
            return StatusMsg.badRequest("Device Not Found");
        }

        holder.deviceDao.removeDevice(device.token);
        req.user.isUpdated = true;

        return StatusMsg.ok("Device Removed Successfully");
    }

    @POST
    @Path("/all")
    public HttpRes all(HttpReq req) {
        WidgetReq widgetreq = req.getContentAs(WidgetReq.class);

        if (widgetreq != null) {
            return new HttpRes(JsonParser.toString(holder.deviceDao.getAllDevicesByBlueprint(widgetreq.blueprint_id),
                    "DeviceJsonFilter", "json"));
        } else {
            return new HttpRes(JsonParser.toString(req.user.json.devices, "DeviceJsonFilter", "json"));
        }

    }

}
