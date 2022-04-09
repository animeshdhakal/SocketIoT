package app.socketiot.server.api.model.GoogleAssistant;

import java.util.List;

public class ExecuteRes {
    public static class ExecutePayload {
        public static class ExecuteCommand {
            public String[] ids;
            public QueryDevice states;
            public String status;
            public String errorCode;
        }

        public List<ExecuteCommand> commands;
    }

    public String requestId;
    public ExecutePayload payload;
}
