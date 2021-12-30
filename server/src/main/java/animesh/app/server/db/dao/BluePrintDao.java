package animesh.app.server.db.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import animesh.app.server.db.MainDB;
import animesh.app.server.db.model.BluePrint;

public class BluePrintDao {
    public ArrayList<BluePrint> getAllBluePrint(int user_id) {
        ArrayList<BluePrint> bluePrints = new ArrayList<BluePrint>();
        if (MainDB.available()) {
            try {
                PreparedStatement stmt = MainDB.conn.prepareStatement("SELECT * FROM blueprints WHERE user_id = ?");
                stmt.setInt(1, user_id);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        bluePrints.add(new BluePrint(rs.getString("name"), rs.getString("blueprint_id"),
                                rs.getString("json"), rs.getInt("user_id")));
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
        return bluePrints;
    }

    public boolean createBluePrint(BluePrint bluePrint) {
        if (MainDB.available()) {
            try {
                PreparedStatement stmt = MainDB.conn
                        .prepareStatement(
                                "INSERT INTO blueprints (name, user_id, blueprint_id, json) VALUES (?, ?, ?)");
                stmt.setString(1, bluePrint.name);
                stmt.setInt(2, bluePrint.user_id);
                stmt.setString(3, bluePrint.blueprint_id);
                stmt.setString(4, bluePrint.json);

                return true;

            } catch (Exception e) {

            }
        }
        return false;
    }

}
