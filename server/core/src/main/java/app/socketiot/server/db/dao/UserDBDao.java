package app.socketiot.server.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import app.socketiot.server.core.model.DashBoard;
import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.json.JsonParser;
import app.socketiot.server.db.DB;

public class UserDBDao {
    private final DB db;
    private static final Logger log = LogManager.getLogger(UserDBDao.class);

    public UserDBDao(DB db) {
        this.db = db;
    }

    public ConcurrentMap<String, User> getAllUsers() {
        ConcurrentMap<String, User> users = new ConcurrentHashMap<>();
        try (Connection connection = db.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Timestamp lastModified = rs.getTimestamp("last_modified");
                User user = new User(rs.getString("email"), rs.getString("password"),
                        lastModified == null ? 0 : lastModified.getTime(),
                        JsonParser.parsePrivateJson(rs.getString("json"), DashBoard.class));
                users.put(user.email, user);
            }

        } catch (Exception e) {
            log.error("Unexpected Error Occured while getting all users from DB", e);
        }

        return users;
    }

    public void saveAllUsers(List<User> users) {
        try (Connection connection = db.getConnection()) {
            for (User user : users) {
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO users (email, password, last_modified, json) VALUES (?, ?, ?, ?) ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password, last_modified = EXCLUDED.last_modified, json = EXCLUDED.json");
                statement.setString(1, user.email);
                statement.setString(2, user.password);
                statement.setTimestamp(3, new Timestamp(user.lastModified));
                statement.setString(4, JsonParser.toPrivateJson(user.dash));
                statement.executeUpdate();
            }
        } catch (Exception e) {
            log.error("Unexpected Error Occured while saving all users to DB", e);
        }
    }

}
