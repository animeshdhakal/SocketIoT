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
import app.socketiot.server.core.json.model.DeviceJson;
import app.socketiot.server.core.json.model.Widget;
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

        if(bluePrint.json == null || bluePrint.json.widgets == null) {
            return StatusMsg.badRequest("Invalid Blueprint");
        }

        DeviceJson deviceJson = new DeviceJson();

        deviceJson.pins = new ConcurrentHashMap<>();

        for(Widget widget : bluePrint.json.widgets) {
            deviceJson.pins.put(Integer.toString(widget.pin), "");
        }

        holder.deviceDao.addDevice(device);

        Device resDevice = new Device(device.token);
        resDevice.online = null;


        return new HttpRes(resDevice);
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
    

