package com.docman.document.controller;

import com.docman.document.dto.DocumentSearchDTO;
import com.docman.document.service.SearchService;
import com.docman.document.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 全文搜索控制器
 */
@Tag(name = "文档搜索")
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "搜索文档")
    @GetMapping
    public Result<DocumentSearchDTO> search(
            @RequestParam String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long folderId,
            @RequestParam(required = false) Long creatorId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        DocumentSearchDTO results = searchService.search(keyword, status, folderId, creatorId, page, size);
        return Result.success(results);
    }

    @Operation(summary = "重建搜索索引")
    @PostMapping("/rebuild")
    public Result<Void> rebuildIndex() {
        searchService.rebuildIndex();
        return Result.success();
    }

    @Operation(summary = "同步单个文档到索引")
    @PostMapping("/sync/{docId}")
    public Result<Void> syncDocument(@PathVariable Long docId) {
        searchService.syncDocument(docId);
        return Result.success();
    }
}