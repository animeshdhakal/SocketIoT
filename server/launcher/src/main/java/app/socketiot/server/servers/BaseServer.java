package app.socketiot.server.servers;

import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import app.socketiot.server.core.Holder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public abstract class BaseServer {
    private static Logger log = LogManager.getLogger();
    private final int port;
    private final Holder holder;
    private final String host;
    ChannelFuture cf;

    public BaseServer(Holder holder, String host, int port) {
        this.port = port;
        this.holder = holder;
        this.host = host;
    }

    public void start() throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(holder.bossGroup, holder.workerGroup)
                    .channel(holder.channelClass)
                    .childHandler(getInitializer());
            InetSocketAddress addr = (host == null || host.isEmpty()) ? new InetSocketAddress(port)
                    : new InetSocketAddress(host, port);
            this.cf = bootstrap.bind(addr).sync();
            log.info("{} started at port {}", getServerName(), port);
        } catch (Exception e) {
            log.error("Error initializing {} at Port {}", getServerName(), port);
            throw e;
        }

    }

    public abstract ChannelInitializer<SocketChannel> getInitializer();

    public abstract String getServerName();

    public ChannelFuture close() {
        return cf.channel().close();
    }
}