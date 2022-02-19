package app.socketiot.server.core.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;

@ChannelHandler.Sharable
public class UrlRewriterHandler
        extends ChannelInboundHandlerAdapter {
    private final String from;
    private final String to;

    public UrlRewriterHandler(String from, String to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;
            String uri = req.uri();
            if (uri.equals(from)) {
                req.setUri(to);
            }
        }
        ctx.fireChannelRead(msg);
    }

}
