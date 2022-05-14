package app.socketiot.server.api.model;

public class JwtResponse {
    public String access_token;
    public Long expires_in;
    public String refresh_token;

    public JwtResponse(String access_token, String refresh_token, Long expires_in) {
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.expires_in = expires_in;
    }

    public JwtResponse() {
    }
}
