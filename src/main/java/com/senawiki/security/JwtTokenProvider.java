package com.senawiki.security;

import com.senawiki.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final Key key;
    private final long accessTokenValiditySeconds;
    private final long refreshTokenValiditySeconds;

    public JwtTokenProvider(
        @Value("${app.jwt.secret}") String secret,
        @Value("${app.jwt.access-token-validity-seconds:900}") long accessTokenValiditySeconds,
        @Value("${app.jwt.refresh-token-validity-seconds:604800}") long refreshTokenValiditySeconds
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    public String generateAccessToken(User user) {
        return generateToken(user, accessTokenValiditySeconds);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, refreshTokenValiditySeconds);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.debug("JWT expired: {}", ex.getMessage());
            return false;
        } catch (SignatureException ex) {
            log.debug("JWT signature invalid: {}", ex.getMessage());
            return false;
        } catch (MalformedJwtException ex) {
            log.debug("JWT malformed: {}", ex.getMessage());
            return false;
        } catch (UnsupportedJwtException ex) {
            log.debug("JWT unsupported: {}", ex.getMessage());
            return false;
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("JWT invalid: {}", ex.getMessage());
            return false;
        }
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    public long getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    public Instant getRefreshExpiryInstant() {
        return Instant.now().plusSeconds(refreshTokenValiditySeconds);
    }

    private String generateToken(User user, long validitySeconds) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(validitySeconds);
        return Jwts.builder()
            .setSubject(user.getEmail())
            .claim("role", user.getRole().name())
            .claim("authorities", List.of("ROLE_" + user.getRole().name()))
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiry))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
