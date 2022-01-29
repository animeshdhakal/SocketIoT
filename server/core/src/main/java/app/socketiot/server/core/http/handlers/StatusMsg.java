package app.socketiot.server.core.http.handlers;

import app.socketiot.server.core.json.JsonParser;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

class Status {
    public boolean error;
    public String message;

    public Status(boolean error, String message) {
        this.error = error;
        this.message = message;
    }
}

public class StatusMsg extends HttpRes {

    public StatusMsg(boolean error, String message, HttpResponseStatus responseStatus) {
        super();
        Status msg = new Status(error, message);
        try {
            this.buff = Unpooled.copiedBuffer(JsonParser.mapper.writeValueAsString(msg), CharsetUtil.US_ASCII);
            this.status = responseStatus;
            headers.set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        } catch (Exception e) {
        }
    }

    public static StatusMsg ok(String message) {
        return new StatusMsg(false, message, HttpResponseStatus.OK);
    }

    public static StatusMsg badRequest(String message) {
        return new StatusMsg(true, message, HttpResponseStatus.BAD_REQUEST);
    }
}
