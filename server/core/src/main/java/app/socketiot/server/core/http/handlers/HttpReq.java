package app.socketiot.server.core.http.handlers;

import java.util.List;
import java.util.Map;

import app.socketiot.server.core.json.JsonParser;
import app.socketiot.server.core.model.auth.User;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

public class HttpReq {
    private ChannelHandlerContext ctx;
    private FullHttpRequest req;
    private QueryStringDecoder querydecoder;
    private Map<String, String> pathParam;
    public User user;

    public HttpReq(ChannelHandlerContext ctx, FullHttpRequest req, QueryStringDecoder querydecoder,
            Map<String, String> pathParam) {
        this.ctx = ctx;
        this.req = req;
        this.querydecoder = querydecoder;
        this.pathParam = pathParam;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public HttpReq(ChannelHandlerContext ctx, FullHttpRequest req, QueryStringDecoder querydecoder) {
        this(ctx, req, querydecoder, null);
    }

    public HttpVersion getProtocolVersion() {
        return req.protocolVersion();
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public FullHttpRequest getFullHttpRequest() {
        return req;
    }

    public <T> T getContentAs(Class<T> clazz) {
        String content = req.content().toString(CharsetUtil.US_ASCII);
        return JsonParser.parseLimitedJson(clazz, content);
    }

    public String getContent() {
        return req.content().toString(CharsetUtil.US_ASCII);
    }

    public Map<String, List<String>> getAllQueryParam() {
        return querydecoder.parameters();
    }

    public String getQueryParam(String key) {
        List<String> list = querydecoder.parameters().get(key);
        if (list != null) {
            return list.get(0);
        }
        return null;
    }

    public String getCookie(String key) {
        for (String cookie : req.headers().getAll(HttpHeaderNames.COOKIE)) {
            String[] cookie_parts = cookie.split(";");
            for (String cookie_part : cookie_parts) {
                String[] cookie_part_parts = cookie_part.split("=");
                if (cookie_part_parts.length == 2) {
                    if (cookie_part_parts[0].trim().equals(key)) {
                        return cookie_part_parts[1].trim();
                    }
                }
            }
        }
        return null;
    }

    public String getPathParam(String key) {
        if (pathParam != null) {
            return pathParam.get(key);
        }
        return null;
    }

    public String getHeader(String key) {
        return req.headers().get(key);
    }
}
