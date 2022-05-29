package app.socketiot.server.core.model.enums;

public class MsgType {
    public static final short AUTH = 1;
    public static final short WRITE = 2;
    public static final short READ = 3;
    public static final short PING = 4;
    public static final short SYNC = 5;
    public static final short INFO = 6;
    public static final short SYS = 7;

    // For App Only
    public static final short FAILED = 60;
    public static final short REGISTER = 61;
    public static final short ADD_DEVICE = 62;
    public static final short REMOVE_DEVICE = 63;
    public static final short GET_DEVICE = 64;
    public static final short UPDATE_DEVICE = 65;
    public static final short GET_DEVICES_LIST = 66;

}