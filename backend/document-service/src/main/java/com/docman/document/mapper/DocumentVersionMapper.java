package com.docman.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.docman.document.entity.DocumentVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文档版本Mapper
 */
@Mapper
public interface DocumentVersionMapper extends BaseMapper<DocumentVersion> {
    
    /**
     * 根据文档ID查询版本列表
     */
    List<DocumentVersion> selectByDocumentId(@Param("documentId") Long documentId);
    
    /**
     * 获取文档的最大版本号
     */
    Integer selectMaxVersionByDocumentId(@Param("documentId") Long documentId);
}