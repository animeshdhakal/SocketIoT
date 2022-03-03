package app.socketiot.server.core.model;

public class HardwareMessage {
    public int type;
    public String[] body;

    public HardwareMessage(int type, String... args) {
        this.type = type;
        this.body = args;
    }

}
