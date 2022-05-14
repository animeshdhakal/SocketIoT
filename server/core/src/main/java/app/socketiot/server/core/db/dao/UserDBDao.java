package app.socketiot.server.core.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import app.socketiot.server.core.db.DB;
import app.socketiot.server.core.json.JsonParser;
import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.auth.UserJson;

public class UserDBDao {
    private final DB db;
    private final static Logger log = LogManager.getLogger(UserDBDao.class);

    public UserDBDao(DB db) {
        this.db = db;
    }

    public ConcurrentMap<String, User> getAllUsers() {
        ConcurrentMap<String, User> users = new ConcurrentHashMap<>();
        try (Connection connection = db.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.put(rs.getString("email"),
                            new User(rs.getString("email"), rs.getString("password"), rs.getString("token"),
                                    JsonParser.parse(UserJson.class, rs.getString("json"))));
                }
            }
        } catch (Exception e) {
            log.debug("Error while getting all users. Cause", e.getMessage(), e);
        }

        return users;
    }

    public void saveAllUsers(ArrayList<User> users) {
        try (Connection connection = db.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO users (email, password, token, json) VALUES (?, ?, ?, ?) ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password, token = EXCLUDED.token, json = EXCLUDED.json");
            for (User user : users) {
                stmt.setString(1, user.email);
                stmt.setString(2, user.password);
                stmt.setString(3, user.token);
                stmt.setString(4, JsonParser.toJson(user.json));
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (Exception e) {
            log.debug("Error while saving all users. Cause: {}", e.getMessage(), e);
        }
    }

}
