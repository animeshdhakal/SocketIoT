package app.socketiot.server.http.core.handlers;

import java.nio.file.Path;
import java.nio.file.Paths;
import app.socketiot.server.exceptions.ExceptionHandler;
import app.socketiot.server.http.core.HttpRes;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

@ChannelHandler.Sharable
public class ReactHandler extends ChannelInboundHandlerAdapter {
    private final String indexFilePath;
    private final boolean isUnpacked;
    private final String jarPath;

    public ReactHandler(boolean isUnpacked, String jarPath, String indexFilePath) {
        this.indexFilePath = indexFilePath;
        this.isUnpacked = isUnpacked;
        this.jarPath = jarPath;
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
