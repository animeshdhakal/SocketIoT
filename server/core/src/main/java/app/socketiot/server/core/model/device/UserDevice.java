package app.socketiot.server.core.model.device;

import app.socketiot.server.core.model.auth.User;

public class UserDevice {
    public User user;
    public Device device;

    public UserDevice(User user, Device device) {
        this.user = user;
        this.device = device;
    }
}
