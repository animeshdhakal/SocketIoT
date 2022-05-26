package app.socketiot.server.api;

import java.util.concurrent.ConcurrentHashMap;

import app.socketiot.server.api.model.ProvisioningResponse;
import app.socketiot.server.api.model.WidgetReqRes;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.http.JwtHttpHandler;
import app.socketiot.server.core.http.annotations.POST;
import app.socketiot.server.core.http.annotations.Path;
import app.socketiot.server.core.http.handlers.HttpReq;
import app.socketiot.server.core.http.handlers.HttpRes;
import app.socketiot.server.core.http.handlers.StatusMsg;
import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.blueprint.BluePrint;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.core.model.widgets.type.MultiValueWidget;
import app.socketiot.server.core.model.widgets.type.SingleValueWidget;
import app.socketiot.server.core.model.widgets.type.Widget;
import app.socketiot.server.core.pinstore.MultiValuePinStore;
import app.socketiot.server.core.pinstore.SingleValuePinStore;
import app.socketiot.server.utils.RandomUtil;
import io.netty.channel.ChannelHandler;

@Path("/api/device")
@ChannelHandler.Sharable
public class DeviceApiHandler extends JwtHttpHandler {
    public DeviceApiHandler(Holder holder) {
        super(holder);
    }

    boolean addPins(Device device, String blueprint_id) {
        BluePrint bluePrint = holder.bluePrintDao.getBluePrint(blueprint_id);

        if (bluePrint == null) {
            return false;
        }

        if (bluePrint.widgets == null) {
            return false;
        }

        device.pins = new ConcurrentHashMap<>();

        for (Widget widget : bluePrint.widgets) {
            if (widget instanceof SingleValueWidget) {
                device.pins.put(widget.pin, new SingleValuePinStore(""));
            } else if (widget instanceof MultiValueWidget) {
                device.pins.put(widget.pin, new MultiValuePinStore(""));
            }
        }

        return true;
    }

    HttpRes addDevice(User user, Device device) {
        Device dbDevice = user.dash.getLastDevice();

        if (dbDevice != null && dbDevice.name.equals(device.name)) {
            return StatusMsg.badRequest("Name should be unique");
        }

        if (!addPins(device, device.blueprint_id)) {
            return StatusMsg.badRequest("Blueprint not found");
        }

        device.id = dbDevice == null ? 1 : dbDevice.id + 1;

        holder.deviceDao.addDevice(user, device);
        user.dash.addDevice(device);

        Device resDevice = new Device(device.token);
        user.updated();

        return HttpRes.json(resDevice);
    }

    @Path("/add")
    @POST
    public HttpRes add(HttpReq req) {
        Device device = req.getContentAs(Device.class);

        if (device == null || device.name == null || device.blueprint_id == null) {
            return StatusMsg.badRequest("Incomplete Fields");
        }

        device.token = RandomUtil.unique();

        return addDevice(req.user, device);
    }

    @POST
    @Path("/remove")
    public HttpRes remove(HttpReq req) {
        Device device = req.getContentAs(Device.class);

        if (device == null || device.token == null) {
            return StatusMsg.badRequest("Incomplete Fields");
        }

        Device dbDevice = req.user.dash.getDevice(device.token);

        if (dbDevice == null) {
            return StatusMsg.badRequest("Device Not Found");
        }

        req.user.dash.disconnectDevices(device.token);
        req.user.dash.removeDevice(device.token);
        holder.deviceDao.removeDevice(device.token);
        req.user.updated();

        return StatusMsg.ok("Device Removed Successfully");
    }

    HttpRes startProvisioning(User user) {
        user.dash.provisioningToken = RandomUtil.unique();
        return HttpRes.json(new ProvisioningResponse(user.dash.provisioningToken));
    }

    HttpRes endProvisioning(User user, String name, String blueprint_id) {
        if (user.dash.provisioningToken == null) {
            return StatusMsg.badRequest("Provisioning Token Not Found");
        }

        Device device = holder.deviceDao.getDevice(user.dash.provisioningToken);

        if (device == null) {
            return StatusMsg.badRequest("Device Not Yet Connected !");
        }

        device.blueprint_id = blueprint_id;
        device.name = name;

        if (!addPins(device, blueprint_id)) {
            holder.deviceDao.removeDevice(device.token);
            user.dash.disconnectDevices(device.token);
            user.dash.removeDevice(device.token);
            return StatusMsg.badRequest("Provisioning Failed");
        }

        user.dash.isProvisioningDeviceOnline = false;
        user.dash.provisioningToken = null;

        user.updated();

        return StatusMsg.ok("Device Provisioned Successfully");
    }

    @POST
    @Path("/provision")
    public HttpRes provision(HttpReq req) {
        String name = req.getJsonFieldAsString("name");
        String blueprint_id = req.getJsonFieldAsString("blueprint_id");

        if (name != null && blueprint_id != null) {
            return endProvisioning(req.user, name, blueprint_id);
        } else {
            return startProvisioning(req.user);
        }
    }

    @POST
    @Path("/all")
    public HttpRes all(HttpReq req) {
        WidgetReqRes widgetreq = req.getContentAs(WidgetReqRes.class);

        if (widgetreq != null && widgetreq.blueprint_id != null) {
            return HttpRes.json(holder.deviceDao.getAllDevicesByBlueprint(widgetreq.blueprint_id));
        } else {
            return HttpRes.json(req.user.dash.devices);
        }

    }

}
