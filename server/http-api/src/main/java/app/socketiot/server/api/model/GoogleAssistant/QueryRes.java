package app.socketiot.server.api.model.GoogleAssistant;

import java.util.Map;

public class QueryRes {
    public static class QueryPayload {
        public Map<String, QueryDevice> devices;
    }

    public String requestId;
    public QueryPayload payload;
}
