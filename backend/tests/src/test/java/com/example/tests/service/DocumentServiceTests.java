package com.example.tests.service;

import com.example.tests.config.BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Document Service API Tests
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DocumentServiceTests extends BaseTest {

    private static Long createdDocumentId;

    @BeforeAll
    public void setupAuth() {
        if (authToken == null) {
            loginAsTestUser();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test create document - Success")
    public void testCreateDocument() {
        Map<String, Object> documentRequest = new HashMap<>();
        documentRequest.put("title", "Test Document " + System.currentTimeMillis());
        documentRequest.put("content", "This is test document content");
        documentRequest.put("docType", "docx");
        documentRequest.put("folderId", 1);

        Response response = given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(documentRequest)
                .when()
                .post("/api/v1/documents");

        response.then()
                .statusCode(anyOf(is(200), is(201)))
                .body("code", anyOf(equalTo(200), equalTo(201)))
                .body("data", notNullValue())
                .body("data.title", notNullValue());

        createdDocumentId = response.jsonPath().getLong("data.id");
    }

    @Test
    @Order(2)
    @DisplayName("Test create document - Missing required fields")
    public void testCreateDocumentMissingFields() {
        Map<String, Object> documentRequest = new HashMap<>();
        documentRequest.put("title", "Test Document");
        // Missing docType

        given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(documentRequest)
                .when()
                .post("/api/v1/documents")
                .then()
                .statusCode(anyOf(is(400), is(500)));
    }

    @Test
    @Order(3)
    @DisplayName("Test get document by ID")
    public void testGetDocumentById() {
        if (createdDocumentId == null) {
            testCreateDocument();
        }

        given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/api/v1/documents/" + createdDocumentId)
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("data", notNullValue())
                .body("data.id", equalTo(createdDocumentId));
    }

    @Test
    @Order(4)
    @DisplayName("Test get all documents")
    public void testGetAllDocuments() {
        given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/api/v1/documents")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("data", notNullValue());
    }

    @Test
    @Order(5)
    @DisplayName("Test get documents with pagination")
    public void testGetDocumentsPage() {
        given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .param("pageNum", 1)
                .param("pageSize", 10)
                .when()
                .get("/api/v1/documents/page")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("data", notNullValue());
    }

    @Test
    @Order(6)
    @DisplayName("Test update document")
    public void testUpdateDocument() {
        if (createdDocumentId == null) {
            testCreateDocument();
        }

        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("title", "Updated Document Title");
        updateRequest.put("content", "Updated content");

        given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .put("/api/v1/documents/" + createdDocumentId)
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("message", equalTo("文档更新成功"));
    }

    @Test
    @Order(7)
    @DisplayName("Test get documents by creator")
    public void testGetDocumentsByCreator() {
        given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/api/v1/documents/creator/1")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("data", notNullValue());
    }

    @Test
    @Order(8)
    @DisplayName("Test delete document")
    public void testDeleteDocument() {
        // First create a document to delete
        Map<String, Object> documentRequest = new HashMap<>();
        documentRequest.put("title", "Document to Delete");
        documentRequest.put("content", "Will be deleted");
        documentRequest.put("docType", "txt");

        Response createResponse = given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(documentRequest)
                .when()
                .post("/api/v1/documents");

        Long docId = createResponse.jsonPath().getLong("data.id");

        given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .delete("/api/v1/documents/" + docId)
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("message", equalTo("文档删除成功"));
    }

    @Test
    @Order(9)
    @DisplayName("Test get non-existent document")
    public void testGetNonExistentDocument() {
        given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/api/v1/documents/999999")
                .then()
                .statusCode(anyOf(is(404), is(200)));
    }

    @Test
    @Order(10)
    @DisplayName("Test get documents by folder")
    public void testGetDocumentsByFolder() {
        given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/api/v1/documents/folder/1")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("data", notNullValue());
    }
}