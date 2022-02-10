package app.socketiot.server.core.http.handlers;

import app.socketiot.server.core.json.model.Status;
import io.netty.handler.codec.http.HttpResponseStatus;

public class StatusMsg extends HttpRes {

    public StatusMsg(boolean error, String message, HttpResponseStatus responseStatus) {
        super(responseStatus, new Status(error, message));
    }

    public static StatusMsg ok(String message) {
        return new StatusMsg(false, message, HttpResponseStatus.OK);
    }

    public static StatusMsg badRequest(String message) {
        return new StatusMsg(true, message, HttpResponseStatus.BAD_REQUEST);
    }
}
