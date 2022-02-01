package app.socketiot.server.servers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

public class WSEncoder extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception{
        if(ctx.channel().isWritable()){
            if(msg instanceof ByteBuf){
                super.write(ctx, new BinaryWebSocketFrame((ByteBuf)msg), promise);
            }else{
                super.write(ctx, msg, promise);
            }
        }
    }
}
