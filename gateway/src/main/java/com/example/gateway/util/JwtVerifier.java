package com.example.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtVerifier {

    @Value("${jwt.secret}")
    private String secret;

    public Claims verify(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getTenantId(Claims claims) {
        Object tid = claims.get("tid");
        return tid != null ? tid.toString() : null;
    }

    private Key key() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}


