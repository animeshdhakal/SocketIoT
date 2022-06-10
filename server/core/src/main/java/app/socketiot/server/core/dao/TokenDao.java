package app.socketiot.server.core.dao;

import java.io.Closeable;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import app.socketiot.server.core.model.token.ResetToken;
import app.socketiot.server.core.model.token.TokenBase;
import app.socketiot.server.core.model.token.VerifyToken;
import app.socketiot.utils.SerializationUtil;

public class TokenDao implements Closeable {
    private static String DEFAULT_TOKEN_FILE = "tokens.bin";
    public ConcurrentMap<String, TokenBase> tokens;
    private final String dataFolder;

    @SuppressWarnings("unchecked")
    public TokenDao(String dataFolder) {
        this.dataFolder = dataFolder;
        tokens = (ConcurrentMap<String, TokenBase>) SerializationUtil
                .deserialize(Paths.get(dataFolder, DEFAULT_TOKEN_FILE));
        if (tokens == null) {
            tokens = new ConcurrentHashMap<>();
        }
    }

    public void cleanTokens() {
        long now = System.currentTimeMillis();
        tokens.entrySet().removeIf(entry -> entry.getValue().isExpired(now));
    }

    public TokenBase getToken(String email) {
        cleanTokens();
        return tokens.get(email);
    }

    public void addToken(TokenBase token) {
        tokens.put(token.email, token);
        cleanTokens();
    }

    public ResetToken getResetToken(String email) {
        TokenBase token = tokens.get(email);
        if (token instanceof ResetToken) {
            return (ResetToken) token;
        }
        return null;
    }

    public VerifyToken getVerifyToken(String email) {
        TokenBase token = tokens.get(email);
        if (token instanceof VerifyToken) {
            return (VerifyToken) token;
        }
        return null;
    }

    @Override
    public void close() {
        SerializationUtil.serialize(Paths.get(dataFolder, DEFAULT_TOKEN_FILE), tokens);
    }
}
