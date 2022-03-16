package app.socketiot.server.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {

    private static SecretKey key;

    public JwtUtil(String secret) {
        if (secret == null) {
            secret = RandomUtil.unique();
        }
        key = Keys.hmacShaKeyFor(Sha256Util.createHash(secret, secret).getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String email, long seconds) {
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(seconds != 0 ? new Date(System.currentTimeMillis() + seconds * 1000) : null)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createToken(long seconds) {
        return createToken(null, seconds);
    }

    public String createToken() {
        return createToken(0);
    }

    public String getEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean verifyToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
