package com.example.notification.enums;

public enum NotificationType {
    COMMENT("comment", "评论提醒"),
    APPROVAL("approval", "审批通知"),
    COLLABORATION("collaboration", "协作邀请"),
    SYSTEM("system", "系统通知"),
    MENTION("mention", "提及提醒");
    
    private final String code;
    private final String description;
    
    NotificationType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
}