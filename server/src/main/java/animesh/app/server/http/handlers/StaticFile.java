package animesh.app.server.http.handlers;

import java.io.InputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;

public class StaticFile extends HttpRes {
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

    public StaticFile(String path, HttpResponseStatus status) {
        super();

        this.status = status;

        try {
            InputStream fileStream = this.getClass().getResourceAsStream(path);

            buff = Unpooled.copiedBuffer(fileStream.readAllBytes());

            headers.set(HttpHeaderNames.CONTENT_TYPE, getContentType(path));

        } catch (Exception e) {
        }

    }

    public StaticFile(String path) {
        this(path, HttpResponseStatus.OK);
    }
}
