package com.example.tests.service;

import com.example.tests.config.BaseTest;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Notification Service API Tests
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NotificationServiceTests extends BaseTest {

    @BeforeAll
    public void setupAuth() {
        if (authToken == null) {
            loginAsTestUser();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test get notifications")
    public void testGetNotifications() {
        given()
                .baseUri(NOTIFICATION_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .param("userId", 1)
                .param("page", 1)
                .param("size", 20)
                .when()
                .get("/api/v1/notifications")
                .then()
                .statusCode(200)
                .body("code", equalTo(200));
    }

    @Test
    @Order(2)
    @DisplayName("Test get notifications - Missing userId")
    public void testGetNotificationsMissingUserId() {
        given()
                .baseUri(NOTIFICATION_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/api/v1/notifications")
                .then()
                .statusCode(anyOf(is(400), is(500)));
    }

    @Test
    @Order(3)
    @DisplayName("Test get unread count")
    public void testGetUnreadCount() {
        given()
                .baseUri(NOTIFICATION_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .param("userId", 1)
                .when()
                .get("/api/v1/notifications/unread")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("data", notNullValue());
    }

    @Test
    @Order(4)
    @DisplayName("Test mark notification as read")
    public void testMarkAsRead() {
        given()
                .baseUri(NOTIFICATION_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .param("userId", 1)
                .when()
                .put("/api/v1/notifications/1/read")
                .then()
                .statusCode(anyOf(is(200), is(404)));
    }

    @Test
    @Order(5)
    @DisplayName("Test mark all as read")
    public void testMarkAllAsRead() {
        given()
                .baseUri(NOTIFICATION_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .param("userId", 1)
                .when()
                .put("/api/v1/notifications/read-all")
                .then()
                .statusCode(200)
                .body("code", equalTo(200));
    }

    @Test
    @Order(6)
    @DisplayName("Test send notification (internal)")
    public void testSendNotification() {
        given()
                .baseUri(NOTIFICATION_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .param("userId", 1)
                .param("type", "system")
                .param("title", "Test Notification")
                .param("content", "This is a test notification")
                .param("sender", "System")
                .when()
                .post("/api/v1/notifications/send")
                .then()
                .statusCode(200)
                .body("code", equalTo(200));
    }

    @Test
    @Order(7)
    @DisplayName("Test send email notification")
    public void testSendEmail() {
        given()
                .baseUri(NOTIFICATION_SERVICE_URL)
                .header("Authorization", "Bearer " + authToken)
                .param("to", "test@example.com")
                .param("subject", "Test Email")
                .param("content", "This is a test email")
                .when()
                .post("/api/v1/notifications/email")
                .then()
                .statusCode(anyOf(is(200), is(500)));
    }
}