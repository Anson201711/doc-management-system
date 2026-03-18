package com.example.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.user.dto.UserCreateDTO;
import com.example.user.dto.UserResponseDTO;
import com.example.user.dto.UserUpdateDTO;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public UserResponseDTO createUser(UserCreateDTO dto) {
        // Check if username exists
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        Long count = userRepository.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }
        
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        userRepository.insert(user);
        return toDTO(user);
    }
    
    @Transactional
    public UserResponseDTO updateUser(UserUpdateDTO dto) {
        User user = userRepository.selectById(dto.getId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getDepartment() != null) user.setDepartment(dto.getDepartment());
        if (dto.getPosition() != null) user.setPosition(dto.getPosition());
        if (dto.getStatus() != null) user.setStatus(dto.getStatus());
        user.setUpdateTime(LocalDateTime.now());
        
        userRepository.updateById(user);
        return toDTO(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        userRepository.deleteById(id);
    }
    
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return toDTO(user);
    }
    
    public UserResponseDTO getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userRepository.selectOne(wrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return toDTO(user);
    }
    
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.selectList(null);
        return users.stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    public IPage<UserResponseDTO> getUsersPage(int pageNum, int pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        IPage<User> userPage = userRepository.selectPage(page, null);
        return userPage.convert(this::toDTO);
    }
    
    public boolean existsByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userRepository.selectCount(wrapper) > 0;
    }
    
    private UserResponseDTO toDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
}