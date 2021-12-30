package animesh.app.server.http.handlers;

import io.netty.buffer.Unpooled;
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
            this.buff = Unpooled.copiedBuffer(mapper.writeValueAsString(msg), CharsetUtil.US_ASCII);
            this.status = responseStatus;
        } catch (Exception e) {
        }
    }
}
