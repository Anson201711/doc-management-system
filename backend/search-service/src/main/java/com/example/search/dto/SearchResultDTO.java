package com.example.search.dto;

import lombok.Data;

import java.util.List;

@Data
public class SearchResultDTO {
    private Long docId;
    private String title;
    private String content;
    private String docType;
    private Long creatorId;
    private Long folderId;
    private String status;
    private String version;
    private Long fileSize;
    private String fileUrl;
    private List<String> highlightFields;
    private Float score;
}