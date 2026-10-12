package com.secondhand.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * JWT 工具类
 */
public class JwtUtil {

    private static final String SECRET = "secondhand-campus-trading-platform-secret-2026-key-!!";
    private static final long EXPIRE = 7 * 24 * 60 * 60 * 1000L; // 7 天

    private static Key getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /** 生成 token，subject 为 userId，额外携带 role */
    public static String createToken(Long userId, String role) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** 解析 token，返回 Claims，非法/过期返回 null */
    public static Claims parse(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public static Long getUserId(String token) {
        Claims claims = parse(token);
        return claims == null ? null : Long.valueOf(claims.getSubject());
    }

    public static String getRole(String token) {
        Claims claims = parse(token);
        return claims == null ? null : claims.get("role", String.class);
    }
}
