package com.example.tests.config;

import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;

/**
 * Base test configuration for API tests
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {

    protected static String authToken;
    protected static Long testUserId = 1L;
    protected static String testUsername = "admin";
    protected static String testPassword = "admin123";

    // Service base URLs
    protected static final String USER_SERVICE_URL = "http://localhost:8081";
    protected static final String PERMISSION_SERVICE_URL = "http://localhost:8082";
    protected static final String DOCUMENT_SERVICE_URL = "http://localhost:8083";
    protected static final String WORKFLOW_SERVICE_URL = "http://localhost:8084";
    protected static final String NOTIFICATION_SERVICE_URL = "http://localhost:8085";
    protected static final String COLLAB_SERVICE_URL = "http://localhost:8086";
    protected static final String SEARCH_SERVICE_URL = "http://localhost:8087";
    protected static final String STORAGE_SERVICE_URL = "http://localhost:8088";

    @BeforeAll
    public void setup() {
        // Configure RestAssured
        RestAssured.config = RestAssuredConfig.newConfig()
                .logConfig(new LogConfig().enableLoggingOfRequestAndResponseIfValidationFails());
        
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    /**
     * Get request specification with default headers
     */
    protected RequestSpecification getRequestSpec(String baseUrl) {
        return given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .log().all();
    }

    /**
     * Get request specification with auth token
     */
    protected RequestSpecification getAuthenticatedRequest(String baseUrl) {
        return given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .log().all();
    }

    /**
     * Login and get auth token
     */
    protected String loginAndGetToken(String username, String password) {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", username);
        loginRequest.put("password", password);

        Response response = given()
                .baseUri(USER_SERVICE_URL)
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/v1/auth/login");

        if (response.getStatusCode() == 200) {
            return response.jsonPath().getString("data.token");
        }
        return null;
    }

    /**
     * Login as test user
     */
    protected void loginAsTestUser() {
        authToken = loginAndGetToken(testUsername, testPassword);
    }

    /**
     * Get JSON path from response
     */
    protected String getJsonPath(Response response, String path) {
        return response.jsonPath().getString(path);
    }

    /**
     * Check if response is successful
     */
    protected boolean isSuccess(Response response) {
        int statusCode = response.getStatusCode();
        return statusCode >= 200 && statusCode < 300;
    }

    /**
     * Get response code
     */
    protected int getResponseCode(Response response) {
        return response.jsonPath().getInt("code");
    }
}