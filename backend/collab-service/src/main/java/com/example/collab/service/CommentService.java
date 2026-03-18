package com.example.collab.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.collab.client.DocumentClient;
import com.example.collab.dto.CommentCreateDTO;
import com.example.collab.dto.CommentResponseDTO;
import com.example.collab.entity.Comment;
import com.example.collab.mapper.CommentMapper;
import com.example.collab.websocket.CollabWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Comment Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;
    private final DocumentClient documentClient;

    /**
     * Create comment
     */
    @Transactional(rollbackFor = Exception.class)
    public CommentResponseDTO create(CommentCreateDTO dto) {
        // Validate document exists
        if (dto.getDocumentId() != null) {
            try {
                documentClient.checkDocumentExists(dto.getDocumentId());
            } catch (Exception e) {
                log.warn("Document validation failed: {}", e.getMessage());
            }
        }
        
        Comment comment = new Comment();
        BeanUtils.copyProperties(dto, comment);
        comment.setStatus("active");
        commentMapper.insert(comment);
        
        // Broadcast to WebSocket
        CommentResponseDTO result = toResponseDTO(comment);
        try {
            CollabWebSocketHandler.broadcastToDocument(
                dto.getDocumentId(), 
                "COMMENT_ADDED", 
                Map.of("comment", result)
            );
        } catch (Exception e) {
            log.warn("WebSocket broadcast failed: {}", e.getMessage());
        }
        
        return result;
    }

    /**
     * Get comment by ID
     */
    public CommentResponseDTO getById(Long id) {
        Comment comment = commentMapper.selectById(id);
        return comment != null ? toResponseDTO(comment) : null;
    }

    /**
     * Get comments by document ID
     */
    public List<CommentResponseDTO> getByDocumentId(Long documentId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getDocumentId, documentId)
               .eq(Comment::getStatus, "active")
               .orderByAsc(Comment::getCreatedAt);
        List<Comment> comments = commentMapper.selectList(wrapper);
        return comments.stream().map(this::toResponseDTO).toList();
    }

    /**
     * Update comment
     */
    @Transactional(rollbackFor = Exception.class)
    public CommentResponseDTO update(Long id, CommentCreateDTO dto) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new RuntimeException("Comment not found");
        }
        BeanUtils.copyProperties(dto, comment);
        commentMapper.updateById(comment);
        return toResponseDTO(comment);
    }

    /**
     * Delete comment (soft delete)
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Comment comment = commentMapper.selectById(id);
        if (comment != null) {
            comment.setStatus("deleted");
            commentMapper.updateById(comment);
            
            // Broadcast to WebSocket
            try {
                CollabWebSocketHandler.broadcastToDocument(
                    comment.getDocumentId(), 
                    "COMMENT_DELETED", 
                    Map.of("commentId", id)
                );
            } catch (Exception e) {
                log.warn("WebSocket broadcast failed: {}", e.getMessage());
            }
        }
    }

    /**
     * Reply to comment
     */
    @Transactional(rollbackFor = Exception.class)
    public CommentResponseDTO reply(Long parentId, CommentCreateDTO dto) {
        Comment parentComment = commentMapper.selectById(parentId);
        if (parentComment == null) {
            throw new RuntimeException("Parent comment not found");
        }
        dto.setParentId(parentId);
        return create(dto);
    }

    /**
     * Convert entity to DTO
     */
    private CommentResponseDTO toResponseDTO(Comment comment) {
        CommentResponseDTO dto = new CommentResponseDTO();
        BeanUtils.copyProperties(comment, dto);
        return dto;
    }
}