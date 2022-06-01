package app.socketiot.server.core.model.storage;

import java.util.Collection;

public abstract class PinStorage {
    public abstract void updateValue(String value);

    public abstract Collection<String> getValues();
}
