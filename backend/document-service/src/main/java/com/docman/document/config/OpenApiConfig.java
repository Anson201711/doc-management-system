package com.docman.document.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 配置
 */
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("文档服务 API")
                        .version("1.0.0")
                        .description("文档管理系统 - 文档服务接口文档")
                        .contact(new Contact()
                                .name("DocMan Team")
                                .email("support@docman.com")));
    }
}