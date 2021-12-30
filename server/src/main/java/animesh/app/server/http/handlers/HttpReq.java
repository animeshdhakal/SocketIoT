package animesh.app.server.http.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

public class HttpReq {
    public static ObjectMapper mapper = new ObjectMapper();
    public ChannelHandlerContext ctx;
    public HttpRequest req;
    public QueryStringDecoder querydecoder;

    public HttpReq(ChannelHandlerContext ctx, HttpRequest req, QueryStringDecoder querydecoder) {
        this.ctx = ctx;
        this.req = req;
        this.querydecoder = querydecoder;
    }

    public <T> T getContentAs(Class<T> clazz) {
        try {
            return mapper.readValue(((HttpContent) req).content().toString(CharsetUtil.US_ASCII), clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public String getQueryParam(String key) {
        return querydecoder.parameters().get(key).get(0);
    }

}
