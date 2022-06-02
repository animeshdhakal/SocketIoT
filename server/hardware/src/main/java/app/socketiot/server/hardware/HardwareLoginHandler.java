package app.socketiot.server.hardware;

import app.socketiot.server.Holder;
import app.socketiot.server.core.model.device.UserDevice;
import app.socketiot.server.core.model.enums.DeviceStatus;
import app.socketiot.server.core.model.enums.MsgType;
import app.socketiot.server.core.model.message.InternalMessage;
import app.socketiot.utils.IPUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class HardwareLoginHandler extends ChannelInboundHandlerAdapter {
    public final Holder holder;

    public HardwareLoginHandler(Holder holder) {
        this.holder = holder;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        InternalMessage internalMessage = (InternalMessage) msg;

        if (internalMessage.type != MsgType.AUTH) {
            return;
        }

        String token = internalMessage.body[0];

        if (token == null || token.isEmpty()) {
            ctx.writeAndFlush(new InternalMessage(MsgType.AUTH, "0"));
            return;
        }

        UserDevice userDevice = holder.deviceDao.getUserDeviceByToken(token);

        if (userDevice == null) {
            ctx.writeAndFlush(new InternalMessage(MsgType.AUTH, "0"));
            return;
        }

        userDevice.device.lastIP = IPUtil.getIP(ctx.channel().remoteAddress());

        userDevice.device.lastOnline = System.currentTimeMillis();

        userDevice.device.status = DeviceStatus.Online;

        userDevice.user.lastModified = System.currentTimeMillis();

        // TODO: Send Status of Device to App

        ctx.pipeline().replace(this, "HardwareHandler", new HardwareHandler(userDevice));

        ctx.writeAndFlush(new InternalMessage(MsgType.AUTH, "1"));
    }
}
