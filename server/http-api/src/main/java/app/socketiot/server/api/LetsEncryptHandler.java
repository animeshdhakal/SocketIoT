package app.socketiot.server.api;

import app.socketiot.server.core.acme.AcmeClient;
import app.socketiot.server.core.http.handlers.HttpRes;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class LetsEncryptHandler extends ChannelInboundHandlerAdapter {
    private static final String LETS_ENCRYPT_PATH = "/.well-known/acme-challenge/";
    private final AcmeClient acmeClient;

    public LetsEncryptHandler(AcmeClient acmeClient) {
        this.acmeClient = acmeClient;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            String uri = request.uri();
            if (uri.startsWith(LETS_ENCRYPT_PATH)) {
                if (!request.decoderResult().isSuccess()) {
                    ctx.writeAndFlush(HttpRes.badRequest("Error"));
                    return;
                }

                if (request.method() != HttpMethod.GET) {
                    ctx.writeAndFlush(HttpRes.badRequest("Error"));
                    return;
                }

                if (acmeClient == null || acmeClient.content == null) {
                    ctx.writeAndFlush(HttpRes.badRequest("Error"));
                    return;
                }

                final String content = acmeClient.content;

                HttpRes res = new HttpRes(content);

                res.setHeader("content-type", "text/html");

                ChannelFuture future = ctx.writeAndFlush(res);

                if (!HttpUtil.isKeepAlive(request)) {
                    future.addListener(ChannelFutureListener.CLOSE);
                }

                return;

            }
        }

        ctx.fireChannelRead(msg);
    }

}
