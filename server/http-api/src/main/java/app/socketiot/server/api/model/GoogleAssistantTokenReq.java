package app.socketiot.server.api.model;

public class GoogleAssistantTokenReq {
    public String client_id;
    public String client_secret;
    public String refresh_token;
    public String grant_type;
    public String redirect_uri;
    public String code;
}
