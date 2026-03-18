package com.docman.document.repository;

import com.docman.document.entity.DocumentIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * ES文档索引仓库
 */
@Repository
public interface DocumentIndexRepository extends ElasticsearchRepository<DocumentIndex, String> {
}