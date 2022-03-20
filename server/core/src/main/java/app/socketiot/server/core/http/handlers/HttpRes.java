package app.socketiot.server.core.http.handlers;

import app.socketiot.server.core.json.JsonParser;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class HttpRes extends DefaultFullHttpResponse {
    public HttpRes(HttpResponseStatus status, HttpVersion version, String content) {
        super(version, status, Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));
        addRequiredHeaders();
    }

    public HttpRes(HttpResponseStatus status, Object obj) {
        this(status, HttpVersion.HTTP_1_1, JsonParser.toLimitedJson(obj));
        headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
    }

    public HttpRes(Object obj) {
        this(HttpResponseStatus.OK, obj);
    }

    public HttpRes(String content) {
        this(HttpResponseStatus.OK, HttpVersion.HTTP_1_1, content);
    }

    public HttpRes(HttpResponseStatus status, String content) {
        this(status, HttpVersion.HTTP_1_1, content);
    }

    public HttpRes(HttpResponseStatus status) {
        super(HttpVersion.HTTP_1_1, status);
    }

    public HttpRes(byte[] content) {
        super(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(content));
        addRequiredHeaders();
    }

    public static HttpRes ok(String content) {
        return new HttpRes(HttpResponseStatus.OK, content);
    }

    public static HttpRes badRequest(String content) {
        return new HttpRes(HttpResponseStatus.BAD_REQUEST, content);
    }

    public static HttpRes unauthorized(String content) {
        return new HttpRes(HttpResponseStatus.UNAUTHORIZED, content);
    }

    public static HttpRes forbidden(String content) {
        return new HttpRes(HttpResponseStatus.FORBIDDEN, content);
    }

    public static HttpRes notFound(String content) {
        return new HttpRes(HttpResponseStatus.NOT_FOUND, content);
    }

    public static HttpRes internalServerError(String content) {
        return new HttpRes(HttpResponseStatus.INTERNAL_SERVER_ERROR, content);
    }

    public void addRequiredHeaders() {
        headers().set(HttpHeaderNames.CONTENT_LENGTH, content().readableBytes());
    }

    public void setHeader(String key, String value) {
        headers().set(key, value);
    }

    public void setCookie(String key, String value, int maxAge) {
        headers().set(HttpHeaderNames.SET_COOKIE, key + "=" + value + "; Max-Age=" + maxAge);
    }

    public void setCookie(String key, String value) {
        headers().set(HttpHeaderNames.SET_COOKIE, key + "=" + value);
    }

    public void deleteCookie(String key) {
        headers().set(HttpHeaderNames.SET_COOKIE, key + "=; Max-Age=0");
    }

}
