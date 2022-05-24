package app.socketiot.server.api;

import app.socketiot.server.api.model.GoogleAssistant.GoogleAssistantTokenRes;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.http.BaseHttpHandler;
import app.socketiot.server.core.http.annotations.POST;
import app.socketiot.server.core.http.annotations.Path;
import app.socketiot.server.core.http.handlers.HttpReq;
import app.socketiot.server.core.http.handlers.HttpRes;
import app.socketiot.server.core.model.auth.User;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
@Path("/api/google-assistant")
public class GoogleAssistantHandler extends BaseHttpHandler {
    private final Holder holder;

    public GoogleAssistantHandler(Holder holder) {
        super(holder);
        this.holder = holder;
    }

    public String getFormData(String body, String key) {
        try {
            String[] pairs = body.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue[0].equals(key)) {
                    return keyValue[1];
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Path("/token")
    @POST
    public HttpRes token(HttpReq req) {
        String body = req.getContent();
        String grant_type = getFormData(body, "grant_type");
        String refresh_token = getFormData(body, "refresh_token");
        String code = getFormData(body, "code");

        if (grant_type == null) {
            return HttpRes.badRequest("Invalid request");
        }

        String token;

        if (grant_type.equals("authorization_code")) {
            token = code;
        } else if (grant_type.equals("refresh_token")) {
            token = refresh_token;
        } else {
            return HttpRes.badRequest("{\"error\": \"invalid_grant\"}");
        }

        if (!holder.jwtUtil.verifyToken(token)) {
            return HttpRes.badRequest("{\"error\": \"invalid_grant\"}");
        }

        String email = holder.jwtUtil.getEmail(token);

        User user = holder.userDao.getUser(email);

        if (user == null) {
            return HttpRes.badRequest("Invalid user");
        }

        if (user.token == null) {
            user.token = holder.jwtUtil.createToken(email, UserApiHandler.refresh_token_expiry_time);
            user.updated();
        }

        String access_token = holder.jwtUtil.createToken(email, UserApiHandler.access_token_expiry_time);

        return HttpRes.json(
                new GoogleAssistantTokenRes("Bearer", access_token, user.token,
                        UserApiHandler.access_token_expiry_time));
    }

}
