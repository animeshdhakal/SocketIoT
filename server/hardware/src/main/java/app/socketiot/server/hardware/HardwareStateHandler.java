package app.socketiot.server.hardware;

import app.socketiot.server.core.model.enums.DeviceStatus;
import app.socketiot.server.core.model.statebase.HardwareStateBase;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class HardwareStateHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        HardwareStateBase stateBase = ctx.pipeline().get(HardwareStateBase.class);
        if (stateBase != null) {
            // TODO: Update Status of Device to the App
            stateBase.getDevice().status = DeviceStatus.Offline;
            stateBase.getUser().lastModified = System.currentTimeMillis();
        }
    }
}
