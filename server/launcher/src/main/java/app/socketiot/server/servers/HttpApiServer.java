package app.socketiot.server.servers;

import app.socketiot.server.api.BluePrintApiHandler;
import app.socketiot.server.api.DeviceApiHandler;
import app.socketiot.server.api.FileUploadHandler;
import app.socketiot.server.api.LetsEncryptHandler;
import app.socketiot.server.api.OTAHandler;
import app.socketiot.server.api.PinApiHandler;
import app.socketiot.server.api.ReactHandler;
import app.socketiot.server.api.UserApiHandler;
import app.socketiot.server.api.WidgetApiHandler;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.http.StaticFileHandler;
import app.socketiot.server.hardware.HardwareLoginHandler;
import app.socketiot.server.hardware.WebSocketHandler;
import app.socketiot.server.hardware.message.HardwareMessageDecoder;
import app.socketiot.server.hardware.message.HardwareMessageEncoder;
import app.socketiot.server.utils.NumberUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerKeepAliveHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class HttpApiServer extends BaseServer {
    private final ChannelInitializer<SocketChannel> initializer;

    public HttpApiServer(final Holder holder) {
        super(holder, holder.props.getProperty("server.http.host"),
                holder.props.getIntProperty("server.http.port", 4444));

        int hardwareIdleTimeout = NumberUtil
                .calculateHeartBeat(holder.props.getIntProperty("server.hardware.heartbeat", 10));
        int quotaLimit = holder.props.getIntProperty("server.hardware.quotalimit", 10);
        String webSocketPath = "/websocket";

        WebSocketMerger webSocketMerger = new WebSocketMerger() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                if (((HttpRequest) msg).uri().startsWith(webSocketPath)) {
                    initWebSocketPipeline(ctx);
                } else {
                    initHttpPipeline(ctx);
                }

                ctx.fireChannelRead(msg);
            }

            public void initHttpPipeline(ChannelHandlerContext ctx) {
                ChannelPipeline pipeline = ctx.pipeline();
                pipeline.addLast(new StaticFileHandler(holder, "/static"));
                pipeline.addLast(new UserApiHandler(holder));
                pipeline.addLast(new DeviceApiHandler(holder));
                pipeline.addLast(new BluePrintApiHandler(holder));
                pipeline.addLast(new WidgetApiHandler(holder));
                pipeline.addLast(new PinApiHandler(holder));
                pipeline.addLast(new OTAHandler(holder));
                pipeline.addLast(new FileUploadHandler(holder.jarPath, "/api/upload", "/static"));
                pipeline.addLast(new LetsEncryptHandler(holder.sslprovider.acmeClient));
                pipeline.addLast(new ReactHandler(holder, "/static/index.html"));
                pipeline.remove(this);
            }

            public void initWebSocketPipeline(ChannelHandlerContext ctx) {
                ChannelPipeline pipeline = ctx.pipeline();
                pipeline.addFirst(new IdleStateHandler(hardwareIdleTimeout, 0, 0));
                pipeline.addLast(new WebSocketServerProtocolHandler(webSocketPath, null, true));
                pipeline.addLast(new WebSocketHandler());
                pipeline.addLast(new HardwareMessageDecoder(quotaLimit));
                pipeline.addLast(new WSEncoder());
                pipeline.addLast(new HardwareMessageEncoder());
                pipeline.addLast(new HardwareLoginHandler(holder));
                pipeline.remove(ChunkedWriteHandler.class);
                pipeline.remove(this);
            }

        };

        this.initializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                SslContext sslCtx = holder.sslprovider.getSslCtx();
                if (sslCtx != null) {
                    p.addLast(sslCtx.newHandler(ch.alloc()));
                }
                p.addLast(new ProtocolDetector() {
                    @Override
                    public ChannelPipeline buildHttpPipeline(ChannelPipeline p) {
                        p.addLast(new HttpServerCodec());
                        p.addLast(new HttpServerKeepAliveHandler());
                        p.addLast(new HttpObjectAggregator(512 * 1024, true));
                        p.addLast(new ChunkedWriteHandler());
                        p.addLast(webSocketMerger);
                        return p;
                    }

                    @Override
                    public ChannelPipeline buildHardwarePipeline(ChannelPipeline p) {
                        p.addFirst(new IdleStateHandler(hardwareIdleTimeout, 0, 0));
                        p.addLast(new HardwareMessageDecoder(quotaLimit));
                        p.addLast(new HardwareMessageEncoder());
                        p.addLast(new HardwareLoginHandler(holder));
                        return p;
                    }

                });

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
