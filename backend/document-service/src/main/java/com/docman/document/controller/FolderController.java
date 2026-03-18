package com.docman.document.controller;

import com.docman.document.dto.FolderDTO;
import com.docman.document.entity.Folder;
import com.docman.document.service.FolderService;
import com.docman.document.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文件夹管理控制器
 */
@Tag(name = "文件夹管理")
@RestController
@RequestMapping("/api/v1/folders")
@RequiredArgsConstructor
public class FolderController {
    
    private final FolderService folderService;
    
    @Operation(summary = "创建文件夹")
    @PostMapping
    public Result<Folder> create(@Valid @RequestBody FolderDTO dto,
                                   @RequestHeader("X-User-Id") Long userId) {
        Folder folder = folderService.create(dto, userId);
        return Result.success(folder);
    }
    
    @Operation(summary = "获取文件夹详情")
    @GetMapping("/{id}")
    public Result<Folder> getById(@PathVariable Long id) {
        Folder folder = folderService.getById(id);
        return folder != null ? Result.success(folder) : Result.error(404, "文件夹不存在");
    }
    
    @Operation(summary = "获取子文件夹列表")
    @GetMapping("/{id}/children")
    public Result<List<Folder>> listChildren(@PathVariable Long id) {
        List<Folder> children = folderService.listChildren(id);
        return Result.success(children);
    }
    
    @Operation(summary = "重命名文件夹")
    @PutMapping("/{id}/rename")
    public Result<Folder> rename(@PathVariable Long id, @RequestParam String name) {
        Folder folder = folderService.rename(id, name);
        return Result.success(folder);
    }
    
    @Operation(summary = "删除文件夹")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        folderService.delete(id);
        return Result.success();
    }
}