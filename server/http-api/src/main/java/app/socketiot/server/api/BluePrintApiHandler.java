package app.socketiot.server.api;

import app.socketiot.server.core.Holder;
import app.socketiot.server.core.http.JwtHttpHandler;
import app.socketiot.server.core.http.annotations.Path;


@Path("/api/blueprint")
public class BluePrintApiHandler extends JwtHttpHandler {
    public BluePrintApiHandler(Holder holder){
        super(holder);
    }
}
