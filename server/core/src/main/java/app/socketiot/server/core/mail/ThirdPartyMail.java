package app.socketiot.server.core.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThirdPartyMail implements BaseMail {
    private final static Logger log = LogManager.getLogger(GMail.class);
    private final String username;
    private final String password;
    private final String host;
    private final Session session;
    private InternetAddress from;

    public ThirdPartyMail(Properties props, String from) {
        this.username = props.getProperty("mail.smtp.username");
        this.password = props.getProperty("mail.smtp.password");
        this.host = props.getProperty("mail.smtp.host");

        if (username == null || password == null) {
            log.debug("mail.username or mail.password is not set");
            this.session = null;
            return;
        }

        this.session = Session.getDefaultInstance(props);

        try {
            this.from = new InternetAddress(from);
        } catch (AddressException e) {
            log.error("Invalid email address", e);
        }

    }

    public void send(String to, String sub, String body, String contentType) {
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(from);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(sub);
            message.setContent(body, contentType);
            Transport transport = session.getTransport();
            transport.connect(host, username, password);
            transport.sendMessage(message, message.getAllRecipients());
        } catch (Exception e) {
            log.error("Error sending mail to {}", to, e);
        }
    }
}
