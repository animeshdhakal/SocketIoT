package app.socketiot.server;

import app.socketiot.server.cli.properties.ServerProperties;

public class Defaults {
    public final int workerThreads;
    public final int appIdleTimeout;
    public final int hardwareIdleTimeout;
    public final int quotaLimit;
    public final int dbSaveInterval;

    public Defaults(ServerProperties props) {
        this.workerThreads = props.getIntProperty("server.worker.threads", Runtime.getRuntime().availableProcessors());
        this.appIdleTimeout = props.getIntProperty("app.idle.timeout", 10);
        this.hardwareIdleTimeout = props.getIntProperty("hardware.idle.timeout", 400);
        this.quotaLimit = props.getIntProperty("quota.limit", 50);
        this.dbSaveInterval = props.getIntProperty("db.save.interval", 6000);
    }
}
