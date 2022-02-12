package app.socketiot.server.core;

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

public class Mail {
    private final static Logger log = LogManager.getLogger(Mail.class);
    private final Session session;
    private InternetAddress from;
    String PLAIN_TEXT = "text/plain; charset=UTF-8";
    String HTML_TEXT = "text/html; charset=UTF-8";

    public Mail(Holder holder) {
        String username = holder.props.getProperty("mail.username");
        String password = holder.props.getProperty("mail.password");

        if (username == null || password == null) {
            log.debug("mail.username or mail.password is not set");
            this.session = null;
            return;
        }

        Properties mailProps = new Properties();
        mailProps.put("mail.smtp.host", "smtp.gmail.com");
        mailProps.put("mail.smtp.port", "465");
        mailProps.put("mail.smtp.ssl.enable", "true");
        mailProps.put("mail.smtp.auth", "true");

        this.session = Session.getDefaultInstance(mailProps, new Authenticator() {
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
            message.setText(body);
            message.setContent(body, contentType);
            Transport.send(message);
        } catch (Exception e) {
            log.error("Error sending mail to {}", to, e);
        }
    }

}
