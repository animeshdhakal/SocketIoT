package animesh.app.server;

import java.io.InputStream;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.internal.PlatformDependent;
import java.io.File;

public class SSLHandlerProvider {

    public static volatile SslContext sslCtx = null;

    static boolean isOpenSslAvailable() {
        return PlatformDependent.bitMode() != 32 && OpenSsl.isAvailable();
    }

    private static SslProvider fetchSslProvider() {
        return isOpenSslAvailable() ? SslProvider.OPENSSL : SslProvider.JDK;
    }

    public static SslContext build(File serverCert, File serverKey,
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

    public static SslContext build(InputStream serverCert, InputStream serverKey,
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

    public static SslContext build(SslProvider sslProvider) throws CertificateException, SSLException {
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        return SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
                .sslProvider(sslProvider)
                .build();
    }

    public static void init(String serverCertPath, String serverKeyPath, String serverPass) {
        try {
            File serverCert = new File(serverCertPath);
            File serverKey = new File(serverKeyPath);
            if (!serverCert.exists() || !serverKey.exists()) {
                sslCtx = build(fetchSslProvider());
            } else {
                sslCtx = build(serverCert, serverKey, serverPass, fetchSslProvider());
            }
        } catch (Exception e) {
            LoggerUtil.logger.error("SSL Init Failed - " + e.getMessage());
        }

    }
}