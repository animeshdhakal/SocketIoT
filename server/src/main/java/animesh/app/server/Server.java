package animesh.app.server;

import java.util.Arrays;
import java.util.List;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class Server {
    public static void main(String[] args) {
        List<String> arguments = Arrays.asList(args);

        if (arguments.contains("-ssl")) {
            SSLHandlerProvider.init();
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ChannelFuture f;
            f = TcpServer.start(bossGroup, workerGroup);
            f = WebSocketServer.start(bossGroup, workerGroup);
            f.channel().closeFuture().sync();

        } catch (Exception e) {
            System.out.println("Exception from Server Main: " + e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}
