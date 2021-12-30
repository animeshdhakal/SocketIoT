package animesh.app.server;

import java.io.IOException;

import javax.net.ssl.SSLException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;

public class ExceptionHandler {

    public static void handleException(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof DecoderException) {
            Throwable t = cause.getCause();
            if (t instanceof SSLException) {
                Logger.debug("Unsecure Connection or unsupported protocol");
            } else {
                Logger.error("DecoderException: " + t.getMessage());
            }
            ctx.close();

        } else if (cause instanceof SSLException) {
            Logger.warn("SSL Exception: " + cause.getMessage());
            ctx.close();

        } else if (cause instanceof IOException) {
            Logger.debug("IOException: " + cause.getMessage());

        } else {
            String message = cause == null ? "" : cause.getMessage();
            if (message != null && message.contains("Connection reset by peer")) {
                Logger.debug("Connection reset by peer");
            } else if (message != null && message.contains("OutOfDirectMemoryError")) {
                Logger.error("OutOfDirectMemoryError");
            } else {
                Logger.error("Unknown Exception: " + message);
            }

        }
    }
}
