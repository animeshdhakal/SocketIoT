package app.socketiot.server.api;

import app.socketiot.server.api.model.BluePrintList;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.db.model.BluePrint;
import app.socketiot.server.core.http.JwtHttpHandler;
import app.socketiot.server.core.http.annotations.POST;
import app.socketiot.server.core.http.annotations.Path;
import app.socketiot.server.core.http.handlers.HttpReq;
import app.socketiot.server.core.http.handlers.HttpRes;
import app.socketiot.server.core.http.handlers.StatusMsg;
import app.socketiot.server.utils.RandomUtil;


@Path("/api/blueprint")
public class BluePrintApiHandler extends JwtHttpHandler {
    public BluePrintApiHandler(Holder holder){
        super(holder);
    }

    @Path("/create")
    @POST
    public HttpRes add(HttpReq req){
        BluePrint blueprint = req.getContentAs(BluePrint.class);

        if(blueprint == null || blueprint.name == null){
            return StatusMsg.badRequest("Incomplete Fields");
        }

        if(holder.bluePrintDao.getBluePrintByName(blueprint.name) != null){
            return StatusMsg.badRequest("Name should be unique");
        }    

        blueprint.id = RandomUtil.unique(8);
        blueprint.email = req.getUser().email;
        blueprint.json = "{\"widgets\":[]}";

        holder.bluePrintDao.addBluePrint(blueprint);

        return StatusMsg.ok("BluePrint Added Successfully");
    }

    @Path("/delete")
    @POST
    public HttpRes delete(HttpReq req){
        BluePrint blueprint = req.getContentAs(BluePrint.class);

        if(blueprint == null || blueprint.id == null){
            return StatusMsg.badRequest("Incomplete Fields");
        }

        BluePrint dbBluePrint = holder.bluePrintDao.getBluePrint(blueprint.id);

        if(dbBluePrint == null){
            return StatusMsg.badRequest("BluePrint Not Found");
        }

        holder.db.removeBluePrint(blueprint.id);

        return StatusMsg.ok("BluePrint Deleted Successfully");
    }


    @Path("/all")
    @POST
    public HttpRes all(HttpReq req){
        BluePrintList bluePrintList = new BluePrintList(holder.bluePrintDao.getAllBluePrintsByEmail(req.getUser().email));
        return new HttpRes(bluePrintList);
    }

}
