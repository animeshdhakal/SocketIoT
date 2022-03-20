package app.socketiot.server.hardware;

import app.socketiot.server.core.Holder;
import app.socketiot.server.core.json.model.DeviceStatus;
import app.socketiot.server.core.model.HardwareMessage;
import app.socketiot.server.core.model.MsgType;
import app.socketiot.server.core.model.device.UserDevice;
import app.socketiot.server.utils.IPUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class HardwareLoginHandler extends ChannelInboundHandlerAdapter {
    private final Holder holder;

    public HardwareLoginHandler(Holder holder) {
        this.holder = holder;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HardwareMessage) {
            HardwareMessage message = (HardwareMessage) msg;
            if (message.body.length > 0) {
                UserDevice userDevice = holder.deviceDao.getUserDevice(message.body[0]);

                if (userDevice != null) {
                    userDevice.device.lastIP = IPUtil.getIP(ctx.channel().remoteAddress());
                    userDevice.device.status = DeviceStatus.Online;

                    userDevice.user.json.addHardChannel(ctx.channel());

                    ctx.pipeline().replace(HardwareLoginHandler.class, "HardwareHandler",
                            new HardwareHandler(holder, userDevice));

                    userDevice.user.json.sendToApps(ctx,
                            new HardwareMessage(MsgType.DEVICE_STATUS, String.valueOf(userDevice.device.id),
                                    DeviceStatus.Online.toString()));

                    ctx.writeAndFlush(new HardwareMessage(MsgType.AUTH, "1"));
                } else {
                    ctx.writeAndFlush(new HardwareMessage(MsgType.AUTH, "0"));
                }
            }
        }
    }
}
