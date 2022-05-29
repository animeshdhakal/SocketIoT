package app.socketiot.server.servers;

import app.socketiot.server.AppLoginHandler;
import app.socketiot.server.Holder;
import app.socketiot.server.hardware.HardwareLoginHandler;
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
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
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
                pipeline.addLast(new TestHttpServer());
                pipeline.remove(this);
            }

            public void initAppWSPipeline(ChannelHandlerContext ctx) {
                ChannelPipeline pipeline = ctx.pipeline();
                pipeline.addFirst(new IdleStateHandler(holder.defaults.appIdleTimeout, 0, 0));
                pipeline.addLast(new WebSocketServerProtocolHandler(appWSPath, null, true));
                pipeline.addLast(new WSMessageDecoder());
                pipeline.addLast(new MessageDecoder(holder.defaults.quotaLimit));
                pipeline.addLast(new WSMessageEncoder());
                pipeline.addLast(new MessageEncoder());
                pipeline.addLast(appLoginHandler);
                pipeline.remove(this);

            }

            public void initHardwareWSPipeline(ChannelHandlerContext ctx) {
                ChannelPipeline pipeline = ctx.pipeline();
                pipeline.addFirst(new IdleStateHandler(hardwareHeartbeat, 0, 0));
                pipeline.addLast(new WebSocketServerProtocolHandler(appWSPath, null, true));
                pipeline.addLast(new WSMessageDecoder());
                pipeline.addLast(new MessageDecoder(holder.defaults.quotaLimit));
                pipeline.addLast(new WSMessageEncoder());
                pipeline.addLast(new MessageEncoder());
                pipeline.addLast(new HardwareLoginHandler());
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
                        pipeline.addLast(new HttpObjectAggregator(512 * 1024, true));
                        pipeline.addLast(protocolMerger);
                        return pipeline;
                    }

                    @Override
                    public ChannelPipeline buildHardwarePipeline(ChannelPipeline pipeline) {
                        pipeline.addFirst(new IdleStateHandler(hardwareHeartbeat, 0, 0));
                        pipeline.addLast(new MessageDecoder(holder.defaults.quotaLimit));
                        pipeline.addLast(new MessageEncoder());
                        pipeline.addLast(new HardwareLoginHandler());
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
