package app.socketiot.server.api.model;

public class GoogleAssistantTokenRes {
    public String token_type;
    public String access_token;
    public Integer expires_in;
    public String refresh_token;

    public GoogleAssistantTokenRes(String token_type, String access_token, String refresh_token, Integer expires_in) {
        this.token_type = token_type;
        this.access_token = access_token;
        this.expires_in = expires_in;
        this.refresh_token = refresh_token;
    }
}
