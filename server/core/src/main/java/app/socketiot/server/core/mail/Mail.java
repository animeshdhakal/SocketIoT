package app.socketiot.server.core.mail;

import app.socketiot.server.core.cli.properties.ServerProperties;

public class Mail {
    private final BaseMail mail;

    public Mail(ServerProperties props) {
        if (props.getProperty("mail.smtp.host", "").equals("smtp.gmail.com")) {
            this.mail = new GMail(props);
        } else {
            this.mail = new ThirdPartyMail(props, props.getProperty("mail.smtp.from"));
        }
    }

    public void sendText(String to, String sub, String body) {
        mail.send(to, sub, body, BaseMail.PLAIN_TEXT);
    }

    public void sendHtml(String to, String sub, String body) {
        mail.send(to, sub, body, BaseMail.HTML_TEXT);
    }
}
