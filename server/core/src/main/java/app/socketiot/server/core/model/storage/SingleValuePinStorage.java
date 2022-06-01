package app.socketiot.server.core.model.storage;

import java.util.Collection;
import java.util.Collections;

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
    public Collection<String> getValues() {
        if (value == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(value);
    }

    @Override
    @JsonValue
    public String toString() {
        return value == null ? "" : value;
    }
}
