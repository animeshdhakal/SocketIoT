package animesh.app.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import animesh.app.server.Logger;

public class MainDB {
    public static Connection conn = null;

    public static void init(String dbName, String user, String password) {

        try {
            Class.forName("org.postgresql.Driver");

            conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/" + dbName + "?tcpKeepAlive=true&socketTimeout=150", user,
                    password);

            Logger.info("Connected to DB");

            Statement stmt = conn.createStatement();

            stmt.executeUpdate(new String(MainDB.class.getResourceAsStream("/create_query.psql").readAllBytes()));

            stmt.close();

        } catch (Exception e) {
            Logger.error("DB Connection failed " + e.getMessage());
        }

    }

    public static boolean available() {
        return conn != null;
    }
}
