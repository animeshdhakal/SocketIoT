package app.socketiot.server.core.statebase;

import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.device.Device;
import io.netty.channel.ChannelInboundHandlerAdapter;

public abstract class HardwareStateBase extends ChannelInboundHandlerAdapter {
    abstract public Device getDevice();

    abstract public User getUser();
}
