package animesh.app.server.http.handlers;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

public class HttpReq {
    private static ObjectMapper mapper = new ObjectMapper();
    private ChannelHandlerContext ctx;
    private FullHttpRequest req;
    private QueryStringDecoder querydecoder;

    public HttpReq(ChannelHandlerContext ctx, FullHttpRequest req, QueryStringDecoder querydecoder) {
        this.ctx = ctx;
        this.req = req;
        this.querydecoder = querydecoder;
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
        try {
            return mapper.readValue(req.content().toString(CharsetUtil.US_ASCII), clazz);
        } catch (Exception e) {
            return null;
        }
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
}
