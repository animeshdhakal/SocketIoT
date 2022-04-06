package app.socketiot.server.api;

import app.socketiot.server.api.model.GoogleAssistantTokenRes;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.http.BaseHttpHandler;
import app.socketiot.server.core.http.annotations.POST;
import app.socketiot.server.core.http.annotations.Path;
import app.socketiot.server.core.http.handlers.HttpReq;
import app.socketiot.server.core.http.handlers.HttpRes;
import app.socketiot.server.core.model.auth.User;
import io.netty.channel.ChannelHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ChannelHandler.Sharable
@Path("/api/google-assistant")
public class GoogleAssistantHandler extends BaseHttpHandler {
    final Holder holder;
    private static Logger log = LogManager.getLogger(GoogleAssistantHandler.class);

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
            return HttpRes.badRequest("Invalid grant_type");
        }

        if (!holder.jwtUtil.verifyToken(token)) {
            return HttpRes.badRequest("Invalid code");
        }

        String email = holder.jwtUtil.getEmail(token);

        User user = holder.userDao.getUser(email);

        if (user == null) {
            return HttpRes.badRequest("Invalid user");
        }

        return new HttpRes(new GoogleAssistantTokenRes("Bearer", token, token, 1 * 12 * 30 * 24 * 60 * 60));
    }

}
