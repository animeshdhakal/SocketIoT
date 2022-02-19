package app.socketiot.server.core.dao;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentMap;

import app.socketiot.server.core.model.auth.User;

public class UserDao {

    private ConcurrentMap<String, User> users;

    public UserDao(ConcurrentMap<String, User> users) {
        this.users = users;
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
