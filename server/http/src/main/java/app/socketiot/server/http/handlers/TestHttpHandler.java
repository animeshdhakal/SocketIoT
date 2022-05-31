package app.socketiot.server.http.handlers;

import app.socketiot.server.http.core.BaseHttpHandler;
import app.socketiot.server.http.core.HttpReq;
import app.socketiot.server.http.core.HttpRes;
import app.socketiot.server.http.core.annotations.GET;
import app.socketiot.server.http.core.annotations.Path;

public class TestHttpHandler extends BaseHttpHandler {
    @Path("/")
    @GET
    public HttpRes home(HttpReq req) {
        return new HttpRes("Hello World!");
    }
}
