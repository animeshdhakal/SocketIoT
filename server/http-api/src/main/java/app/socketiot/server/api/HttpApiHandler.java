package app.socketiot.server.api;

import app.socketiot.server.core.Holder;
import app.socketiot.server.core.http.BaseHttpHandler;
import app.socketiot.server.core.http.annotations.GET;
import app.socketiot.server.core.http.annotations.Path;
import app.socketiot.server.core.http.handlers.HttpReq;
import app.socketiot.server.core.http.handlers.HttpRes;
import io.netty.channel.ChannelHandler;


@Path("/")
@ChannelHandler.Sharable
public class HttpApiHandler extends BaseHttpHandler {
    public HttpApiHandler(Holder holder){
        super(holder);
    }


    @Path("/")
    @GET
    public HttpRes home(HttpReq req){
        return new HttpRes("Home Page");
    }
}
