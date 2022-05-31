package app.socketiot.server.servers;

import app.socketiot.server.AppLoginHandler;
import app.socketiot.server.AppStateHandler;
import app.socketiot.server.Holder;
import app.socketiot.server.hardware.HardwareLoginHandler;
import app.socketiot.server.hardware.HardwareStateHandler;
import app.socketiot.server.http.core.handlers.FileUploadHandler;
import app.socketiot.server.http.core.handlers.ReactHandler;
import app.socketiot.server.http.core.handlers.StaticFileHandler;
import app.socketiot.server.http.handlers.TestHttpHandler;
import app.socketiot.server.internal.codec.MessageDecoder;
import app.socketiot.server.internal.codec.MessageEncoder;
import app.socketiot.server.internal.codec.WSMessageDecoder;
import app.socketiot.server.internal.codec.WSMessageEncoder;
import app.socketiot.server.internal.protocol.ProtocolDetector;
import app.socketiot.server.internal.protocol.ProtocolMerger;
import app.socketiot.utils.NumberUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerKeepAliveHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class SocketIoTServer extends ServerBase {
    private final ChannelInitializer<SocketChannel> initializer;

    public SocketIoTServer(Holder holder) {
        super(holder, holder.props.getProperty("server.http.host"),
                holder.props.getIntProperty("server.http.port", 4444));

        String appWSPath = "/appws";
        String hardwareWSPath = "/ws";

        int hardwareHeartbeat = NumberUtil.calcHeartBeat(holder.defaults.hardwareIdleTimeout);

        var appLoginHandler = new AppLoginHandler(holder);
        var appStateHandler = new AppStateHandler();

        var hardwareLoginHandler = new HardwareLoginHandler(holder);
        var harwareStateHandler = new HardwareStateHandler();

        var staticFileHandler = new StaticFileHandler(holder.isUnpacked, holder.jarPath, "/static");
        var reactHandler = new ReactHandler(holder.isUnpacked, holder.jarPath, "/static/index.html");
        var fileUploadHandler = new FileUploadHandler(holder.jarPath, "/api/upload", "/static");

        ProtocolMerger protocolMerger = new ProtocolMerger() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                String uri = ((HttpRequest) msg).uri();

                if (uri.startsWith(hardwareWSPath)) {
                    initHardwareWSPipeline(ctx);
                } else if (uri.startsWith(appWSPath)) {
                    initAppWSPipeline(ctx);
                } else {
                    initHttpPipeline(ctx);
                }

                ctx.fireChannelRead(msg);
            }

            public void initHttpPipeline(ChannelHandlerContext ctx) {
                ChannelPipeline pipeline = ctx.pipeline();
                pipeline.addLast(new TestHttpHandler());
                pipeline.addLast(staticFileHandler);
                pipeline.addLast(fileUploadHandler);
                pipeline.addLast(reactHandler);
                pipeline.remove(this);
            }

            public void initAppWSPipeline(ChannelHandlerContext ctx) {
                ChannelPipeline pipeline = ctx.pipeline();
                pipeline.addFirst(new IdleStateHandler(holder.defaults.appIdleTimeout, 0, 0));
                pipeline.addLast(appStateHandler);
                pipeline.addLast(new WebSocketServerProtocolHandler(appWSPath, null, true));
                pipeline.addLast(new WSMessageDecoder());
                pipeline.addLast(new MessageDecoder(holder.defaults.quotaLimit));
                pipeline.addLast(new WSMessageEncoder());
                pipeline.addLast(new MessageEncoder());
                pipeline.addLast(appLoginHandler);
                pipeline.remove(ChunkedWriteHandler.class);
                pipeline.remove(this);

            }

            public void initHardwareWSPipeline(ChannelHandlerContext ctx) {
                ChannelPipeline pipeline = ctx.pipeline();
                pipeline.addFirst(new IdleStateHandler(hardwareHeartbeat, 0, 0));
                pipeline.addLast(harwareStateHandler);
                pipeline.addLast(new WebSocketServerProtocolHandler(appWSPath, null, true));
                pipeline.addLast(new WSMessageDecoder());
                pipeline.addLast(new MessageDecoder(holder.defaults.quotaLimit));
                pipeline.addLast(new WSMessageEncoder());
                pipeline.addLast(new MessageEncoder());
                pipeline.addLast(hardwareLoginHandler);
                pipeline.remove(ChunkedWriteHandler.class);
                pipeline.remove(this);
            }

        };

        this.initializer = new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast(new ProtocolDetector() {
                    @Override
                    public ChannelPipeline buildHttpPipeline(ChannelPipeline pipeline) {
                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new HttpServerKeepAliveHandler());
                        pipeline.addLast(new HttpObjectAggregator(512 * 1024, true));
                        pipeline.addLast(new ChunkedWriteHandler());
                        pipeline.addLast(protocolMerger);
                        return pipeline;
                    }

                    @Override
                    public ChannelPipeline buildHardwarePipeline(ChannelPipeline pipeline) {
                        pipeline.addFirst(new IdleStateHandler(hardwareHeartbeat, 0, 0));
                        pipeline.addLast(harwareStateHandler);
                        pipeline.addLast(new MessageDecoder(holder.defaults.quotaLimit));
                        pipeline.addLast(new MessageEncoder());
                        pipeline.addLast(hardwareLoginHandler);
                        return pipeline;
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
        return "SocketIoTServer";
    }
}
