package com.docman.document.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO配置属性
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private Integer urlExpiration;
}