package app.socketiot.server.api;

import app.socketiot.server.core.Holder;
import app.socketiot.server.core.dao.UserDao;
import app.socketiot.server.core.db.model.User;
import app.socketiot.server.core.http.BaseHttpHandler;
import app.socketiot.server.core.http.JwtHttpHandler;
import app.socketiot.server.core.http.annotations.GET;
import app.socketiot.server.core.http.annotations.Path;
import app.socketiot.server.core.http.annotations.StaticFolder;
import app.socketiot.server.core.http.handlers.HttpReq;
import app.socketiot.server.core.http.handlers.HttpRes;
import app.socketiot.server.utils.JwtUtil;

@Path("/")
@StaticFolder("/static")
public class HttpApi extends BaseHttpHandler {
    private UserDao userDao;

    public HttpApi(Holder holder) {
        this.userDao = holder.userDao;
    }

    @GET()
    @Path("/")
    public HttpRes index(HttpReq req) {
        return new HttpRes("Hello World");
    }
}
