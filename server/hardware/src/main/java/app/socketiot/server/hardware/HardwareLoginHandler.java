package app.socketiot.server.hardware;

import app.socketiot.server.core.Holder;
import app.socketiot.server.core.model.HardwareMessage;
import app.socketiot.server.core.model.MsgType;
import app.socketiot.server.core.model.device.Device;
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
            if (message.body.length != 0) {
                Device device = holder.deviceDao.getDeviceByToken(message.body[0]);
                if (device != null) {
                    device.lastIP = IPUtil.getIP(ctx.channel().remoteAddress());
                    device.online = true;
                    device.hardGroup.add(ctx.channel());
                    holder.deviceDao.updateDevice(device);
                    ctx.pipeline().replace(this, "HardWareHandler", new HardwareHandler(holder, device));
                    ctx.writeAndFlush(new HardwareMessage(MsgType.AUTH, "1"));
                    device.sendToApps(ctx, new HardwareMessage(MsgType.DEVICE_STATUS, String.valueOf(device.id), "1"));
                } else {
                    ctx.writeAndFlush(new HardwareMessage(MsgType.AUTH, "0"));
                }
            }
        }
    }
}
