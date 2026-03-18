package com.example.workflow.dto;

import lombok.Data;

@Data
public class ApprovalDTO {
    private Long taskId;
    private Long approverId;
    private String comment;
}