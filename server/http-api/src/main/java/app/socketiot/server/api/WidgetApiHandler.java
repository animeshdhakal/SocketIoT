package app.socketiot.server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import com.fasterxml.jackson.databind.DeserializationFeature;
import app.socketiot.server.api.model.WidgetReq;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.http.JwtHttpHandler;
import app.socketiot.server.core.http.annotations.POST;
import app.socketiot.server.core.http.annotations.Path;
import app.socketiot.server.core.http.handlers.HttpReq;
import app.socketiot.server.core.http.handlers.HttpRes;
import app.socketiot.server.core.http.handlers.StatusMsg;
import app.socketiot.server.core.json.model.DeviceJson;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.core.model.widgets.Widget;
import io.netty.channel.ChannelHandler;

@Path("/api/widget")
@ChannelHandler.Sharable
public class WidgetApiHandler extends JwtHttpHandler {
    static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public WidgetApiHandler(Holder holder) {
        super(holder);
    }

    @Path("/add")
    @POST
    public HttpRes add(HttpReq req) {
        try {
            WidgetReq widget = mapper.readValue(req.getContent(), WidgetReq.class);
            if (widget == null || widget.blueprint_id == null || widget.widgets == null) {
                return StatusMsg.badRequest("Incomplete Fields");
            }

            if (holder.bluePrintDao.replaceWidgets(req.user.email, widget.blueprint_id, widget.widgets)) {
                List<Device> devices = holder.deviceDao.getAllDevicesByBluePrint(widget.blueprint_id);
                for (Device device : devices) {
                    DeviceJson deviceJson = new DeviceJson();
                    deviceJson.pins = new ConcurrentHashMap<>();
                    for (Widget awidget : widget.widgets) {
                        if (device.json.pins.get(awidget.pin) != null) {
                            deviceJson.pins.put(awidget.pin, device.json.pins.get(awidget.pin));
                        } else {
                            deviceJson.pins.put(awidget.pin, "");
                        }
                    }
                    device.json = deviceJson;
                }
                return StatusMsg.ok("Widgets Added Successfully");
            } else {
                return StatusMsg.badRequest("Invalid Blueprint Id");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return StatusMsg.badRequest("Incomplete Fields");
        }

    }

}
