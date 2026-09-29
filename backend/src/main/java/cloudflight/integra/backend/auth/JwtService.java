package cloudflight.integra.backend.auth;

import cloudflight.integra.backend.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    public String generateToken(User user) {
        Date now = new Date();
        var builder = Jwts.builder()
            .subject(user.getEmail())
            .claim("userId",user.getId())
            .claim("role", user.getRole().name())
            .issuedAt(now)
            .expiration(new Date(now.getTime() + expirationMs));

            if (user.getMxId() != null) builder.claim("mxId", user.getMxId());
            if (user.getBirthDate() != null) builder.claim("birthDate", user.getBirthDate().toString());

            return builder.signWith(key).compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
