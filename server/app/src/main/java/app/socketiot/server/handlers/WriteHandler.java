package app.socketiot.server.handlers;

import app.socketiot.server.core.dao.DeviceDao;
import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.message.InternalMessage;
import io.netty.channel.ChannelHandlerContext;

public class WriteHandler {
    public static void handleMessage(DeviceDao deviceDao, ChannelHandlerContext ctx, User user,
            InternalMessage message) {

    }
}
