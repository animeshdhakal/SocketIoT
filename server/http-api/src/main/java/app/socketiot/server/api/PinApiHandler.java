package app.socketiot.server.api;

import app.socketiot.server.core.Holder;
import app.socketiot.server.core.http.BaseHttpHandler;
import app.socketiot.server.core.http.annotations.GET;
import app.socketiot.server.core.http.annotations.Path;
import app.socketiot.server.core.http.handlers.HttpReq;
import app.socketiot.server.core.http.handlers.HttpRes;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.core.model.device.UserDevice;
import app.socketiot.server.core.pinstore.PinStore;
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

        short spin = Short.valueOf(pin);
        PinStore store = userDevice.device.pins.get(spin);

        if (store == null) {
            return HttpRes.badRequest("Invalid Pin");
        }

        store.updateValue(value);

        userDevice.user.json.broadCastWriteMessage(null, userDevice.device.id, spin, store);

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

        String value = device.pins.get(NumberUtil.parsePin(pin)).getValue();

        return HttpRes.ok(value);
    }
}
