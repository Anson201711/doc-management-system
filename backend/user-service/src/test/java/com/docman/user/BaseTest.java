package com.docman.user;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 基础测试配置
 */
@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseTest {
}