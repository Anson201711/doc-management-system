package com.docman.user.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT 工具类单元测试
 */
@SpringBootTest
@ActiveProfiles("test")
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void testGenerateToken() {
        String token = jwtTokenProvider.generateToken(1L, "testuser");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testValidateToken() {
        String token = jwtTokenProvider.generateToken(1L, "testuser");
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void testInvalidToken() {
        assertFalse(jwtTokenProvider.validateToken("invalid.token.here"));
    }

    @Test
    void testParseToken() {
        String token = jwtTokenProvider.generateToken(123L, "testuser");
        Claims claims = jwtTokenProvider.parseToken(token);
        
        assertNotNull(claims);
        assertEquals("testuser", claims.getSubject());
        assertEquals(123L, claims.get("userId", Long.class));
    }

    @Test
    void testGetUsernameFromToken() {
        String token = jwtTokenProvider.generateToken(1L, "testuser");
        String username = jwtTokenProvider.getUsernameFromToken(token);
        assertEquals("testuser", username);
    }

    @Test
    void testGetUserIdFromToken() {
        String token = jwtTokenProvider.generateToken(999L, "testuser");
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        assertEquals(999L, userId);
    }

    @Test
    void testIsTokenExpired() {
        // 新生成的 token 不会过期
        String token = jwtTokenProvider.generateToken(1L, "testuser");
        assertFalse(jwtTokenProvider.isTokenExpired(token));
    }
}