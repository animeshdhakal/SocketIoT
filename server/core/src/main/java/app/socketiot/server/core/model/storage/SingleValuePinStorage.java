package app.socketiot.server.core.model.storage;

import com.fasterxml.jackson.annotation.JsonValue;

public class SingleValuePinStorage extends PinStorage {
    public volatile String value;

    public SingleValuePinStorage(String value) {
        this.value = value;
    }

    public void updateValue(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return value == null ? "" : value;
    }
}
