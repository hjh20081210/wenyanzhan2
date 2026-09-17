package com.wenyan.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/** JWT 工具 */
public final class JwtUtil {
    private static final String SECRET = "wenyan-zhan-secret-key-2024-springboot3-abcdefgh"; // >=32字节
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    private static final long EXPIRE = 30L * 24 * 3600 * 1000; // 30天

    private JwtUtil() {}

    public static String createToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + EXPIRE))
                .signWith(KEY)
                .compact();
    }

    public static Long parseUserId(String token) {
        Claims claims = Jwts.parser().verifyWith(KEY).build()
                .parseSignedClaims(token).getPayload();
        return Long.valueOf(claims.getSubject());
    }

    public static boolean isValid(String token) {
        try {
            parseUserId(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
