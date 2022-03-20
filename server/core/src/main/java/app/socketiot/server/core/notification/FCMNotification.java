package app.socketiot.server.core.notification;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import app.socketiot.server.core.cli.properties.ServerProperties;
import app.socketiot.server.core.json.JsonParser;
import app.socketiot.server.core.notification.model.FCMMessage;
import app.socketiot.server.core.notification.model.FCMResponse;
import io.netty.handler.codec.http.HttpHeaderNames;

public class FCMNotification {
    private final static Logger log = LogManager.getLogger(FCMNotification.class);
    private final AsyncHttpClient client;
    private final String token;
    private final String url = "https://fcm.googleapis.com/fcm/send";

    public FCMNotification(ServerProperties properties, AsyncHttpClient client) {
        this.token = properties.getProperty("server.notification.key");
        this.client = client;
    }

    private static void processError(String errorMessage, String to) {
        if (errorMessage != null
                && (errorMessage.contains("NotRegistered") || errorMessage.contains("InvalidRegistration"))) {
            System.out.println("Device not registered: " + to);
        } else {
            System.out.println("Error sending message to: " + to);
        }
    }

    public void send(FCMMessage message) {
        if (token == null) {
            log.error("FCM Notification Token Not Provided, Notification Failed");
            return;
        }

        this.client
                .preparePost(url)
                .setHeader("Authorization", "key=" + token)
                .setBody(JsonParser.toJson(message))
                .setHeader(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=utf-8")
                .execute(new AsyncCompletionHandler<Response>() {
                    @Override
                    public Response onCompleted(Response response) throws Exception {
                        if (response.getStatusCode() == 200) {
                            FCMResponse fcmResponse = JsonParser.parse(FCMResponse.class, response.getResponseBody());
                            if (fcmResponse.failure == 1) {
                                String errorMessage = fcmResponse.results != null
                                        && fcmResponse.results.length > 0
                                                ? fcmResponse.results[0].error
                                                : message.to;
                                processError(errorMessage, message.to);
                            }
                        }

                        return response;
                    }

                    @Override
                    public void onThrowable(Throwable t) {
                        processError(t.getMessage(), message.to);
                    }
                });
    }
}
