package com.docman.permission.interceptor;

import com.docman.permission.dto.Result;
import com.docman.permission.service.PermissionCheckService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 权限校验拦截器
 * 用于拦截需要对文档进行权限校验的请求
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionInterceptor implements HandlerInterceptor {
    
    private final PermissionCheckService permissionCheckService;
    private final ObjectMapper objectMapper;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的用户信息
        String userIdHeader = request.getHeader("X-User-Id");
        String documentIdParam = request.getParameter("documentId");
        
        // 如果没有用户ID，说明可能是公开接口，直接放行
        if (userIdHeader == null || userIdHeader.isEmpty()) {
            return true;
        }
        
        Long userId;
        Long documentId;
        
        try {
            userId = Long.parseLong(userIdHeader);
        } catch (NumberFormatException e) {
            log.warn("无效的用户ID: {}", userIdHeader);
            return true; // 无效用户ID，放行
        }
        
        // 从路径中提取文档ID（如果有）
        String pathInfo = request.getRequestURI();
        if (pathInfo.contains("/documents/")) {
            String[] parts = pathInfo.split("/");
            for (int i = 0; i < parts.length; i++) {
                if ("documents".equals(parts[i]) && i + 1 < parts.length) {
                    try {
                        documentId = Long.parseLong(parts[i + 1]);
                        // 检查用户是否有文档访问权限
                        if (!permissionCheckService.hasDocumentPermission(userId, documentId, "read")) {
                            sendErrorResponse(response, Result.error("没有文档访问权限"));
                            return false;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                    break;
                }
            }
        }
        
        // 如果是写操作，需要检查写权限
        if ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod()) || "DELETE".equalsIgnoreCase(request.getMethod())) {
            if (documentIdParam != null && !documentIdParam.isEmpty()) {
                try {
                    Long docId = Long.parseLong(documentIdParam);
                    if (!permissionCheckService.hasDocumentPermission(userId, docId, "write")) {
                        sendErrorResponse(response, Result.error("没有文档写权限"));
                        return false;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        
        return true;
    }
    
    private void sendErrorResponse(HttpServletResponse response, Result<?> result) throws Exception {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}