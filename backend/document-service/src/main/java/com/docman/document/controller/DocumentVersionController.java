package com.docman.document.controller;

import com.docman.document.dto.VersionCreateDTO;
import com.docman.document.service.DocumentVersionService;
import com.docman.document.vo.DocumentVersionVO;
import com.docman.document.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文档版本控制器
 */
@Tag(name = "文档版本管理")
@RestController
@RequestMapping("/api/v1/documents/{docId}/versions")
@RequiredArgsConstructor
public class DocumentVersionController {
    
    private final DocumentVersionService documentVersionService;
    
    @Operation(summary = "获取版本列表")
    @GetMapping
    public Result<List<DocumentVersionVO>> list(@PathVariable Long docId) {
        List<DocumentVersionVO> versions = documentVersionService.listByDocumentId(docId);
        return Result.success(versions);
    }
    
    @Operation(summary = "获取指定版本")
    @GetMapping("/{version}")
    public Result<DocumentVersionVO> getVersion(@PathVariable Long docId,
                                                   @PathVariable Integer version) {
        DocumentVersionVO versionVO = documentVersionService.getVersion(docId, version);
        return versionVO != null ? Result.success(versionVO) : Result.error(404, "版本不存在");
    }
    
    @Operation(summary = "创建新版本")
    @PostMapping
    public Result<DocumentVersionVO> create(@PathVariable Long docId,
                                               @RequestBody VersionCreateDTO dto,
                                               @RequestHeader("X-User-Id") Long userId) {
        DocumentVersionVO vo = documentVersionService.create(docId, dto, userId);
        return Result.success(vo);
    }
    
    @Operation(summary = "回滚版本")
    @PostMapping("/{version}/rollback")
    public Result<Void> rollback(@PathVariable Long docId,
                                   @PathVariable Integer version,
                                   @RequestHeader("X-User-Id") Long userId) {
        documentVersionService.rollback(docId, version, userId);
        return Result.success();
    }
}