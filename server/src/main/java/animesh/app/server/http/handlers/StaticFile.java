package animesh.app.server.http.handlers;

import java.io.InputStream;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpResponseStatus;

public class StaticFile extends HttpRes {
    public StaticFile(String path, HttpResponseStatus status) {
        try {
            InputStream fileStream = this.getClass().getResourceAsStream(path);
            buff = Unpooled.copiedBuffer(fileStream.readAllBytes());
        } catch (Exception e) {
        }
    }
}
