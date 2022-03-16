package app.socketiot.server.core;

import java.security.cert.CertificateException;
import javax.net.ssl.SSLException;
import org.apache.logging.log4j.Logger;

import app.socketiot.server.core.acme.AcmeClient;
import app.socketiot.server.core.cli.properties.ServerProperties;

import org.apache.logging.log4j.LogManager;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.internal.PlatformDependent;
import java.io.File;

public class SSLHandlerProvider {

    private volatile SslContext sslCtx = null;
    private boolean isAutoGenerate;
    private boolean isInitializeOnStart;
    public AcmeClient acmeClient;
    private static Logger log = LogManager.getLogger();

    public SSLHandlerProvider(
            Holder holder) {
        if (holder.args.hasArg("-ssl")) {
            log.info("SSL Enabled");

            String certPath = holder.props.getProperty("ssl.cert");
            String keyPath = holder.props.getProperty("ssl.key");
            String keyPass = holder.props.getProperty("ssl.key.password");
            String email = holder.props.getProperty("ssl.email");

            if (certPath == null || certPath.isEmpty()) {
                log.info("Custom Certificate Not Found");
                isAutoGenerate = true;
            } else {
                isAutoGenerate = false;
            }

            String host = holder.props.getProperty("server.host");

            if (AcmeClient.DOMAIN_CHAIN_FILE.exists() && AcmeClient.DOMAIN_KEY_FILE.exists()) {
                log.info("Let's Encrypt Certificates Found");

                certPath = AcmeClient.DOMAIN_CHAIN_FILE.getAbsolutePath();
                keyPath = AcmeClient.DOMAIN_KEY_FILE.getAbsolutePath();
                keyPass = null;

                this.isInitializeOnStart = false;
                this.acmeClient = new AcmeClient(email, host);

            } else {
                log.info("Let's Encrypt Certificate Not Found");
                if (host == null || host.isEmpty() || email == null || email.isEmpty()) {
                    log.error("Host or Email is not set for Ssl. Auto Certificate Generation Disabled");
                    this.acmeClient = null;
                    this.isInitializeOnStart = false;
                } else {
                    log.info("Auto Certificate Generation Enabled");
                    this.isInitializeOnStart = true;
                    this.acmeClient = new AcmeClient(email, host);
                    return;
                }
            }

            if (isOpenSslAvailable()) {
                log.info("Using Native OpenSsl Provider");
            }

            this.sslCtx = initSslContext(certPath, keyPath, keyPass);

        }
    }

    public void generateInitialCertificates(ServerProperties props) {
        if (isAutoGenerate && isInitializeOnStart) {
            System.out.println("Generating own initial certificates...");
            try {
                regenerate();
                System.out.println("The certificate for your domain "
                        + props.getProperty("server.host") + " has been generated!");
            } catch (Exception e) {
                System.out.println("Error during certificate generation.");
                System.out.println(e.getMessage());
            }
        }
    }

    private SslContext initSslContext(String certPath, String keyPath, String keyPass) {
        try {
            if (certPath != null && !certPath.isEmpty() && keyPath != null && !keyPath.isEmpty()) {
                File certFile = new File(certPath);
                File keyFile = new File(keyPath);

                if (!certFile.exists() || !keyFile.exists()) {

                    log.warn("Using one way SSL Certificate. This is not Secure !!!");
                    return build(fetchSslProvider());
                }

                return build(certFile, keyFile, keyPass, fetchSslProvider());
            }

            log.warn("Using one way SSL Certificate. This is not Secure !!!");
            return build(fetchSslProvider());

        } catch (Exception e) {
            log.error("Error while loading SSL Certificates", e);
            throw new RuntimeException("Error loading SSL Certificates", e);
        }

    }

    public boolean runRenewalWorker() {
        return isAutoGenerate && acmeClient != null;
    }

    public void regenerate() throws Exception {
        this.acmeClient.requestCertificate();

        String certPath = AcmeClient.DOMAIN_CHAIN_FILE.getAbsolutePath();
        String keyPath = AcmeClient.DOMAIN_KEY_FILE.getAbsolutePath();

        this.sslCtx = initSslContext(certPath, keyPath, null);
    }

    public static boolean isOpenSslAvailable() {
        return PlatformDependent.bitMode() != 32 && OpenSsl.isAvailable();
    }

    private SslProvider fetchSslProvider() {
        return isOpenSslAvailable() ? SslProvider.OPENSSL : SslProvider.JDK;
    }

    public static SslContext build(SslProvider sslProvider) throws CertificateException, SSLException {
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        return SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
                .sslProvider(sslProvider)
                .build();
    }

    public SslContext build(File serverCert, File serverKey,
            String serverPass, SslProvider sslProvider) throws SSLException {
        SslContextBuilder sslContextBuilder;
        if (serverPass == null || serverPass.isEmpty()) {
            sslContextBuilder = SslContextBuilder.forServer(serverCert, serverKey)
                    .sslProvider(sslProvider);
        } else {
            sslContextBuilder = SslContextBuilder.forServer(serverCert, serverKey, serverPass)
                    .sslProvider(sslProvider);
        }
        sslContextBuilder.protocols("TLSv1.3", "TLSv1.2");

        return sslContextBuilder.build();
    }

    public static SslContext build(File serverCert, File serverKey, String serverPass,
            SslProvider sslProvider, File clientCert) throws SSLException {
        log.info("Creating SSL context for cert '{}', key '{}', key pass '{}'",
                serverCert.getAbsolutePath(), serverKey.getAbsoluteFile(), serverPass);
        if (serverPass == null || serverPass.isEmpty()) {
            return SslContextBuilder.forServer(serverCert, serverKey)
                    .sslProvider(sslProvider)
                    .trustManager(clientCert)
                    .build();
        } else {
            return SslContextBuilder.forServer(serverCert, serverKey, serverPass)
                    .sslProvider(sslProvider)
                    .trustManager(clientCert)
                    .build();
        }
    }

    public SslContext getSslCtx() {
        return sslCtx;
    }
}
