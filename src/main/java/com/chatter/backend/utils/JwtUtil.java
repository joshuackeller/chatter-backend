package com.chatter.backend.utils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.chatter.backend.model.Account;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final String JWT_SECRET = System.getenv("JWT_SECRET"); // Read from environment
    private final Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));

    public String generateToken(Account account) {
        return Jwts.builder()
                .setSubject(account.getUsername())
                .claim("accountId", account.getId())
                .setIssuedAt(new Date())
                .signWith(key)
                .compact();
    }

    public UUID extractId(String token) {
        try {
            return UUID.fromString(Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("accountId", String.class));
        } catch (ExpiredJwtException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Token expired");
        } catch (UnsupportedJwtException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Token is not supported");
        } catch (MalformedJwtException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Token is malformed");
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid token claims");
        } catch (SecurityException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Security exception occurred");
        }
    }

    public boolean validateToken(String token, Account account) {
        return extractId(token).equals(account.getId());
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
