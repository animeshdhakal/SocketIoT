package app.socketiot.server.core;

import java.io.InputStream;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLException;
import org.apache.logging.log4j.Logger;
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
    private static Logger log = LogManager.getLogger();

    public SSLHandlerProvider(
            Holder holder) {
        if (holder.args.hasArg("--ssl")) {
            try {
                File serverCert = new File(holder.props.getProperty("ssl.cert"), "");
                File serverKey = new File(holder.props.getProperty("ssl.key"), "");
                if (!serverCert.exists() || !serverKey.exists()) {
                    sslCtx = build(fetchSslProvider());
                } else {
                    sslCtx = build(serverCert, serverKey, holder.props.getProperty("ssl.key.password", ""),
                            fetchSslProvider());
                }
            } catch (CertificateException | SSLException e) {
                log.error("Error creating SSLContext", e);
            }
        }
    }

    boolean isOpenSslAvailable() {
        return PlatformDependent.bitMode() != 32 && OpenSsl.isAvailable();
    }

    private SslProvider fetchSslProvider() {
        return isOpenSslAvailable() ? SslProvider.OPENSSL : SslProvider.JDK;
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

    public SslContext build(InputStream serverCert, InputStream serverKey,
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

    public SslContext build(SslProvider sslProvider) throws CertificateException, SSLException {
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        return SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
                .sslProvider(sslProvider)
                .build();
    }

    public SslContext getSslCtx() {
        return sslCtx;
    }
}