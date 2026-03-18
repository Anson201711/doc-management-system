package com.docman.document.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.docman.document.dto.FolderDTO;
import com.docman.document.entity.Folder;
import com.docman.document.mapper.FolderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 文件夹服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FolderServiceImpl extends ServiceImpl<FolderMapper, Folder> implements FolderService {
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Folder create(FolderDTO dto, Long ownerId) {
        Folder folder = new Folder();
        folder.setName(dto.getName());
        folder.setParentId(dto.getParentId());
        folder.setOwnerId(ownerId);
        
        this.save(folder);
        return folder;
    }
    
    @Override
    public Folder getById(Long id) {
        return this.getById(id);
    }
    
    @Override
    public List<Folder> listChildren(Long parentId) {
        return baseMapper.selectByParentId(parentId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Folder rename(Long id, String name) {
        Folder folder = this.getById(id);
        if (folder == null) {
            throw new RuntimeException("文件夹不存在");
        }
        
        folder.setName(name);
        this.updateById(folder);
        return folder;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        this.removeById(id);
    }
}