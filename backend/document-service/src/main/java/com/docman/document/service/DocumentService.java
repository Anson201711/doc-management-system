package com.docman.document.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.docman.document.dto.DocumentCreateDTO;
import com.docman.document.dto.DocumentUpdateDTO;
import com.docman.document.entity.Document;
import com.docman.document.mapper.DocumentMapper;
import com.docman.document.vo.DocumentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 文档服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentMapper documentMapper;
    private final SearchService searchService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 创建文档
     */
    @Transactional
    @CacheEvict(value = "documents", allEntries = true)
    public DocumentVO create(DocumentCreateDTO dto, Long userId) {
        Document document = new Document();
        document.setTitle(dto.getTitle());
        document.setContent(dto.getContent());
        document.setFolderId(dto.getFolderId());
        document.setStatus("draft");
        document.setCreatorId(userId);
        document.setDeleted(0);
        document.setCreatedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());
        
        documentMapper.insert(document);
        
        log.info("用户 {} 创建文档 {}", userId, document.getId());
        
        // 同步到搜索索引
        searchService.syncDocument(document.getId());
        
        return toVO(document);
    }

    /**
     * 根据ID获取文档
     */
    @Cacheable(value = "document_detail", key = "#id", unless = "#result == null")
    public DocumentVO getById(Long id) {
        Document document = documentMapper.selectById(id);
        return document != null ? toVO(document) : null;
    }

    /**
     * 更新文档
     */
    @Transactional
    @CacheEvict(value = {"documents", "document_detail"}, key = "#id")
    public DocumentVO update(Long id, DocumentUpdateDTO dto) {
        Document document = documentMapper.selectById(id);
        if (document == null) {
            throw new RuntimeException("文档不存在");
        }
        
        if (dto.getTitle() != null) {
            document.setTitle(dto.getTitle());
        }
        if (dto.getContent() != null) {
            document.setContent(dto.getContent());
        }
        if (dto.getStatus() != null) {
            document.setStatus(dto.getStatus());
        }
        if (dto.getFolderId() != null) {
            document.setFolderId(dto.getFolderId());
        }
        
        document.setUpdatedAt(LocalDateTime.now());
        documentMapper.updateById(document);
        
        // 同步到搜索索引
        searchService.syncDocument(id);
        
        log.info("更新文档 {}", id);
        return toVO(document);
    }

    /**
     * 删除文档
     */
    @Transactional
    @CacheEvict(value = {"documents", "document_detail"}, key = "#id")
    public void delete(Long id) {
        Document document = documentMapper.selectById(id);
        if (document == null) {
            throw new RuntimeException("文档不存在");
        }
        
        document.setDeleted(1);
        document.setUpdatedAt(LocalDateTime.now());
        documentMapper.updateById(document);
        
        // 删除搜索索引
        searchService.deleteDocumentIndex(id);
        
        log.info("删除文档 {}", id);
    }

    /**
     * 文档列表
     */
    @Cacheable(value = "documents", key = "#folderId + ':' + #creatorId + ':' + #status + ':' + #page + ':' + #size", 
                unless = "#result.size() > 100")
    public List<DocumentVO> list(Long folderId, Long creatorId, String status, int page, int size) {
        Page<Document> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Document::getDeleted, 0);
        
        if (folderId != null) {
            wrapper.eq(Document::getFolderId, folderId);
        }
        if (creatorId != null) {
            wrapper.eq(Document::getCreatorId, creatorId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Document::getStatus, status);
        }
        
        wrapper.orderByDesc(Document::getUpdatedAt);
        
        IPage<Document> pageResult = documentMapper.selectPage(pageParam, wrapper);
        
        return pageResult.getRecords().stream()
                .map(this::toVO)
                .toList();
    }

    /**
     * 复制文档
     */
    @Transactional
    @CacheEvict(value = "documents", allEntries = true)
    public DocumentVO copy(Long id, Long targetFolderId, Long userId) {
        Document source = documentMapper.selectById(id);
        if (source == null) {
            throw new RuntimeException("源文档不存在");
        }
        
        Document target = new Document();
        target.setTitle(source.getTitle() + " (副本)");
        target.setContent(source.getContent());
        target.setFolderId(targetFolderId != null ? targetFolderId : source.getFolderId());
        target.setStatus("draft");
        target.setCreatorId(userId);
        target.setDeleted(0);
        target.setCreatedAt(LocalDateTime.now());
        target.setUpdatedAt(LocalDateTime.now());
        
        documentMapper.insert(target);
        
        // 同步到搜索索引
        searchService.syncDocument(target.getId());
        
        log.info("用户 {} 复制文档 {} 到 {}", userId, id, target.getId());
        return toVO(target);
    }

    /**
     * 移动文档
     */
    @Transactional
    @CacheEvict(value = {"documents", "document_detail"}, key = "#id")
    public DocumentVO move(Long id, Long targetFolderId) {
        Document document = documentMapper.selectById(id);
        if (document == null) {
            throw new RuntimeException("文档不存在");
        }
        
        document.setFolderId(targetFolderId);
        document.setUpdatedAt(LocalDateTime.now());
        documentMapper.updateById(document);
        
        // 同步到搜索索引
        searchService.syncDocument(id);
        
        log.info("移动文档 {} 到文件夹 {}", id, targetFolderId);
        return toVO(document);
    }

    private DocumentVO toVO(Document doc) {
        if (doc == null) return null;
        
        DocumentVO vo = new DocumentVO();
        vo.setId(doc.getId());
        vo.setTitle(doc.getTitle());
        vo.setContent(doc.getContent());
        vo.setFolderId(doc.getFolderId());
        vo.setStatus(doc.getStatus());
        vo.setCreatorId(doc.getCreatorId());
        vo.setCreatedAt(doc.getCreatedAt() != null ? doc.getCreatedAt().format(FORMATTER) : null);
        vo.setUpdatedAt(doc.getUpdatedAt() != null ? doc.getUpdatedAt().format(FORMATTER) : null);
        
        return vo;
    }
}