package app.socketiot.server.core.http;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Matcher;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.exceptions.ExceptionHandler;
import app.socketiot.server.core.http.annotations.POST;
import app.socketiot.server.core.http.annotations.Path;
import app.socketiot.server.core.http.handlers.HttpReq;
import app.socketiot.server.core.http.handlers.HttpRes;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;

public class BaseHttpHandler extends ChannelInboundHandlerAdapter {
    private Method[] methods = null;
    private String basePath = null;
    protected final Holder holder;

    public BaseHttpHandler(Holder holder) {
        super();
        this.holder = holder;
        methods = this.getClass().getDeclaredMethods();
        Path path = this.getClass().getAnnotation(Path.class);
        if (path != null && !path.value().equals("/")) {
            basePath = path.value();
        } else {
            basePath = "";
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            if (!process(ctx, (FullHttpRequest) msg)) {
                ctx.fireChannelRead(msg);
            }
        }
    }

    public static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpResponse response) {
        HttpUtil.setContentLength(response, response.content().readableBytes());
        ctx.writeAndFlush(response);
    }

    public void completeHttp(Method method, HttpReq req) throws Exception {
        HttpRes res = (HttpRes) method.invoke(this, req);
        sendHttpResponse(req.getCtx(), res);
    }

    public boolean process(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        QueryStringDecoder querydecoder = new QueryStringDecoder(req.uri());

        for (Method method : methods) {
            Path path = method.getAnnotation(Path.class);
            if (path != null) {
                HttpMethod httpMethod = null;
                if (method.isAnnotationPresent(POST.class)) {
                    httpMethod = HttpMethod.POST;
                } else {
                    httpMethod = HttpMethod.GET;
                }

                String actualPath = basePath + path.value();

                UriTemplate uritemplate = new UriTemplate(actualPath);

                Matcher matcher = uritemplate.matcher(querydecoder.path());
                if (matcher.matches() && httpMethod.equals(req.method())) {
                    Map<String, String> pathParam = uritemplate.extractParameters(matcher);
                    completeHttp(method,
                            new HttpReq(ctx, req, querydecoder, pathParam));
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ExceptionHandler.handleException(ctx, cause);
    }

}