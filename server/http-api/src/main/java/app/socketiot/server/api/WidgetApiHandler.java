package app.socketiot.server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import com.fasterxml.jackson.databind.DeserializationFeature;
import app.socketiot.server.api.model.WidgetReqRes;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.http.JwtHttpHandler;
import app.socketiot.server.core.http.annotations.POST;
import app.socketiot.server.core.http.annotations.Path;
import app.socketiot.server.core.http.handlers.HttpReq;
import app.socketiot.server.core.http.handlers.HttpRes;
import app.socketiot.server.core.http.handlers.StatusMsg;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.core.model.widgets.type.MultiValueWidget;
import app.socketiot.server.core.model.widgets.type.SingleValueWidget;
import app.socketiot.server.core.model.widgets.type.Widget;
import app.socketiot.server.core.pinstore.MultiValuePinStore;
import app.socketiot.server.core.pinstore.SingleValuePinStore;
import io.netty.channel.ChannelHandler;

@Path("/api/widget")
@ChannelHandler.Sharable
public class WidgetApiHandler extends JwtHttpHandler {
    private static final ObjectMapper mapper = new ObjectMapper().configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
            false);

    public WidgetApiHandler(Holder holder) {
        super(holder);
    }

    @Path("/add")
    @POST
    public HttpRes add(HttpReq req) {
        try {
            WidgetReqRes widget = mapper.readValue(req.getContent(), WidgetReqRes.class);
            if (widget == null || widget.blueprint_id == null || widget.widgets == null) {
                return StatusMsg.badRequest("Incomplete Fields");
            }

            if (req.user.dash.replaceWidgets(widget.blueprint_id, widget.widgets)) {
                List<Device> devices = holder.deviceDao.getAllDevicesByBlueprint(widget.blueprint_id);
                for (Device device : devices) {
                    device.pins = new ConcurrentHashMap<>();
                    for (Widget awidget : widget.widgets) {
                        if (device.pins.get(awidget.pin) != null) {
                            device.pins.put(awidget.pin, device.pins.get(awidget.pin));
                        } else {
                            if (awidget instanceof SingleValueWidget) {
                                device.pins.put(awidget.pin, new SingleValuePinStore(""));
                            } else if (awidget instanceof MultiValueWidget) {
                                device.pins.put(awidget.pin, new MultiValuePinStore(""));
                            }
                        }
                    }
                }
                req.user.isUpdated = true;
                return StatusMsg.ok("Widgets Added Successfully");
            } else {
                return StatusMsg.badRequest("Invalid Blueprint Id");
            }

        } catch (Exception e) {
            return StatusMsg.badRequest("Incomplete Fields");
        }

    }

}
