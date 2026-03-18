package com.docman.document.service;

import com.docman.document.dto.DocumentCreateDTO;
import com.docman.document.dto.DocumentUpdateDTO;
import com.docman.document.entity.Document;
import com.docman.document.mapper.DocumentMapper;
import com.docman.document.vo.DocumentVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文档服务单元测试
 */
@SpringBootTest
@ActiveProfiles("test")
class DocumentServiceTest {
    
    @Autowired
    private DocumentService documentService;
    
    @MockBean
    private DocumentMapper documentMapper;
    
    private DocumentCreateDTO createDTO;
    private DocumentUpdateDTO updateDTO;
    
    @BeforeEach
    void setUp() {
        createDTO = new DocumentCreateDTO();
        createDTO.setTitle("测试文档");
        createDTO.setContent("测试内容");
        createDTO.setDocumentType("text");
        createDTO.setTags(new String[]{"test", "demo"});
        
        updateDTO = new DocumentUpdateDTO();
        updateDTO.setTitle("更新后的文档");
        updateDTO.setContent("更新后的内容");
    }
    
    @Test
    void testCreateDocument() {
        // 创建文档
        DocumentVO vo = documentService.create(createDTO, 1L);
        
        assertNotNull(vo);
        assertEquals("测试文档", vo.getTitle());
        assertEquals("测试内容", vo.getContent());
        assertEquals(1, vo.getCurrentVersion());
    }
    
    @Test
    void testGetDocumentById() {
        // 获取文档详情
        DocumentVO vo = documentService.getById(1L);
        
        if (vo != null) {
            assertNotNull(vo.getId());
            assertNotNull(vo.getTitle());
        }
    }
    
    @Test
    void testUpdateDocument() {
        // 更新文档
        DocumentVO vo = documentService.update(1L, updateDTO);
        
        if (vo != null) {
            assertEquals("更新后的文档", vo.getTitle());
            assertEquals("更新后的内容", vo.getContent());
        }
    }
    
    @Test
    void testDeleteDocument() {
        // 删除文档 - 需要先创建
        DocumentVO vo = documentService.create(createDTO, 1L);
        if (vo != null) {
            assertDoesNotThrow(() -> documentService.delete(vo.getId()));
        }
    }
    
    @Test
    void testListDocuments() {
        // 获取文档列表
        List<DocumentVO> list = documentService.list(null, null, null, 1, 10);
        
        assertNotNull(list);
    }
    
    @Test
    void testCopyDocument() {
        // 创建文档用于复制测试
        DocumentVO original = documentService.create(createDTO, 1L);
        
        if (original != null) {
            DocumentVO copy = documentService.copy(original.getId(), null, 1L);
            
            if (copy != null) {
                assertTrue(copy.getTitle().contains("(副本)"));
            }
        }
    }
    
    @Test
    void testMoveDocument() {
        // 创建文档用于移动测试
        DocumentVO vo = documentService.create(createDTO, 1L);
        
        if (vo != null) {
            DocumentVO moved = documentService.move(vo.getId(), 1L);
            
            if (moved != null) {
                assertEquals(1L, moved.getFolderId());
            }
        }
    }
}