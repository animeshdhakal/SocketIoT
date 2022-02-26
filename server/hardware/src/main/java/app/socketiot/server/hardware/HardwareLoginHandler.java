package app.socketiot.server.hardware;

import app.socketiot.server.core.Holder;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.hardware.message.HardwareMessage;
import app.socketiot.server.hardware.message.MsgType;
import app.socketiot.server.utils.IPUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

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
                    boolean isHardware = message.body.length > 1;
                    if (isHardware) {
                        device.lastIP = IPUtil.getIP(ctx.channel().remoteAddress());
                        device.online = true;
                        device.hardGroup.add(ctx.channel());
                    } else {
                        device.dashGroup.add(ctx.channel());
                    }
                    holder.deviceDao.updateDevice(device);
                    ctx.pipeline().replace(this, "HardWareHandler", new HardwareHandler(holder, device, isHardware));
                    ctx.writeAndFlush(new HardwareMessage(MsgType.AUTH, "1"));
                } else {
                    ctx.writeAndFlush(new HardwareMessage(MsgType.AUTH, "0"));
                }
            }
        }
    }
}
