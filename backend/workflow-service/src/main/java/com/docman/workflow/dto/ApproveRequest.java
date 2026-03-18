package com.docman.workflow.dto;

import lombok.Data;

/**
 * 审批操作请求
 */
@Data
public class ApproveRequest {
    
    /**
     * 审批意见
     */
    private String comment;
}