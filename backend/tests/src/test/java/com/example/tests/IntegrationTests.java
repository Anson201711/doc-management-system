package com.example.tests.service;

import com.example.tests.config.BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Integration Tests - End-to-End Scenarios
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationTests extends BaseTest {

    private static Long testDocumentId;
    private static Long testWorkflowId;
    private static String testUserToken;

    @BeforeAll
    public void setup() {
        // Create test user and login
        createTestUserIfNotExists();
        testUserToken = loginAsTestUser();
    }

    @Test
    @Order(1)
    @DisplayName("E2E: Create document and upload file")
    public void testCreateDocumentFlow() {
        // Create document
        Map<String, Object> docRequest = new HashMap<>();
        docRequest.put("title", "Integration Test Document " + System.currentTimeMillis());
        docRequest.put("content", "Test content for integration");
        docRequest.put("folderId", 1);
        docRequest.put("tags", new String[]{"integration", "test"});

        Response docResponse = given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(ContentType.JSON)
                .body(docRequest)
                .when()
                .post("/api/v1/documents");

        docResponse.then()
                .statusCode(200)
                .body("code", equalTo(200));

        testDocumentId = docResponse.jsonPath().getLong("data.id");
        Assertions.assertNotNull(testDocumentId);
    }

    @Test
    @Order(2)
    @DisplayName("E2E: Add collaborator to document")
    public void testAddCollaboratorFlow() {
        Map<String, Object> collabRequest = new HashMap<>();
        collabRequest.put("userId", 2);
        collabRequest.put("permission", "edit");

        Response collabResponse = given()
                .baseUri(PERMISSION_SERVICE_URL)
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(ContentType.JSON)
                .body(collabRequest)
                .when()
                .post("/api/v1/documents/" + testDocumentId + "/collaborators");

        collabResponse.then()
                .statusCode(200)
                .body("code", equalTo(200));
    }

    @Test
    @Order(3)
    @DisplayName("E2E: Create approval workflow")
    public void testCreateWorkflowFlow() {
        Map<String, Object> workflowRequest = new HashMap<>();
        workflowRequest.put("documentId", testDocumentId);
        workflowRequest.put("title", "Approval for " + testDocumentId);
        workflowRequest.put("workflowType", "document_approval");
        workflowRequest.put("approvers", new Long[]{2L, 3L});

        Response workflowResponse = given()
                .baseUri(WORKFLOW_SERVICE_URL)
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(ContentType.JSON)
                .body(workflowRequest)
                .when()
                .post("/api/v1/workflows");

        workflowResponse.then()
                .statusCode(200)
                .body("code", equalTo(200));

        testWorkflowId = workflowResponse.jsonPath().getLong("data.id");
    }

    @Test
    @Order(4)
    @DisplayName("E2E: Approve workflow")
    public void testApproveWorkflowFlow() {
        Map<String, Object> approvalRequest = new HashMap<>();
        approvalRequest.put("action", "approve");
        approvalRequest.put("comment", "Approved");

        Response approveResponse = given()
                .baseUri(WORKFLOW_SERVICE_URL)
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(ContentType.JSON)
                .body(approvalRequest)
                .when()
                .post("/api/v1/workflows/" + testWorkflowId + "/approve");

        approveResponse.then()
                .statusCode(200)
                .body("code", equalTo(200));
    }

    @Test
    @Order(5)
    @DisplayName("E2E: Add comment to document")
    public void testAddCommentFlow() {
        Map<String, Object> commentRequest = new HashMap<>();
        commentRequest.put("content", "Great document!");
        commentRequest.put("parentId", null);

        Response commentResponse = given()
                .baseUri(COLLAB_SERVICE_URL)
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(ContentType.JSON)
                .body(commentRequest)
                .when()
                .post("/api/v1/documents/" + testDocumentId + "/comments");

        commentResponse.then()
                .statusCode(200)
                .body("code", equalTo(200));
    }

    @Test
    @Order(6)
    @DisplayName("E2E: Search document")
    public void testSearchDocumentFlow() {
        Response searchResponse = given()
                .baseUri(SEARCH_SERVICE_URL)
                .header("Authorization", "Bearer " + testUserToken)
                .param("keyword", "Integration Test")
                .when()
                .get("/api/v1/search/documents");

        searchResponse.then()
                .statusCode(200)
                .body("code", equalTo(200));
    }

    @Test
    @Order(7)
    @DisplayName("E2E: Send notification")
    public void testNotificationFlow() {
        Map<String, Object> notifRequest = new HashMap<>();
        notifRequest.put("userId", 2);
        notifRequest.put("type", "document_share");
        notifRequest.put("title", "Document Shared");
        notifRequest.put("content", "A document has been shared with you");

        Response notifResponse = given()
                .baseUri(NOTIFICATION_SERVICE_URL)
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(ContentType.JSON)
                .body(notifRequest)
                .when()
                .post("/api/v1/notifications");

        notifResponse.then()
                .statusCode(200)
                .body("code", equalTo(200));
    }

    @Test
    @Order(8)
    @DisplayName("E2E: Full workflow - document lifecycle")
    public void testFullDocumentLifecycle() {
        // 1. Create document
        Map<String, Object> docRequest = new HashMap<>();
        docRequest.put("title", "Full Lifecycle Test " + System.currentTimeMillis());
        docRequest.put("content", "Testing full lifecycle");

        Response docResponse = given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(ContentType.JSON)
                .body(docRequest)
                .when()
                .post("/api/v1/documents");

        docResponse.then().statusCode(200);
        Long docId = docResponse.jsonPath().getLong("data.id");

        // 2. Update document
        docRequest.put("content", "Updated content");
        given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(ContentType.JSON)
                .body(docRequest)
                .when()
                .put("/api/v1/documents/" + docId)
                .then()
                .statusCode(200);

        // 3. Share document
        Map<String, Object> shareRequest = new HashMap<>();
        shareRequest.put("userId", 3);
        shareRequest.put("permission", "view");
        given()
                .baseUri(PERMISSION_SERVICE_URL)
                .header("Authorization", "Bearer " + testUserToken)
                .contentType(ContentType.JSON)
                .body(shareRequest)
                .when()
                .post("/api/v1/documents/" + docId + "/share")
                .then()
                .statusCode(200);

        // 4. Get document with permissions
        given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .header("Authorization", "Bearer " + testUserToken)
                .when()
                .get("/api/v1/documents/" + docId)
                .then()
                .statusCode(200);

        // 5. Delete document
        given()
                .baseUri(DOCUMENT_SERVICE_URL)
                .header("Authorization", "Bearer " + testUserToken)
                .when()
                .delete("/api/v1/documents/" + docId)
                .then()
                .statusCode(200);
    }
}