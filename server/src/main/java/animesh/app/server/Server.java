package animesh.app.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class Server {
    public static void main(String[] args) {
        ArgParser argParser = new ArgParser(args);

        if (argParser.hasArg("-ssl")) {
            SSLHandlerProvider.init();
        }

        int tcpPort = argParser.getInt("-tcp", 8080);
        int httpPort = argParser.getInt("-http", 8081);

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
