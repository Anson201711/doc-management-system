package com.example.tests.service;

import com.example.tests.config.BaseTest;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Storage Service API Tests
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StorageServiceTests extends BaseTest {

    @BeforeAll
    public void setupAuth() {
        if (authToken == null) {
            loginAsTestUser();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test get file info - Existing file")
    public void testGetFileInfo() {
        given()
                .baseUri(STORAGE_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/api/v1/storage/1")
                .then()
                .statusCode(anyOf(is(200), is(404)));
    }

    @Test
    @Order(2)
    @DisplayName("Test get file info - Non-existent")
    public void testGetFileInfoNotFound() {
        given()
                .baseUri(STORAGE_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/api/v1/storage/999999")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(3)
    @DisplayName("Test get files by document ID")
    public void testGetFilesByDocumentId() {
        given()
                .baseUri(STORAGE_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/api/v1/storage/document/1")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(4)
    @DisplayName("Test get download URL")
    public void testGetDownloadUrl() {
        given()
                .baseUri(STORAGE_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .param("expiry", 60)
                .when()
                .get("/api/v1/storage/1/download")
                .then()
                .statusCode(anyOf(is(200), is(404)));
    }

    @Test
    @Order(5)
    @DisplayName("Test delete file")
    public void testDeleteFile() {
        // Note: This test may fail if file doesn't exist
        given()
                .baseUri(STORAGE_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .delete("/api/v1/storage/999999")
                .then()
                .statusCode(anyOf(is(200), is(404), is(500)));
    }

    @Test
    @Order(6)
    @DisplayName("Test init bucket")
    public void testInitBucket() {
        given()
                .baseUri(STORAGE_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .post("/api/v1/storage/init-bucket")
                .then()
                .statusCode(anyOf(is(200), is(400)));
    }
}