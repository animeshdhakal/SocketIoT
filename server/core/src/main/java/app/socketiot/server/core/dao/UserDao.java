package app.socketiot.server.core.dao;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.device.Device;

public class UserDao {

    public final ConcurrentMap<String, User> users;

    public UserDao(ConcurrentMap<String, User> users) {
        if (users == null) {
            this.users = new ConcurrentHashMap<>();
        } else {
            this.users = users;
        }

    }

    public User getUser(String email) {
        return users.get(email);
    }

    public void addUser(User user) {
        users.put(user.email, user);
        user.updated();
    }

    public void putUser(User user) {
        users.put(user.email, user);
    }

    public void removeUser(String email) {
        users.remove(email);
    }

    public User getUserFromProvisioningToken(String provisioningToken) {
        for (User user : users.values()) {
            if (user.dash.provisioningToken != null && user.dash.provisioningToken.equals(provisioningToken)) {
                return user;
            }
        }
        return null;
    }

    public void removeDevice(String token) {
        for (User user : users.values()) {
            for (Device device : user.dash.devices) {
                if (device.token.equals(token)) {
                    user.dash.removeDevice(device.token);
                    return;
                }
            }
        }
    }

    public ArrayList<User> getAllUsers() {
        ArrayList<User> data = new ArrayList<>();
        for (User user : users.values()) {
            if (user.isUpdated) {
                data.add(user);
                user.isUpdated = false;
            }
        }
        return data;
    }
}
