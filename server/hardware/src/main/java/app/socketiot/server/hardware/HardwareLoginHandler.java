package app.socketiot.server.hardware;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import app.socketiot.server.core.Holder;
import app.socketiot.server.core.json.model.DeviceStatus;
import app.socketiot.server.core.model.HardwareMessage;
import app.socketiot.server.core.model.MsgType;
import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.core.model.device.UserDevice;
import app.socketiot.server.utils.IPUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class HardwareLoginHandler extends ChannelInboundHandlerAdapter {
    private final Holder holder;
    private final static Logger log = LogManager.getLogger(HardwareLoginHandler.class);

    public HardwareLoginHandler(Holder holder) {
        this.holder = holder;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HardwareMessage) {
            HardwareMessage message = (HardwareMessage) msg;

            if (message.body.length < 1 || message.body[0] == null) {
                return;
            }

            UserDevice userDevice = holder.deviceDao.getUserDevice(message.body[0]);

            if (userDevice != null) {
                userDevice.device.lastIP = IPUtil.getIP(ctx.channel().remoteAddress());
                userDevice.device.status = DeviceStatus.Online;

                userDevice.user.dash.addHardChannel(ctx.channel());

                ctx.pipeline().replace(HardwareLoginHandler.class, "HardwareHandler",
                        new HardwareHandler(holder, userDevice));

                userDevice.user.dash.sendToApps(ctx.channel(),
                        new HardwareMessage(MsgType.DEVICE_STATUS, String.valueOf(userDevice.device.id),
                                DeviceStatus.Online.toString()));

                ctx.writeAndFlush(new HardwareMessage(MsgType.AUTH, "1"));
            }

            else {
                User user = holder.userDao.getUserFromProvisioningToken(message.body[0]);

                if (user != null && !user.dash.isProvisioningDeviceOnline) {

                    log.debug("Provisioning Device for user {}", user.email);

                    Device device = new Device();
                    Device dbDevice = user.dash.getLastDevice();
                    device.token = user.dash.provisioningToken;
                    device.id = dbDevice == null ? 1 : dbDevice.id + 1;
                    device.name = "Device " + device.id;
                    device.status = DeviceStatus.Online;
                    device.lastIP = IPUtil.getIP(ctx.channel().remoteAddress());

                    user.dash.isProvisioningDeviceOnline = true;

                    // Will be Updated Later By the user
                    device.blueprint_id = "";

                    holder.deviceDao.addDevice(user, device);
                    user.dash.addDevice(device);
                    user.dash.addHardChannel(ctx.channel());

                    user.updated();

                    ctx.pipeline().replace(HardwareLoginHandler.class, "HardwareHandler",
                            new HardwareHandler(holder, new UserDevice(user, device)));

                    // We are not using the device.id here because end user doesn't know it
                    user.dash.sendToApps(ctx.channel(),
                            new HardwareMessage(MsgType.DEVICE_STATUS, "0", DeviceStatus.Online.toString()));
                    ctx.writeAndFlush(new HardwareMessage(MsgType.AUTH, "1"));
                } else {
                    ctx.writeAndFlush(new HardwareMessage(MsgType.AUTH, "0"));
                }
            }

        }
    }
}
