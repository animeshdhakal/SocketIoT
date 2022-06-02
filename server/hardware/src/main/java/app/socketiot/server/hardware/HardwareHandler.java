package app.socketiot.server.hardware;

import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.core.model.device.UserDevice;
import app.socketiot.server.core.model.enums.MsgType;
import app.socketiot.server.core.model.message.InternalMessage;
import app.socketiot.server.core.model.statebase.HardwareStateBase;
import app.socketiot.server.hardware.handlers.HardwareSyncHandler;
import app.socketiot.server.hardware.handlers.HardwareWriteHandler;
import io.netty.channel.ChannelHandlerContext;

public class HardwareHandler extends HardwareStateBase {
    private final UserDevice userDevice;

    public HardwareHandler(UserDevice userDevice) {
        this.userDevice = userDevice;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof InternalMessage) {
            InternalMessage message = (InternalMessage) msg;

            switch (message.type) {
                case MsgType.WRITE:
                    HardwareWriteHandler.handleMessage(ctx, userDevice, message);
                    return;
                case MsgType.SYNC:
                    HardwareSyncHandler.handleMessage(ctx, userDevice, message);
                    return;
            }
        }
    }

    @Override
    public User getUser() {
        return userDevice.user;
    }

    @Override
    public Device getDevice() {
        return userDevice.device;
    }
}
