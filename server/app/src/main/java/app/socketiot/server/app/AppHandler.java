package app.socketiot.server.app;

import app.socketiot.server.core.Holder;
import app.socketiot.server.core.exceptions.ExceptionHandler;
import app.socketiot.server.core.model.HardwareMessage;
import app.socketiot.server.core.model.MsgType;
import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.device.Device;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class AppHandler extends ChannelInboundHandlerAdapter {
    private final User user;
    private final Holder holder;

    public AppHandler(Holder holder, User user) {
        this.holder = holder;
        this.user = user;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HardwareMessage) {
            HardwareMessage message = (HardwareMessage) msg;

            switch (message.type) {
                case MsgType.WRITE:
                    if (message.body.length > 2) {
                        int deviceId = Integer.parseInt(message.body[0]);
                        Device device = holder.deviceDao.getDeviceByEmailAndID(user.email, deviceId);
                        device.updatePin(ctx, message.body[1], message.body[2]);
                    }
                    break;
                case MsgType.PING:
                    break;
                case MsgType.SYNC:
                    if (message.body.length > 0) {
                        int deviceId = Integer.parseInt(message.body[0]);
                        Device device = holder.deviceDao.getDeviceByEmailAndID(user.email, deviceId);
                        if(device == null){
                            System.out.println("Device Not Found " + deviceId);
                            return;                        
                        }
                        for (short key : device.json.pins.keySet()) {
                            ctx.writeAndFlush(new HardwareMessage(MsgType.WRITE, message.body[0], Integer.toString(key), device.json.pins.get(key)));
                        }
                    }
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        for (Device device : holder.deviceDao.getAllDevicesByEmail(user.email)) {
            device.appGroup.remove(ctx.channel());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ExceptionHandler.handleException(ctx, cause);
    }
}
