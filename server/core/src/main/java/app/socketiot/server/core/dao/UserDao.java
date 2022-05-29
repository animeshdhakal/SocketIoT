package app.socketiot.server.core.dao;

import java.util.concurrent.ConcurrentMap;

import app.socketiot.server.core.model.auth.User;

public class UserDao {
    private ConcurrentMap<String, User> users;

    public UserDao(ConcurrentMap<String, User> users) {
        this.users = users;
    }

    public ConcurrentMap<String, User> getAllUsers() {
        return users;
    }

    public User getUser(String email) {
        return users.get(email);
    }

    public void addUser(User user) {
        users.put(user.email, user);
    }

    public boolean ifUserExists(String email) {
        return users.containsKey(email);
    }
}
