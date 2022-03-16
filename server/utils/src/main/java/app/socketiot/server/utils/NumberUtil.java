package app.socketiot.server.utils;

public class NumberUtil {
    public static int calculateHeartBeat(int heartBeat) {
        return heartBeat * 2;
    }

    public static short parsePin(String pin) {
        return Short.parseShort(pin);
    }
}
