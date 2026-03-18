package com.example.document.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.document.entity.Document;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DocumentRepository extends BaseMapper<Document> {
}