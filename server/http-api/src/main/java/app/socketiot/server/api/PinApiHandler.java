package app.socketiot.server.api;

import app.socketiot.server.core.Holder;
import app.socketiot.server.core.http.BaseHttpHandler;
import app.socketiot.server.core.http.annotations.GET;
import app.socketiot.server.core.http.annotations.Path;
import app.socketiot.server.core.http.handlers.HttpReq;
import app.socketiot.server.core.http.handlers.HttpRes;
import app.socketiot.server.core.model.HardwareMessage;
import app.socketiot.server.core.model.MsgType;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.core.model.device.UserDevice;
import app.socketiot.server.utils.NumberUtil;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
@Path("/api/pin")
public class PinApiHandler extends BaseHttpHandler {

    public PinApiHandler(Holder holder) {
        super(holder);
    }

    @GET
    @Path("/set")
    public HttpRes put(HttpReq req) {
        String token = req.getQueryParam("token");
        String pin = req.getQueryParam("pin");
        String value = req.getQueryParam("value");

        if (token == null || pin == null || value == null) {
            return HttpRes.badRequest("Incomplete Fields");
        }

        UserDevice userDevice = holder.deviceDao.getUserDevice(token);

        if (userDevice == null) {
            return HttpRes.badRequest("Device Not Found");
        }

        if (!userDevice.device.updatePin(req.getCtx(), pin, value)) {
            return HttpRes.badRequest("Invalid Pin");
        }

        userDevice.user.json.sendToApps(req.getCtx(),
                new HardwareMessage(MsgType.WRITE, String.valueOf(userDevice.device.id), pin, value));
        userDevice.user.json.sendToHardware(req.getCtx(), userDevice.device.id,
                new HardwareMessage(MsgType.WRITE, pin, value));

        userDevice.user.isUpdated = true;

        return HttpRes.ok("OK");
    }

    @GET
    @Path("/get")
    public HttpRes get(HttpReq req) {
        String token = req.getQueryParam("token");
        String pin = req.getQueryParam("pin");

        if (token == null || pin == null) {
            return HttpRes.badRequest("Incomplete Fields");
        }

        Device device = holder.deviceDao.getDevice(token);

        if (device == null) {
            return HttpRes.badRequest("Device Not Found");
        }

        String value = device.pins.get(NumberUtil.parsePin(pin));

        return HttpRes.ok(value);
    }
}
