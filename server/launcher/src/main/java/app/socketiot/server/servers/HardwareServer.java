package app.socketiot.server.servers;

import app.socketiot.server.core.Holder;
import app.socketiot.server.hardware.HardwareHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;

public class HardwareServer extends BaseServer {
    private final ChannelInitializer<SocketChannel> initializer;

    public HardwareServer(final Holder holder) {
        super(holder, null, 2222);

        this.initializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                SslContext sslCtx = holder.sslprovider.getSslCtx();
                if (sslCtx != null) {
                    p.addLast(sslCtx.newHandler(ch.alloc()));
                }
                p.addLast(new HardwareHandler(holder));
            }

        };
    }

    @Override
    public ChannelInitializer<SocketChannel> getInitializer() {
        return initializer;
    }

    @Override
    public String getServerName() {
        return "HardwareServer";
    }
}
