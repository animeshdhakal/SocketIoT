package app.socketiot.server.http.core;

import java.util.Map;

import app.socketiot.server.core.model.json.JsonParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

public class HttpReq {
    public ChannelHandlerContext ctx;
    public Map<String, String> pathParams;
    public FullHttpRequest request;
    public QueryStringDecoder queryStringDecoder;

    public HttpReq(ChannelHandlerContext ctx, FullHttpRequest request,
            QueryStringDecoder queryStringDecoder, Map<String, String> pathParams) {
        this.ctx = ctx;
        this.pathParams = pathParams;
        this.request = request;
        this.queryStringDecoder = queryStringDecoder;
    }

    public String getPathParam(String key) {
        return pathParams.get(key);
    }

    public String getQueryParam(String key) {
        return queryStringDecoder.parameters().get(key).get(0);
    }

    public String getContent() {
        return request.content().toString(CharsetUtil.UTF_8);
    }

    public <T> T getContentAs(Class<T> clazz) {
        return JsonParser.parseProtectedJson(getContent(), clazz);
    }
}