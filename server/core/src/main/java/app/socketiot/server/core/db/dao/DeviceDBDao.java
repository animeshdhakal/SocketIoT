package app.socketiot.server.core.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import app.socketiot.server.core.db.DB;
import app.socketiot.server.core.json.JsonParser;
import app.socketiot.server.core.json.model.DeviceJson;
import app.socketiot.server.core.model.device.Device;

public class DeviceDBDao {
    private final DB db;

    public DeviceDBDao(DB db) {
        this.db = db;
    }

    public ConcurrentMap<String, Device> getAllDevices() {
        ConcurrentMap<String, Device> devices = new ConcurrentHashMap<>();
        try (Connection connection = db.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM devices");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    devices.put(rs.getString("token"),
                            new Device(rs.getString("name"), rs.getString("email"),
                                    rs.getString("blueprint_id"),
                                    rs.getString("token"), rs.getInt("id"),
                                    JsonParser.parse(DeviceJson.class, rs.getString("json"))));
                }
            }
        } catch (Exception e) {
        }
        return devices;
    }

    public void saveAllDevices(ArrayList<Device> devices) {
        try (Connection connection = db.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO devices (name, email, blueprint_id, token, id, json) VALUES (?, ?, ?, ?, ? ?) ON CONFLICT (token) DO UPDATE SET name = EXCLUDED.name, email = EXCLUDED.email, blueprint_id = EXCLUDED.blueprint_id, json = EXCLUDED.json");
            for (Device device : devices) {
                stmt.setString(1, device.name);
                stmt.setString(2, device.email);
                stmt.setString(3, device.blueprint_id);
                stmt.setString(4, device.token);
                stmt.setInt(5, device.id);
                stmt.setString(6, JsonParser.toString(device.json));
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (Exception e) {
        }
    }

    public void removeDevice(String token) {
        try (Connection connection = db.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM devices WHERE token = ?");
            stmt.setString(1, token);
            stmt.execute();
        } catch (Exception e) {

        }
    }
}
