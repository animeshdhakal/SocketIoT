package animesh.app.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpMethod.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import java.io.InputStream;

class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        // Handle a bad request.
        if (!req.decoderResult().isSuccess()) {
            sendHttpResponse(ctx, req,
                    new DefaultFullHttpResponse(req.protocolVersion(), BAD_REQUEST, ctx.alloc().buffer(0)));
            return;
        }

        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(req.uri());

        String[] path = queryStringDecoder.path().split("/");

        if (path.length > 3 && GET.equals(req.method())) {
            if ("set".equals(path[1])) {
                String token = path[2];
                String pin = path[3];
                String value = queryStringDecoder.parameters().get("value").get(0);
                if (ClientHandler.checkAuth(token)) {
                    ClientHandler.broadCastMessage(ctx, ClientHandler.createMessage(MsgType.WRITE, pin, value), token);
                    FullHttpResponse res = new DefaultFullHttpResponse(req.protocolVersion(), OK,
                            Unpooled.copiedBuffer("Success", CharsetUtil.US_ASCII));
                    sendHttpResponse(ctx, req, res);
                }

            } else if ("get".equals(path[1])) {
                String token = path[2];
                String pin = path[3];
                if (ClientHandler.checkAuth(token)) {
                    String pinVal = ClientHandler.getPinVal(token, pin);
                    if (pinVal != null) {
                        FullHttpResponse res = new DefaultFullHttpResponse(req.protocolVersion(), OK,
                                Unpooled.copiedBuffer(pinVal, CharsetUtil.US_ASCII));
                        sendHttpResponse(ctx, req, res);
                    } else {
                        FullHttpResponse res = new DefaultFullHttpResponse(req.protocolVersion(), NOT_FOUND,
                                Unpooled.copiedBuffer("Pin not found", CharsetUtil.US_ASCII));
                        sendHttpResponse(ctx, req, res);
                    }
                }
            }
        }

        if (req.uri().contains("/static/")) {
            ByteBuf content = getResourceFile(req.uri());
            if (content == null) {
                sendHttpResponse(ctx, req,
                        new DefaultFullHttpResponse(req.protocolVersion(), NOT_FOUND, ctx.alloc().buffer(0)));
            } else {
                FullHttpResponse res = new DefaultFullHttpResponse(req.protocolVersion(), OK, content);

                // res.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
                HttpUtil.setContentLength(res, content.readableBytes());

                sendHttpResponse(ctx, req, res);
            }

        }

        if ("/".equals(req.uri())) {

            ByteBuf content = getResourceFile("/html/index.html");

            FullHttpResponse res = new DefaultFullHttpResponse(req.protocolVersion(), OK, content);

            res.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
            HttpUtil.setContentLength(res, content.readableBytes());

            sendHttpResponse(ctx, req, res);
        } else {
            sendHttpResponse(ctx, req,
                    new DefaultFullHttpResponse(req.protocolVersion(), NOT_FOUND, ctx.alloc().buffer(0)));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LoggerUtil.logger.error(cause.getMessage());
        ctx.close();
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        // Generate an error page if response getStatus code is not OK (200).
        HttpResponseStatus responseStatus = res.status();
        if (responseStatus.code() != 200) {
            ByteBufUtil.writeUtf8(res.content(), responseStatus.toString());
            HttpUtil.setContentLength(res, res.content().readableBytes());
        }
        // Send the response and close the connection if necessary.
        boolean keepAlive = HttpUtil.isKeepAlive(req) && responseStatus.code() == 200;
        HttpUtil.setKeepAlive(res, keepAlive);

        ChannelFuture future = ctx.writeAndFlush(res);
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    public static ByteBuf getResourceFile(String path) {
        try {
            InputStream fileStream = HttpServerHandler.class.getResourceAsStream(path);
            ByteBuf content = Unpooled.copiedBuffer(fileStream.readAllBytes());
            return content;
        } catch (Exception e) {
            return null;
        }
    }
}