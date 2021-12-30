package animesh.app.server.db.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import animesh.app.server.db.MainDB;
import animesh.app.server.db.model.Device;

public class DeviceDao {
    public ArrayList<Device> getAllDevices(int user_id) {
        ArrayList<Device> bluePrints = new ArrayList<Device>();
        if (MainDB.available()) {
            try {
                PreparedStatement stmt = MainDB.conn.prepareStatement("SELECT * FROM devices WHERE user_id = ?");
                stmt.setInt(1, user_id);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        bluePrints.add(new Device(rs.getString("name"), rs.getString("token"),
                                rs.getString("json"), rs.getString("blueprint_id"), rs.getInt("user_id")));
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
        return bluePrints;
    }

    public boolean addDevice(Device bluePrint) {
        if (MainDB.available()) {
            try {
                PreparedStatement stmt = MainDB.conn
                        .prepareStatement(
                                "INSERT INTO devices (name, user_id, blueprint_id, token, json) VALUES (?, ?, ?)");
                stmt.setString(1, bluePrint.name);
                stmt.setInt(2, bluePrint.user_id);
                stmt.setString(3, bluePrint.blueprint_id);
                stmt.setString(4, bluePrint.token);
                stmt.setString(4, bluePrint.json);

                return true;

            } catch (Exception e) {

            }
        }
        return false;
    }
}
