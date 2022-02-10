package app.socketiot.server.workers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import app.socketiot.server.core.SSLHandlerProvider;
import app.socketiot.server.core.acme.AcmeClient;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CertificateWorker implements Runnable {

    private static final Logger log = LogManager.getLogger(CertificateWorker.class);

    private final SSLHandlerProvider sslProvider;
    private final static int renewBeforeDays = 21;

    public CertificateWorker(SSLHandlerProvider sslProvider) {
        this.sslProvider = sslProvider;
    }

    private static long getDateDiff(Date expirationDate) {
        long now = System.currentTimeMillis();
        return TimeUnit.MILLISECONDS.toDays(expirationDate.getTime() - now);
    }

    private static X509Certificate readX509Certificate() throws IOException {
        try (InputStream fis = new FileInputStream(AcmeClient.DOMAIN_CHAIN_FILE)) {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) certificateFactory.generateCertificate(fis);
        } catch (CertificateException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void run() {
        try {
            if (AcmeClient.DOMAIN_CHAIN_FILE.exists()) {
                // stream closed inside utilities method
                X509Certificate cert = readX509Certificate();

                Date expirationDate = cert.getNotAfter();
                long daysToExpire = getDateDiff(expirationDate);
                log.info("Certificate expiration date is {}. Days left : {}", expirationDate, daysToExpire);

                if (daysToExpire <= renewBeforeDays) {
                    renew();
                }
            } else {
                renew();
            }
        } catch (Exception e) {
            log.error("Error during certificate renewal.", e);
        }
    }

    private void renew() throws Exception {
        log.warn("Trying to renew...");
        sslProvider.regenerate();
        log.info("Success! The certificate for your domain has been renewed!");
    }

}