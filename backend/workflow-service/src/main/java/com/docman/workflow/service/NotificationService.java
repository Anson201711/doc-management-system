package com.docman.workflow.service;

import com.docman.workflow.entity.Workflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 通知服务 - 负责审批通知推送
 * 实际项目中会通过Feign调用notification-service
 */
@Slf4j
@Service
public class NotificationService {
    
    /**
     * 发送审批通知
     */
    public void sendApprovalNotification(Workflow workflow) {
        // TODO: 通过Feign调用notification-service发送通知
        log.info("发送审批通知: workflowId={}, title={}, approverId={}", 
                workflow.getId(), workflow.getTitle(), workflow.getApproverId());
        
        // 模拟通知发送
        // 实际实现:
        // notificationClient.sendNotification(new NotificationRequest(...));
    }
    
    /**
     * 发送审批结果通知
     */
    public void sendApprovalResultNotification(Workflow workflow, String result) {
        // TODO: 通过Feign调用notification-service发送通知
        log.info("发送审批结果通知: workflowId={}, title={}, creatorId={}, result={}", 
                workflow.getId(), workflow.getTitle(), workflow.getCreatorId(), result);
        
        // 模拟通知发送
        // 实际实现:
        // notificationClient.sendNotification(new NotificationRequest(...));
    }
}