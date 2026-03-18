package com.docman.workflow.feign;

import com.docman.workflow.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户服务Feign客户端
 */
@FeignClient(name = "user-service", path = "/api/v1/users")
public interface UserClient {
    
    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    Result<?> getUserById(@PathVariable("id") Long id);
}