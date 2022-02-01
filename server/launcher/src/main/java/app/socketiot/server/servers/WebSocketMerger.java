package app.socketiot.server.servers;

import app.socketiot.server.core.exceptions.ExceptionHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler;


@ChannelHandler.Sharable
public abstract class WebSocketMerger extends ChannelInboundHandlerAdapter {
    @Override
    public abstract void channelRead(ChannelHandlerContext ctx, Object o);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ExceptionHandler.handleException(ctx, cause);
    }
}
