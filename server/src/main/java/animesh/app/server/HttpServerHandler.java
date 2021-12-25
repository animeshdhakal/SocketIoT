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

import java.io.InputStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import animesh.app.server.db.dao.UserDao;
import animesh.app.server.db.model.StatusMsg;
import animesh.app.server.db.model.User;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static ObjectMapper mapper = new ObjectMapper();

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
                    ClientHandler.broadCastMessage(ctx,
                            ClientHandler.createMessage(MsgType.WRITE, pin, value), token);
                    FullHttpResponse res = new DefaultFullHttpResponse(req.protocolVersion(), OK,
                            Unpooled.copiedBuffer("Success", CharsetUtil.US_ASCII));
                    sendHttpResponse(ctx, req, res);
                    return;
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
                        return;
                    } else {
                        FullHttpResponse res = new DefaultFullHttpResponse(req.protocolVersion(),
                                NOT_FOUND,
                                Unpooled.copiedBuffer("Pin not found", CharsetUtil.US_ASCII));
                        sendHttpResponse(ctx, req, res);
                        return;
                    }
                }
            }
        }

        if (queryStringDecoder.path().contains("/static/") && GET.equals(req.method())) {
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

        } else if ("/register".equals(queryStringDecoder.path()) && POST.equals(req.method())) {

            User user = mapper.readValue(req.content().toString(CharsetUtil.US_ASCII), User.class);
            user.hashPass();
            if (user.email != null && user.password != null) {
                if (UserDao.createUser(user)) {
                    sendHttpResponse(ctx, req, new StatusMsg(false, "User Registered Successfully"), OK);
                } else {
                    sendHttpResponse(ctx, req, new StatusMsg(true, "User Already Exists"), BAD_REQUEST);
                }
            } else {
                sendHttpResponse(ctx, req, new StatusMsg(true, "Incomplete Fields"), BAD_REQUEST);
            }

        } else if ("/".equals(queryStringDecoder.path())) {

            ByteBuf content = getResourceFile("/html/index.html");

            FullHttpResponse res = new DefaultFullHttpResponse(req.protocolVersion(), OK, content);

            res.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");

            sendHttpResponse(ctx, req, res);

        } else {
            sendHttpResponse(ctx, req,
                    new DefaultFullHttpResponse(req.protocolVersion(), NOT_FOUND,
                            Unpooled.copiedBuffer("Not Found", CharsetUtil.US_ASCII)));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // LoggerUtil.logger.error(cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        // Generate an error page if response getStatus code is not OK (200).
        HttpUtil.setContentLength(res, res.content().readableBytes());

        // Send the response and close the connection if necessary.
        boolean keepAlive = HttpUtil.isKeepAlive(req);
        ChannelFuture f = ctx.writeAndFlush(res);
        if (!keepAlive) {
            f.addListener(ChannelFutureListener.CLOSE);
        }

    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, StatusMsg msg,
            HttpResponseStatus status)
            throws JsonProcessingException {
        FullHttpResponse resp = new DefaultFullHttpResponse(req.protocolVersion(), status,
                Unpooled.copiedBuffer(mapper.writeValueAsString(msg), CharsetUtil.US_ASCII));
        resp.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");
        sendHttpResponse(ctx, req, resp);
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