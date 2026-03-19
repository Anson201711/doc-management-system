package com.example.tests.service;

import com.example.tests.config.BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Workflow Service API Tests
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WorkflowServiceTests extends BaseTest {

    private static Long createdWorkflowId;

    @BeforeAll
    public void setupAuth() {
        if (authToken == null) {
            loginAsTestUser();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test create workflow - Success")
    public void testCreateWorkflow() {
        Map<String, Object> workflowRequest = new HashMap<>();
        workflowRequest.put("documentId", 1);
        workflowRequest.put("title", "Test Workflow " + System.currentTimeMillis());
        workflowRequest.put("workflowType", "document_approval");
        workflowRequest.put("approvers", new Long[]{2L, 3L});
        workflowRequest.put("content", "Please review this document");

        Response response = given()
                .baseUri(WORKFLOW_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(workflowRequest)
                .when()
                .post("/api/v1/workflows");

        response.then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("data", notNullValue())
                .body("data.id", notNullValue());

        createdWorkflowId = response.jsonPath().getLong("data.id");
    }

    @Test
    @Order(2)
    @DisplayName("Test create workflow - Missing fields")
    public void testCreateWorkflowMissingFields() {
        Map<String, Object> workflowRequest = new HashMap<>();
        workflowRequest.put("documentId", 1);
        // Missing title, workflowType, approvers

        given()
                .baseUri(WORKFLOW_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(workflowRequest)
                .when()
                .post("/api/v1/workflows")
                .then()
                .statusCode(anyOf(is(400), is(500)));
    }

    @Test
    @Order(3)
    @DisplayName("Test get workflow by ID")
    public void testGetWorkflowById() {
        if (createdWorkflowId == null) {
            testCreateWorkflow();
        }

        given()
                .baseUri(WORKFLOW_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/api/v1/workflows/" + createdWorkflowId)
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("data", notNullValue());
    }

    @Test
    @Order(4)
    @DisplayName("Test get workflows - All")
    public void testGetWorkflowsAll() {
        given()
                .baseUri(WORKFLOW_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .param("page", 1)
                .param("size", 10)
                .when()
                .get("/api/v1/workflows")
                .then()
                .statusCode(200)
                .body("code", equalTo(200));
    }

    @Test
    @Order(5)
    @DisplayName("Test get workflows by document ID")
    public void testGetWorkflowsByDocId() {
        given()
                .baseUri(WORKFLOW_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .param("docId", 1)
                .when()
                .get("/api/v1/workflows")
                .then()
                .statusCode(200)
                .body("code", equalTo(200));
    }

    @Test
    @Order(6)
    @DisplayName("Test get my approval tasks")
    public void testGetMyApprovalTasks() {
        given()
                .baseUri(WORKFLOW_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .param("userId", 1)
                .when()
                .get("/api/v1/workflows/my")
                .then()
                .statusCode(200)
                .body("code", equalTo(200));
    }

    @Test
    @Order(7)
    @DisplayName("Test approve workflow")
    public void testApproveWorkflow() {
        if (createdWorkflowId == null) {
            testCreateWorkflow();
        }

        Map<String, Object> approvalRequest = new HashMap<>();
        approvalRequest.put("taskId", 1);
        approvalRequest.put("approverId", 2);
        approvalRequest.put("comment", "Approved");

        given()
                .baseUri(WORKFLOW_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(approvalRequest)
                .when()
                .put("/api/v1/workflows/" + createdWorkflowId + "/approve")
                .then()
                .statusCode(anyOf(is(200), is(400)));
    }

    @Test
    @Order(8)
    @DisplayName("Test reject workflow")
    public void testRejectWorkflow() {
        // Create a new workflow for rejection test
        Map<String, Object> workflowRequest = new HashMap<>();
        workflowRequest.put("documentId", 1);
        workflowRequest.put("title", "Workflow for Rejection");
        workflowRequest.put("workflowType", "document_approval");
        workflowRequest.put("approvers", new Long[]{2L});
        
        Response createResponse = given()
                .baseUri(WORKFLOW_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(workflowRequest)
                .when()
                .post("/api/v1/workflows");
        
        Long workflowId = createResponse.jsonPath().getLong("data.id");

        Map<String, Object> rejectionRequest = new HashMap<>();
        rejectionRequest.put("taskId", 1);
        rejectionRequest.put("approverId", 2);
        rejectionRequest.put("comment", "需要修改");

        given()
                .baseUri(WORKFLOW_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(rejectionRequest)
                .when()
                .put("/api/v1/workflows/" + workflowId + "/reject")
                .then()
                .statusCode(anyOf(is(200), is(400)));
    }
}