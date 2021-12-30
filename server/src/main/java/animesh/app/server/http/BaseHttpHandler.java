package animesh.app.server.http;

import java.lang.reflect.Method;
import animesh.app.server.http.annotations.POST;
import animesh.app.server.http.annotations.Path;
import animesh.app.server.http.annotations.StaticFolder;
import animesh.app.server.http.handlers.HttpReq;
import animesh.app.server.http.handlers.HttpRes;
import animesh.app.server.http.handlers.HttpStatus;
import animesh.app.server.http.handlers.StaticFile;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;

public class BaseHttpHandler extends ChannelInboundHandlerAdapter {
    private Method[] methods = null;

    private String staticFolder = null;

    public BaseHttpHandler() {
        super();
        methods = this.getClass().getDeclaredMethods();
        if (this.getClass().isAnnotationPresent(StaticFolder.class)) {
            StaticFolder path = this.getClass().getAnnotation(StaticFolder.class);
            staticFolder = path.value();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;
            process(ctx, req);
        }
    }

    public void sendHttpResponse(ChannelHandlerContext ctx, FullHttpResponse response) {
        if (ctx.channel().isActive()) {
            ctx.writeAndFlush(response);
        }
    }

    public void process(ChannelHandlerContext ctx, HttpRequest req) {
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
                    try {
                        HttpRes res = (HttpRes) method.invoke(this, new HttpReq(ctx, req, querydecoder));

                        ByteBuf content = res.getContent();

                        if (content != null) {
                            FullHttpResponse resp = new DefaultFullHttpResponse(req.protocolVersion(),
                                    res.getHttpStatus(),
                                    content);

                            HttpUtil.setContentLength(resp, content.readableBytes());

                            sendHttpResponse(ctx, resp);

                            return;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (staticFolder != null) {
            if (querydecoder.path().contains(staticFolder)) {
                StaticFile file = new StaticFile(querydecoder.path(), HttpStatus.OK);

                if (file.getContent() != null) {
                    FullHttpResponse resp = new DefaultFullHttpResponse(req.protocolVersion(), file.getHttpStatus(),
                            file.getContent());

                    HttpUtil.setContentLength(resp, file.getContent().readableBytes());

                    sendHttpResponse(ctx, resp);
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}