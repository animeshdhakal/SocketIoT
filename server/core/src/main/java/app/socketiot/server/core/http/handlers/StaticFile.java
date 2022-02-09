package app.socketiot.server.core.http.handlers;

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

    public StaticFile(String path) {
        super(HttpResponseStatus.OK);
        try {
            super.content().writeBytes(this.getClass().getResourceAsStream(path).readAllBytes());
            addRequiredHeaders();
            headers().set(HttpHeaderNames.CONTENT_TYPE, getContentType(path));
        } catch (Exception e) {
        }
    }

    public StaticFile(Class<?> clazz, String path) {
        super(HttpStatus.OK);
        try {
            super.content().writeBytes(clazz.getResourceAsStream(path).readAllBytes());
            addRequiredHeaders();
            headers().set(HttpHeaderNames.CONTENT_TYPE, getContentType(path));
        } catch (Exception e) {
        }
    }
}
