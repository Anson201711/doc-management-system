package com.docman.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.docman.document.entity.DocumentComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文档评论Mapper
 */
@Mapper
public interface DocumentCommentMapper extends BaseMapper<DocumentComment> {
}