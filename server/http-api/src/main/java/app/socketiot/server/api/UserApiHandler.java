package app.socketiot.server.api;

import app.socketiot.server.core.http.BaseHttpHandler;
import java.util.concurrent.TimeUnit;
import app.socketiot.server.api.model.JwtResponse;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.PlaceHolders;
import app.socketiot.server.core.dao.TokenDao;
import app.socketiot.server.core.dao.UserDao;
import app.socketiot.server.core.http.annotations.GET;
import app.socketiot.server.core.http.annotations.POST;
import app.socketiot.server.core.http.annotations.Path;
import app.socketiot.server.core.http.handlers.HttpReq;
import app.socketiot.server.core.http.handlers.HttpRes;
import app.socketiot.server.core.http.handlers.StatusMsg;
import app.socketiot.server.core.mail.Mail;
import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.auth.Dashboard;
import app.socketiot.server.core.model.token.ResetToken;
import app.socketiot.server.core.model.token.VerifyUserToken;
import app.socketiot.server.utils.FileReadUtil;
import app.socketiot.server.utils.RandomUtil;
import app.socketiot.server.utils.Sha256Util;
import app.socketiot.server.utils.ValidatorUtil;
import io.netty.channel.ChannelHandler;

@Path("/api/user")
@ChannelHandler.Sharable
public class UserApiHandler extends BaseHttpHandler {
    private final UserDao userDao;
    private final TokenDao tokenDao;
    private final Mail mail;
    private final String verifyUserMailBody;
    private final String resetPasswordMailBody;
    private final String baseUrl;

    public final static long access_token_expiry_time = 1000 * 60; // 1 minute
    public final static long refresh_token_expiry_time = 1000 * 60 * 60 * 24 * 30 * 12; // 1 Year

    public UserApiHandler(Holder holder) {
        super(holder);
        this.userDao = holder.userDao;
        this.tokenDao = holder.tokenDao;
        this.mail = holder.mail;
        this.verifyUserMailBody = FileReadUtil.readVerifyUserMailBody();
        this.resetPasswordMailBody = FileReadUtil.readResetPasswordMailBody();

        String host = holder.props.getProperty("server.host");
        if (host == null) {
            host = "localhost";
        }
        String protocol = holder.args.hasArg("-ssl") ? "https" : "http";
        this.baseUrl = protocol + "://" + host;
    }

    @POST
    @Path("/register")
    public HttpRes register(HttpReq req) {
        User user = req.getContentAs(User.class);

        if (user == null || user.email == null || user.password == null) {
            return StatusMsg.badRequest("Incomplete Fields");
        }

        if (!ValidatorUtil.validateEmail(user.email)) {
            return StatusMsg.badRequest("Invalid Email");
        }

        if (userDao.getUser(user.email) != null || tokenDao.ifVerifyTokenExists(user.email)) {
            return StatusMsg.badRequest("User already exists");
        }

        user.password = Sha256Util.createHash(user.password, user.email);

        VerifyUserToken tk = new VerifyUserToken(user.email, user.password, TimeUnit.DAYS.toMillis(30));
        String token = RandomUtil.unique() + RandomUtil.unique();
        tokenDao.addToken(token, tk);

        mail.sendHtml(user.email, "Verify Email",
                verifyUserMailBody.replace(PlaceHolders.URL, baseUrl + "/api/user/verify?token=" + token));

        return StatusMsg.ok("Email has been sent to verify you account");
    }

    @GET
    @Path("/verify")
    public HttpRes verify(HttpReq req) {
        String token = req.getQueryParam("token");

        if (token == null) {
            return HttpRes.badRequest("Incomplete Fields");
        }
        VerifyUserToken tk = tokenDao.getVerifyUserToken(token);

        if (tk == null) {
            return HttpRes.badRequest("Invalid Token");
        }

        if (tk.isExpired(System.currentTimeMillis())) {
            tokenDao.removeToken(token);
            return HttpRes.badRequest("Please Reregister your account");
        }

        userDao.addUser(new User(tk.email, tk.password, null, new Dashboard()));

        tokenDao.removeToken(token);

        return HttpRes.redirect("/login");
    }

    @POST
    @Path("/login")
    public HttpRes login(HttpReq req) {
        User user = req.getContentAs(User.class);

        if (user == null || user.email == null || user.password == null) {
            return StatusMsg.badRequest("Incomplete Fields");
        }

        User dbUser = userDao.getUser(user.email);

        if (tokenDao.ifVerifyTokenExists(user.email)) {
            return StatusMsg.badRequest("Please Verify Your Account");
        }

        if (dbUser == null) {
            return StatusMsg.badRequest("User does not exist");
        }

        if (!dbUser.password.equals(Sha256Util.createHash(user.password, user.email))) {
            return StatusMsg.badRequest("Incorrect Password");
        }

        try {
            if (dbUser.token == null || holder.jwtUtil.isTokenExpired(dbUser.token)) {
                dbUser.token = holder.jwtUtil.createToken(dbUser.email, refresh_token_expiry_time);
                dbUser.updated();
            }
        } catch (IllegalArgumentException e) {
            return StatusMsg.badRequest("Invalid Token");
        }

        String access_token = holder.jwtUtil.createToken(dbUser.email, access_token_expiry_time);

        return HttpRes.json(new JwtResponse(access_token, dbUser.token, access_token_expiry_time));
    }

    public HttpRes handleResetFirstStep(String email) {
        if (userDao.getUser(email) == null) {
            return StatusMsg.badRequest("User does not exist");
        }
        if (tokenDao.ifVerifyTokenExists(email)) {
            return StatusMsg.badRequest("Please Verify Your Account");
        }

        if (tokenDao.ifResetTokenExists(email)) {
            tokenDao.cleanTokens();
            return StatusMsg.badRequest("Please wait before requesting another reset");
        }

        String token = RandomUtil.unique() + RandomUtil.unique();
        tokenDao.addToken(token, new ResetToken(email, TimeUnit.HOURS.toMillis(1)));

        mail.sendHtml(email, "Reset Password",
                resetPasswordMailBody.replace(PlaceHolders.URL, baseUrl + "/reset?token=" + token));

        return StatusMsg.ok("Email has been sent to reset your password");
    }

    public HttpRes handleResetSecondStep(String token, String password) {
        ResetToken tk = tokenDao.getResetToken(token);

        if (tk == null) {
            return StatusMsg.badRequest("Invalid Token");
        }

        if (tk.isExpired(System.currentTimeMillis())) {
            tokenDao.removeToken(token);
            return StatusMsg.badRequest("Token Expired");
        }

        User user = userDao.getUser(tk.email);
        if (user == null) {
            return StatusMsg.badRequest("User does not exist");
        }

        user.password = Sha256Util.createHash(password, user.email);

        user.token = holder.jwtUtil.createToken(user.email, refresh_token_expiry_time);

        userDao.updateUser(user);
        tokenDao.removeToken(token);

        return StatusMsg.ok("Password has been reset");
    }

    @POST
    @Path("/reset")
    public HttpRes resetPassword(HttpReq req) {
        String email = req.getJsonFieldAsString("email");
        String password = req.getJsonFieldAsString("password");
        String token = req.getJsonFieldAsString("token");

        if (email != null) {
            return handleResetFirstStep(email);
        }

        if (token != null && password != null) {
            return handleResetSecondStep(token, password);
        }

        return StatusMsg.badRequest("Incomplete Fields");
    }

    @POST
    @Path("/refresh")
    public HttpRes refreshToken(HttpReq req) {
        String refresh_token = req.getJsonFieldAsString("refresh_token");

        if (refresh_token == null) {
            return StatusMsg.badRequest("Incomplete Fields");
        }

        if (!holder.jwtUtil.verifyToken(refresh_token)) {
            return StatusMsg.badRequest("Invalid Token");
        }

        String email = holder.jwtUtil.getEmail(refresh_token);
        User user = userDao.getUser(email);

        if (user == null || !user.token.equals(refresh_token)) {
            return StatusMsg.badRequest("Invalid Token");
        }

        return HttpRes.json(new JwtResponse(holder.jwtUtil.createToken(user.email, access_token_expiry_time),
                user.token, access_token_expiry_time));
    }

}
