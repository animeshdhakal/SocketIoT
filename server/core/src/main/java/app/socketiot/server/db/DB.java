package app.socketiot.server.db;

import java.sql.Connection;
import java.sql.Statement;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import app.socketiot.server.cli.properties.ServerProperties;

public class DB {
    public static final Logger log = LogManager.getLogger(DB.class);
    private HikariDataSource ds;

    public DB(ServerProperties props) {
        HikariConfig config = new HikariConfig();
        String dbUsername = props.getProperty("db.username");
        String dbPassword = props.getProperty("db.password");
        String dbUrl = props.getProperty("db.url");

        if (dbUsername == null || dbPassword == null || dbUrl == null) {
            log.warn("Database credentials are not set.");
            return;
        }

        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setMaximumPoolSize(5);
        config.setMaxLifetime(0);

        try {
            Class.forName("org.postgresql.Driver");
            this.ds = new HikariDataSource(config);
        } catch (Exception e) {
            log.error("Unable to Connect to Database", e);
            System.exit(1);
        }

        try {
            Statement stmt = ds.getConnection().createStatement();
            stmt.execute(new String(this.getClass().getResourceAsStream("/create_table.psql").readAllBytes()));
            stmt.close();
        } catch (Exception e) {
            log.error("Unable to create table", e);
        }

        log.info("Connected to DB");
    }

    public Connection getConnection() throws Exception {
        return ds.getConnection();
    }
}
