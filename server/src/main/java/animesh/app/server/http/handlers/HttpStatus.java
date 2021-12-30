package animesh.app.server.http.handlers;

import io.netty.handler.codec.http.HttpResponseStatus;

public class HttpStatus extends HttpResponseStatus {
    public HttpStatus(int status, String reason) {
        super(status, reason);
    }
}
