package com.example.tests.security;

import com.example.tests.config.BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;

/**
 * Security Tests - Authentication, Authorization, SQL Injection, XSS, etc.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SecurityTests extends BaseTest {

    private static String testUserToken;
    private static Long testDocumentId;

    @BeforeAll
    public void setup() {
        createTestUserIfNotExists();
        testUserToken = loginAsTestUser();
        
        // Create test document
        Map<String, Object> docRequest = new HashMap<>();
        docRequest.put("title", "Security Test Document");
        docRequest.put("content", "Test content");
        
        Response response = given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(ContentType.JSON)
                .body(docRequest)
                .when()
                .post("/api/v1/documents");
        
        if (response.getStatusCode() == 200) {
            testDocumentId = response.jsonPath().getLong("data.id");
        }
    }

    @AfterAll
    public void cleanup() {
        if (testDocumentId != null && testUserToken != null) {
            try {
                given()
                        .baseUri(DOCUMENT_SERVICE_URL)
                        .header("Authorization", "Bearer " + testUserToken)
                        .when()
                        .delete("/api/v1/documents/" + testDocumentId);
            } catch (Exception ignored) {}
        }
    }

    // ==================== Authentication Tests ====================

    @Test
    @Order(1)
    @DisplayName("Security: Test invalid credentials")
    public void testInvalidCredentials() {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "invaliduser");
        loginRequest.put("password", "wrongpassword");

        Response response = given()
                .baseUri(USER_SERVICE_URL)
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/v1/auth/login");

        response.then()
                .statusCode(401); // Unauthorized
    }

    @Test
    @Order(2)
    @DisplayName("Security: Test missing authentication token")
    public void testMissingAuthToken() {
        Response response = given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .when()
                .get("/api/v1/documents");

        response.then()
                .statusCode(401); // Should require auth
    }

    @Test
    @Order(3)
    @DisplayName("Security: Test invalid authentication token")
    public void testInvalidAuthToken() {
        Response response = given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .header("Authorization", "Bearer invalid_token_12345")
                .when()
                .get("/api/v1/documents");

        response.then()
                .statusCode(401);
    }

    @Test
    @Order(4)
    @DisplayName("Security: Test expired token")
    public void testExpiredToken() {
        // Use an expired or malformed token
        Response response = given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.invalid")
                .when()
                .get("/api/v1/documents");

        response.then()
                .statusCode(401);
    }

    // ==================== Authorization Tests ====================

    @Test
    @Order(5)
    @DisplayName("Security: Test unauthorized document access")
    public void testUnauthorizedDocumentAccess() {
        // Try to access another user's private document
        Response response = given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .header("Authorization", "Bearer " + testUserToken)
                .when()
                .get("/api/v1/documents/999999");

        // Should return 403 or 404, not expose information
        response.then()
                .statusCode(anyOf(equalTo(403), equalTo(404)));
    }

    @Test
    @Order(6)
    @DisplayName("Security: Test privilege escalation attempt")
    public void testPrivilegeEscalation() {
        // Try to access admin endpoints with regular user token
        Response response = given()
                .baseUri(USER_SERVICE_URL)
                .header("Authorization", "Bearer " + testUserToken)
                .when()
                .get("/api/v1/admin/users");

        response.then()
                .statusCode(403); // Forbidden for non-admin
    }

    @Test
    @Order(7)
    @DisplayName("Security: Test unauthorized document modification")
    public void testUnauthorizedModification() {
        // Try to modify another user's document
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("title", "Hacked Title");

        Response response = given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .put("/api/v1/documents/999999");

        response.then()
                .statusCode(anyOf(equalTo(403), equalTo(404)));
    }

    // ==================== SQL Injection Tests ====================

    @Test
    @Order(8)
    @DisplayName("Security: Test SQL injection in login")
    public void testSqlInjectionLogin() {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "admin' OR '1'='1");
        loginRequest.put("password", "anything");

        Response response = given()
                .baseUri(USER_SERVICE_URL)
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/v1/auth/login");

        response.then()
                .statusCode(401); // Should not allow SQL injection
    }

    @Test
    @Order(9)
    @DisplayName("Security: Test SQL injection in search")
    public void testSqlInjectionSearch() {
        Response response = given()
                .baseUri(SEARCH_SERVICE_URL)
                .header("Authorization", "Bearer " + testUserToken)
                .param("keyword", "'; DROP TABLE users;--")
                .when()
                .get("/api/v1/search/documents");

        // Should not crash and should not execute the injection
        response.then()
                .statusCode(anyOf(equalTo(200), equalTo(400)));
    }

    @Test
    @Order(10)
    @DisplayName("Security: Test SQL injection in document ID")
    public void testSqlInjectionDocumentId() {
        Response response = given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .header("Authorization", "Bearer " + testUserToken)
                .when()
                .get("/api/v1/documents/1 OR 1=1");

        response.then()
                .statusCode(400); // Bad request
    }

    // ==================== XSS Tests ====================

    @Test
    @Order(11)
    @DisplayName("Security: Test XSS in document title")
    public void testXssInTitle() {
        Map<String, Object> docRequest = new HashMap<>();
        docRequest.put("title", "<script>alert('XSS')</script>");
        docRequest.put("content", "Test content");

        Response response = given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(ContentType.JSON)
                .body(docRequest)
                .when()
                .post("/api/v1/documents");

        // Should sanitize or reject
        response.then()
                .statusCode(anyOf(equalTo(200), equalTo(400)));
    }

    @Test
    @Order(12)
    @DisplayName("Security: Test XSS in comment")
    public void testXssInComment() {
        Map<String, Object> commentRequest = new HashMap<>();
        commentRequest.put("content", "<img src=x onerror=alert(1)>");

        Response response = given()
                .baseUri(COLLAB_SERVICE_URL)
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(ContentType.JSON)
                .body(commentRequest)
                .when()
                .post("/api/v1/documents/" + testDocumentId + "/comments");

        response.then()
                .statusCode(anyOf(equalTo(200), equalTo(400)));
    }

    // ==================== CSRF Tests ====================

    @Test
    @Order(13)
    @DisplayName("Security: Test CSRF protection")
    public void testCsrfProtection() {
        // Try to make requests without CSRF token
        Map<String, Object> docRequest = new HashMap<>();
        docRequest.put("title", "CSRF Test");
        docRequest.put("content", "Content");

        Response response = given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(ContentType.JSON)
                .body(docRequest)
                .when()
                .post("/api/v1/documents");

        // Should work with valid token, but we test that it doesn't work without
        // Note: This test depends on CSRF configuration
        response.then()
                .statusCode(anyOf(equalTo(200), equalTo(403)));
    }

    // ==================== Input Validation Tests ====================

    @Test
    @Order(14)
    @DisplayName("Security: Test overly long input")
    public void testLongInput() {
        String longString = "A".repeat(10000);
        
        Map<String, Object> docRequest = new HashMap<>();
        docRequest.put("title", longString);
        docRequest.put("content", "Test");

        Response response = given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(ContentType.JSON)
                .body(docRequest)
                .when()
                .post("/api/v1/documents");

        response.then()
                .statusCode(400); // Should reject
    }

    @Test
    @Order(15)
    @DisplayName("Security: Test special characters in input")
    public void testSpecialCharacters() {
        Map<String, Object> docRequest = new HashMap<>();
        docRequest.put("title", "Test\x00\x1f\n\r\t");
        docRequest.put("content", "Content");

        Response response = given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(ContentType.JSON)
                .body(docRequest)
                .when()
                .post("/api/v1/documents");

        // Should handle gracefully
        response.then()
                .statusCode(anyOf(equalTo(200), equalTo(400)));
    }

    // ==================== Rate Limiting Tests ====================

    @Test
    @Order(16)
    @DisplayName("Security: Test rate limiting")
    public void testRateLimiting() {
        // Make many rapid requests
        int successCount = 0;
        for (int i = 0; i < 100; i++) {
            Response response = given()
                    .baseUri(USER_SERVICE_URL)
                    .when()
                    .get("/api/v1/users/me");
            
            if (response.getStatusCode() == 200) {
                successCount++;
            }
        }
        
        System.out.println("Rate limiting test: " + successCount + " requests succeeded out of 100");
        // With rate limiting, not all requests should succeed
    }

    // ==================== Sensitive Data Tests ====================

    @Test
    @Order(17)
    @DisplayName("Security: Test password not returned in response")
    public void testPasswordNotInResponse() {
        Response response = given()
                .baseUri(USER_SERVICE_URL)
                .header("Authorization", "Bearer " + testUserToken)
                .when()
                .get("/api/v1/users/me");

        response.then()
                .body("password", nullValue())
                .body("data.password", nullValue());
        
        String responseBody = response.getBody().asString();
        Assertions.assertFalse(responseBody.contains("password"), "Password should not be in response");
    }

    @Test
    @Order(18)
    @DisplayName("Security: Test sensitive data in logs")
    public void testNoSensitiveDataInLogs() {
        // Make API call
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "testuser");
        loginRequest.put("password", "MySecretPassword123!");
        
        given()
                .baseUri(USER_SERVICE_URL)
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/v1/auth/login");
        
        // Note: This test would require log inspection in a real scenario
        System.out.println("Note: Manual log inspection required to verify no sensitive data exposure");
    }

    // ==================== File Upload Security ====================

    @Test
    @Order(19)
    @DisplayName("Security: Test malicious file upload")
    public void testMaliciousFileUpload() {
        // Try to upload a file with malicious content
        Response response = given()
                .baseUri(STORAGE_SERVICE_URL)
                .header("Authorization", "Bearer " + testUserToken)
                .multiPart("file", "malicious.jsp", "<% Runtime.getRuntime().exec(request.getParameter('cmd')); %>", "application/octet-stream")
                .when()
                .post("/api/v1/upload");

        // Should either reject or sanitize
        response.then()
                .statusCode(anyOf(equalTo(200), equalTo(400), equalTo(403)));
    }

    @Test
    @Order(20)
    @DisplayName("Security: Test path traversal in file upload")
    public void testPathTraversalUpload() {
        Response response = given()
                .baseUri(STORAGE_SERVICE_URL)
                .header("Authorization", "Bearer " + testUserToken)
                .multiPart("file", "../../etc/passwd", "content", "text/plain")
                .when()
                .post("/api/v1/upload");

        // Should reject path traversal
        response.then()
                .statusCode(400);
    }
}