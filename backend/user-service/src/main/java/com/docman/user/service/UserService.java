package com.docman.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.docman.user.entity.User;
import com.docman.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService extends ServiceImpl<UserMapper, User> {

    private final PasswordEncoder passwordEncoder;

    /**
     * 根据用户名查询用户
     */
    public User findByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }

    /**
     * 根据邮箱查询用户
     */
    public User findByEmail(String email) {
        return baseMapper.selectByEmail(email);
    }

    /**
     * 根据ID查询用户
     */
    public User findById(Long id) {
        return baseMapper.selectById(id);
    }

    /**
     * 注册新用户
     */
    @Transactional
    public User register(String username, String email, String password, String fullName) {
        // 检查用户名是否存在
        if (findByUsername(username) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否存在
        if (findByEmail(email) != null) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setStatus("active");
        
        baseMapper.insert(user);
        log.info("User registered: {}", username);
        
        return user;
    }

    /**
     * 验证用户密码
     */
    public boolean verifyPassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPasswordHash());
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public User updateUser(Long userId, String fullName, String avatarUrl, String phone, Long departmentId) {
        User user = baseMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (fullName != null) {
            user.setFullName(fullName);
        }
        if (avatarUrl != null) {
            user.setAvatarUrl(avatarUrl);
        }
        if (phone != null) {
            user.setPhone(phone);
        }
        if (departmentId != null) {
            user.setDepartmentId(departmentId);
        }
        
        baseMapper.updateById(user);
        log.info("User updated: {}", userId);
        
        return user;
    }

    /**
     * 更新密码
     */
    @Transactional
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = baseMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (!verifyPassword(user, oldPassword)) {
            throw new RuntimeException("原密码错误");
        }
        
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        baseMapper.updateById(user);
        log.info("Password updated for user: {}", userId);
    }

    /**
     * 检查用户名是否存在
     */
    public boolean existsByUsername(String username) {
        return baseMapper.selectByUsername(username) != null;
    }

    /**
     * 检查邮箱是否存在
     */
    public boolean existsByEmail(String email) {
        return baseMapper.selectByEmail(email) != null;
    }
}