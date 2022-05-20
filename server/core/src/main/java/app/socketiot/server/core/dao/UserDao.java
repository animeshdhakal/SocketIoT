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
        user.isUpdated = true;
        users.put(user.email, user);
    }

    public void putUser(User user) {
        users.put(user.email, user);
    }

    public void removeUser(String email) {
        users.remove(email);
    }

    public void updateUser(User user) {
        user.isUpdated = true;
    }

    public User getUserFromProvisioningToken(String provisioningToken) {
        for (User user : users.values()) {
            if (user.json.provisioningToken.equals(provisioningToken)) {
                return user;
            }
        }
        return null;
    }

    public void removeDevice(String token) {
        for (User user : users.values()) {
            for (Device device : user.json.devices) {
                if (device.token.equals(token)) {
                    user.json.removeDevice(device.token);
                    return;
                }
            }
        }
    }

    public ArrayList<User> getAllUsers() {
        ArrayList<User> data = new ArrayList<>();
        for (User user : users.values()) {
            if (user.isUpdated || user.json.isUpdated) {
                data.add(user);
                user.isUpdated = false;
                user.json.isUpdated = false;
            }
        }
        return data;
    }
}
