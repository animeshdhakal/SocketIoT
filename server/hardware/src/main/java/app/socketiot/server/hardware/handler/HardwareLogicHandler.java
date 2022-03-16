package app.socketiot.server.hardware.handler;

import app.socketiot.server.core.model.HardwareMessage;
import app.socketiot.server.core.model.MsgType;
import app.socketiot.server.core.model.device.Device;
import io.netty.channel.ChannelHandlerContext;

public class HardwareLogicHandler {
    public final Device device;

    public HardwareLogicHandler(Device device) {
        this.device = device;
    }

    public void handleWrite(ChannelHandlerContext ctx, HardwareMessage msg) {
        if (msg.body.length < 2)
            return;

        if (device != null) {
            device.updatePin(ctx, msg.body[0], msg.body[1]);
        }
    }

    public void handleSync(ChannelHandlerContext ctx) {
        for (short key : device.json.pins.keySet()) {
            ctx.writeAndFlush(new HardwareMessage(MsgType.WRITE, Integer.toString(key), device.json.pins.get(key)));
        }
    }

}
