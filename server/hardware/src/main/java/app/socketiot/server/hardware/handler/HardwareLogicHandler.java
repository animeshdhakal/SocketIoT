package app.socketiot.server.hardware.handler;

import app.socketiot.server.core.model.HardwareMessage;
import app.socketiot.server.core.model.MsgType;
import app.socketiot.server.core.model.device.UserDevice;
import io.netty.channel.ChannelHandlerContext;

public class HardwareLogicHandler {
    public final UserDevice userDevice;

    public HardwareLogicHandler(UserDevice userDevice) {
        this.userDevice = userDevice;
    }

    public void handleWrite(ChannelHandlerContext ctx, HardwareMessage msg) {
        if (msg.body.length < 2)
            return;

        userDevice.device.updatePin(ctx, msg.body[0], msg.body[1]);
        userDevice.user.json.sendToHardware(ctx, userDevice.device.id, msg);
        userDevice.user.json.sendToApps(ctx,
                new HardwareMessage(MsgType.WRITE, String.valueOf(userDevice.device.id), msg.body[0], msg.body[1]));
        userDevice.user.isUpdated = true;
    }

    public void handleSync(ChannelHandlerContext ctx) {
        for (short key : userDevice.device.pins.keySet()) {
            ctx.writeAndFlush(
                    new HardwareMessage(MsgType.WRITE, Integer.toString(key), userDevice.device.pins.get(key)));
        }
    }

}
