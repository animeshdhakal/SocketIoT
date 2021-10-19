package animesh.app.server;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;

import io.netty.handler.ssl.SslHandler;

public class SSLHandlerProvider {
    static String keystorePath = "/ssl/keystore.jks";
    static String keystoretype = "JKS";
    static String protocol = "TLS";
    static String keystorePass = "animesh123";
    static String algorithm = "SunX509";
    static SSLContext serverSSLContext = null;

    static SslHandler getSslHandler() {
        SSLEngine sslEngine = null;
        if (serverSSLContext != null) {
            sslEngine = serverSSLContext.createSSLEngine();
            sslEngine.setUseClientMode(false);
            sslEngine.setNeedClientAuth(false);

            return new SslHandler(sslEngine);
        }
        return null;

    }

    static void init() {
        KeyStore ks = null;
        InputStream keystorestream = Server.class.getResourceAsStream(keystorePath);
        try {
            ks = KeyStore.getInstance(keystoretype);
            ks.load(keystorestream, keystorePass.toCharArray());

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
            kmf.init(ks, keystorePass.toCharArray());
            KeyManager[] keyManagers = kmf.getKeyManagers();
            TrustManager[] trustManagers = null;

            serverSSLContext = SSLContext.getInstance(protocol);
            serverSSLContext.init(keyManagers, trustManagers, null);

        } catch (

        Exception e) {
            e.printStackTrace();
        }

    }
}
