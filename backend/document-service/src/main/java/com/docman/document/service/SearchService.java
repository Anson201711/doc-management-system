package com.docman.document.service;

import com.docman.document.dto.DocumentSearchDTO;
import com.docman.document.entity.Document;
import com.docman.document.entity.DocumentIndex;
import com.docman.document.mapper.DocumentMapper;
import com.docman.document.repository.DocumentIndexRepository;
import com.docman.document.vo.DocumentVO;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.json.JsonData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 全文搜索服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final DocumentIndexRepository indexRepository;
    private final DocumentMapper documentMapper;
    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * 索引单个文档
     */
    @Transactional
    public void indexDocument(Document document) {
        DocumentIndex index = toIndex(document);
        indexRepository.save(index);
        log.info("文档 {} 索引成功", document.getId());
    }

    /**
     * 批量索引文档
     */
    @Transactional
    public void batchIndexDocuments(List<Document> documents) {
        List<DocumentIndex> indices = documents.stream()
                .map(this::toIndex)
                .collect(Collectors.toList());
        indexRepository.saveAll(indices);
        log.info("批量索引 {} 个文档", documents.size());
    }

    /**
     * 删除文档索引
     */
    @Transactional
    public void deleteDocumentIndex(Long docId) {
        indexRepository.deleteById(docId.toString());
        log.info("文档 {} 索引已删除", docId);
    }

    /**
     * 搜索文档
     */
    public DocumentSearchDTO search(String keyword, String status, Long folderId, 
                                     Long creatorId, int page, int size) {
        // 构建查询条件
        List<Query> mustQueries = new ArrayList<>();
        List<Query> filterQueries = new ArrayList<>();

        // 关键字匹配（标题和内容）
        if (keyword != null && !keyword.isEmpty()) {
            mustQueries.add(MultiMatchQuery.of(m -> m
                    .query(keyword)
                    .fields("title^2", "content", "attachmentNames")
                    .fuzziness("AUTO")
            )._toQuery());
        } else {
            mustQueries.add(MatchAllQuery.of(m -> m)._toQuery());
        }

        // 状态过滤
        if (status != null && !status.isEmpty()) {
            filterQueries.add(TermQuery.of(t -> t
                    .field("status")
                    .value(status)
            )._toQuery());
        }

        // 文件夹过滤
        if (folderId != null) {
            filterQueries.add(TermQuery.of(t -> t
                    .field("folderId")
                    .value(folderId)
            )._toQuery());
        }

        // 创建者过滤
        if (creatorId != null) {
            filterQueries.add(TermQuery.of(t -> t
                    .field("creatorId")
                    .value(creatorId)
            )._toQuery());
        }

        // 组合查询
        BoolQuery boolQuery = BoolQuery.of(b -> {
            b.must(mustQueries);
            if (!filterQueries.isEmpty()) {
                b.filter(filterQueries);
            }
            return b;
        });

        // 设置高亮
        List<HighlightField> highlightFields = Arrays.asList(
                new HighlightField("title"),
                new HighlightField("content")
        );
        Highlight highlight = new Highlight(highlightFields);

        // 构建ES查询
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(boolQuery._toQuery())
                .withHighlightQuery(new HighlightQuery(highlight, DocumentIndex.class))
                .withPageable(PageRequest.of(page - 1, size))
                .build();

        // 执行搜索
        SearchHits<DocumentIndex> hits = elasticsearchTemplate.search(searchQuery, DocumentIndex.class);

        // 处理结果
        List<DocumentSearchDTO.SearchResult> results = new ArrayList<>();
        for (SearchHit<DocumentIndex> hit : hits.getSearchHits()) {
            DocumentIndex index = hit.getContent();
            
            DocumentSearchDTO.SearchResult result = new DocumentSearchDTO.SearchResult();
            result.setDocId(index.getDocId());
            result.setTitle(index.getTitle());
            result.setContent(getHighlightContent(hit, "content", index.getContent()));
            result.setFolderId(index.getFolderId());
            result.setCreatorId(index.getCreatorId());
            result.setCreatorName(index.getCreatorName());
            result.setCreatedAt(index.getCreatedAt());
            result.setUpdatedAt(index.getUpdatedAt());
            result.setScore(hit.getScore());
            
            results.add(result);
        }

        // 构建返回结果
        DocumentSearchDTO searchResult = new DocumentSearchDTO();
        searchResult.setResults(results);
        searchResult.setTotal((int) hits.getTotalHits());
        searchResult.setPage(page);
        searchResult.setSize(size);
        searchResult.setKeyword(keyword);

        return searchResult;
    }

    /**
     * 高亮内容处理
     */
    private String getHighlightContent(SearchHit<DocumentIndex> hit, String field, String defaultContent) {
        Map<String, List<String>> highlights = hit.getHighlightFields();
        if (highlights != null && highlights.containsKey(field)) {
            return String.join("...", highlights.get(field));
        }
        // 返回截断的内容
        if (defaultContent != null && defaultContent.length() > 200) {
            return defaultContent.substring(0, 200) + "...";
        }
        return defaultContent;
    }

    /**
     * 重建索引（全部）
     */
    @Transactional
    public void rebuildIndex() {
        log.info("开始重建文档索引...");
        
        // 清空现有索引
        indexRepository.deleteAll();
        
        // 查询所有文档
        List<Document> allDocs = documentMapper.selectList(null);
        
        // 批量索引
        batchIndexDocuments(allDocs);
        
        log.info("文档索引重建完成，共索引 {} 个文档", allDocs.size());
    }

    /**
     * 同步单个文档到索引
     */
    public void syncDocument(Long docId) {
        Document document = documentMapper.selectById(docId);
        if (document != null) {
            indexDocument(document);
        }
    }

    private DocumentIndex toIndex(Document doc) {
        DocumentIndex index = new DocumentIndex();
        index.setId(doc.getId().toString());
        index.setDocId(doc.getId());
        index.setTitle(doc.getTitle());
        index.setContent(doc.getContent());
        index.setStatus(doc.getStatus());
        index.setFolderId(doc.getFolderId());
        index.setCreatorId(doc.getCreatorId());
        index.setCreatedAt(doc.getCreatedAt() != null ? 
                doc.getCreatedAt().format(FORMATTER) : null);
        index.setUpdatedAt(doc.getUpdatedAt() != null ? 
                doc.getUpdatedAt().format(FORMATTER) : null);
        return index;
    }
}