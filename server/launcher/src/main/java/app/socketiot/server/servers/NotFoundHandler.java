package app.socketiot.server.servers;

import app.socketiot.server.core.http.handlers.HttpRes;
import app.socketiot.server.core.http.handlers.HttpStatus;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;


@ChannelHandler.Sharable
public class NotFoundHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(io.netty.channel.ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FullHttpRequest){
            FullHttpRequest request = (FullHttpRequest) msg;
            HttpRes httpRes = new HttpRes("Not Found", HttpStatus.NOT_FOUND);
            ctx.writeAndFlush(httpRes.getFullHttpResponse(request.protocolVersion()));
        }
    }
}
