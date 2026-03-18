package com.docman.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.docman.document.entity.Document;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文档Mapper
 */
@Mapper
public interface DocumentMapper extends BaseMapper<Document> {
    
    /**
     * 根据文件夹ID查询文档列表
     */
    List<Document> selectByFolderId(@Param("folderId") Long folderId);
    
    /**
     * 根据创建者ID查询文档列表
     */
    List<Document> selectByCreatorId(@Param("creatorId") Long creatorId);
}