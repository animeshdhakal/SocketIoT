package app.socketiot.server.mail;

import app.socketiot.server.BlockingIOHandler;
import app.socketiot.server.cli.properties.ServerProperties;

public class Mail {
    private final BaseMail mail;
    private final BlockingIOHandler blockingIOHandler;

    public Mail(ServerProperties props, BlockingIOHandler blockingIOHandler) {
        if (props.getProperty("mail.smtp.host", "").equals("smtp.gmail.com")) {
            this.mail = new GMail(props);
        } else {
            this.mail = new ThirdPartyMail(props, props.getProperty("mail.smtp.from"));
        }
        this.blockingIOHandler = blockingIOHandler;
    }

    public void sendText(String to, String sub, String body) {
        blockingIOHandler.execute(() -> mail.send(to, sub, body, BaseMail.PLAIN_TEXT));
    }

    public void sendHtml(String to, String sub, String body) {
        blockingIOHandler.execute(() -> mail.send(to, sub, body, BaseMail.HTML_TEXT));
    }
}