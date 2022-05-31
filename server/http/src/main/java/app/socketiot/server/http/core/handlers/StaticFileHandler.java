package app.socketiot.server.http.core.handlers;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import app.socketiot.server.exceptions.ExceptionHandler;
import app.socketiot.server.http.core.HttpRes;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.ReferenceCountUtil;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import io.netty.channel.ChannelFuture;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedFile;

import static io.netty.handler.codec.http.HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN;
import static io.netty.handler.codec.http.HttpHeaderNames.CACHE_CONTROL;
import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderNames.DATE;
import static io.netty.handler.codec.http.HttpHeaderNames.EXPIRES;
import static io.netty.handler.codec.http.HttpHeaderNames.IF_MODIFIED_SINCE;
import static io.netty.handler.codec.http.HttpHeaderNames.LAST_MODIFIED;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_MODIFIED;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@ChannelHandler.Sharable
public class StaticFileHandler extends ChannelInboundHandlerAdapter {
    private final String[] paths;
    private boolean isUnpacked;
    private final String jarPath;
    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    public static final int HTTP_CACHE_SECONDS = 60;

    public StaticFileHandler(boolean isUnpacked, String jarPath, String... paths) {
        this.paths = paths;
        this.isUnpacked = isUnpacked;
        this.jarPath = jarPath;
    }

    public static String getContentType(String file) {
        String ext = "";
        int i = file.lastIndexOf('.');
        if (i > 0) {
            ext = file.substring(i + 1);
        }
        String content_type = "text/plain";

        switch (ext) {
            case "html":
                content_type = "text/html";
                break;
            case "css":
                content_type = "text/css";
                break;
            case "js":
                content_type = "text/javascript";
                break;
        }
        return content_type;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest req = (FullHttpRequest) msg;
            if (isStaticFile(req.uri())) {
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

    private boolean isStaticFile(String uri) {
        for (String path : paths) {
            if (uri.startsWith(path)) {
                return true;
            }
        }
        return false;
    }

    private static void setDateHeader(FullHttpResponse response) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        Calendar time = new GregorianCalendar();
        response.headers().set(DATE, dateFormatter.format(time.getTime()));
    }

    private static void sendNotModified(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, NOT_MODIFIED);
        setDateHeader(response);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    public static void sendStaticFile(ChannelHandlerContext ctx, FullHttpRequest req, Path path) throws Exception {
        if (path == null || Files.notExists(path) || Files.isDirectory(path)) {
            ctx.writeAndFlush(HttpRes.notFound("File Not Found"));
            return;
        }

        File file = path.toFile();

        // Cache Validation
        String ifModifiedSince = req.headers().get(IF_MODIFIED_SINCE);
        if (ifModifiedSince != null && !ifModifiedSince.isEmpty()) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
            Date ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince);
            long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
            long fileLastModifiedSeconds = file.lastModified() / 1000;
            if (ifModifiedSinceDateSeconds == fileLastModifiedSeconds) {
                sendNotModified(ctx);
                return;
            }
        }

        RandomAccessFile raf;

        try {
            raf = new RandomAccessFile(file, "r");
        } catch (Exception e) {
            ctx.writeAndFlush(HttpRes.internalServerError("Error"));
            return;
        }

        long fileLength = raf.length();

        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        response.headers()
                .set(CONTENT_LENGTH, fileLength)
                .set(CONTENT_TYPE, getContentType(file.getName()))
                .set(ACCESS_CONTROL_ALLOW_ORIGIN, "*");

        setDateAndCacheHeaders(response, file);
        if (HttpUtil.isKeepAlive(req)) {
            response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        ctx.write(response);

        ChannelFuture sendFileFuture;
        ChannelFuture lastContentFuture;
        if (ctx.pipeline().get(SslHandler.class) == null) {
            ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
            lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        } else {
            sendFileFuture = ctx.writeAndFlush(new HttpChunkedInput(new ChunkedFile(raf, 128 * 1024)),
                    ctx.newProgressivePromise());
            lastContentFuture = sendFileFuture;
        }

        if (!HttpUtil.isKeepAlive(req)) {
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    public void processStaticFile(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {

        if (!req.decoderResult().isSuccess()) {
            ctx.writeAndFlush(HttpRes.badRequest("Decoder Error"));
            return;
        }

        if (req.method() != HttpMethod.GET) {
            return;
        }

        if (isNotValid(req.uri())) {
            ctx.writeAndFlush(HttpRes.badRequest("Invalid Static File"));
            return;
        }

        Path path = null;

        if (isUnpacked) {
            path = Paths.get(jarPath, req.uri());
        } else {
            InputStream is = getClass().getResourceAsStream(req.uri());
            if (is == null) {
                ctx.writeAndFlush(HttpRes.notFound("File Not Found"));
                return;
            } else {
                HttpRes res = new HttpRes(is.readAllBytes());
                res.setHeader(CONTENT_TYPE.toString(), getContentType(req.uri()));
                ctx.writeAndFlush(res);
                return;
            }
        }

        sendStaticFile(ctx, req, path);

    }

    private static void setDateAndCacheHeaders(HttpResponse response, File fileToCache) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        // Date header
        Calendar time = new GregorianCalendar();
        response.headers().set(DATE, dateFormatter.format(time.getTime()));

        // Add cache headers
        time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
        response.headers()
                .set(EXPIRES, dateFormatter.format(time.getTime()))
                .set(CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS)
                .set(LAST_MODIFIED, dateFormatter.format(new Date(fileToCache.lastModified())));
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

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ExceptionHandler.handleException(ctx, cause);
    }

}