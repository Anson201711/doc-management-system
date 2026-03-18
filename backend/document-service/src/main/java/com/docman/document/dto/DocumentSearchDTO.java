package com.docman.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 文档搜索请求/响应
 */
@Data
@Schema(description = "文档搜索结果")
public class DocumentSearchDTO {

    @Schema(description = "搜索结果列表")
    private List<SearchResult> results;

    @Schema(description = "总记录数")
    private int total;

    @Schema(description = "当前页码")
    private int page;

    @Schema(description = "每页大小")
    private int size;

    @Schema(description = "搜索关键字")
    private String keyword;

    @Data
    @Schema(description = "搜索结果项")
    public static class SearchResult {
        private Long docId;
        private String title;
        private String content;
        private Long folderId;
        private Long creatorId;
        private String creatorName;
        private String createdAt;
        private String updatedAt;
        private Double score;
    }
}