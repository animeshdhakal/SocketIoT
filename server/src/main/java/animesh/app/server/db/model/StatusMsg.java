package animesh.app.server.db.model;

public class StatusMsg {
    public boolean error;
    public String message;

    public StatusMsg(boolean error, String message) {
        this.error = error;
        this.message = message;
    }
}
