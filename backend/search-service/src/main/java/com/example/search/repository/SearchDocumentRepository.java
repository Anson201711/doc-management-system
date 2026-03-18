package com.example.search.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.search.entity.SearchDocument;
import org.apache.ibatis.annotations.Mapper;

/**
 * Search Document Repository
 * MyBatis-Plus mapper for search documents
 */
@Mapper
public interface SearchDocumentRepository extends BaseMapper<SearchDocument> {
}