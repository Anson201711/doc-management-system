package com.example.tests.service;

import com.example.tests.config.BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * User Service API Tests
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTests extends BaseTest {

    private static String newUserToken;
    private static Long createdUserId;

    @Test
    @Order(1)
    @DisplayName("Test user login - Success")
    public void testLoginSuccess() {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "admin");
        loginRequest.put("password", "admin123");

        given()
                .baseUri(USER_SERVICE_URL)
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("data.token", notNullValue())
                .body("data.user.username", equalTo("admin"));
    }

    @Test
    @Order(2)
    @DisplayName("Test user login - Invalid credentials")
    public void testLoginInvalidCredentials() {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "admin");
        loginRequest.put("password", "wrongpassword");

        given()
                .baseUri(USER_SERVICE_URL)
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(401)
                .body("code", equalTo(401))
                .body("message", equalTo("用户名或密码错误"));
    }

    @Test
    @Order(3)
    @DisplayName("Test user login - Missing credentials")
    public void testLoginMissingCredentials() {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "admin");

        given()
                .baseUri(USER_SERVICE_URL)
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(400)
                .body("code", equalTo(400));
    }

    @Test
    @Order(4)
    @DisplayName("Test user register - Success")
    public void testRegisterSuccess() {
        String uniqueUsername = "testuser_" + System.currentTimeMillis();
        
        Map<String, String> registerRequest = new HashMap<>();
        registerRequest.put("username", uniqueUsername);
        registerRequest.put("password", "Test@123456");
        registerRequest.put("email", uniqueUsername + "@example.com");
        registerRequest.put("fullName", "Test User");

        Response response = given()
                .baseUri(USER_SERVICE_URL)
                .contentType(ContentType.JSON)
                .body(registerRequest)
                .when()
                .post("/api/v1/auth/register");

        response.then()
                .statusCode(anyOf(is(200), is(201)))
                .body("code", anyOf(equalTo(200), equalTo(201)))
                .body("data.token", notNullValue())
                .body("data.user.username", equalTo(uniqueUsername));

        newUserToken = response.jsonPath().getString("data.token");
        createdUserId = response.jsonPath().getLong("data.user.id");
    }

    @Test
    @Order(5)
    @DisplayName("Test user register - Duplicate username")
    public void testRegisterDuplicateUsername() {
        Map<String, String> registerRequest = new HashMap<>();
        registerRequest.put("username", "admin");
        registerRequest.put("password", "Test@123456");
        registerRequest.put("email", "admin2@example.com");

        given()
                .baseUri(USER_SERVICE_URL)
                .contentType(ContentType.JSON)
                .body(registerRequest)
                .when()
                .post("/api/v1/auth/register")
                .then()
                .statusCode(400)
                .body("code", equalTo(400))
                .body("message", equalTo("用户名已存在"));
    }

    @Test
    @Order(6)
    @DisplayName("Test get current user info")
    public void testGetCurrentUser() {
        if (authToken == null) {
            loginAsTestUser();
        }

        given()
                .baseUri(USER_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/api/v1/auth/me")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("data", notNullValue())
                .body("data.username", notNullValue());
    }

    @Test
    @Order(7)
    @DisplayName("Test get current user - Unauthorized")
    public void testGetCurrentUserUnauthorized() {
        given()
                .baseUri(USER_SERVICE_URL)
                .when()
                .get("/api/v1/auth/me")
                .then()
                .statusCode(401)
                .body("code", equalTo(401));
    }

    @Test
    @Order(8)
    @DisplayName("Test get all users")
    public void testGetAllUsers() {
        if (authToken == null) {
            loginAsTestUser();
        }

        given()
                .baseUri(USER_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/api/v1/users")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("data", notNullValue());
    }

    @Test
    @Order(9)
    @DisplayName("Test get user by ID")
    public void testGetUserById() {
        if (authToken == null) {
            loginAsTestUser();
        }

        given()
                .baseUri(USER_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/api/v1/users/1")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("data", notNullValue())
                .body("data.id", equalTo(1));
    }

    @Test
    @Order(10)
    @DisplayName("Test update user")
    public void testUpdateUser() {
        if (authToken == null) {
            loginAsTestUser();
        }

        Map<String, String> updateRequest = new HashMap<>();
        updateRequest.put("fullName", "Updated Name");
        updateRequest.put("email", "updated@example.com");

        given()
                .baseUri(USER_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .put("/api/v1/users/1")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("message", equalTo("用户更新成功"));
    }

    @Test
    @Order(11)
    @DisplayName("Test check username exists")
    public void testCheckUsername() {
        given()
                .baseUri(USER_SERVICE_URL)
                .when()
                .get("/api/v1/users/check/admin")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("exists", equalTo(true));
    }

    @Test
    @Order(12)
    @DisplayName("Test refresh token")
    public void testRefreshToken() {
        if (authToken == null) {
            loginAsTestUser();
        }

        // First get refresh token from login
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "admin");
        loginRequest.put("password", "admin123");

        String refreshToken = given()
                .baseUri(USER_SERVICE_URL)
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/v1/auth/login")
                .jsonPath()
                .getString("data.refreshToken");

        // Then refresh
        Map<String, String> refreshRequest = new HashMap<>();
        refreshRequest.put("refreshToken", refreshToken);

        given()
                .baseUri(USER_SERVICE_URL)
                .contentType(ContentType.JSON)
                .body(refreshRequest)
                .when()
                .post("/api/v1/auth/refresh")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("data.token", notNullValue());
    }

    @Test
    @Order(13)
    @DisplayName("Test logout")
    public void testLogout() {
        if (authToken == null) {
            loginAsTestUser();
        }

        given()
                .baseUri(USER_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .post("/api/v1/auth/logout")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("message", equalTo("登出成功"));
    }
}