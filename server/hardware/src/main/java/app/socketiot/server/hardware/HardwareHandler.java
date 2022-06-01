package app.socketiot.server.hardware;

import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.core.model.device.UserDevice;
import app.socketiot.server.core.model.statebase.HardwareStateBase;

public class HardwareHandler extends HardwareStateBase {
    private final UserDevice userDevice;

    public HardwareHandler(UserDevice userDevice) {
        this.userDevice = userDevice;
    }

    @Override
    public User getUser() {
        return userDevice.user;
    }

    @Override
    public Device getDevice() {
        return userDevice.device;
    }
}
