package app.socketiot.server.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {
    static String secret = "eZ3X4gkdyK7jZV64WK6uJk3WMcJ7aq5GQLmgmw5cdYZJ3Rb9KRtjeb3s5Szhq9TcrmW8QvyDjLmXpNExt5Fq3PPFZPksLV3DVnS5YQtpasTwEq8qaj2JBrgXnHEqyPp6LSBWfdG8HJxykvrgyEK8KeVKb9Rxsx9JpnZZEss25ZFGCfekAP57Q2LzcUXqzBm5NG8ArfVhqUxAgMg6jdQsRyfMNpcZ3wedCHzyUcCKZJajCusdENAPPdBj8Wsev5ED";

    static SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

    public static String createToken(String email, long seconds) {
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(seconds != 0 ? new Date(System.currentTimeMillis() + seconds * 1000) : null)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public static String createToken(long seconds) {
        return createToken(null, seconds);
    }

    public static String createToken() {
        return createToken(0);
    }

    public static String getEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build().parseClaimsJws(token).getBody().getSubject();
    }

    public static boolean verifyToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
