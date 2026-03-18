package com.docman.document.controller;

import com.docman.document.dto.CommentCreateDTO;
import com.docman.document.dto.CommentVO;
import com.docman.document.service.CommentService;
import com.docman.document.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论管理控制器
 */
@Tag(name = "评论管理")
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "创建评论")
    @PostMapping
    public Result<CommentVO> create(@Valid @RequestBody CommentCreateDTO dto,
                                      @RequestHeader("X-User-Id") Long userId,
                                      @RequestHeader(value = "X-User-Name", defaultValue = "User") String userName) {
        CommentVO vo = commentService.create(dto, userId, userName);
        return Result.success(vo);
    }

    @Operation(summary = "获取文档评论列表")
    @GetMapping("/documents/{docId}")
    public Result<List<CommentVO>> listByDocument(@PathVariable Long docId,
                                                    @RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "20") int size) {
        List<CommentVO> list = commentService.listByDocumentId(docId, page, size);
        return Result.success(list);
    }

    @Operation(summary = "获取评论详情")
    @GetMapping("/{id}")
    public Result<CommentVO> getById(@PathVariable Long id) {
        CommentVO vo = commentService.getById(id);
        return vo != null ? Result.success(vo) : Result.error(404, "评论不存在");
    }

    @Operation(summary = "更新评论")
    @PutMapping("/{id}")
    public Result<CommentVO> update(@PathVariable Long id,
                                     @RequestParam String content,
                                     @RequestHeader("X-User-Id") Long userId) {
        CommentVO vo = commentService.update(id, content, userId);
        return Result.success(vo);
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                                @RequestHeader("X-User-Id") Long userId) {
        commentService.delete(id, userId);
        return Result.success();
    }

    @Operation(summary = "获取评论的回复")
    @GetMapping("/{id}/replies")
    public Result<List<CommentVO>> getReplies(@PathVariable Long id) {
        List<CommentVO> replies = commentService.getReplies(id);
        return Result.success(replies);
    }

    @Operation(summary = "获取评论树（带回复）")
    @GetMapping("/documents/{docId}/tree")
    public Result<List<CommentVO>> getCommentsTree(@PathVariable Long docId) {
        List<CommentVO> tree = commentService.getCommentsWithReplies(docId);
        return Result.success(tree);
    }
}