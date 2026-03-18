package com.docman.document.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.docman.document.dto.DocumentCreateDTO;
import com.docman.document.dto.DocumentUpdateDTO;
import com.docman.document.entity.Document;
import com.docman.document.entity.DocumentVersion;
import com.docman.document.mapper.DocumentMapper;
import com.docman.document.mapper.DocumentVersionMapper;
import com.docman.document.vo.DocumentVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文档服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document> implements DocumentService {
    
    private final DocumentVersionMapper documentVersionMapper;
    private final ObjectMapper objectMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentVO create(DocumentCreateDTO dto, Long creatorId) {
        Document document = new Document();
        document.setTitle(dto.getTitle());
        document.setContent(dto.getContent());
        document.setFolderId(dto.getFolderId());
        document.setCreatorId(creatorId);
        document.setStatus("draft");
        document.setDocumentType(dto.getDocumentType());
        document.setCurrentVersion(1);
        
        // 处理标签
        if (dto.getTags() != null && dto.getTags().length > 0) {
            try {
                document.setTags(objectMapper.writeValueAsString(dto.getTags()));
            } catch (JsonProcessingException e) {
                log.error("标签序列化失败", e);
            }
        }
        
        this.save(document);
        
        // 创建初始版本
        DocumentVersion version = new DocumentVersion();
        version.setDocumentId(document.getId());
        version.setVersion(1);
        version.setContent(dto.getContent());
        version.setChangeSummary("初始版本");
        version.setCreatorId(creatorId);
        documentVersionMapper.insert(version);
        
        return toVO(document);
    }
    
    @Override
    public DocumentVO getById(Long id) {
        Document document = this.getById(id);
        if (document == null) {
            return null;
        }
        return toVO(document);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentVO update(Long id, DocumentUpdateDTO dto) {
        Document document = this.getById(id);
        if (document == null) {
            throw new RuntimeException("文档不存在");
        }
        
        // 如果内容变更，创建新版本
        boolean contentChanged = dto.getContent() != null && !dto.getContent().equals(document.getContent());
        
        document.setTitle(dto.getTitle());
        if (dto.getContent() != null) {
            document.setContent(dto.getContent());
        }
        if (dto.getStatus() != null) {
            document.setStatus(dto.getStatus());
        }
        if (dto.getDocumentType() != null) {
            document.setDocumentType(dto.getDocumentType());
        }
        
        // 处理标签
        if (dto.getTags() != null) {
            try {
                document.setTags(objectMapper.writeValueAsString(dto.getTags()));
            } catch (JsonProcessingException e) {
                log.error("标签序列化失败", e);
            }
        }
        
        this.updateById(document);
        
        // 内容变更时创建新版本
        if (contentChanged) {
            int newVersion = document.getCurrentVersion() + 1;
            document.setCurrentVersion(newVersion);
            this.updateById(document);
            
            DocumentVersion version = new DocumentVersion();
            version.setDocumentId(document.getId());
            version.setVersion(newVersion);
            version.setContent(dto.getContent());
            version.setChangeSummary(dto.getChangeSummary());
            version.setCreatorId(document.getCreatorId());
            documentVersionMapper.insert(version);
        }
        
        return toVO(document);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        this.removeById(id);
    }
    
    @Override
    public List<DocumentVO> list(Long folderId, Long creatorId, String status, int page, int size) {
        Page<Document> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();
        if (folderId != null) {
            wrapper.eq(Document::getFolderId, folderId);
        }
        if (creatorId != null) {
            wrapper.eq(Document::getCreatorId, creatorId);
        }
        if (status != null) {
            wrapper.eq(Document::getStatus, status);
        }
        
        wrapper.orderByDesc(Document::getUpdatedAt);
        
        Page<Document> result = this.page(pageParam, wrapper);
        
        return result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentVO copy(Long id, Long targetFolderId, Long userId) {
        Document original = this.getById(id);
        if (original == null) {
            throw new RuntimeException("文档不存在");
        }
        
        Document copy = new Document();
        copy.setTitle(original.getTitle() + " (副本)");
        copy.setContent(original.getContent());
        copy.setFolderId(targetFolderId);
        copy.setCreatorId(userId);
        copy.setStatus(original.getStatus());
        copy.setDocumentType(original.getDocumentType());
        copy.setTags(original.getTags());
        copy.setCurrentVersion(1);
        
        this.save(copy);
        
        // 复制版本历史
        LambdaQueryWrapper<DocumentVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentVersion::getDocumentId, id);
        List<DocumentVersion> versions = documentVersionMapper.selectList(wrapper);
        
        for (DocumentVersion version : versions) {
            DocumentVersion newVersion = new DocumentVersion();
            newVersion.setDocumentId(copy.getId());
            newVersion.setVersion(version.getVersion());
            newVersion.setContent(version.getContent());
            newVersion.setChangeSummary(version.getChangeSummary());
            newVersion.setCreatorId(userId);
            documentVersionMapper.insert(newVersion);
        }
        
        return toVO(copy);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentVO move(Long id, Long targetFolderId) {
        Document document = this.getById(id);
        if (document == null) {
            throw new RuntimeException("文档不存在");
        }
        
        document.setFolderId(targetFolderId);
        this.updateById(document);
        
        return toVO(document);
    }
    
    private DocumentVO toVO(Document document) {
        DocumentVO vo = new DocumentVO();
        vo.setId(document.getId());
        vo.setTitle(document.getTitle());
        vo.setContent(document.getContent());
        vo.setFolderId(document.getFolderId());
        vo.setCreatorId(document.getCreatorId());
        vo.setStatus(document.getStatus());
        vo.setDocumentType(document.getDocumentType());
        vo.setCurrentVersion(document.getCurrentVersion());
        vo.setCreatedAt(document.getCreatedAt());
        vo.setUpdatedAt(document.getUpdatedAt());
        
        // 解析标签
        if (document.getTags() != null) {
            try {
                vo.setTags(objectMapper.readValue(document.getTags(), String[].class));
            } catch (JsonProcessingException e) {
                log.error("标签反序列化失败", e);
            }
        }
        
        return vo;
    }
}