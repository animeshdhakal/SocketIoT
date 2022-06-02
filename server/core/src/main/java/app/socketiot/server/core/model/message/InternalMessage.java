package app.socketiot.server.core.model.message;

import app.socketiot.server.core.model.json.Json;
import app.socketiot.server.core.model.json.JsonParser;

public class InternalMessage {
    public int type;
    public String[] body;
    public char resStatus;

    public InternalMessage(int type, String... args) {
        this.type = type;
        this.body = args;
    }

    public InternalMessage(int type, Json obj) {
        this.type = type;
        this.body = new String[] { JsonParser.toProtectedJson(obj) };
    }
}