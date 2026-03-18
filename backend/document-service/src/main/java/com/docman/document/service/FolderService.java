package com.docman.document.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.docman.document.dto.FolderDTO;
import com.docman.document.entity.Folder;

import java.util.List;

/**
 * 文件夹服务接口
 */
public interface FolderService extends IService<Folder> {
    
    /**
     * 创建文件夹
     */
    Folder create(FolderDTO dto, Long ownerId);
    
    /**
     * 获取文件夹详情
     */
    Folder getById(Long id);
    
    /**
     * 获取子文件夹列表
     */
    List<Folder> listChildren(Long parentId);
    
    /**
     * 重命名文件夹
     */
    Folder rename(Long id, String name);
    
    /**
     * 删除文件夹
     */
    void delete(Long id);
}