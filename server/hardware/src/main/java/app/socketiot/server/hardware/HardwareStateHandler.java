package app.socketiot.server.hardware;

import app.socketiot.server.core.model.enums.DeviceStatus;
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
        HardwareHandler hardwareHandler = ctx.pipeline().get(HardwareHandler.class);
        if (hardwareHandler != null) {
            hardwareHandler.userDevice.device.status = DeviceStatus.Offline;
            hardwareHandler.userDevice.user.lastModified = System.currentTimeMillis();
        }
    }
}
