package app.socketiot.server.internal.protocol;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public abstract class ProtocolMerger extends ChannelInboundHandlerAdapter {
    @Override
    public abstract void channelRead(ChannelHandlerContext ctx, Object msg);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

    }
}
