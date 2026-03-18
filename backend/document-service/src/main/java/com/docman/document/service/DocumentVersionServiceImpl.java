package com.docman.document.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.docman.document.dto.VersionCreateDTO;
import com.docman.document.entity.Document;
import com.docman.document.entity.DocumentVersion;
import com.docman.document.mapper.DocumentMapper;
import com.docman.document.mapper.DocumentVersionMapper;
import com.docman.document.vo.DocumentVersionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 文档版本服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentVersionServiceImpl extends ServiceImpl<DocumentVersionMapper, DocumentVersion> 
        implements DocumentVersionService {
    
    private final DocumentMapper documentMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentVersionVO create(Long documentId, VersionCreateDTO dto, Long creatorId) {
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new RuntimeException("文档不存在");
        }
        
        int newVersion = document.getCurrentVersion() + 1;
        
        DocumentVersion version = new DocumentVersion();
        version.setDocumentId(documentId);
        version.setVersion(newVersion);
        version.setContent(dto.getContent());
        version.setChangeSummary(dto.getChangeSummary());
        version.setCreatorId(creatorId);
        
        this.save(version);
        
        // 更新文档当前版本
        document.setCurrentVersion(newVersion);
        document.setContent(dto.getContent());
        documentMapper.updateById(document);
        
        return toVO(version);
    }
    
    @Override
    public List<DocumentVersionVO> listByDocumentId(Long documentId) {
        LambdaQueryWrapper<DocumentVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentVersion::getDocumentId, documentId)
                .orderByDesc(DocumentVersion::getVersion);
        
        return this.list(wrapper).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public DocumentVersionVO getVersion(Long documentId, Integer version) {
        LambdaQueryWrapper<DocumentVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentVersion::getDocumentId, documentId)
                .eq(DocumentVersion::getVersion, version);
        
        DocumentVersion versionEntity = this.getOne(wrapper);
        if (versionEntity == null) {
            return null;
        }
        
        return toVO(versionEntity);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollback(Long documentId, Integer version, Long userId) {
        DocumentVersion targetVersion = this.getVersion(documentId, version);
        if (targetVersion == null) {
            throw new RuntimeException("版本不存在");
        }
        
        Document document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new RuntimeException("文档不存在");
        }
        
        // 创建新版本记录回滚操作
        int newVersion = document.getCurrentVersion() + 1;
        DocumentVersion rollbackVersion = new DocumentVersion();
        rollbackVersion.setDocumentId(documentId);
        rollbackVersion.setVersion(newVersion);
        rollbackVersion.setContent(targetVersion.getContent());
        rollbackVersion.setChangeSummary("从版本 " + version + " 回滚");
        rollbackVersion.setCreatorId(userId);
        
        this.save(rollbackVersion);
        
        // 更新文档内容
        document.setCurrentVersion(newVersion);
        document.setContent(targetVersion.getContent());
        documentMapper.updateById(document);
    }
    
    private DocumentVersionVO toVO(DocumentVersion version) {
        DocumentVersionVO vo = new DocumentVersionVO();
        vo.setId(version.getId());
        vo.setDocumentId(version.getDocumentId());
        vo.setVersion(version.getVersion());
        vo.setContent(version.getContent());
        vo.setChangeSummary(version.getChangeSummary());
        vo.setCreatorId(version.getCreatorId());
        vo.setCreatedAt(version.getCreatedAt());
        return vo;
    }
}