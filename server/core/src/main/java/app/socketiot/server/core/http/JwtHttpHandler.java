package app.socketiot.server.core.http;

import java.lang.reflect.Method;
import app.socketiot.server.core.http.handlers.HttpReq;
import app.socketiot.server.core.http.handlers.HttpRes;
import app.socketiot.server.utils.JwtUtil;
import io.netty.handler.codec.http.HttpResponseStatus;

public class JwtHttpHandler extends BaseHttpHandler {
    @Override
    public void completeHttp(Method method, HttpReq req) throws Exception {
        String authtoken = req.getHeader("Authorization");
        if (authtoken != null && authtoken.startsWith("Bearer ")) {
            authtoken = authtoken.substring(7);
            if (JwtUtil.verifyToken(authtoken)) {
                super.completeHttp(method, req);
                return;
            }
        }
        sendHttpResponse(req.getCtx(), new HttpRes("Unauthorized", HttpResponseStatus.UNAUTHORIZED)
                .getFullHttpResponse(req.getProtocolVersion()));
    }
}
