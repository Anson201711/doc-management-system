package com.docman.document.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.docman.document.dto.VersionCreateDTO;
import com.docman.document.entity.DocumentVersion;
import com.docman.document.vo.DocumentVersionVO;

import java.util.List;

/**
 * 文档版本服务接口
 */
public interface DocumentVersionService extends IService<DocumentVersion> {
    
    /**
     * 创建版本
     */
    DocumentVersionVO create(Long documentId, VersionCreateDTO dto, Long creatorId);
    
    /**
     * 获取文档版本列表
     */
    List<DocumentVersionVO> listByDocumentId(Long documentId);
    
    /**
     * 获取指定版本
     */
    DocumentVersionVO getVersion(Long documentId, Integer version);
    
    /**
     * 回滚版本
     */
    void rollback(Long documentId, Integer version, Long userId);
}