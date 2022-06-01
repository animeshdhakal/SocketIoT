package app.socketiot.server.core.model.storage;

import java.util.Collection;

import app.socketiot.server.core.model.structure.LimitedQueue;

public class MultiValuePinStorage extends PinStorage {
    public final LimitedQueue<String> values;
    public final MultiValuePinStorageType type;

    public MultiValuePinStorage(MultiValuePinStorageType type) {
        this.type = type;
        this.values = type.getQueue();
    }

    public void updateValue(String value) {
        this.values.add(value);
    }

    @Override
    public Collection<String> getValues() {
        return this.values;
    }
}
