package app.socketiot.server;

import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.enums.MsgType;
import app.socketiot.server.core.model.message.InternalMessage;
import app.socketiot.server.exceptions.ExceptionHandler;
import app.socketiot.server.handlers.blueprint.AddWidgetsBluePrintHandler;
import app.socketiot.server.handlers.blueprint.CreateBluePrintHandler;
import app.socketiot.server.handlers.blueprint.DeleteBluePrintHandler;
import app.socketiot.server.handlers.blueprint.GetBluePrintsListHandler;
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
    public final Holder holder;

    public AppHandler(Holder holder, User user) {
        this.user = user;
        this.holder = holder;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        InternalMessage message = (InternalMessage) msg;

        switch (message.type) {
            case MsgType.ADD_DEVICE:
                AddDeviceHandler.handleMessage(holder, ctx, user, message);
                break;
            case MsgType.GET_DEVICES_LIST:
                GetDevicesListHandler.handleMessage(ctx, user, message);
                break;
            case MsgType.REMOVE_DEVICE:
                RemoveDeviceHandler.handleMessage(holder.deviceDao, ctx, user, message);
                break;
            case MsgType.GET_DEVICE:
                GetDeviceHandler.handleMessage(ctx, user, message);
                break;
            case MsgType.UPDATE_DEVICE:
                UpdateDeviceHandler.handleMessage(ctx, user, message);
                break;
            case MsgType.CREATE_BLUEPRINT:
                CreateBluePrintHandler.handleMessage(holder.bluePrintDao, ctx, user, message);
                break;
            case MsgType.DELETE_BLUEPRINT:
                DeleteBluePrintHandler.handleMessage(holder.bluePrintDao, ctx, user, message);
                break;
            case MsgType.GET_BLUEPRINTS_LIST:
                GetBluePrintsListHandler.handleMessage(ctx, user, message);
                break;
            case MsgType.ADD_WIDGETS_BLUEPRINT:
                AddWidgetsBluePrintHandler.handleMessage(holder.deviceDao, ctx, user, message);
                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ExceptionHandler.handleException(ctx, cause);
    }
}
