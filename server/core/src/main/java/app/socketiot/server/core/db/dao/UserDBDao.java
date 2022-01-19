package app.socketiot.server.core.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.db.model.User;

public class UserDBDao {
    private final Holder holder;

    public UserDBDao(Holder holder) {
        this.holder = holder;
    }

    public ConcurrentMap<String, User> getAllUsers() {
        ConcurrentMap<String, User> users = new ConcurrentHashMap<>();
        try (Connection connection = holder.db.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.put(rs.getString("email"),
                            new User(rs.getString("email"), rs.getString("password"), rs.getInt("id")));
                }
            }
        } catch (Exception e) {

        }

        return users;
    }

    public boolean saveUser(Connection connection, User user) {
        try {
            PreparedStatement stmt = connection
                    .prepareStatement("INSERT INTO users (email, password) VALUES (?, ?, ?)");
            stmt.setString(1, user.email);
            stmt.setString(2, user.password);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public boolean updateUser(Connection connection, User user) {
        try {
            PreparedStatement stmt = connection
                    .prepareStatement("UPDATE users SET password = ? WHERE email = ?");
            stmt.setString(3, user.email);
            stmt.setString(1, user.password);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void saveAllUsers(ArrayList<User> users) {
        try (Connection connection = holder.db.getConnection()) {
            for (User user : users) {
                saveUser(connection, user);
            }
        } catch (Exception e) {

        }
    }

}
