package app.socketiot.server.servers;

import app.socketiot.server.api.BluePrintApiHandler;
import app.socketiot.server.api.DeviceApiHandler;
import app.socketiot.server.api.FileUploadHandler;
import app.socketiot.server.api.GoogleAssistantHandler;
import app.socketiot.server.api.LetsEncryptHandler;
import app.socketiot.server.api.OTAHandler;
import app.socketiot.server.api.PinApiHandler;
import app.socketiot.server.api.ReactHandler;
import app.socketiot.server.api.UserApiHandler;
import app.socketiot.server.api.WidgetApiHandler;
import app.socketiot.server.app.AppLoginHandler;
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
        String webSocketPath = "/ws";
        String appPath = "/appws";

        var staticFileHandler = new StaticFileHandler(holder, "/static");
        var userApiHandler = new UserApiHandler(holder);
        var bluePrintApiHandler = new BluePrintApiHandler(holder);
        var deviceApiHandler = new DeviceApiHandler(holder);
        var pinApiHandler = new PinApiHandler(holder);
        var widgetApiHandler = new WidgetApiHandler(holder);
        var otaHandler = new OTAHandler(holder);
        var fileUploadHandler = new FileUploadHandler(holder.jarPath, "/api/upload", "/static");
        var letsEncryptHandler = new LetsEncryptHandler(holder.sslprovider.acmeClient);
        var reactHandler = new ReactHandler(holder, "/static/index.html");
        var googleassistantHandler = new GoogleAssistantHandler(holder);

        var hardwareLoginHandler = new HardwareLoginHandler(holder);
        var appLoginHandler = new AppLoginHandler(holder);

        WebSocketMerger webSocketMerger = new WebSocketMerger() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                String uri = ((HttpRequest) msg).uri();
                if (uri.startsWith(webSocketPath)) {
                    initWebSocketPipeline(ctx);
                } else if (uri.startsWith(appPath)) {
                    initAppWebSocketPipeline(ctx);
                } else {
                    initHttpPipeline(ctx);
                }

                ctx.fireChannelRead(msg);
            }

            public void initHttpPipeline(ChannelHandlerContext ctx) {
                ChannelPipeline pipeline = ctx.pipeline();
                pipeline.addLast(staticFileHandler);
                pipeline.addLast(userApiHandler);
                pipeline.addLast(deviceApiHandler);
                pipeline.addLast(bluePrintApiHandler);
                pipeline.addLast(widgetApiHandler);
                pipeline.addLast(pinApiHandler);
                pipeline.addLast(otaHandler);
                pipeline.addLast(fileUploadHandler);
                pipeline.addLast(googleassistantHandler);
                pipeline.addLast(letsEncryptHandler);
                pipeline.addLast(reactHandler);
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
                pipeline.addLast(hardwareLoginHandler);
                pipeline.remove(ChunkedWriteHandler.class);
                pipeline.remove(this);
            }

            public void initAppWebSocketPipeline(ChannelHandlerContext ctx) {
                ChannelPipeline pipeline = ctx.pipeline();
                pipeline.addFirst(new IdleStateHandler(hardwareIdleTimeout, 0, 0));
                pipeline.addLast(new WebSocketServerProtocolHandler(appPath, null, true));
                pipeline.addLast(new WebSocketHandler());
                pipeline.addLast(new HardwareMessageDecoder(quotaLimit));
                pipeline.addLast(new WSEncoder());
                pipeline.addLast(new HardwareMessageEncoder());
                pipeline.addLast(appLoginHandler);
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
                        p.addLast(hardwareLoginHandler);
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
