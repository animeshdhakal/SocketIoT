package app.socketiot.server.api;

import app.socketiot.server.core.Holder;
import app.socketiot.server.core.db.model.BluePrint;
import app.socketiot.server.core.http.BaseHttpHandler;
import app.socketiot.server.core.http.annotations.POST;
import app.socketiot.server.core.http.annotations.Path;
import app.socketiot.server.core.http.handlers.HttpReq;
import app.socketiot.server.core.http.handlers.HttpRes;
import app.socketiot.server.core.http.handlers.StatusMsg;
import app.socketiot.server.core.json.JsonParser;
import app.socketiot.server.core.json.model.BluePrintJson;


@Path("/api/blueprint")
public class OpenBluePrintApi extends BaseHttpHandler {
    public OpenBluePrintApi(Holder holder) {
        super(holder);
    }

    @Path("/get")
    @POST
    public HttpRes get(HttpReq req) {
        BluePrint bluePrint = req.getContentAs(BluePrint.class);

        if (bluePrint == null || bluePrint.id == null) {
            return StatusMsg.badRequest("Incomplete Fields");
        }

        bluePrint = holder.bluePrintDao.getBluePrint(bluePrint.id);

        if (bluePrint == null) {
            return StatusMsg.badRequest("BluePrint Not Found");
        }

        BluePrintJson bluePrintJson = JsonParser.parse(BluePrintJson.class, bluePrint.json);

        if (bluePrintJson == null || bluePrintJson.widgets == null) {
            return StatusMsg.badRequest("Invalid Blueprint");
        }

        return new HttpRes(bluePrintJson);
    }

}
