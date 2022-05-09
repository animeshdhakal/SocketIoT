package app.socketiot.server.core.dao;

import java.io.Closeable;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import app.socketiot.server.core.model.token.TokenBase;
import app.socketiot.server.core.model.token.VerifyUserToken;
import app.socketiot.server.utils.SerializationUtil;

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

    public void addToken(String token, TokenBase tk) {
        tokens.put(token, tk);
        cleanTokens();
    }

    public void removeToken(String token) {
        tokens.remove(token);
    }

    public TokenBase getToken(String token) {
        return tokens.get(token);
    }

    public VerifyUserToken getVerifyUserToken(String token) {
        TokenBase tk = getToken(token);
        if (tk instanceof VerifyUserToken) {
            return (VerifyUserToken) tk;
        }
        return null;
    }

    public boolean ifUserExists(String email) {
        TokenBase tk = tokens.values().stream().filter(t -> t.email.equals(email)).findFirst().orElse(null);
        if (tk != null && tk instanceof VerifyUserToken) {
            return true;
        }
        return false;
    }

    @Override
    public void close() {
        SerializationUtil.serialize(Paths.get(dataFolder, DEFAULT_TOKEN_FILE), tokens);
    }
}
