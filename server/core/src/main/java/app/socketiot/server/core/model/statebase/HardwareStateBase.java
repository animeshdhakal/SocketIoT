package app.socketiot.server.core.model.statebase;

import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.device.Device;
import io.netty.channel.ChannelInboundHandlerAdapter;

public abstract class HardwareStateBase extends ChannelInboundHandlerAdapter {
    public abstract User getUser();

    public abstract Device getDevice();
}
