package app.socketiot.server.core.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.db.model.BluePrint;

public class BluePrintDBDao {
    private final Holder holder;

    public BluePrintDBDao(Holder holder) {
        this.holder = holder;
    }

    public ConcurrentHashMap<String, BluePrint> getAllBluePrints(){
        ConcurrentHashMap<String, BluePrint> bluePrints = new ConcurrentHashMap<>();

        try(Connection connection = holder.db.getConnection()){
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM blueprints");
            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    bluePrints.put(rs.getString("blueprint_id"),
                            new BluePrint(rs.getString("name"), rs.getString("email"), rs.getString("blueprint_id"), rs.getString("json")));
                }
            }
        }catch(Exception e){
        }
        return bluePrints;
    }

    public void saveAllBluePrints(ArrayList<BluePrint> blueprints){
        try(Connection connection = holder.db.getConnection()){
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO blueprints (name, user_id, blueprint_id, json) VALUES (?, ?, ?, ?) ON CONFLICT (blueprint_id) DO UPDATE SET name = EXCLUDED.name, user_id = EXCLUDED.user_id, json = EXCLUDED.json");
            for(BluePrint bluePrint : blueprints){
                stmt.setString(1, bluePrint.name);
                stmt.setString(2, bluePrint.email);
                stmt.setString(3, bluePrint.blueprint_id);
                stmt.setString(4, bluePrint.json);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
