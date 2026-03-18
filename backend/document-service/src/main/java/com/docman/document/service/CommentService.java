package com.docman.document.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.docman.document.dto.CommentCreateDTO;
import com.docman.document.dto.CommentVO;
import com.docman.document.entity.Document;
import com.docman.document.entity.DocumentComment;
import com.docman.document.mapper.DocumentCommentMapper;
import com.docman.document.mapper.DocumentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文档评论服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final DocumentCommentMapper commentMapper;
    private final DocumentMapper documentMapper;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 创建评论
     */
    @Transactional
    public CommentVO create(CommentCreateDTO dto, Long userId, String userName) {
        // 验证文档存在
        Document document = documentMapper.selectById(dto.getDocumentId());
        if (document == null) {
            throw new RuntimeException("文档不存在");
        }

        // 如果是回复，验证父评论存在
        if (dto.getParentId() != null) {
            DocumentComment parent = commentMapper.selectById(dto.getParentId());
            if (parent == null) {
                throw new RuntimeException("父评论不存在");
            }
        }

        DocumentComment comment = new DocumentComment();
        comment.setDocumentId(dto.getDocumentId());
        comment.setParentId(dto.getParentId());
        comment.setContent(dto.getContent());
        comment.setCommentType(dto.getCommentType() != null ? dto.getCommentType() : "comment");
        comment.setStartPosition(dto.getStartPosition());
        comment.setEndPosition(dto.getEndPosition());
        comment.setSelectedText(dto.getSelectedText());
        comment.setCreatorId(userId);
        comment.setCreatorName(userName);
        comment.setDeleted(0);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        commentMapper.insert(comment);
        
        log.info("用户 {} 在文档 {} 创建评论成功", userId, dto.getDocumentId());
        
        return toVO(comment);
    }

    /**
     * 获取文档评论列表
     */
    public List<CommentVO> listByDocumentId(Long documentId, int page, int size) {
        Page<DocumentComment> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<DocumentComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentComment::getDocumentId, documentId)
               .orderByAsc(DocumentComment::getCreatedAt);
        
        IPage<DocumentComment> commentPage = commentMapper.selectPage(pageParam, wrapper);
        
        return toVOList(commentPage.getRecords());
    }

    /**
     * 获取评论详情
     */
    public CommentVO getById(Long id) {
        DocumentComment comment = commentMapper.selectById(id);
        return comment != null ? toVO(comment) : null;
    }

    /**
     * 更新评论
     */
    @Transactional
    public CommentVO update(Long id, String content, Long userId) {
        DocumentComment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        if (!comment.getCreatorId().equals(userId)) {
            throw new RuntimeException("无权限修改此评论");
        }

        comment.setContent(content);
        comment.setUpdatedAt(LocalDateTime.now());
        commentMapper.updateById(comment);

        return toVO(comment);
    }

    /**
     * 删除评论
     */
    @Transactional
    public void delete(Long id, Long userId) {
        DocumentComment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        // 检查是否是评论作者
        if (!comment.getCreatorId().equals(userId)) {
            throw new RuntimeException("无权限删除此评论");
        }

        // 软删除
        comment.setDeleted(1);
        comment.setUpdatedAt(LocalDateTime.now());
        commentMapper.updateById(comment);
        
        log.info("用户 {} 删除评论 {}", userId, id);
    }

    /**
     * 获取评论的回复
     */
    public List<CommentVO> getReplies(Long parentId) {
        LambdaQueryWrapper<DocumentComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentComment::getParentId, parentId)
               .orderByAsc(DocumentComment::getCreatedAt);
        
        List<DocumentComment> comments = commentMapper.selectList(wrapper);
        return toVOList(comments);
    }

    /**
     * 获取评论及其回复（树形结构）
     */
    public List<CommentVO> getCommentsWithReplies(Long documentId) {
        // 获取所有顶级评论
        LambdaQueryWrapper<DocumentComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentComment::getDocumentId, documentId)
               .isNull(DocumentComment::getParentId)
               .orderByAsc(DocumentComment::getCreatedAt);
        
        List<DocumentComment> topComments = commentMapper.selectList(wrapper);
        
        // 获取所有回复
        List<Long> parentIds = topComments.stream()
                .map(DocumentComment::getId)
                .collect(Collectors.toList());
        
        Map<Long, List<DocumentComment>> replyMap = Map.of();
        if (!parentIds.isEmpty()) {
            LambdaQueryWrapper<DocumentComment> replyWrapper = new LambdaQueryWrapper<>();
            replyWrapper.in(DocumentComment::getParentId, parentIds)
                       .orderByAsc(DocumentComment::getCreatedAt);
            List<DocumentComment> allReplies = commentMapper.selectList(replyWrapper);
            replyMap = allReplies.stream()
                    .collect(Collectors.groupingBy(DocumentComment::getParentId));
        }
        
        // 转换为VO并设置回复
        List<CommentVO> result = new ArrayList<>();
        for (DocumentComment comment : topComments) {
            CommentVO vo = toVO(comment);
            List<DocumentComment> replies = replyMap.getOrDefault(comment.getId(), new ArrayList<>());
            vo.setReplyCount(replies.size());
            result.add(vo);
        }
        
        return result;
    }

    private CommentVO toVO(DocumentComment comment) {
        if (comment == null) return null;
        
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setDocumentId(comment.getDocumentId());
        vo.setParentId(comment.getParentId());
        vo.setContent(comment.getContent());
        vo.setCommentType(comment.getCommentType());
        vo.setStartPosition(comment.getStartPosition());
        vo.setEndPosition(comment.getEndPosition());
        vo.setSelectedText(comment.getSelectedText());
        vo.setCreatorId(comment.getCreatorId());
        vo.setCreatorName(comment.getCreatorName());
        vo.setCreatedAt(comment.getCreatedAt() != null ? 
                comment.getCreatedAt().format(FORMATTER) : null);
        vo.setUpdatedAt(comment.getUpdatedAt() != null ? 
                comment.getUpdatedAt().format(FORMATTER) : null);
        
        // 统计回复数
        LambdaQueryWrapper<DocumentComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentComment::getParentId, comment.getId());
        vo.setReplyCount((int) commentMapper.selectCount(wrapper));
        
        return vo;
    }

    private List<CommentVO> toVOList(List<DocumentComment> comments) {
        return comments.stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }
}