package com.example.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import com.example.search.dto.SearchRequest;
import com.example.search.dto.SearchResponse;
import com.example.search.entity.SearchDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Search Service
 * Provides full-text search functionality using Elasticsearch
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {
    
    private final ElasticsearchClient elasticsearchClient;
    
    private static final String INDEX_NAME = "documents";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    /**
     * Index a document
     */
    public void indexDocument(SearchDocument document) {
        try {
            document.setIndexedAt(LocalDateTime.now());
            IndexRequest<SearchDocument> request = IndexRequest.of(i -> i
                    .index(INDEX_NAME)
                    .id(String.valueOf(document.getDocumentId()))
                    .document(document)
            );
            elasticsearchClient.index(request);
            log.info("Indexed document: {}", document.getDocumentId());
        } catch (IOException e) {
            log.error("Failed to index document: {}", document.getDocumentId(), e);
            throw new RuntimeException("Failed to index document", e);
        }
    }
    
    /**
     * Delete a document from index
     */
    public void deleteDocument(Long documentId) {
        try {
            DeleteRequest request = DeleteRequest.of(d -> d
                    .index(INDEX_NAME)
                    .id(String.valueOf(documentId))
            );
            elasticsearchClient.delete(request);
            log.info("Deleted document from index: {}", documentId);
        } catch (IOException e) {
            log.error("Failed to delete document from index: {}", documentId, e);
            throw new RuntimeException("Failed to delete document", e);
        }
    }
    
    /**
     * Search documents
     */
    public SearchResponse search(SearchRequest request) {
        try {
            int from = (request.getPage() - 1) * request.getPageSize();
            
            // Build query
            BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
            
            // Keyword search (multi-match)
            if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
                boolQueryBuilder.must(Query.of(q -> q
                        .multiMatch(mm -> mm
                                .query(request.getKeyword())
                                .fields("title^2", "content")
                                .fuzziness("AUTO")
                        )
                ));
            }
            
            // Filter by document types
            if (request.getDocumentTypes() != null && !request.getDocumentTypes().isEmpty()) {
                boolQueryBuilder.filter(Query.of(q -> q
                        .terms(t -> t
                                .field("documentType")
                                .terms(ts -> ts.value(request.getDocumentTypes().stream()
                                        .map(t -> co.elastic.clients.elasticsearch._types.FieldValue.of(t))
                                        .toList()))
                        )
                ));
            }
            
            // Filter by status
            if (request.getStatus() != null && !request.getStatus().isEmpty()) {
                boolQueryBuilder.filter(Query.of(q -> q
                        .term(TermQuery.of(t -> t.field("status").value(request.getStatus())))
                ));
            }
            
            // Build highlight
            Map<String, HighlightField> highlightFields = new HashMap<>();
            for (String field : request.getHighlightFields()) {
                highlightFields.put(field, HighlightField.of(hf -> hf
                        .preTags("<em>")
                        .postTags("</em>")
                        .fragmentSize(150)
                        .numberOfFragments(3)
                ));
            }
            
            SearchRequest.Builder searchRequest = new SearchRequest.Builder()
                    .index(INDEX_NAME)
                    .query(Query.of(q -> q.bool(boolQueryBuilder.build())))
                    .from(from)
                    .size(request.getPageSize())
                    .highlight(h -> h.fields(highlightFields));
            
            SearchResponse response = elasticsearchClient.search(searchRequest.build(), SearchDocument.class);
            
            // Convert to DTO
            SearchResponse result = new SearchResponse();
            result.setTotalHits(response.hits().total() != null ? response.hits().total().value() : 0L);
            result.setPage(request.getPage());
            result.setPageSize(request.getPageSize());
            result.setTotalPages((int) Math.ceil((double) result.getTotalHits() / request.getPageSize()));
            result.setTook(response.took());
            
            List<SearchResponse.SearchResult> results = new ArrayList<>();
            for (Hit<SearchDocument> hit : response.hits().hits()) {
                SearchResponse.SearchResult searchResult = new SearchResponse.SearchResult();
                searchResult.setDocumentId(Long.parseLong(hit.id()));
                searchResult.setScore(hit.score());
                
                SearchDocument source = hit.source();
                if (source != null) {
                    searchResult.setTitle(source.getTitle());
                    searchResult.setContent(source.getContent() != null ? 
                            source.getContent().substring(0, Math.min(200, source.getContent().length())) : null);
                    searchResult.setDocumentType(source.getDocumentType());
                    searchResult.setOwnerName(source.getOwnerName());
                    searchResult.setCreatedAt(source.getCreatedAt() != null ? 
                            source.getCreatedAt().format(DATE_FORMATTER) : null);
                }
                
                // Process highlights
                if (hit.highlight() != null && !hit.highlight().isEmpty()) {
                    searchResult.setHighlights(hit.highlight());
                }
                
                results.add(searchResult);
            }
            result.setResults(results);
            
            return result;
        } catch (IOException e) {
            log.error("Search failed", e);
            throw new RuntimeException("Search failed", e);
        }
    }
    
    /**
     * Get search suggestions (autocomplete)
     */
    public List<String> getSuggestions(String prefix) {
        try {
            SearchRequest request = SearchRequest.of(s -> s
                    .index(INDEX_NAME)
                    .query(q -> q
                            .bool(b -> b
                                    .should(sh -> sh
                                            .prefix(p -> p.field("title").value(prefix))
                                    )
                            )
                    )
                    .size(10)
                    .source(sc -> sc.filter(f -> f.includes("title")))
            );
            
            SearchResponse response = elasticsearchClient.search(request, SearchDocument.class);
            
            Set<String> suggestions = new HashSet<>();
            for (Hit<SearchDocument> hit : response.hits().hits()) {
                if (hit.source() != null && hit.source().getTitle() != null) {
                    suggestions.add(hit.source().getTitle());
                }
            }
            
            return new ArrayList<>(suggestions);
        } catch (IOException e) {
            log.error("Failed to get suggestions", e);
            return Collections.emptyList();
        }
    }
}