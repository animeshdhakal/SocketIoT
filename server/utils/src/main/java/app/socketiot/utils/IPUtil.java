package app.socketiot.utils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class IPUtil {
    public static String getIP(SocketAddress addr) {
        return ((InetSocketAddress) addr).getAddress().getHostAddress();
    }
}