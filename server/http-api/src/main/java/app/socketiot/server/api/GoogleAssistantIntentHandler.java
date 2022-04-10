package app.socketiot.server.api;

import java.util.ArrayList;
import java.util.HashMap;
import app.socketiot.server.api.model.GoogleAssistant.Command;
import app.socketiot.server.api.model.GoogleAssistant.Device;
import app.socketiot.server.api.model.GoogleAssistant.DeviceInfo;
import app.socketiot.server.api.model.GoogleAssistant.DeviceName;
import app.socketiot.server.api.model.GoogleAssistant.ExecuteRes;
import app.socketiot.server.api.model.GoogleAssistant.Execution;
import app.socketiot.server.api.model.GoogleAssistant.Input;
import app.socketiot.server.api.model.GoogleAssistant.IntentReq;
import app.socketiot.server.api.model.GoogleAssistant.Payload;
import app.socketiot.server.api.model.GoogleAssistant.QueryDevice;
import app.socketiot.server.api.model.GoogleAssistant.QueryRes;
import app.socketiot.server.api.model.GoogleAssistant.SyncRes;
import app.socketiot.server.api.model.GoogleAssistant.ExecuteRes.ExecutePayload;
import app.socketiot.server.api.model.GoogleAssistant.ExecuteRes.ExecutePayload.ExecuteCommand;
import app.socketiot.server.api.model.GoogleAssistant.QueryRes.QueryPayload;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.http.JwtHttpHandler;
import app.socketiot.server.core.http.annotations.POST;
import app.socketiot.server.core.http.annotations.Path;
import app.socketiot.server.core.http.handlers.HttpReq;
import app.socketiot.server.core.http.handlers.HttpRes;
import app.socketiot.server.core.json.model.DeviceStatus;
import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.blueprint.BluePrint;
import app.socketiot.server.core.model.widgets.OnOffWidget;
import app.socketiot.server.utils.NumberUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
@Path("/api/google-assistant")
public class GoogleAssistantIntentHandler extends JwtHttpHandler {
    private final Holder holder;

    public GoogleAssistantIntentHandler(Holder holder) {
        super(holder);
        this.holder = holder;
    }

    public void addDevice(Payload payload, Device device) {
        Device[] ndevice = new Device[payload.devices.length + 1];
        for (int i = 0; i < payload.devices.length; i++) {
            ndevice[i] = payload.devices[i];
        }
        ndevice[payload.devices.length] = device;
        payload.devices = ndevice;
    }

    public HttpRes handleSync(IntentReq intentReq, User user) {
        SyncRes res = new SyncRes();
        res.requestId = intentReq.requestId;
        res.payload = new Payload();
        res.payload.agentUserId = user.email;

        for (app.socketiot.server.core.model.device.Device device : user.json.devices) {
            res.payload.devices = new Device[0];
            BluePrint bluePrint = holder.bluePrintDao.getBluePrint(device.blueprint_id);
            for (int pin : device.pins.keySet()) {
                Device ndevice = new Device();
                ndevice.name = new DeviceName();
                ndevice.name.name = bluePrint.getWidgetNameByPin(pin);
                ndevice.name.defaultNames = new String[] { ndevice.name.name };
                ndevice.name.nicknames = new String[] { ndevice.name.name };
                ndevice.id = device.token + '\0' + String.valueOf(pin);
                ndevice.type = "action.devices.types.SWITCH";
                ndevice.traits = new String[] { "action.devices.traits.OnOff" };
                ndevice.willReportState = true;
                ndevice.deviceInfo = new DeviceInfo();
                ndevice.deviceInfo.manufacturer = "SocketIoT";
                ndevice.deviceInfo.model = "SocketIoT";
                ndevice.deviceInfo.hwVersion = "1.0 Beta";
                addDevice(res.payload, ndevice);
            }
        }

        return new HttpRes(res);
    }

    public HttpRes handleQuery(IntentReq intentReq, User user) {
        QueryRes res = new QueryRes();
        res.requestId = intentReq.requestId;
        res.payload = new QueryPayload();
        res.payload.devices = new HashMap<>();

        for (Device gdevice : intentReq.inputs[0].payload.devices) {
            String id = gdevice.id;
            String[] parts = id.split("\0");
            String token = parts[0];
            QueryDevice qdevice = new QueryDevice();

            if (parts.length < 2) {
                qdevice.status = "ERROR";
                res.payload.devices.put(gdevice.id, qdevice);
                continue;
            }

            short pin = NumberUtil.parsePin(parts[1]);

            app.socketiot.server.core.model.device.Device device = holder.deviceDao.getDevice(token);
            BluePrint bluePrint = holder.bluePrintDao.getBluePrint(device.blueprint_id);

            if (device == null || pin == -1) {
                qdevice.status = "ERROR";
                res.payload.devices.put(gdevice.id, qdevice);
                continue;
            }

            OnOffWidget widget = bluePrint.getOnOffWidgetByPin(parts[1]);

            qdevice.online = device.status == DeviceStatus.Online;
            qdevice.on = device.pins.get(pin) != widget.onValue;
            qdevice.status = "SUCCESS";
            res.payload.devices.put(gdevice.id, qdevice);
        }
        return new HttpRes(res);
    }

    public HttpRes handleExecute(Channel c, IntentReq intentReq, User user) {
        ExecuteRes res = new ExecuteRes();
        res.requestId = intentReq.requestId;
        res.payload = new ExecutePayload();
        res.payload.commands = new ArrayList<>();

        for (Input input : intentReq.inputs) {
            for (Command command : input.payload.commands) {
                for (Device device : command.devices) {
                    String id = device.id;
                    String[] parts = id.split("\0");
                    String token = parts[0];

                    app.socketiot.server.core.model.device.Device d = holder.deviceDao.getDevice(token);

                    ExecuteCommand ec = new ExecuteCommand();
                    ec.ids = new String[] { id };

                    if (d == null) {
                        ec.status = "ERROR";
                        ec.errorCode = "deviceNotFound";
                        res.payload.commands.add(ec);
                        continue;
                    }

                    BluePrint bluePrint = holder.bluePrintDao.getBluePrint(d.blueprint_id);

                    ec.status = "SUCCESS";
                    ec.states = new QueryDevice();
                    ec.states.online = d.status == DeviceStatus.Online;

                    for (Execution execution : command.execution) {
                        if (execution.command.equals("action.devices.commands.OnOff")) {
                            OnOffWidget widget = bluePrint.getOnOffWidgetByPin(parts[1]);

                            if (widget == null) {
                                ec.status = "ERROR";
                                ec.errorCode = "deviceNotInput";
                                res.payload.commands.add(ec);
                                continue;
                            }

                            if (execution.params.on == true) {
                                d.updatePin(parts[1], widget.offValue);
                                ec.states.on = false;
                                user.json.broadCastWriteMessage(c, d.id, parts[1], widget.onValue);
                            } else {
                                d.updatePin(parts[1], widget.offValue);
                                ec.states.on = true;
                                user.json.broadCastWriteMessage(c, d.id, parts[1], widget.offValue);
                            }

                            user.isUpdated = true;
                        }
                    }

                    res.payload.commands.add(ec);
                }
            }
        }

        return new HttpRes(res);
    }

    @Path("/fulfillment")
    @POST
    public HttpRes fulfillment(HttpReq req) {
        IntentReq intentreq = req.getContentAs(IntentReq.class);

        if (intentreq == null || intentreq.inputs == null) {
            return HttpRes.badRequest("Invalid request");
        }

        for (Input intent : intentreq.inputs) {
            if (intent.intent.equals("action.devices.SYNC")) {
                return handleSync(intentreq, req.user);
            } else if (intent.intent.equals("action.devices.QUERY")) {
                return handleQuery(intentreq, req.user);
            } else if (intent.intent.equals("action.devices.EXECUTE")) {
                return handleExecute(req.getCtx().channel(), intentreq, req.user);
            }
        }

        return HttpRes.ok("");
    }
}
