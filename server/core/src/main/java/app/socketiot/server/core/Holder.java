package app.socketiot.server.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import app.socketiot.server.core.cli.ArgParser;
import app.socketiot.server.core.cli.properties.ServerProperties;
import app.socketiot.server.core.dao.BluePrintDao;
import app.socketiot.server.core.dao.DeviceDao;
import app.socketiot.server.core.dao.UserDao;
import app.socketiot.server.core.db.DB;
import app.socketiot.server.core.db.dao.BluePrintDBDao;
import app.socketiot.server.core.db.dao.DeviceDBDao;
import app.socketiot.server.core.db.dao.UserDBDao;
import app.socketiot.server.core.mail.Mail;
import app.socketiot.server.core.notification.FCMNotification;
import app.socketiot.server.utils.JarUtil;
import app.socketiot.server.utils.JwtUtil;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Holder {
    private static Logger log = LogManager.getLogger();
    public final ServerProperties props;
    public final EventLoopGroup bossGroup;
    public final EventLoopGroup workerGroup;
    public final Class<? extends ServerChannel> channelClass;
    public final ArgParser args;
    public final SSLHandlerProvider sslprovider;
    public final DB db;
    public final UserDBDao userDBDao;
    public final UserDao userDao;
    public final DeviceDBDao deviceDBDao;
    public final DeviceDao deviceDao;
    public final BluePrintDao bluePrintDao;
    public final BluePrintDBDao bluePrintDBDao;
    public final BlockingIOHandler blockingIOHandler;
    public final JwtUtil jwtUtil;
    public final Mail mail;
    public final AsyncHttpClient httpClient;
    public final FCMNotification notification;
    public final boolean isUnpacked;
    public final String jarPath;

    public Holder(ArgParser args, ServerProperties props) {
        this.args = args;
        this.props = props;
        int workerThreads = props.getIntProperty("server.worker.threads",
                Runtime.getRuntime().availableProcessors() * 2);
        if (Epoll.isAvailable()) {
            log.info("Using native epoll transport.");
            bossGroup = new EpollEventLoopGroup(1);
            workerGroup = new EpollEventLoopGroup(workerThreads);
            channelClass = EpollServerSocketChannel.class;
        } else {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup(workerThreads);
            channelClass = NioServerSocketChannel.class;
        }
        this.db = new DB(this);
        this.userDBDao = new UserDBDao(db);
        this.deviceDBDao = new DeviceDBDao(db);
        this.bluePrintDBDao = new BluePrintDBDao(db);
        this.userDao = new UserDao(userDBDao.getAllUsers());
        this.deviceDao = new DeviceDao(deviceDBDao.getAllDevices());
        this.bluePrintDao = new BluePrintDao(bluePrintDBDao.getAllBluePrints());
        this.blockingIOHandler = new BlockingIOHandler(
                props.getIntProperty("server.blocking.io.threads", 4));
        this.jwtUtil = new JwtUtil(props.getProperty("server.jwt.secret"));
        this.mail = new Mail(props, blockingIOHandler);
        this.sslprovider = new SSLHandlerProvider(this);
        this.httpClient = new DefaultAsyncHttpClient(new DefaultAsyncHttpClientConfig.Builder()
                .setUserAgent(null)
                .setKeepAlive(true)
                .setUseOpenSsl(SSLHandlerProvider.isOpenSslAvailable())
                .setUseNativeTransport(Epoll.isAvailable())
                .build());
        this.notification = new FCMNotification(props, httpClient);
        this.jarPath = JarUtil.getJarPath();
        this.isUnpacked = JarUtil.unpackStaticFiles(jarPath, "static");
    }

    public void close() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}
