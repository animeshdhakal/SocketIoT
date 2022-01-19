package app.socketiot.server.core.http.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class HttpRes {
    static ObjectMapper mapper = new ObjectMapper();
    protected ByteBuf buff = null;
    protected HttpResponseStatus status = HttpResponseStatus.OK;
    protected HttpHeaders headers = new DefaultHttpHeaders();

    public HttpRes(String text, HttpResponseStatus status) {
        this();
        this.status = status;
        if (buff != null) {
            buff.release();
        } else {
            buff = Unpooled.copiedBuffer(text, CharsetUtil.US_ASCII);
        }
    }

    public HttpRes() {
    }

    public HttpRes(Object obj) {
        try {
            buff = Unpooled.copiedBuffer(mapper.writeValueAsString(obj), CharsetUtil.US_ASCII);
            this.status = HttpResponseStatus.OK;
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public FullHttpResponse getFullHttpResponse(HttpVersion version) {
        if (buff == null || buff.readableBytes() == 0) {
            return new DefaultFullHttpResponse(version, status);
        }

        FullHttpResponse resp = new DefaultFullHttpResponse(version, status, buff);

        resp.headers().set(headers);

        return resp;

    }

    public void setHeader(String key, String value) {
        headers.set(key, value);
    }

    public void setCookie(String key, String value, int maxAge) {
        headers.set(HttpHeaderNames.SET_COOKIE, key + "=" + value + "; Max-Age=" + maxAge);
    }

    public void setCookie(String key, String value) {
        headers.set(HttpHeaderNames.SET_COOKIE, key + "=" + value);
    }

    public void deleteCookie(String key) {
        headers.set(HttpHeaderNames.SET_COOKIE, key + "=; Max-Age=0");
    }

}
