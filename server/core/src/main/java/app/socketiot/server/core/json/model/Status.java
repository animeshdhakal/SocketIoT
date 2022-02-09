package app.socketiot.server.core.json.model;

public class Status {
    public boolean error;
    public String message;

    public Status(boolean error, String message) {
        this.error = error;
        this.message = message;
    }
}
