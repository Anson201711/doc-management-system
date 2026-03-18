package com.docman.permission.service;

import com.docman.permission.entity.Role;
import com.docman.permission.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * RoleService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class RoleServiceTest {
    
    @Mock
    private RoleRepository roleRepository;
    
    @Mock
    private com.docman.permission.repository.RolePermissionRepository rolePermissionRepository;
    
    @InjectMocks
    private RoleService roleService;
    
    private Role testRole;
    
    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setId(1L);
        testRole.setName("admin");
        testRole.setDescription("管理员角色");
        testRole.setStatus("active");
        testRole.setCreatedAt(LocalDateTime.now());
        testRole.setUpdatedAt(LocalDateTime.now());
    }
    
    @Test
    void testFindById() {
        when(roleRepository.selectById(1L)).thenReturn(testRole);
        
        Role result = roleService.findById(1L);
        
        assertNotNull(result);
        assertEquals("admin", result.getName());
        verify(roleRepository, times(1)).selectById(1L);
    }
    
    @Test
    void testFindByIdNotFound() {
        when(roleRepository.selectById(999L)).thenReturn(null);
        
        Role result = roleService.findById(999L);
        
        assertNull(result);
    }
    
    @Test
    void testFindAll() {
        Role role2 = new Role();
        role2.setId(2L);
        role2.setName("user");
        role2.setDescription("普通用户");
        
        when(roleRepository.selectList(null)).thenReturn(Arrays.asList(testRole, role2));
        
        List<Role> results = roleService.findAll();
        
        assertEquals(2, results.size());
        verify(roleRepository, times(1)).selectList(null);
    }
    
    @Test
    void testCreate() {
        com.docman.permission.dto.RoleDTO dto = new com.docman.permission.dto.RoleDTO();
        dto.setName("editor");
        dto.setDescription("编辑角色");
        dto.setStatus("active");
        
        when(roleRepository.insert(any(Role.class))).thenReturn(1L);
        
        Role result = roleService.create(dto);
        
        assertNotNull(result);
        assertEquals("editor", result.getName());
        verify(roleRepository, times(1)).insert(any(Role.class));
    }
    
    @Test
    void testUpdate() {
        com.docman.permission.dto.RoleDTO dto = new com.docman.permission.dto.RoleDTO();
        dto.setName("updated-admin");
        dto.setDescription("更新的管理员角色");
        
        when(roleRepository.selectById(1L)).thenReturn(testRole);
        when(roleRepository.updateById(any(Role.class))).thenReturn(true);
        
        Role result = roleService.update(1L, dto);
        
        assertNotNull(result);
        assertEquals("updated-admin", result.getName());
        verify(roleRepository, times(1)).selectById(1L);
        verify(roleRepository, times(1)).updateById(any(Role.class));
    }
    
    @Test
    void testDelete() {
        when(roleRepository.deleteById(1L)).thenReturn(1);
        
        roleService.delete(1L);
        
        verify(roleRepository, times(1)).deleteById(1L);
    }
}