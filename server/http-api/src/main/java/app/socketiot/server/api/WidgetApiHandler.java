package app.socketiot.server.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.socketiot.server.api.model.WidgetReq;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.http.JwtHttpHandler;
import app.socketiot.server.core.http.annotations.POST;
import app.socketiot.server.core.http.annotations.Path;
import app.socketiot.server.core.http.handlers.HttpReq;
import app.socketiot.server.core.http.handlers.HttpRes;
import app.socketiot.server.core.http.handlers.StatusMsg;
import io.netty.channel.ChannelHandler;

@Path("/api/widget")
@ChannelHandler.Sharable
public class WidgetApiHandler extends JwtHttpHandler {
    ObjectMapper mapper = new ObjectMapper();

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
