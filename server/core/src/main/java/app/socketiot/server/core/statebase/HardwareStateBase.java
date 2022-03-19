package app.socketiot.server.core.statebase;

import app.socketiot.server.core.model.device.UserDevice;
import io.netty.channel.ChannelInboundHandlerAdapter;

public abstract class HardwareStateBase extends ChannelInboundHandlerAdapter {
    abstract public UserDevice getUserDevice();
}
