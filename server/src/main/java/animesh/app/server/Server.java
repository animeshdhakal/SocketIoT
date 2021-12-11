package animesh.app.server;

import java.util.Properties;
import java.io.FileReader;
import java.io.IOException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class Server {
    public static void main(String[] args) {
        ArgParser argParser = new ArgParser(args);
        Properties props = new Properties();

        try {
            FileReader fileReader = new FileReader("server.properties");
            props.load(fileReader);
        } catch (IOException e) {
        }

        if (argParser.hasArg("-ssl")) {
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
            f.channel().closeFuture().sync();

        } catch (Exception e) {
            System.out.println("Exception from Server Main: " + e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}
