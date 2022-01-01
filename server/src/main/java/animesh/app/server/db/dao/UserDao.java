package animesh.app.server.db.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import animesh.app.server.db.MainDB;
import animesh.app.server.db.model.User;

public class UserDao {
    public User getUser(String email, String password) {
        return new User(email, password);
    }

    public boolean createUser(User user) {
        PreparedStatement stmt = null;
        if (MainDB.available()) {
            try {
                stmt = MainDB.conn.prepareStatement("INSERT INTO users (email, password, token) VALUES (?, ?, ?)");
                stmt.setString(1, user.email);
                stmt.setString(2, user.password);
                stmt.setString(3, user.token);
                stmt.executeUpdate();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public User getUser(String email) {
        // get user from db
        PreparedStatement stmt = null;
        if (MainDB.available()) {
            try {
                stmt = MainDB.conn.prepareStatement("SELECT * FROM users WHERE email = ?");
                stmt.setString(1, email);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        User user = new User(rs.getString("email"), rs.getString("password"));
                        user.token = rs.getString("token");
                        return user;
                    } else {
                        return null;
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public boolean updateUser(User user) {
        PreparedStatement stmt = null;
        if (MainDB.available()) {
            try {
                stmt = MainDB.conn.prepareStatement("UPDATE users SET token = ? WHERE email = ?");
                stmt.setString(1, user.token);
                stmt.setString(2, user.email);
                stmt.executeUpdate();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public boolean deleteUser(String email) {
        PreparedStatement stmt = null;
        if (MainDB.available()) {
            try {
                stmt = MainDB.conn.prepareStatement("DELETE FROM users and projects WHERE email = ?");
                stmt.setString(1, email);
                stmt.executeUpdate();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

}
