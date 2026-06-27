package com.example.common.auth;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @Test
    void generateToken_and_parseToken_shouldWork() {
        Long userId = 1L;
        String username = "testuser";

        String token = JwtUtil.generateToken(userId, username);
        assertNotNull(token);
        assertFalse(token.isEmpty());

        Claims claims = JwtUtil.parseToken(token);
        assertEquals(userId, claims.get("userId", Long.class));
        assertEquals(username, claims.getSubject());
    }

    @Test
    void getUserIdFromToken_shouldReturnUserId() {
        String token = JwtUtil.generateToken(42L, "alice");
        assertEquals(42L, JwtUtil.getUserIdFromToken(token));
    }

    @Test
    void getUsernameFromToken_shouldReturnUsername() {
        String token = JwtUtil.generateToken(1L, "bob");
        assertEquals("bob", JwtUtil.getUsernameFromToken(token));
    }

    @Test
    void parseToken_withInvalidToken_shouldThrow() {
        assertThrows(Exception.class, () -> JwtUtil.parseToken("invalid.token.here"));
    }
}
