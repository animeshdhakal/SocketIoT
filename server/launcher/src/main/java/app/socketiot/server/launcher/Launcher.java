package app.socketiot.server.launcher;

import java.net.BindException;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import app.socketiot.server.Holder;
import app.socketiot.server.cli.ArgParser;
import app.socketiot.server.cli.properties.ServerProperties;
import app.socketiot.server.servers.ServerBase;
import app.socketiot.server.servers.SocketIoTServer;
import app.socketiot.server.utils.LoggerUtil;

public class Launcher {

    public static void main(String[] args) {
        ArgParser argParser = new ArgParser(args);

        ServerProperties props = new ServerProperties();

        LoggerUtil.configureLogger(argParser, props);

        Security.addProvider(new BouncyCastleProvider());

        Holder holder = new Holder(argParser, props);

        ServerBase[] servers = new ServerBase[] {
                new SocketIoTServer(holder)
        };

        for (ServerBase server : servers) {
            try {
                server.start();
            } catch (BindException exception) {
                System.err.println("Port Already In Use");
                return;
            } catch (Exception exception) {
                System.err.println("Error Starting Server");
                return;
            }
        }

        WorkerLauncher.launch(holder, servers);

        System.out.println("Server Started");
    }

}
