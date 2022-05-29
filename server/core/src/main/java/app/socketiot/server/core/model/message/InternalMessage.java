package app.socketiot.server.core.model.message;

public class InternalMessage {
    public int type;
    public String[] body;
    public char resStatus;

    public InternalMessage(int type, String... args) {
        this.type = type;
        this.body = args;
    }
}