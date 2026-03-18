package com.example.document.dto;

import lombok.Data;

@Data
public class DocumentUpdateDTO {
    private Long id;
    private String title;
    private String content;
    private String docType;
    private String status;
    private String version;
    private Long folderId;
    private String fileUrl;
}