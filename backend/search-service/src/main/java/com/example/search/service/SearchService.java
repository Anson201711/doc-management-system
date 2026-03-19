package com.example.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import com.example.search.dto.SearchRequestDTO;
import com.example.search.dto.SearchResultDTO;
import com.example.search.entity.DocumentIndex;
import com.example.search.repository.DocumentIndexRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {
    
    private final DocumentIndexRepository documentIndexRepository;
    private final ElasticsearchClient elasticsearchClient;
    
    private static final String DOCUMENT_INDEX = "documents";
    private static final String DOCUMENT_SERVICE_URL = "http://localhost:8082/api/v1/documents";
    private static final String CACHE_SEARCH = "search";
    
    /**
     * 创建索引
     */
    public void createIndex() {
        try {
            boolean exists = elasticsearchClient.indices()
                    .exists(ExistsRequest.of(e -> e.index(DOCUMENT_INDEX)))
                    .value();
            
            if (!exists) {
                Map<String, Object> settings = new HashMap<>();
                settings.put("number_of_shards", 1);
                settings.put("number_of_replicas", 0);
                
                Map<String, Object> properties = new HashMap<>();
                properties.put("docId", Map.of("type", "long"));
                properties.put("title", Map.of("type", "text", "analyzer", "ik_max_word", "search_analyzer", "ik_smart"));
                properties.put("content", Map.of("type", "text", "analyzer", "ik_max_word", "search_analyzer", "ik_smart"));
                properties.put("docType", Map.of("type", "keyword"));
                properties.put("creatorId", Map.of("type", "long"));
                properties.put("folderId", Map.of("type", "long"));
                properties.put("status", Map.of("type", "keyword"));
                properties.put("version", Map.of("type", "keyword"));
                properties.put("fileSize", Map.of("type", "long"));
                properties.put("fileUrl", Map.of("type", "keyword"));
                properties.put("createTime", Map.of("type", "date"));
                properties.put("updateTime", Map.of("type", "date"));
                
                Map<String, Object> mapping = new HashMap<>();
                mapping.put("settings", settings);
                mapping.put("mappings", Map.of("properties", properties));
                
                elasticsearchClient.indices().create(CreateIndexRequest.of(c -> c
                        .index(DOCUMENT_INDEX)
                        .settings(s -> s.numberOfShards("1").numberOfReplicas("0"))
                ));
                log.info("Index {} created successfully", DOCUMENT_INDEX);
            }
        } catch (Exception e) {
            log.error("Failed to create index", e);
            throw new RuntimeException("Failed to create index", e);
        }
    }
    
    /**
     * 索引单个文档
     */
    public void indexDocument(DocumentIndex document) {
        documentIndexRepository.save(document);
        log.info("Document indexed: {}", document.getId());
    }
    
    /**
     * 从 document-service 获取所有文档并重建索引
     */
    public void rebuildIndex() {
        try {
            createIndex();
            
            RestTemplate restTemplate = new RestTemplate();
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(DOCUMENT_SERVICE_URL, Map.class);
            
            if (response != null && response.containsKey("data")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> documents = (List<Map<String, Object>>) response.get("data");
                
                for (Map<String, Object> doc : documents) {
                    DocumentIndex index = new DocumentIndex();
                    index.setId(String.valueOf(doc.get("id")));
                    index.setDocId(((Number) doc.get("id")).longValue());
                    index.setTitle((String) doc.get("title"));
                    index.setContent((String) doc.get("content"));
                    index.setDocType((String) doc.get("docType"));
                    index.setCreatorId(((Number) doc.get("creatorId")).longValue());
                    index.setFolderId(((Number) doc.get("folderId")).longValue());
                    index.setStatus((String) doc.get("status"));
                    index.setVersion((String) doc.get("version"));
                    
                    Object fileSize = doc.get("fileSize");
                    index.setFileSize(fileSize != null ? ((Number) fileSize).longValue() : null);
                    
                    index.setFileUrl((String) doc.get("fileUrl"));
                    
                    documentIndexRepository.save(index);
                }
                log.info("Indexed {} documents", documents.size());
            }
        } catch (Exception e) {
            log.error("Failed to rebuild index", e);
            throw new RuntimeException("Failed to rebuild index", e);
        }
    }
    
    /**
     * 搜索文档 - 缓存搜索结果
     */
    @Cacheable(value = CACHE_SEARCH, key = "#p0.hashCode()", unless = "#result == null")
    public List<SearchResultDTO> search(SearchRequestDTO request) {
        try {
            BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
            
            // 关键词搜索
            if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
                boolQueryBuilder.must(Query.of(q -> q
                        .multiMatch(mm -> mm
                                .query(request.getKeyword())
                                .fields("title^2", "content")
                                .analyzer("ik_max_word")
                        )
                ));
            }
            
            // 过滤条件
            if (request.getDocType() != null) {
                boolQueryBuilder.filter(Query.of(q -> q.term(t -> t.field("docType").value(request.getDocType()))));
            }
            if (request.getCreatorId() != null) {
                boolQueryBuilder.filter(Query.of(q -> q.term(t -> t.field("creatorId").value(request.getCreatorId()))));
            }
            if (request.getFolderId() != null) {
                boolQueryBuilder.filter(Query.of(q -> q.term(t -> t.field("folderId").value(request.getFolderId()))));
            }
            if (request.getStatus() != null) {
                boolQueryBuilder.filter(Query.of(q -> q.term(t -> t.field("status").value(request.getStatus()))));
            }
            
            int from = (request.getPageNum() - 1) * request.getPageSize();
            
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index(DOCUMENT_INDEX)
                    .query(Query.of(q -> q.bool(boolQueryBuilder.build())))
                    .from(from)
                    .size(request.getPageSize())
                    .highlight(h -> h
                            .fields("title", f -> f)
                            .fields("content", f -> f)
                    )
            );
            
            SearchResponse<DocumentIndex> response = elasticsearchClient.search(searchRequest, DocumentIndex.class);
            
            List<SearchResultDTO> results = new ArrayList<>();
            for (Hit<DocumentIndex> hit : response.hits().hits()) {
                DocumentIndex source = hit.source();
                if (source != null) {
                    SearchResultDTO dto = new SearchResultDTO();
                    dto.setDocId(source.getDocId());
                    dto.setTitle(source.getTitle());
                    dto.setContent(source.getContent());
                    dto.setDocType(source.getDocType());
                    dto.setCreatorId(source.getCreatorId());
                    dto.setFolderId(source.getFolderId());
                    dto.setStatus(source.getStatus());
                    dto.setVersion(source.getVersion());
                    dto.setFileSize(source.getFileSize());
                    dto.setFileUrl(source.getFileUrl());
                    dto.setScore(hit.score() != null ? hit.score().floatValue() : null);
                    
                    // 高亮片段
                    if (hit.highlight() != null) {
                        List<String> highlights = new ArrayList<>();
                        hit.highlight().forEach((field, fragments) -> highlights.addAll(fragments));
                        dto.setHighlightFields(highlights);
                    }
                    
                    results.add(dto);
                }
            }
            
            return results;
        } catch (IOException e) {
            log.error("Search failed", e);
            throw new RuntimeException("Search failed", e);
        }
    }
    
    /**
     * 删除文档索引
     */
    @CacheEvict(value = CACHE_SEARCH, allEntries = true)
    public void deleteDocument(String id) {
        documentIndexRepository.deleteById(id);
        log.info("Document index deleted: {}", id);
    }
}