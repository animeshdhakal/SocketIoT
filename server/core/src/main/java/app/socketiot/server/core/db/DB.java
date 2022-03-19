package app.socketiot.server.core.db;

import java.sql.Connection;
import java.sql.Statement;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import app.socketiot.server.core.Holder;

public class DB {
    private final HikariDataSource ds;
    private final static Logger log = LogManager.getLogger(DB.class);

    public DB(Holder holder) {
        HikariConfig config = new HikariConfig();
        config.setUsername(holder.props.getProperty("db.username"));
        config.setPassword(holder.props.getProperty("db.password"));
        config.setJdbcUrl(holder.props.getProperty("db.url"));
        config.setMaximumPoolSize(5);
        config.setMaxLifetime(0);

        HikariDataSource dataSource;
        try {
            Class.forName("org.postgresql.Driver");
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            log.error("Unable to Connect to DB : {}", e.getMessage());
            this.ds = null;
            return;
        }

        this.ds = dataSource;

        log.info("Connected to DB");

        try {
            Statement stmt = dataSource.getConnection().createStatement();
            stmt.execute(new String(this.getClass().getResourceAsStream("/create_query.psql").readAllBytes()));
            stmt.close();
        } catch (Exception e) {
            log.error("Unable to create table : {}", e.getMessage());
        }
    }

    public Connection getConnection() throws Exception {
        return this.ds.getConnection();
    }

}
