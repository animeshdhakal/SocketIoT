package animesh.app.server.http.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

public class HttpRes {
    static ObjectMapper mapper = new ObjectMapper();
    ByteBuf buff = null;
    HttpResponseStatus status = HttpResponseStatus.OK;

    public HttpRes(String text, HttpResponseStatus status) {
        this.status = status;
        if (buff != null) {
            buff.release();
        } else {
            buff = Unpooled.copiedBuffer(text, CharsetUtil.US_ASCII);
        }
    }

    public HttpRes() {
    }

    public HttpRes(String text) {
        this(text, HttpResponseStatus.OK);
    }

    public HttpResponseStatus getHttpStatus() {
        return status;
    }

    public ByteBuf getContent() {
        return buff;
    }

}
