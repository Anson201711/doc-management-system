package com.docman.document.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * ES文档索引实体
 */
@Data
@Document(indexName = "documents")
public class DocumentIndex {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long docId;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String content;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Long)
    private Long folderId;

    @Field(type = FieldType.Long)
    private Long creatorId;

    @Field(type = FieldType.Text)
    private String creatorName;

    @Field(type = FieldType.Keyword)
    private String tags;

    @Field(type = FieldType.Text)
    private String attachmentNames;

    @Field(type = FieldType.Date)
    private String createdAt;

    @Field(type = FieldType.Date)
    private String updatedAt;
}