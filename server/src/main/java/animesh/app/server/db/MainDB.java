package animesh.app.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import animesh.app.server.Logger;

public class MainDB {
    public static Connection conn = null;

    static String createTableQuery = ""
            + "CREATE TABLE IF NOT EXISTS users (\n"
            + "    id SERIAL PRIMARY KEY,\n"
            + "    email VARCHAR(255) NOT NULL,\n"
            + "    password VARCHAR(255) NOT NULL\n"
            + "); \n"
            + "CREATE TABLE IF NOT EXISTS projects (\n"
            + "    id SERIAL PRIMARY KEY,\n"
            + "    name VARCHAR(255) NOT NULL,\n"
            + "    token VARCHAR(255) NOT NULL,\n"
            + "    user_id INTEGER NOT NULL,\n"
            + "    json text \n"
            + ");";

    public static void init(String dbName, String user, String password) {
        try {
            Class.forName("org.postgresql.Driver");

            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + dbName, user,
                    password);

            Logger.info("Connected to DB");

            Statement stmt = conn.createStatement();
            stmt.executeUpdate(createTableQuery);
            stmt.close();

        } catch (Exception e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
        }

    }

    public static boolean available() {
        return conn != null;
    }
}
