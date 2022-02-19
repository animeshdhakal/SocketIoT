package app.socketiot.server.core.notification.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FCMResult {
    public final String error;

    @JsonCreator
    public FCMResult(@JsonProperty("error") String error) {
        this.error = error;
    }
}
