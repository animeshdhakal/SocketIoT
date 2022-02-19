package app.socketiot.server.api;

import java.nio.file.Path;
import java.nio.file.Paths;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.exceptions.ExceptionHandler;
import app.socketiot.server.core.http.StaticFileHandler;
import app.socketiot.server.core.http.handlers.HttpRes;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

@ChannelHandler.Sharable
public class ReactHandler extends ChannelInboundHandlerAdapter {
    private final String indexFilePath;
    private final boolean isUnpacked;
    private final String jarPath;

    public ReactHandler(Holder holder, String indexFilePath) {
        this.indexFilePath = indexFilePath;
        this.isUnpacked = holder.isUnpacked;
        this.jarPath = holder.jarPath;
    }

    private void sendNotFound(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(HttpRes.notFound("Not Found"));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            if (isUnpacked) {
                Path path = Paths.get(jarPath, indexFilePath);
                StaticFileHandler.sendStaticFile(ctx, (FullHttpRequest) msg, path);
                return;
            } else {
                try {
                    HttpRes res;
                    res = new HttpRes(this.getClass().getResourceAsStream(indexFilePath).readAllBytes());
                    ctx.writeAndFlush(res);
                    return;
                } catch (Exception e) {
                    sendNotFound(ctx);
                    return;
                }
            }
        }

        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ExceptionHandler.handleException(ctx, cause);
    }

}
