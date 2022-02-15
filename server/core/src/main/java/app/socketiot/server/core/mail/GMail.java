package app.socketiot.server.core.mail;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GMail implements BaseMail {
    private final static Logger log = LogManager.getLogger(GMail.class);
    private final Session session;
    private InternetAddress from;

    public GMail(Properties props) {
        String username = props.getProperty("mail.smtp.username");
        String password = props.getProperty("mail.smtp.password");

        if (username == null || password == null) {
            log.debug("mail.username or mail.password is not set");
            this.session = null;
            return;
        }

        this.session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            this.from = new InternetAddress(username);
        } catch (AddressException e) {
            log.error("Invalid email address", e);
        }
    }

    public void send(String to, String sub, String body, String contentType) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(from);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(sub);
            message.setContent(body, contentType);
            Transport.send(message);
        } catch (Exception e) {
            log.error("Error sending mail to {}", to, e);
        }
    }
}
