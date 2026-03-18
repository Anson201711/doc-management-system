package com.example.collab.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.collab.client.DocumentClient;
import com.example.collab.dto.AnnotationCreateDTO;
import com.example.collab.dto.AnnotationResponseDTO;
import com.example.collab.entity.Annotation;
import com.example.collab.mapper.AnnotationMapper;
import com.example.collab.websocket.CollabWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Annotation Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnnotationService {

    private final AnnotationMapper annotationMapper;
    private final DocumentClient documentClient;

    /**
     * Create annotation
     */
    @Transactional(rollbackFor = Exception.class)
    public AnnotationResponseDTO create(AnnotationCreateDTO dto) {
        // Validate document exists
        if (dto.getDocumentId() != null) {
            try {
                documentClient.checkDocumentExists(dto.getDocumentId());
            } catch (Exception e) {
                log.warn("Document validation failed: {}", e.getMessage());
            }
        }
        
        Annotation annotation = new Annotation();
        BeanUtils.copyProperties(dto, annotation);
        annotation.setStatus("active");
        annotationMapper.insert(annotation);
        
        // Broadcast to WebSocket
        AnnotationResponseDTO result = toResponseDTO(annotation);
        try {
            CollabWebSocketHandler.broadcastToDocument(
                dto.getDocumentId(), 
                "ANNOTATION_ADDED", 
                Map.of("annotation", result)
            );
        } catch (Exception e) {
            log.warn("WebSocket broadcast failed: {}", e.getMessage());
        }
        
        return result;
    }

    /**
     * Get annotation by ID
     */
    public AnnotationResponseDTO getById(Long id) {
        Annotation annotation = annotationMapper.selectById(id);
        return annotation != null ? toResponseDTO(annotation) : null;
    }

    /**
     * Get annotations by document ID
     */
    public List<AnnotationResponseDTO> getByDocumentId(Long documentId) {
        LambdaQueryWrapper<Annotation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Annotation::getDocumentId, documentId)
               .eq(Annotation::getStatus, "active")
               .orderByAsc(Annotation::getPageNumber)
               .orderByAsc(Annotation::getCreatedAt);
        List<Annotation> annotations = annotationMapper.selectList(wrapper);
        return annotations.stream().map(this::toResponseDTO).toList();
    }

    /**
     * Update annotation
     */
    @Transactional(rollbackFor = Exception.class)
    public AnnotationResponseDTO update(Long id, AnnotationCreateDTO dto) {
        Annotation annotation = annotationMapper.selectById(id);
        if (annotation == null) {
            throw new RuntimeException("Annotation not found");
        }
        BeanUtils.copyProperties(dto, annotation);
        annotationMapper.updateById(annotation);
        return toResponseDTO(annotation);
    }

    /**
     * Delete annotation (soft delete)
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Annotation annotation = annotationMapper.selectById(id);
        if (annotation != null) {
            annotation.setStatus("deleted");
            annotationMapper.updateById(annotation);
            
            // Broadcast to WebSocket
            try {
                CollabWebSocketHandler.broadcastToDocument(
                    annotation.getDocumentId(), 
                    "ANNOTATION_DELETED", 
                    Map.of("annotationId", id)
                );
            } catch (Exception e) {
                log.warn("WebSocket broadcast failed: {}", e.getMessage());
            }
        }
    }

    /**
     * Convert entity to DTO
     */
    private AnnotationResponseDTO toResponseDTO(Annotation annotation) {
        AnnotationResponseDTO dto = new AnnotationResponseDTO();
        BeanUtils.copyProperties(annotation, dto);
        return dto;
    }
}