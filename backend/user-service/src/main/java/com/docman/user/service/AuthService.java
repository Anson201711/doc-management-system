package com.docman.user.service;

import com.docman.user.entity.User;
import com.docman.user.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 认证服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * 用户登录
     */
    public String login(String username, String password) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        if (!"active".equals(user.getStatus())) {
            throw new RuntimeException("账户已被禁用");
        }
        
        if (!userService.verifyPassword(user, password)) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 生成 Token
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());
        
        // 将用户信息存入 Redis
        String cacheKey = "user:" + user.getId();
        redisTemplate.opsForValue().set(cacheKey, user, 30, TimeUnit.MINUTES);
        
        log.info("User logged in: {}", username);
        return token;
    }

    /**
     * 用户登出
     */
    public void logout(String token, Long userId) {
        if (token != null && userId != null) {
            // 将 Token 加入黑名单
            String blackListKey = "jwt:blacklist:" + token;
            redisTemplate.opsForValue().set(blackListKey, "blacklisted", jwtExpiration, TimeUnit.MILLISECONDS);
            
            // 删除用户缓存
            String cacheKey = "user:" + userId;
            redisTemplate.delete(cacheKey);
            
            log.info("User logged out: {}", userId);
        }
    }

    /**
     * 刷新 Token
     */
    public String refreshToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("Token 无效");
        }
        
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        String username = jwtTokenProvider.getUsernameFromToken(token);
        
        // 检查 Token 是否在黑名单
        String blackListKey = "jwt:blacklist:" + token;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(blackListKey))) {
            throw new RuntimeException("Token 已失效");
        }
        
        // 生成新 Token
        return jwtTokenProvider.generateToken(userId, username);
    }

    /**
     * 获取当前用户信息
     */
    public User getCurrentUser(Long userId) {
        // 先从缓存获取
        String cacheKey = "user:" + userId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof User) {
            return (User) cached;
        }
        
        // 从数据库获取
        User user = userService.findById(userId);
        if (user != null) {
            // 存入缓存
            redisTemplate.opsForValue().set(cacheKey, user, 30, TimeUnit.MINUTES);
        }
        
        return user;
    }
}