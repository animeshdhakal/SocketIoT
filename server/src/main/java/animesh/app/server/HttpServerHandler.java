package animesh.app.server;

import animesh.app.server.db.dao.UserDao;
import animesh.app.server.db.model.User;
import animesh.app.server.http.BaseHttpHandler;
import animesh.app.server.http.annotations.GET;
import animesh.app.server.http.annotations.NotFound;
import animesh.app.server.http.annotations.POST;
import animesh.app.server.http.annotations.Path;
import animesh.app.server.http.annotations.StaticFolder;
import animesh.app.server.http.handlers.HttpReq;
import animesh.app.server.http.handlers.HttpRes;
import animesh.app.server.http.handlers.StaticFile;
import animesh.app.server.http.handlers.StatusMsg;

@StaticFolder("/static")
public class HttpServerHandler extends BaseHttpHandler {

    @GET
    @Path("/")
    public HttpRes index(HttpReq req) {
        return new StaticFile("/html/index.html");
    }

    @POST
    @Path("/api/register")
    public HttpRes register(HttpReq req) {
        User user = req.getContentAs(User.class);
        if (user == null || user.email == null || user.password == null) {
            return StatusMsg.badRequest("Incomplete Fields");
        }
        user.hashPass();

        UserDao userDao = new UserDao();

        if (userDao.getUser(user.email) != null) {
            return StatusMsg.badRequest("User already exists");
        }

        if (!userDao.createUser(user)) {
            return StatusMsg.badRequest("Server Error");
        }

        return StatusMsg.ok("User created");
    }

    @POST
    @Path("/api/login")
    public HttpRes login(HttpReq req) {
        User user = req.getContentAs(User.class);
        if (user == null || user.email == null || user.password == null) {
            return StatusMsg.badRequest("Incomplete Fields");
        }

        UserDao userDao = new UserDao();

        User dbUser = userDao.getUser(user.email);

        user.hashPass();

        if (dbUser == null || !dbUser.password.equals(user.password)) {
            return StatusMsg.badRequest("Invalid Credentials");
        }

        return StatusMsg.ok("User logged in");
    }

    @NotFound
    public HttpRes notFound(HttpReq req) {
        return StatusMsg.badRequest("Not Found");
    }

}