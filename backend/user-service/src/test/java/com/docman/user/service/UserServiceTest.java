package com.docman.user.service;

import com.docman.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务单元测试
 */
@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testFindByUsername() {
        User user = userService.findByUsername("admin");
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
        assertEquals("admin@docman.com", user.getEmail());
    }

    @Test
    void testFindByEmail() {
        User user = userService.findByEmail("admin@docman.com");
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
    }

    @Test
    void testFindById() {
        User user = userService.findById(1L);
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
    }

    @Test
    void testExistsByUsername() {
        assertTrue(userService.existsByUsername("admin"));
        assertFalse(userService.existsByUsername("nonexistent"));
    }

    @Test
    void testExistsByEmail() {
        assertTrue(userService.existsByEmail("admin@docman.com"));
        assertFalse(userService.existsByEmail("nonexistent@test.com"));
    }

    @Test
    void testVerifyPassword() {
        User user = userService.findByUsername("admin");
        assertTrue(userService.verifyPassword(user, "password123"));
        assertFalse(userService.verifyPassword(user, "wrongpassword"));
    }

    @Test
    void testUpdateUser() {
        User user = userService.updateUser(1L, "新名字", null, null, null);
        assertNotNull(user);
        assertEquals("新名字", user.getFullName());
    }
}