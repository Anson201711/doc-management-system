package com.example.tests.service;

import com.example.tests.config.BaseTest;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Permission Service API Tests
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PermissionServiceTests extends BaseTest {

    @BeforeAll
    public void setupAuth() {
        if (authToken == null) {
            loginAsTestUser();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test check permission - Has permission")
    public void testCheckPermissionHasPermission() {
        given()
                .baseUri(PERMISSION_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .param("userId", 1)
                .param("resource", "document")
                .param("action", "read")
                .when()
                .get("/api/v1/permissions/check")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("hasPermission", notNullValue());
    }

    @Test
    @Order(2)
    @DisplayName("Test check permission - Missing parameters")
    public void testCheckPermissionMissingParams() {
        given()
                .baseUri(PERMISSION_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .param("userId", 1)
                // Missing resource and action
                .when()
                .get("/api/v1/permissions/check")
                .then()
                .statusCode(anyOf(is(400), is(500)));
    }

    @Test
    @Order(3)
    @DisplayName("Test assign role - Success")
    public void testAssignRole() {
        given()
                .baseUri(PERMISSION_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .param("userId", 2)
                .param("role", "editor")
                .when()
                .post("/api/v1/permissions/role")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("message", equalTo("角色分配成功"));
    }

    @Test
    @Order(4)
    @DisplayName("Test revoke role - Success")
    public void testRevokeRole() {
        given()
                .baseUri(PERMISSION_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .param("userId", 2)
                .param("role", "editor")
                .when()
                .delete("/api/v1/permissions/role")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("message", equalTo("角色撤销成功"));
    }

    @Test
    @Order(5)
    @DisplayName("Test assign role - Missing parameters")
    public void testAssignRoleMissingParams() {
        given()
                .baseUri(PERMISSION_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .param("userId", 2)
                // Missing role
                .when()
                .post("/api/v1/permissions/role")
                .then()
                .statusCode(anyOf(is(400), is(500)));
    }

    @Test
    @Order(6)
    @DisplayName("Test revoke role - Missing parameters")
    public void testRevokeRoleMissingParams() {
        given()
                .baseUri(PERMISSION_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                // Missing userId and role
                .when()
                .delete("/api/v1/permissions/role")
                .then()
                .statusCode(anyOf(is(400), is(500)));
    }
}