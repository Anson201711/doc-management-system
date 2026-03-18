package com.example.document.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.document.dto.DocumentCreateDTO;
import com.example.document.dto.DocumentResponseDTO;
import com.example.document.dto.DocumentUpdateDTO;
import com.example.document.entity.Document;
import com.example.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {
    
    private final DocumentRepository documentRepository;
    
    @Transactional
    public DocumentResponseDTO createDocument(DocumentCreateDTO dto) {
        Document document = new Document();
        BeanUtils.copyProperties(dto, document);
        document.setStatus("DRAFT");
        document.setVersion("1.0");
        document.setCreateTime(LocalDateTime.now());
        document.setUpdateTime(LocalDateTime.now());
        
        documentRepository.insert(document);
        return toDTO(document);
    }
    
    @Transactional
    public DocumentResponseDTO updateDocument(DocumentUpdateDTO dto) {
        Document document = documentRepository.selectById(dto.getId());
        if (document == null) {
            throw new RuntimeException("文档不存在");
        }
        
        if (dto.getTitle() != null) document.setTitle(dto.getTitle());
        if (dto.getContent() != null) document.setContent(dto.getContent());
        if (dto.getDocType() != null) document.setDocType(dto.getDocType());
        if (dto.getStatus() != null) document.setStatus(dto.getStatus());
        if (dto.getVersion() != null) document.setVersion(dto.getVersion());
        if (dto.getFolderId() != null) document.setFolderId(dto.getFolderId());
        if (dto.getFileUrl() != null) document.setFileUrl(dto.getFileUrl());
        document.setUpdateTime(LocalDateTime.now());
        
        documentRepository.updateById(document);
        return toDTO(document);
    }
    
    @Transactional
    public void deleteDocument(Long id) {
        Document document = documentRepository.selectById(id);
        if (document == null) {
            throw new RuntimeException("文档不存在");
        }
        documentRepository.deleteById(id);
    }
    
    public DocumentResponseDTO getDocumentById(Long id) {
        Document document = documentRepository.selectById(id);
        if (document == null) {
            throw new RuntimeException("文档不存在");
        }
        return toDTO(document);
    }
    
    public List<DocumentResponseDTO> getAllDocuments() {
        List<Document> documents = documentRepository.selectList(null);
        return documents.stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    public IPage<DocumentResponseDTO> getDocumentsPage(int pageNum, int pageSize) {
        Page<Document> page = new Page<>(pageNum, pageSize);
        IPage<Document> docPage = documentRepository.selectPage(page, null);
        return docPage.convert(this::toDTO);
    }
    
    public List<DocumentResponseDTO> getDocumentsByCreator(Long creatorId) {
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Document::getCreatorId, creatorId);
        List<Document> documents = documentRepository.selectList(wrapper);
        return documents.stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    public List<DocumentResponseDTO> getDocumentsByFolder(Long folderId) {
        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Document::getFolderId, folderId);
        List<Document> documents = documentRepository.selectList(wrapper);
        return documents.stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    private DocumentResponseDTO toDTO(Document document) {
        DocumentResponseDTO dto = new DocumentResponseDTO();
        BeanUtils.copyProperties(document, dto);
        return dto;
    }
}