package app.socketiot.server.hardware;

import app.socketiot.server.core.model.device.UserDevice;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class HardwareHandler extends ChannelInboundHandlerAdapter {
    public final UserDevice userDevice;

    public HardwareHandler(UserDevice userDevice) {
        this.userDevice = userDevice;
    }
}
