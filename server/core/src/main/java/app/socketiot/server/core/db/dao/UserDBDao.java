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
                            new User(rs.getString("email"), rs.getString("password")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }


    public void saveAllUsers(ArrayList<User> users) {
        try(Connection connection = holder.db.getConnection()){
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO users (email, password) VALUES (?, ?) ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password");
            for (User user : users) {
                stmt.setString(1, user.email);
                stmt.setString(2, user.password);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}