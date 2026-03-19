package com.example.tests.service;

import com.example.tests.config.BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Collaboration Service API Tests
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CollabServiceTests extends BaseTest {

    private static Long createdCommentId;
    private static Long createdAnnotationId;

    @BeforeAll
    public void setupAuth() {
        if (authToken == null) {
            loginAsTestUser();
        }
    }

    // ==================== Comment Tests ====================

    @Test
    @Order(1)
    @DisplayName("Test add comment - Success")
    public void testAddComment() {
        Map<String, Object> commentRequest = new HashMap<>();
        commentRequest.put("documentId", 1);
        commentRequest.put("content", "Test comment " + System.currentTimeMillis());
        commentRequest.put("authorId", 1);
        commentRequest.put("authorName", "Admin");

        Response response = given()
                .baseUri(COLLAB_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(commentRequest)
                .when()
                .post("/api/v1/comments");

        response.then()
                .statusCode(anyOf(is(200), is(201)))
                .body("documentId", equalTo(1))
                .body("content", notNullValue());

        createdCommentId = response.jsonPath().getLong("id");
    }

    @Test
    @Order(2)
    @DisplayName("Test add comment - Missing fields")
    public void testAddCommentMissingFields() {
        Map<String, Object> commentRequest = new HashMap<>();
        commentRequest.put("documentId", 1);
        // Missing content, authorId

        given()
                .baseUri(COLLAB_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(commentRequest)
                .when()
                .post("/api/v1/comments")
                .then()
                .statusCode(anyOf(is(400), is(500)));
    }

    @Test
    @Order(3)
    @DisplayName("Test get comments by document ID")
    public void testGetCommentsByDocId() {
        given()
                .baseUri(COLLAB_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .param("docId", 1)
                .when()
                .get("/api/v1/comments")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(4)
    @DisplayName("Test get comment by ID")
    public void testGetCommentById() {
        if (createdCommentId == null) {
            testAddComment();
        }

        given()
                .baseUri(COLLAB_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/api/v1/comments/" + createdCommentId)
                .then()
                .statusCode(anyOf(is(200), is(404)));
    }

    @Test
    @Order(5)
    @DisplayName("Test update comment")
    public void testUpdateComment() {
        if (createdCommentId == null) {
            testAddComment();
        }

        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("documentId", 1);
        updateRequest.put("content", "Updated comment");
        updateRequest.put("authorId", 1);
        updateRequest.put("authorName", "Admin");

        given()
                .baseUri(COLLAB_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .put("/api/v1/comments/" + createdCommentId)
                .then()
                .statusCode(anyOf(is(200), is(404)));
    }

    @Test
    @Order(6)
    @DisplayName("Test reply to comment")
    public void testReplyToComment() {
        if (createdCommentId == null) {
            testAddComment();
        }

        Map<String, Object> replyRequest = new HashMap<>();
        replyRequest.put("documentId", 1);
        replyRequest.put("content", "This is a reply");
        replyRequest.put("authorId", 2);
        replyRequest.put("authorName", "User2");

        given()
                .baseUri(COLLAB_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(replyRequest)
                .when()
                .post("/api/v1/comments/" + createdCommentId + "/reply")
                .then()
                .statusCode(anyOf(is(200), is(201)));
    }

    @Test
    @Order(7)
    @DisplayName("Test delete comment")
    public void testDeleteComment() {
        // Create a new comment to delete
        Map<String, Object> commentRequest = new HashMap<>();
        commentRequest.put("documentId", 1);
        commentRequest.put("content", "Comment to delete");
        commentRequest.put("authorId", 1);
        commentRequest.put("authorName", "Admin");

        Response response = given()
                .baseUri(COLLAB_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(commentRequest)
                .when()
                .post("/api/v1/comments");

        Long commentId = response.jsonPath().getLong("id");

        given()
                .baseUri(COLLAB_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .delete("/api/v1/comments/" + commentId)
                .then()
                .statusCode(204);
    }

    // ==================== Annotation Tests ====================

    @Test
    @Order(8)
    @DisplayName("Test add annotation - Success")
    public void testAddAnnotation() {
        Map<String, Object> annotationRequest = new HashMap<>();
        annotationRequest.put("documentId", 1);
        annotationRequest.put("content", "Test annotation");
        annotationRequest.put("position", Map.of("page", 1, "x", 100, "y", 200));
        annotationRequest.put("authorId", 1);
        annotationRequest.put("authorName", "Admin");

        Response response = given()
                .baseUri(COLLAB_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(annotationRequest)
                .when()
                .post("/api/v1/annotations");

        response.then()
                .statusCode(anyOf(is(200), is(201)))
                .body("documentId", equalTo(1));

        createdAnnotationId = response.jsonPath().getLong("id");
    }

    @Test
    @Order(9)
    @DisplayName("Test add annotation - Missing fields")
    public void testAddAnnotationMissingFields() {
        Map<String, Object> annotationRequest = new HashMap<>();
        annotationRequest.put("documentId", 1);
        // Missing content, position, authorId

        given()
                .baseUri(COLLAB_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(annotationRequest)
                .when()
                .post("/api/v1/annotations")
                .then()
                .statusCode(anyOf(is(400), is(500)));
    }

    @Test
    @Order(10)
    @DisplayName("Test get annotations by document ID")
    public void testGetAnnotationsByDocId() {
        given()
                .baseUri(COLLAB_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .param("docId", 1)
                .when()
                .get("/api/v1/annotations")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(11)
    @DisplayName("Test get annotation by ID")
    public void testGetAnnotationById() {
        if (createdAnnotationId == null) {
            testAddAnnotation();
        }

        given()
                .baseUri(COLLAB_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/api/v1/annotations/" + createdAnnotationId)
                .then()
                .statusCode(anyOf(is(200), is(404)));
    }

    @Test
    @Order(12)
    @DisplayName("Test update annotation")
    public void testUpdateAnnotation() {
        if (createdAnnotationId == null) {
            testAddAnnotation();
        }

        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("documentId", 1);
        updateRequest.put("content", "Updated annotation");
        updateRequest.put("position", Map.of("page", 1, "x", 150, "y", 250));
        updateRequest.put("authorId", 1);
        updateRequest.put("authorName", "Admin");

        given()
                .baseUri(COLLAB_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .put("/api/v1/annotations/" + createdAnnotationId)
                .then()
                .statusCode(anyOf(is(200), is(404)));
    }

    @Test
    @Order(13)
    @DisplayName("Test delete annotation")
    public void testDeleteAnnotation() {
        // Create a new annotation to delete
        Map<String, Object> annotationRequest = new HashMap<>();
        annotationRequest.put("documentId", 1);
        annotationRequest.put("content", "Annotation to delete");
        annotationRequest.put("position", Map.of("page", 1, "x", 50, "y", 50));
        annotationRequest.put("authorId", 1);
        annotationRequest.put("authorName", "Admin");

        Response response = given()
                .baseUri(COLLAB_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(annotationRequest)
                .when()
                .post("/api/v1/annotations");

        Long annotationId = response.jsonPath().getLong("id");

        given()
                .baseUri(COLLAB_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .delete("/api/v1/annotations/" + annotationId)
                .then()
                .statusCode(204);
    }
}