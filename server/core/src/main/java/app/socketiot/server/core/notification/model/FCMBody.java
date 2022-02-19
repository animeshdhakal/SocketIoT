package app.socketiot.server.core.notification.model;

public class FCMBody {
    public final String title;
    public final String body;

    public FCMBody(String title, String body) {
        this.title = title;
        this.body = body;
    }
}
