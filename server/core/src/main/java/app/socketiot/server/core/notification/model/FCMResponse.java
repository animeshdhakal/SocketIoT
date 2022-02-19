package app.socketiot.server.core.notification.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FCMResponse {

    public final int success;
    public final int failure;

    @JsonProperty("multicast_id")
    private final long multicastId;

    public final FCMResult[] results;

    @JsonCreator
    public FCMResponse(@JsonProperty("success") int success,
            @JsonProperty("failure") int failure,
            @JsonProperty("multicast_id") long multicastId,
            @JsonProperty("results") FCMResult[] results) {
        this.success = success;
        this.failure = failure;
        this.multicastId = multicastId;
        this.results = results;
    }
}