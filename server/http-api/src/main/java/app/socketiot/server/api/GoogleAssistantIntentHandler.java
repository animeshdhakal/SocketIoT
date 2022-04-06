package app.socketiot.server.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import app.socketiot.server.api.model.GAIntent;
import app.socketiot.server.core.Holder;
import app.socketiot.server.core.http.JwtHttpHandler;
import app.socketiot.server.core.http.annotations.POST;
import app.socketiot.server.core.http.annotations.Path;
import app.socketiot.server.core.http.handlers.HttpReq;
import app.socketiot.server.core.http.handlers.HttpRes;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
@Path("/api/google-assistant")
public class GoogleAssistantIntentHandler extends JwtHttpHandler {
    private final Holder holder;
    private static Logger log = LogManager.getLogger(GoogleAssistantIntentHandler.class);

    public GoogleAssistantIntentHandler(Holder holder) {
        super(holder);
        this.holder = holder;
    }

    @Path("/fulfillment")
    @POST
    public HttpRes fulfillment(HttpReq req) {
        GAIntent intentreq = req.getContentAs(GAIntent.class);
        if (intentreq == null || intentreq.inputs == null) {
            return HttpRes.badRequest("Invalid request");
        }

        log.info("Google Assistant Intent: {}", intentreq.inputs[0].intent);
        log.info("Got Intent {}", req.getContent());

        for (GAIntent.Input input : intentreq.inputs) {
            if (input.intent.equals("action.devices.SYNC")) {
                GAIntent intentres = new GAIntent();
                intentres.requestId = intentreq.requestId;
                intentres.inputs = new GAIntent.Input[] {
                        new GAIntent.Input()
                };
                intentres.inputs[0].intent = "action.devices.QUERY";
                intentres.inputs[0].payload = new GAIntent.Input.Payload();
                intentres.inputs[0].payload.devices = new GAIntent.Input.Payload.Device[] {
                        new GAIntent.Input.Payload.Device()
                };
                intentres.inputs[0].payload.devices[0].id = "123";
                intentres.inputs[0].payload.devices[0].type = "action.devices.types.LIGHT";
                intentres.inputs[0].payload.devices[0].traits = new String[] {
                        "action.devices.traits.OnOff"
                };
                intentres.inputs[0].payload.devices[0].name = new GAIntent.Input.Payload.Device.Name();
                intentres.inputs[0].payload.devices[0].name.name = "Light";
                intentres.inputs[0].payload.devices[0].name.nicknames = new String[] {
                        "Light"
                };
                intentres.inputs[0].payload.devices[0].name.defaultNames = new String[] {
                        "Light"
                };

                return new HttpRes(intentres);
            }
        }

        return HttpRes.ok("");
    }
}
