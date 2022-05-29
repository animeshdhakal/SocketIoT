package app.socketiot.server;

import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.enums.MsgType;
import app.socketiot.server.core.model.message.InternalMessage;
import app.socketiot.server.handlers.device.AddDeviceHandler;
import app.socketiot.server.handlers.device.GetDeviceHandler;
import app.socketiot.server.handlers.device.GetDevicesListHandler;
import app.socketiot.server.handlers.device.RemoveDeviceHandler;
import app.socketiot.server.handlers.device.UpdateDeviceHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class AppHandler extends ChannelInboundHandlerAdapter {
    public final User user;

    public AppHandler(User user) {
        this.user = user;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof InternalMessage) {
            InternalMessage message = (InternalMessage) msg;

            switch (message.type) {
                case MsgType.ADD_DEVICE:
                    AddDeviceHandler.handleMessage(ctx, user, message);
                    break;
                case MsgType.GET_DEVICES_LIST:
                    GetDevicesListHandler.handleMessage(ctx, user, message);
                    break;
                case MsgType.REMOVE_DEVICE:
                    RemoveDeviceHandler.handleMessage(ctx, user, message);
                    break;
                case MsgType.GET_DEVICE:
                    GetDeviceHandler.handleMessage(ctx, user, message);
                    break;
                case MsgType.UPDATE_DEVICE:
                    UpdateDeviceHandler.handleMessage(ctx, user, message);
                    break;
            }

        }
    }
}
