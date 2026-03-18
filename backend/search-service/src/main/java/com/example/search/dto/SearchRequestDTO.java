package com.example.search.dto;

import lombok.Data;

@Data
public class SearchRequestDTO {
    private String keyword;
    private String docType;
    private Long creatorId;
    private Long folderId;
    private String status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}