package com.docman.permission.service;

import com.docman.permission.entity.DocPermission;
import com.docman.permission.repository.DocPermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * DocPermissionService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class DocPermissionServiceTest {
    
    @Mock
    private DocPermissionRepository docPermissionRepository;
    
    @InjectMocks
    private DocPermissionService docPermissionService;
    
    private DocPermission testDocPermission;
    
    @BeforeEach
    void setUp() {
        testDocPermission = new DocPermission();
        testDocPermission.setId(1L);
        testDocPermission.setDocumentId(100L);
        testDocPermission.setUserId(1L);
        testDocPermission.setPermissionType("read");
        testDocPermission.setCreatedBy(1L);
        testDocPermission.setCreatedAt(LocalDateTime.now());
        testDocPermission.setUpdatedAt(LocalDateTime.now());
    }
    
    @Test
    void testFindByDocumentId() {
        when(docPermissionRepository.selectByDocumentId(100L)).thenReturn(Arrays.asList(testDocPermission));
        
        List<DocPermission> results = docPermissionService.findByDocumentId(100L);
        
        assertEquals(1, results.size());
        assertEquals(100L, results.get(0).getDocumentId());
        verify(docPermissionRepository, times(1)).selectByDocumentId(100L);
    }
    
    @Test
    void testFindByDocumentIdAndUserId() {
        when(docPermissionRepository.selectByDocumentIdAndUserId(100L, 1L)).thenReturn(Arrays.asList(testDocPermission));
        
        List<DocPermission> results = docPermissionService.findByDocumentIdAndUserId(100L, 1L);
        
        assertEquals(1, results.size());
        assertEquals("read", results.get(0).getPermissionType());
    }
    
    @Test
    void testAddUserPermission_New() {
        com.docman.permission.dto.DocPermissionDTO dto = new com.docman.permission.dto.DocPermissionDTO();
        dto.setDocumentId(100L);
        dto.setUserId(2L);
        dto.setPermissionType("write");
        
        when(docPermissionRepository.selectOne(any())).thenReturn(null);
        when(docPermissionRepository.insert(any(DocPermission.class))).thenReturn(1L);
        
        DocPermission result = docPermissionService.addUserPermission(dto);
        
        assertNotNull(result);
        assertEquals("write", result.getPermissionType());
        verify(docPermissionRepository, times(1)).insert(any(DocPermission.class));
    }
    
    @Test
    void testAddUserPermission_Update() {
        com.docman.permission.dto.DocPermissionDTO dto = new com.docman.permission.dto.DocPermissionDTO();
        dto.setDocumentId(100L);
        dto.setUserId(1L);
        dto.setPermissionType("admin");
        
        when(docPermissionRepository.selectOne(any())).thenReturn(testDocPermission);
        when(docPermissionRepository.updateById(any(DocPermission.class))).thenReturn(true);
        
        DocPermission result = docPermissionService.addUserPermission(dto);
        
        assertNotNull(result);
        assertEquals("admin", result.getPermissionType());
        verify(docPermissionRepository, times(1)).updateById(any(DocPermission.class));
    }
    
    @Test
    void testDelete() {
        doNothing().when(docPermissionRepository).deleteById(1L);
        
        docPermissionService.delete(1L);
        
        verify(docPermissionRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void testHasPermission_Read() {
        when(docPermissionRepository.selectList(any())).thenReturn(Arrays.asList(testDocPermission));
        
        boolean result = docPermissionService.hasPermission(100L, 1L, "read");
        
        assertTrue(result);
    }
    
    @Test
    void testHasPermission_Write_NotHasReadPermission() {
        testDocPermission.setPermissionType("read");
        when(docPermissionRepository.selectList(any())).thenReturn(Arrays.asList(testDocPermission));
        
        boolean result = docPermissionService.hasPermission(100L, 1L, "write");
        
        assertFalse(result);
    }
    
    @Test
    void testHasPermission_Admin_HasAll() {
        testDocPermission.setPermissionType("admin");
        when(docPermissionRepository.selectList(any())).thenReturn(Arrays.asList(testDocPermission));
        
        boolean result = docPermissionService.hasPermission(100L, 1L, "write");
        
        assertTrue(result);
    }
}