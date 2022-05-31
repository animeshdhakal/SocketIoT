package app.socketiot.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import app.socketiot.server.cli.ArgParser;
import app.socketiot.server.cli.properties.ServerProperties;
import app.socketiot.server.core.dao.BluePrintDao;
import app.socketiot.server.core.dao.DeviceDao;
import app.socketiot.server.core.dao.UserDao;
import app.socketiot.server.db.DB;
import app.socketiot.server.db.dao.UserDBDao;
import app.socketiot.utils.JarUtil;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Holder {
    public static final Logger log = LogManager.getLogger(Holder.class);
    public final ArgParser argParser;
    public final ServerProperties props;
    public final EventLoopGroup bossGroup;
    public final EventLoopGroup workerGroup;
    public final Class<? extends ServerChannel> channelClass;
    public final DB db;
    public final UserDBDao userDBDao;
    public final UserDao userDao;
    public final DeviceDao deviceDao;
    public final BluePrintDao bluePrintDao;
    public final Defaults defaults;
    public final boolean isUnpacked;
    public final String jarPath;

    public Holder(ArgParser argParser, ServerProperties props) {
        this.argParser = argParser;
        this.props = props;
        this.defaults = new Defaults(props);

        if (Epoll.isAvailable()) {
            log.info("Using Native Epoll Transport.");
            bossGroup = new EpollEventLoopGroup(1);
            workerGroup = new EpollEventLoopGroup(defaults.workerThreads);
            channelClass = EpollServerSocketChannel.class;
        } else {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup(defaults.workerThreads);
            channelClass = NioServerSocketChannel.class;
        }

        this.db = new DB(props);
        this.userDBDao = new UserDBDao(db);
        this.userDao = new UserDao(userDBDao.getAllUsers());
        this.deviceDao = new DeviceDao(userDao.getAllUsers());
        this.bluePrintDao = new BluePrintDao(userDao.getAllUsers());
        this.jarPath = JarUtil.getJarPath();
        this.isUnpacked = JarUtil.unpackStaticFiles(jarPath, "static");
    }

    public void close() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
