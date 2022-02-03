package app.socketiot.server.core.http;

import java.util.regex.Pattern;
import app.socketiot.server.core.http.handlers.HttpRes;
import app.socketiot.server.core.http.handlers.HttpStatus;
import app.socketiot.server.core.http.handlers.StaticFile;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.ReferenceCountUtil;

@ChannelHandler.Sharable
public class StaticFileHandler extends ChannelInboundHandlerAdapter {
    private String path;
    private Class<?> resourceClass;

    public StaticFileHandler(Class<?> resourceClass, String path) {
        this.path = path;
        this.resourceClass = resourceClass;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest req = (FullHttpRequest) msg;
            if (req.uri().startsWith(path)) {
                try {
                    processStaticFile(ctx, req);
                } finally {
                    ReferenceCountUtil.release(msg);
                }
                return;
            }
        }
        ctx.fireChannelRead(msg);
    }

    public void processStaticFile(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {

        if (!req.decoderResult().isSuccess()) {
            HttpRes res = new HttpRes("Error", HttpStatus.BAD_REQUEST);
            BaseHttpHandler.sendHttpResponse(ctx, res.getFullHttpResponse(req.protocolVersion()));
            return;
        }

        if (req.method() != HttpMethod.GET) {
            return;
        }

        if (isNotValid(req.uri())) {
            HttpRes res = new HttpRes("Error", HttpStatus.BAD_REQUEST);
            BaseHttpHandler.sendHttpResponse(ctx, res.getFullHttpResponse(req.protocolVersion()));
            return;
        }

        HttpRes res = new StaticFile(resourceClass, req.uri(), HttpStatus.OK);

        if (res.getContent() == null) {
            res = new HttpRes("Error", HttpStatus.NOT_FOUND);
        }

        BaseHttpHandler.sendHttpResponse(ctx, res.getFullHttpResponse(req.protocolVersion()));

    }

    private static final Pattern INVALID_URI = Pattern.compile(".*[<>&\"].*");

    private static boolean isNotValid(String uri) {
        if (uri.isEmpty() || uri.charAt(0) != '/') {
            return true;
        }

        return uri.contains("/.")
                || uri.contains("./")
                || uri.contains(".\\")
                || uri.contains("\\.")
                || uri.charAt(0) == '.' || uri.charAt(uri.length() - 1) == '.'
                || INVALID_URI.matcher(uri).matches();

    }

}
