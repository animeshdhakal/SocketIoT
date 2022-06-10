package app.socketiot.server.mail;

public interface BaseMail {
    String PLAIN_TEXT = "text/plain; charset=UTF-8";
    String HTML_TEXT = "text/html; charset=UTF-8";

    public void send(String to, String sub, String body, String contentType);
}