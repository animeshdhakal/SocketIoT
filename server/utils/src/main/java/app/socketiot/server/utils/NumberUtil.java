package app.socketiot.server.utils;

public class NumberUtil {
    public static int calculateHeartBeat(int heartBeat) {
        return heartBeat * 2;
    }

    public static short parsePin(String pin) {
        try {
            return Short.parseShort(pin);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
