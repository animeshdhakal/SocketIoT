package app.socketiot.server.api;

import java.util.concurrent.ConcurrentHashMap;
import app.socketiot.server.api.model.DevicesList;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.db.model.BluePrint;
import app.socketiot.server.core.db.model.Device;
import app.socketiot.server.core.db.model.User;
import app.socketiot.server.core.http.JwtHttpHandler;
import app.socketiot.server.core.http.annotations.POST;
import app.socketiot.server.core.http.annotations.Path;
import app.socketiot.server.core.http.handlers.HttpReq;
import app.socketiot.server.core.http.handlers.HttpRes;
import app.socketiot.server.core.http.handlers.StatusMsg;
import app.socketiot.server.core.json.JsonParser;
import app.socketiot.server.core.json.model.BluePrintJson;
import app.socketiot.server.core.json.model.DeviceJson;
import app.socketiot.server.core.json.model.Widget;
import app.socketiot.server.utils.RandomUtil;

@Path("/api/device")
public class DeviceApiHandler extends JwtHttpHandler {
    public DeviceApiHandler(Holder holder) {
        super(holder);
    }

    @Path("/add")
    @POST
    public HttpRes add(HttpReq req) {
        User user = req.getUser();
        Device device = req.getContentAs(Device.class);
        
        if(device == null || device.name == null || device.blueprint_id == null) {
            return StatusMsg.badRequest("Incomplete Fields");
        }


        Device dbDevice = holder.deviceDao.getDeviceByEmail(user.email);

        if(dbDevice != null && dbDevice.name.equals(device.name)) {
            return StatusMsg.badRequest("Name should be unique");
        }


        device.email = req.getUser().email;
        device.token = RandomUtil.unique();

        BluePrint bluePrint = holder.bluePrintDao.getBluePrint(device.blueprint_id);

        if(bluePrint == null) {
            return StatusMsg.badRequest("BluePrint Not Found");
        }

        BluePrintJson bluePrintJson = JsonParser.parse(BluePrintJson.class, bluePrint.json);

        if(bluePrintJson == null || bluePrintJson.widgets == null) {
            return StatusMsg.badRequest("Invalid Blueprint");
        }

        DeviceJson deviceJson = new DeviceJson();

        deviceJson.pins = new ConcurrentHashMap<>();

        for(Widget widget : bluePrintJson.widgets) {
            deviceJson.pins.put(Integer.toString(widget.pin), "");
        }

        device.json = JsonParser.toString(deviceJson);

        holder.deviceDao.addDevice(device);

        return new HttpRes(new Device(device.token));
    }

    @POST
    @Path("/remove")
    public HttpRes remove(HttpReq req) {
        Device device = req.getContentAs(Device.class);

        if(device == null || device.token == null) {
            return StatusMsg.badRequest("Incomplete Fields");
        }

        Device dbDevice = holder.deviceDao.getDeviceByToken(device.token);

        if(dbDevice == null) {
            return StatusMsg.badRequest("Device Not Found");
        }

        holder.db.removeDevice(dbDevice.token);

        return StatusMsg.ok("Device Removed Successfully");
    }

    @POST
    @Path("/all")
    public HttpRes all(HttpReq req) {
        User user = req.getUser();
        
        DevicesList devices = new DevicesList(holder.deviceDao.getAllDevicesByEmail(user.email));

        return new HttpRes(devices);
    }

}
    

