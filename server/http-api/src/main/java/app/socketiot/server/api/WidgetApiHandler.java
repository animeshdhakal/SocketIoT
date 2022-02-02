package app.socketiot.server.api;

import app.socketiot.server.core.Holder;
import app.socketiot.server.core.http.JwtHttpHandler;
import app.socketiot.server.core.http.annotations.POST;
import app.socketiot.server.core.http.annotations.Path;
import app.socketiot.server.core.http.handlers.HttpReq;
import app.socketiot.server.core.http.handlers.HttpRes;
import app.socketiot.server.core.http.handlers.StatusMsg;
import app.socketiot.server.core.json.model.Widget;
import io.netty.channel.ChannelHandler;

@Path("/api/widget")
@ChannelHandler.Sharable
public class WidgetApiHandler extends JwtHttpHandler {
    public WidgetApiHandler(Holder holder) {
        super(holder);
    }

    @Path("/add")
    @POST
    public HttpRes add(HttpReq req) {
        Widget widget = req.getContentAs(Widget.class);

        if (widget == null || widget.blueprint_id == null || widget.type == null  || widget.width == -1 || widget.height == -1 || widget.pin == -1) {
            return StatusMsg.badRequest("Incomplete Fields");
        }

        
        if(!holder.bluePrintDao.addWidget(req.getUser().email, widget.blueprint_id, widget)){
            return StatusMsg.badRequest("BluePrint Not Found");
        }

        return StatusMsg.ok("Widget Added Successfully");
    }

    @Path("/remove")
    @POST
    public HttpRes delete(HttpReq req) {
        Widget widget = req.getContentAs(Widget.class);

        if (widget == null || widget.id == -1 || widget.blueprint_id == null) {
            return StatusMsg.badRequest("Incomplete Fields");
        }

        if(!holder.bluePrintDao.removeWidget(req.getUser().email, widget.blueprint_id, widget.id)){
            return StatusMsg.badRequest("Widget Not Found");
        }

        return StatusMsg.ok("Widget Deleted Successfully");
    }

}
