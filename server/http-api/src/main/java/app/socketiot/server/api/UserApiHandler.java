package app.socketiot.server.api;

import app.socketiot.server.core.http.BaseHttpHandler;
import app.socketiot.server.api.model.JwtResponse;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.dao.UserDao;
import app.socketiot.server.core.http.annotations.POST;
import app.socketiot.server.core.http.annotations.Path;
import app.socketiot.server.core.http.handlers.HttpReq;
import app.socketiot.server.core.http.handlers.HttpRes;
import app.socketiot.server.core.http.handlers.StatusMsg;
import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.auth.UserJson;
import app.socketiot.server.utils.Sha256Util;
import io.netty.channel.ChannelHandler;

@Path("/api/user")
@ChannelHandler.Sharable
public class UserApiHandler extends BaseHttpHandler {
    private UserDao userDao;

    public UserApiHandler(Holder holder) {
        super(holder);
        this.userDao = holder.userDao;
    }

    @POST
    @Path("/register")
    public HttpRes register(HttpReq req) {
        User user = req.getContentAs(User.class);

        if (user == null || user.email == null || user.password == null) {
            return StatusMsg.badRequest("Incomplete Fields");
        }

        if (userDao.getUser(user.email) != null) {
            return StatusMsg.badRequest("User already exists");
        }

        user.password = Sha256Util.createHash(user.password, user.email);
        user.json = new UserJson();

        userDao.addUser(user);

        return StatusMsg.ok("User Registered Successfully");
    }

    @POST
    @Path("/login")
    public HttpRes login(HttpReq req) {
        User user = req.getContentAs(User.class);

        if (user == null || user.email == null || user.password == null) {
            return StatusMsg.badRequest("Incomplete Fields");
        }

        User dbUser = userDao.getUser(user.email);

        if (dbUser == null) {
            return StatusMsg.badRequest("User does not exist");
        }

        if (!dbUser.password.equals(Sha256Util.createHash(user.password, user.email))) {
            return StatusMsg.badRequest("Incorrect Password");
        }

        String token = holder.jwtUtil.createToken(dbUser.email, 1 * 12 * 30 * 24 * 60 * 60);

        return new HttpRes(new JwtResponse(token));
    }

    @POST
    @Path("/token")
    public HttpRes gettoken(HttpReq req) {
        return new HttpRes("token");
    }
}
