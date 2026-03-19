package com.example.tests.service;

import com.example.tests.config.BaseTest;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Search Service API Tests
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SearchServiceTests extends BaseTest {

    @BeforeAll
    public void setupAuth() {
        if (authToken == null) {
            loginAsTestUser();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test search documents - With keyword")
    public void testSearchWithKeyword() {
        given()
                .baseUri(SEARCH_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .param("q", "test")
                .param("pageNum", 1)
                .param("pageSize", 10)
                .when()
                .get("/api/v1/search")
                .then()
                .statusCode(200)
                .body("code", equalTo(200));
    }

    @Test
    @Order(2)
    @DisplayName("Test search documents - With filters")
    public void testSearchWithFilters() {
        given()
                .baseUri(SEARCH_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .param("docType", "docx")
                .param("creatorId", 1)
                .param("status", "published")
                .when()
                .get("/api/v1/search")
                .then()
                .statusCode(200)
                .body("code", equalTo(200));
    }

    @Test
    @Order(3)
    @DisplayName("Test search documents - Empty query")
    public void testSearchEmptyQuery() {
        given()
                .baseUri(SEARCH_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/api/v1/search")
                .then()
                .statusCode(200)
                .body("code", equalTo(200));
    }

    @Test
    @Order(4)
    @DisplayName("Test get documents list")
    public void testGetDocumentsList() {
        given()
                .baseUri(SEARCH_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .param("docType", "docx")
                .param("pageNum", 1)
                .param("pageSize", 10)
                .when()
                .get("/api/v1/search/documents")
                .then()
                .statusCode(200)
                .body("code", equalTo(200));
    }

    @Test
    @Order(5)
    @DisplayName("Test rebuild search index")
    public void testRebuildIndex() {
        given()
                .baseUri(SEARCH_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .post("/api/v1/search/index")
                .then()
                .statusCode(anyOf(is(200), is(202)))
                .body("code", anyOf(equalTo(200), equalTo(202)));
    }

    @Test
    @Order(6)
    @DisplayName("Test delete search index")
    public void testDeleteIndex() {
        given()
                .baseUri(SEARCH_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .delete("/api/v1/search/index/doc_1")
                .then()
                .statusCode(200)
                .body("code", equalTo(200));
    }
}