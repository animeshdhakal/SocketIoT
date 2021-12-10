package animesh.app.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.TimeUnit;

import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class TcpServer extends ChannelInboundHandlerAdapter {

    public static ChannelFuture start(EventLoopGroup bossGroup, EventLoopGroup workerGroup, int PORT) throws Exception {

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                // .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        SslHandler sslHandler = SSLHandlerProvider.getSslHandler();
                        pipeline.addLast(sslHandler);
                        pipeline.addLast(new IdleStateHandler(10, 0, 0, TimeUnit.SECONDS));
                        pipeline.addLast(new ClientHandler(false));
                    }
                }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

        ChannelFuture f = b.bind(PORT).sync();

        System.out.println("TCP Server Started at PORT " + PORT);

        return f;

    }

}
