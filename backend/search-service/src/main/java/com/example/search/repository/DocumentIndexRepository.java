package com.example.search.repository;

import com.example.search.entity.DocumentIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentIndexRepository extends ElasticsearchRepository<DocumentIndex, String> {
}