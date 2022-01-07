package animesh.app.server;

import java.security.SecureRandom;

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
import animesh.app.server.utils.SHA256Util;

@StaticFolder("/static")
public class HttpServerHandler extends BaseHttpHandler {

    @GET
    @Path("/")
    public HttpRes index(HttpReq req) {
        return new StaticFile("/html/index.html");
    }

    @GET
    @Path("/api/set")
    public HttpRes set(HttpReq req) {
        String token = req.getQueryParam("token");

        if (token != null && ClientHandler.checkAuth(token)) {

            for (String key : req.getAllQueryParam().keySet()) {
                try {
                    String param = req.getQueryParam(key);
                    Integer.parseInt(param);
                    ClientHandler.broadCastMessage(req.getCtx(), ClientHandler.createMessage(MsgType.WRITE, key, param),
                            token);

                } catch (Exception e) {
                }
            }
            return new HttpRes("Valid Token");
        }
        return new HttpRes("Invalid Token");
    }

    @GET
    @Path("/api/get")
    public HttpRes get(HttpReq req) {
        String token = req.getQueryParam("token");
        String pin = req.getQueryParam("pin");

        if (token != null && pin != null) {
            String val = ClientHandler.getPinVal(token, pin);
            if (val != null) {
                return new HttpRes(val);
            }

        }
        return new HttpRes("Invalid Token");
    }

    @POST
    @Path("/api/user/register")
    public HttpRes register(HttpReq req) {
        User user = req.getContentAs(User.class);
        if (user == null || user.email == null || user.password == null) {
            return StatusMsg.badRequest("Incomplete Fields");
        }
        user.hashPass();

        if (user.exists()) {
            return StatusMsg.badRequest("User already exists");
        }

        if (!user.save()) {
            return StatusMsg.badRequest("Server Error");
        }

        return StatusMsg.ok("User created");
    }

    @POST
    @Path("/api/user/login")
    public HttpRes login(HttpReq req) {
        User user = req.getContentAs(User.class);
        if (user == null || user.email == null || user.password == null) {
            return StatusMsg.badRequest("Incomplete Fields");
        }

        user.hashPass();

        User dbUser = new User();

        if (!dbUser.get(user.email) || !dbUser.password.equals(user.password)) {
            return StatusMsg.badRequest("Invalid Credentials");
        }

        HttpRes res = StatusMsg.ok("User logged in");

        if (dbUser.token.length() < 1) {
            SecureRandom random = new SecureRandom();
            byte[] bytes = new byte[20];
            random.nextBytes(bytes);
            String token = SHA256Util.createHash(new String(bytes), user.email);
            dbUser.token = token;
            if (!dbUser.update()) {
                return StatusMsg.badRequest("Server Error");
            }
        }

        res.setCookie("token", dbUser.token);

        return res;
    }

    @POST
    @Path("/api/blueprint/create")
    public HttpRes createBlueprint(HttpReq req) {
        return StatusMsg.badRequest("Invalid Token");
    }

    @NotFound
    public HttpRes notFound(HttpReq req) {
        return StatusMsg.badRequest("Not Found");
    }

}