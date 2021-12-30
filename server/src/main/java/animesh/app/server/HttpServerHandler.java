package animesh.app.server;

import animesh.app.server.http.BaseHttpHandler;
import animesh.app.server.http.annotations.GET;
import animesh.app.server.http.annotations.Path;
import animesh.app.server.http.annotations.StaticFolder;
import animesh.app.server.http.handlers.HttpReq;
import animesh.app.server.http.handlers.HttpRes;
import animesh.app.server.http.handlers.HttpStatus;
import animesh.app.server.http.handlers.StaticFile;

@StaticFolder("/static")
public class HttpServerHandler extends BaseHttpHandler {
    @GET
    @Path("/")
    public HttpRes home(HttpReq req) {
        return new StaticFile("/html/index.html", HttpStatus.OK);
    }
}