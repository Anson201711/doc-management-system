package com.docman.document.controller;

import com.docman.document.dto.DocumentCreateDTO;
import com.docman.document.dto.DocumentUpdateDTO;
import com.docman.document.service.DocumentService;
import com.docman.document.vo.DocumentVO;
import com.docman.document.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文档管理控制器
 */
@Tag(name = "文档管理")
@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {
    
    private final DocumentService documentService;
    
    @Operation(summary = "创建文档")
    @PostMapping
    public Result<DocumentVO> create(@Valid @RequestBody DocumentCreateDTO dto,
                                      @RequestHeader("X-User-Id") Long userId) {
        DocumentVO vo = documentService.create(dto, userId);
        return Result.success(vo);
    }
    
    @Operation(summary = "获取文档详情")
    @GetMapping("/{id}")
    public Result<DocumentVO> getById(@PathVariable Long id) {
        DocumentVO vo = documentService.getById(id);
        return vo != null ? Result.success(vo) : Result.error(404, "文档不存在");
    }
    
    @Operation(summary = "更新文档")
    @PutMapping("/{id}")
    public Result<DocumentVO> update(@PathVariable Long id, 
                                       @Valid @RequestBody DocumentUpdateDTO dto) {
        DocumentVO vo = documentService.update(id, dto);
        return Result.success(vo);
    }
    
    @Operation(summary = "删除文档")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        documentService.delete(id);
        return Result.success();
    }
    
    @Operation(summary = "文档列表")
    @GetMapping
    public Result<List<DocumentVO>> list(
            @RequestParam(required = false) Long folderId,
            @RequestParam(required = false) Long creatorId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<DocumentVO> list = documentService.list(folderId, creatorId, status, page, size);
        return Result.success(list);
    }
    
    @Operation(summary = "复制文档")
    @PostMapping("/{id}/copy")
    public Result<DocumentVO> copy(@PathVariable Long id,
                                     @RequestParam(required = false) Long targetFolderId,
                                     @RequestHeader("X-User-Id") Long userId) {
        DocumentVO vo = documentService.copy(id, targetFolderId, userId);
        return Result.success(vo);
    }
    
    @Operation(summary = "移动文档")
    @PostMapping("/{id}/move")
    public Result<DocumentVO> move(@PathVariable Long id,
                                     @RequestParam Long targetFolderId) {
        DocumentVO vo = documentService.move(id, targetFolderId);
        return Result.success(vo);
    }
}