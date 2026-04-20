package com.yumefusaka.yuelivingapi.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.ttl}")
    private long ttl;

    private byte[] getSecretKey() {
        return secretKey.getBytes(StandardCharsets.UTF_8);
    }

    public String generateToken(Long userId, String username) {
        return generateToken(userId, username, null);
    }

    public String generateToken(Long userId, String username, Long roleId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        if (roleId != null) {
            claims.put("roleId", roleId);
        }
        return createToken(claims);
    }

    public String createToken(Map<String, Object> claims) {
        return Jwts.builder()
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, getSecretKey())
                .setExpiration(new Date(System.currentTimeMillis() + ttl))
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return getLongClaim(claims, "userId");
    }

    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    public Long getRoleIdFromToken(String token) {
        Claims claims = parseToken(token);
        return getLongClaim(claims, "roleId");
    }

    private Long getLongClaim(Claims claims, String name) {
        Object value = claims.get(name);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.valueOf((String) value);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }
}
