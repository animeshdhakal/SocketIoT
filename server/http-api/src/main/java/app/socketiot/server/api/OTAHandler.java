package app.socketiot.server.api;

import app.socketiot.server.api.model.OTABegin;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.http.JwtHttpHandler;
import app.socketiot.server.core.http.annotations.POST;
import app.socketiot.server.core.http.annotations.Path;
import app.socketiot.server.core.http.handlers.HttpReq;
import app.socketiot.server.core.http.handlers.HttpRes;
import app.socketiot.server.core.http.handlers.StatusMsg;
import app.socketiot.server.core.model.HardwareMessage;
import app.socketiot.server.core.model.MsgType;
import app.socketiot.server.core.model.device.Device;
import app.socketiot.server.utils.HardwareInfoUtil;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
@Path("/api/ota")
public class OTAHandler extends JwtHttpHandler {
    public OTAHandler(Holder holder) {
        super(holder);
    }

    @Path("/begin")
    @POST
    public HttpRes begin(HttpReq req) {
        OTABegin otaBegin = req.getContentAs(OTABegin.class);
        if (otaBegin == null || otaBegin.firmwarePath == null || otaBegin.blueprint_id == null
                || otaBegin.devices == null
                || otaBegin.devices.size() == 0) {
            return StatusMsg.badRequest("Incomplete Fields");
        }
        if (req.user.json.getBlueprint(otaBegin.blueprint_id) == null) {
            return StatusMsg.badRequest("BluePrint Not Found");
        }

        java.nio.file.Path firmwarePath = java.nio.file.Path.of(holder.jarPath, otaBegin.firmwarePath);

        if (!firmwarePath.toFile().exists()) {
            return StatusMsg.badRequest("Firmware Not Found");
        }

        String build = HardwareInfoUtil.getPatternFromPath(firmwarePath, "\0build\0");

        if (build == null) {
            return StatusMsg.badRequest("Invalid Firmware");
        }

        String host = req.getHeader("host");
        if (host == null) {
            host = "localhost";
        }

        String otaUrl = "https://" + host + otaBegin.firmwarePath;

        HardwareMessage otaMessage = new HardwareMessage(MsgType.SYS, "ota", otaUrl);

        for (String deviceToken : otaBegin.devices) {
            Device device = holder.deviceDao.getDevice(deviceToken);
            if (device == null) {
                return StatusMsg.badRequest("Device Not Found");
            }
            if (!device.blueprint_id.equals(otaBegin.blueprint_id)) {
                return StatusMsg.badRequest("Device Not Found");
            }
            holder.deviceDao.sendToHardware(device.id, otaMessage);
        }

        return StatusMsg.ok("OTA Begins");
    }

}
