package app.socketiot.server.core.notification.model;

public class FCMMessage {
    public final String to;
    public final Priority priority;
    public final FCMBody notification;

    public FCMMessage(String to, Priority priority, String title, String body) {
        this.to = to;
        this.priority = priority;
        this.notification = new FCMBody(title, body);
    }
}
