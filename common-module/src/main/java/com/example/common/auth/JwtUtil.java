package com.example.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类：签发、解析、校验 Token
 *
 * @author demo
 */
public class JwtUtil {

    /** 密钥（生产环境应从配置中心读取） */
    private static final String SECRET = "MySpringCloudDemo-JWT-Secret-Key-2024-Long-Enough-For-HS256";
    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000L; // 24 小时

    private static SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 签发 Token
     *
     * @param userId   用户 ID
     * @param username 用户名
     * @return JWT Token 字符串
     */
    public static String generateToken(Long userId, String username) {
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 解析 Token
     *
     * @param token JWT Token
     * @return Claims 对象
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从 Token 中获取用户 ID
     *
     * @param token JWT Token
     * @return 用户 ID
     */
    public static Long getUserIdFromToken(String token) {
        return parseToken(token).get("userId", Long.class);
    }

    /**
     * 从 Token 中获取用户名
     *
     * @param token JWT Token
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }
}
