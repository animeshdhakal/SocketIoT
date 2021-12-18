package animesh.app.server;

import java.util.Properties;
import animesh.app.server.db.MainDB;
import java.io.FileReader;
import java.io.IOException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class Server {
    public static void main(String[] args) {
        ArgParser argParser = new ArgParser(args);
        Properties props = new Properties();

        LoggerUtil.logger.info("Starting Server...");

        MainDB.init("animeshdhakal", "animeshdhakal", "animeshdhakal");

        try {
            FileReader fileReader = new FileReader("server.properties");
            props.load(fileReader);
        } catch (IOException e) {
        }

        if (argParser.hasArg("-ssl")) {
            LoggerUtil.logger.info("Initing SSL...");
            SSLHandlerProvider.init(props.getProperty("ssl.cert", ""), props.getProperty("ssl.key", ""),
                    props.getProperty("ssl.key.pass", ""));
        }

        int tcpPort = Integer.parseInt(props.getProperty("tcp.port", "8080"));
        int httpPort = Integer.parseInt(props.getProperty("http.port", "8081"));

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ChannelFuture f;
            f = TcpServer.start(bossGroup, workerGroup, tcpPort);
            f = WebSocketServer.start(bossGroup, workerGroup, httpPort);
            System.out.println("Server started");
            f.channel().closeFuture().sync();

        } catch (Exception e) {
            LoggerUtil.logger.error("Exception from Server Main: " + e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}
