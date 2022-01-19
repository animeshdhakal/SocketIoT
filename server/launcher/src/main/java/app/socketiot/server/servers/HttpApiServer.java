package app.socketiot.server.servers;

import app.socketiot.server.api.HttpApi;
import app.socketiot.server.api.UserApiHandler;
import app.socketiot.server.core.Holder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

public class HttpApiServer extends BaseServer {
    private final ChannelInitializer<SocketChannel> initializer;

    public HttpApiServer(final Holder holder) {
        super(holder, 4444);

        this.initializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                SslContext sslCtx = holder.sslprovider.getSslCtx();
                if (sslCtx != null) {
                    p.addLast(sslCtx.newHandler(ch.alloc()));
                }
                p.addLast(new HttpServerCodec());
                p.addLast(new HttpObjectAggregator(512 * 1024));
                p.addLast(new HttpApi(holder));
                p.addLast(new UserApiHandler(holder));
            }

        };
    }

    @Override
    public ChannelInitializer<SocketChannel> getInitializer() {
        return initializer;
    }

    @Override
    public String getServerName() {
        return "HttpApiServer";
    }
}
