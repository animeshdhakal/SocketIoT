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

    public AppHandler(Holder holder, User user) {
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
                        Device device = user.json.getDevice(deviceId);
                        if (device != null) {
                            device.updatePin(ctx, message.body[1], message.body[2]);
                            user.json.sendToHardware(ctx, deviceId,
                                    new HardwareMessage(MsgType.WRITE, message.body[1], message.body[2]));
                            user.json.sendToApps(ctx, message);
                            user.isUpdated = true;
                        }
                    }
                    break;
                case MsgType.SYNC:
                    if (message.body.length > 0) {
                        int deviceId = Integer.parseInt(message.body[0]);
                        Device device = user.json.getDevice(deviceId);
                        if (device != null) {
                            for (short key : device.pins.keySet()) {
                                ctx.writeAndFlush(new HardwareMessage(MsgType.WRITE, message.body[0],
                                        Integer.toString(key),
                                        device.pins.get(key)));
                            }
                            if (device.pins.size() == 0) {
                                ctx.writeAndFlush(message);
                            }
                        }
                    }
                case MsgType.PING:
                    break;
            }
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        user.json.removeAppChannel(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ExceptionHandler.handleException(ctx, cause);
    }
}
