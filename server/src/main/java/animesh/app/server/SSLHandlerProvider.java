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
    static String keystorePath = "/ssl/keystore.p12";
    static String keystoretype = "PKCS12";
    static String protocol = "TLS";
    static String keystorePass = "changeit";
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
        try {
            serverSSLContext = SSLContext.getInstance(protocol);
            KeyStore keystore = KeyStore.getInstance(keystoretype);
            keystore.load(SSLHandlerProvider.class.getResourceAsStream(keystorePath), keystorePass.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
            kmf.init(keystore, keystorePass.toCharArray());
            serverSSLContext.init(kmf.getKeyManagers(), null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
