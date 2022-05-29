package app.socketiot.server.core.model;

import app.socketiot.server.core.model.json.JsonParser;

public class StatusMsg {
    public boolean error;
    public String message;

    public StatusMsg(boolean error, String message) {
        this.error = error;
        this.message = message;
    }

    public static String success(String message) {
        return JsonParser.toProtectedJson(new StatusMsg(false, message));
    }

    public static String error(String message) {
        return JsonParser.toProtectedJson(new StatusMsg(true, message));
    }
}
