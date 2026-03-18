package com.example.workflow.controller;

import com.example.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/workflows")
@RequiredArgsConstructor
public class WorkflowController {
    
    private final WorkflowService workflowService;
    
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startWorkflow(
            @RequestParam String workflowType,
            @RequestParam Long documentId) {
        workflowService.startWorkflow(workflowType, documentId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "工作流启动成功");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/approve")
    public ResponseEntity<Map<String, Object>> approveTask(
            @RequestParam Long taskId,
            @RequestParam Long userId) {
        workflowService.approveTask(taskId, userId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "任务审批通过");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/reject")
    public ResponseEntity<Map<String, Object>> rejectTask(
            @RequestParam Long taskId,
            @RequestParam Long userId) {
        workflowService.rejectTask(taskId, userId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "任务已拒绝");
        return ResponseEntity.ok(response);
    }
}