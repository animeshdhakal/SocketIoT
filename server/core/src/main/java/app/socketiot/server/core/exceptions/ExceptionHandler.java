package app.socketiot.server.core.exceptions;

import java.io.IOException;
import javax.net.ssl.SSLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;

public class ExceptionHandler {
    static Logger log = LogManager.getLogger(ExceptionHandler.class);

    public static void handleException(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof DecoderException) {
            Throwable t = cause.getCause();
            if (t instanceof SSLException) {
                log.debug("Unsecured connection attempt or not supported protocol. Channel : {}. Reason : {}",
                        ctx.channel().remoteAddress(), cause.getMessage());
            } else {
                log.error("DecoderException Pipeline: {}", ctx.pipeline(), cause);
            }
            ctx.close();

        } else if (cause instanceof SSLException) {
            log.debug("SSL exception. {}. {}", cause.getMessage(), ctx.channel().remoteAddress());
            ctx.close();

        } else if (cause instanceof IOException) {
            log.trace("IOException.", cause);

        } else {
            String message = cause == null ? "" : cause.getMessage();
            if (message != null && message.contains("Connection reset by peer")) {
                log.debug("Connection reset by peer. {}", ctx.channel().remoteAddress());
            } else if (message != null && message.contains("OutOfDirectMemoryError")) {
                log.error("OutofDirectMemoryError");
            } else {
                log.error("Unexpected error! Handler class : {}. Name : {}. Reason : {}. Channel : {}.",
                        ctx.handler().getClass(), ctx.name(), message, ctx.channel());
            }

        }
    }
}