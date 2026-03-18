package com.docman.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.docman.document.entity.Attachment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 附件Mapper
 */
@Mapper
public interface AttachmentMapper extends BaseMapper<Attachment> {
    
    /**
     * 根据文档ID查询附件列表
     */
    List<Attachment> selectByDocumentId(@Param("documentId") Long documentId);
}