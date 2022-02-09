package app.socketiot.server.core.http;

import java.lang.reflect.Method;

import app.socketiot.server.core.Holder;
import app.socketiot.server.core.db.model.User;
import app.socketiot.server.core.http.handlers.HttpReq;
import app.socketiot.server.core.http.handlers.HttpRes;

public class JwtHttpHandler extends BaseHttpHandler {

    public JwtHttpHandler(Holder holder) {
        super(holder);
    }

    @Override
    public void completeHttp(Method method, HttpReq req) throws Exception {
        String authtoken = req.getHeader("Authorization");
        if (authtoken != null && authtoken.startsWith("Bearer ")) {
            authtoken = authtoken.substring(7);
            if (holder.jwtUtil.verifyToken(authtoken)) {
                String email = holder.jwtUtil.getEmail(authtoken);
                User user = holder.userDao.getUser(email);
                if (user != null) {
                    req.setUser(user);
                    super.completeHttp(method, req);
                    return;
                }
            }
        }
        sendHttpResponse(req.getCtx(), HttpRes.unauthorized("Unauthorized"));
    }
}
