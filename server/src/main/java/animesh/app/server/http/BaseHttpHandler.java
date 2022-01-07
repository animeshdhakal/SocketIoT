package animesh.app.server.http;

import java.lang.reflect.Method;
import animesh.app.server.ExceptionHandler;
import animesh.app.server.http.annotations.NotFound;
import animesh.app.server.http.annotations.POST;
import animesh.app.server.http.annotations.Path;
import animesh.app.server.http.annotations.StaticFolder;
import animesh.app.server.http.handlers.HttpReq;
import animesh.app.server.http.handlers.HttpRes;
import animesh.app.server.http.handlers.HttpStatus;
import animesh.app.server.http.handlers.StaticFile;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;

public class BaseHttpHandler extends ChannelInboundHandlerAdapter {
    private static Method[] methods = null;
    private static Method notFoundMethod = null;
    private static String staticFolder = null;

    public BaseHttpHandler() {
        super();
        if (methods == null) {
            methods = this.getClass().getDeclaredMethods();
            if (this.getClass().isAnnotationPresent(StaticFolder.class)) {
                StaticFolder path = this.getClass().getAnnotation(StaticFolder.class);
                staticFolder = path.value();
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            process(ctx, (FullHttpRequest) msg);
        }
    }

    public void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse response) {
        HttpUtil.setContentLength(response, response.content().readableBytes());

        boolean keepAlive = HttpUtil.isKeepAlive(req);
        ChannelFuture f = ctx.writeAndFlush(response);
        if (keepAlive) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    public void sendHttpResponse(Method method, HttpReq req) throws Exception {
        HttpRes res = (HttpRes) method.invoke(this, req);

        sendHttpResponse(req.getCtx(), req.getFullHttpRequest(), res.getFullHttpResponse(req.getProtocolVersion()));
    }

    public void process(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
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

                if (path.value().equals(querydecoder.path()) && httpMethod.equals(req.method())) {
                    sendHttpResponse(method, new HttpReq(ctx, req, querydecoder));
                    return;
                }
            }

            if (method.isAnnotationPresent(NotFound.class)) {
                notFoundMethod = method;
            }
        }

        if (staticFolder != null) {
            if (querydecoder.path().contains(staticFolder)) {
                StaticFile file = new StaticFile(querydecoder.path(), HttpStatus.OK);

                if (file.getContent() != null) {
                    sendHttpResponse(ctx, req, file.getFullHttpResponse(req.protocolVersion()));
                }
            }
        }

        if (notFoundMethod != null) {
            sendHttpResponse(notFoundMethod, new HttpReq(ctx, req, querydecoder));
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ExceptionHandler.handleException(ctx, cause);
    }
}