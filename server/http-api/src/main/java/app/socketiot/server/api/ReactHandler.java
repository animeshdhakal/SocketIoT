package app.socketiot.server.api;

import app.socketiot.server.core.http.BaseHttpHandler;
import app.socketiot.server.core.http.handlers.HttpRes;
import app.socketiot.server.core.http.handlers.HttpStatus;
import app.socketiot.server.core.http.handlers.StaticFile;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

@ChannelHandler.Sharable
public class ReactHandler extends ChannelInboundHandlerAdapter {
    private String indexFilePath;
    private Class<?> clazz;

    public ReactHandler(Class<?> clazz, String indexFilePath) {
        this.indexFilePath = indexFilePath;
        this.clazz = clazz;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest req = (FullHttpRequest) msg;
            HttpRes res = new StaticFile(clazz, indexFilePath, HttpStatus.OK);
            if (res.getContent() == null) {
                res = new HttpRes("Not Found", HttpStatus.NOT_FOUND);
            }
            BaseHttpHandler.sendHttpResponse(ctx, res.getFullHttpResponse(req.protocolVersion()));
            return;
        }

        ctx.fireChannelRead(msg);
    }

}
