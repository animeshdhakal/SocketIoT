package app.socketiot.server.core.model.storage;

import app.socketiot.server.core.model.structure.LimitedQueue;

class ValueLimits {
    public static final int JOYSTICK = 4;
}

public enum MultiValuePinStorageType {
    JOYSTICK;

    public LimitedQueue<String> getQueue() {
        switch (this) {
            case JOYSTICK:
                return new LimitedQueue<String>(ValueLimits.JOYSTICK);
            default:
                return null;
        }
    }
}
