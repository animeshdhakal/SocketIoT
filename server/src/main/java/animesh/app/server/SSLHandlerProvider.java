package animesh.app.server;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;

import io.netty.handler.ssl.SslHandler;

public class SSLHandlerProvider {
    private static final String jksFileName = "ssl/server.jks";
    private static final String STOREPASS = "animesh";
    private static final String KEYSTORETYPE = "jks";
    private static final String PROTOCOL = "TLS";
    public static SSLContext sslContext = null;

    static SslHandler getSslHandler() {
        if (sslContext != null) {
            SSLEngine engine = sslContext.createSSLEngine();
            engine.setUseClientMode(false);
            return new SslHandler(engine);
        }
        return null;
    }

    static void init() {
        try {

            final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            final InputStream inputStream = classloader.getResourceAsStream(jksFileName);

            final KeyStore trustStore = KeyStore.getInstance(KEYSTORETYPE);
            trustStore.load(inputStream, STOREPASS.toCharArray());

            final KeyManagerFactory keyManagerFactory = KeyManagerFactory
                    .getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(trustStore, STOREPASS.toCharArray());

            final TrustManagerFactory trustManagerFactory = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            sslContext = SSLContext.getInstance(PROTOCOL);
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(),
                    new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}