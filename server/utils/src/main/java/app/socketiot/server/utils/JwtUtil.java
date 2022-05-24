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

    public String createToken(String email, int seconds) {
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis() + (seconds * 1000)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean isTokenExpired(String token) throws IllegalArgumentException {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build().parseClaimsJws(token).getBody().getExpiration().before(new Date());
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
