package app.socketiot.server.hardware;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

@ChannelHandler.Sharable
public class WebSocketHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        if(msg instanceof BinaryWebSocketFrame){
            ctx.fireChannelRead(((BinaryWebSocketFrame)msg).content());
        }
    }
}
