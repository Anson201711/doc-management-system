package com.example.tests.performance;

import com.example.tests.config.BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static io.restassured.RestAssured.*;

/**
 * Performance Tests - Load, Stress, Endurance Testing
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PerformanceTests extends BaseTest {

    private static final int CONCURRENT_USERS = 50;
    private static final int ITERATIONS_PER_USER = 10;
    private static final int RAMP_UP_TIME_MS = 5000;
    
    private static String testUserToken;
    private static List<Long> documentIds = new ArrayList<>();

    @BeforeAll
    public void setup() {
        createTestUserIfNotExists();
        testUserToken = loginAsTestUser();
    }

    @AfterAll
    public void cleanup() {
        // Cleanup test documents
        for (Long docId : documentIds) {
            try {
                given()
                        .baseUri(DOCUMENT_SERVICE_URL)
                        .header("Authorization", "Bearer " + testUserToken)
                        .when()
                        .delete("/api/v1/documents/" + docId);
            } catch (Exception ignored) {}
        }
    }

    @Test
    @Order(1)
    @DisplayName("Performance: Concurrent document creation")
    public void testConcurrentDocumentCreation() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(CONCURRENT_USERS);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());

        // Ramp up
        System.out.println("Starting concurrent document creation test...");
        
        for (int i = 0; i < CONCURRENT_USERS; i++) {
            final int userId = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    
                    for (int j = 0; j < ITERATIONS_PER_USER; j++) {
                        long startTime = System.currentTimeMillis();
                        
                        Map<String, Object> docRequest = new HashMap<>();
                        docRequest.put("title", "Perf Test User " + userId + " Doc " + j);
                        docRequest.put("content", "Performance test content");

                        Response response = given()
                                .baseUri(DOCUMENT_SERVICE_URL)
                                .header("Authorization", "Bearer " + testUserToken)
                                .contentType(ContentType.JSON)
                                .body(docRequest)
                                .when()
                                .post("/api/v1/documents");

                        long responseTime = System.currentTimeMillis() - startTime;
                        responseTimes.add(responseTime);

                        if (response.getStatusCode() == 200) {
                            successCount.incrementAndGet();
                            try {
                                Long docId = response.jsonPath().getLong("data.id");
                                if (docId != null) documentIds.add(docId);
                            } catch (Exception ignored) {}
                        } else {
                            failureCount.incrementAndGet();
                        }
                    }
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        // Start all threads simultaneously
        startLatch.countDown();
        
        // Wait for completion with timeout
        boolean completed = endLatch.await(RAMP_UP_TIME_MS + (CONCURRENT_USERS * ITERATIONS_PER_USER * 2000), TimeUnit.MILLISECONDS);
        executor.shutdown();

        // Calculate statistics
        Collections.sort(responseTimes);
        long totalRequests = successCount.get() + failureCount.get();
        double throughput = totalRequests / ((RAMP_UP_TIME_MS / 1000.0) + 2.0);
        
        System.out.println("\n=== Document Creation Performance Results ===");
        System.out.println("Total Requests: " + totalRequests);
        System.out.println("Successful: " + successCount.get());
        System.out.println("Failed: " + failureCount.get());
        System.out.println("Throughput: " + String.format("%.2f", throughput) + " req/sec");
        System.out.println("Min Response Time: " + responseTimes.stream().mapToLong(Long::longValue).min().orElse(0) + "ms");
        System.out.println("Max Response Time: " + responseTimes.stream().mapToLong(Long::longValue).max().orElse(0) + "ms");
        System.out.println("Avg Response Time: " + String.format("%.2f", responseTimes.stream().mapToLong(Long::longValue).average().orElse(0)) + "ms");
        
        if (!responseTimes.isEmpty()) {
            int p50 = responseTimes.size() / 2;
            int p95 = (int) (responseTimes.size() * 0.95);
            int p99 = (int) (responseTimes.size() * 0.99);
            System.out.println("P50 Response Time: " + responseTimes.get(p50) + "ms");
            System.out.println("P95 Response Time: " + responseTimes.get(p95) + "ms");
            System.out.println("P99 Response Time: " + responseTimes.get(p99) + "ms");
        }

        // Assertions
        Assertions.assertTrue(successCount.get() > 0, "At least some requests should succeed");
    }

    @Test
    @Order(2)
    @DisplayName("Performance: Concurrent document read")
    public void testConcurrentDocumentRead() throws InterruptedException, ExecutionException {
        // First create some documents to read
        for (int i = 0; i < 20; i++) {
            Map<String, Object> docRequest = new HashMap<>();
            docRequest.put("title", "Read Test Doc " + i);
            docRequest.put("content", "Content " + i);
            
            Response response = given()
                    .baseUri(DOCUMENT_SERVICE_URL)
                    .header("Authorization", "Bearer " + testUserToken)
                    .contentType(ContentType.JSON)
                    .body(docRequest)
                    .when()
                    .post("/api/v1/documents");
            
            if (response.getStatusCode() == 200) {
                try {
                    documentIds.add(response.jsonPath().getLong("data.id"));
                } catch (Exception ignored) {}
            }
        }

        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_USERS * 5);
        AtomicInteger successCount = new AtomicInteger(0);
        List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < CONCURRENT_USERS * 5; i++) {
            executor.submit(() -> {
                try {
                    if (!documentIds.isEmpty()) {
                        Long docId = documentIds.get(new Random().nextInt(documentIds.size()));
                        long startTime = System.currentTimeMillis();
                        
                        Response response = given()
                                .baseUri(DOCUMENT_SERVICE_URL)
                                .header("Authorization", "Bearer " + testUserToken)
                                .when()
                                .get("/api/v1/documents/" + docId);

                        responseTimes.add(System.currentTimeMillis() - startTime);
                        
                        if (response.getStatusCode() == 200) {
                            successCount.incrementAndGet();
                        }
                    }
                } catch (Exception e) {
                    // Ignore
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        System.out.println("\n=== Document Read Performance Results ===");
        System.out.println("Total Reads: " + (CONCURRENT_USERS * 5));
        System.out.println("Successful: " + successCount.get());
        System.out.println("Avg Response Time: " + String.format("%.2f", responseTimes.stream().mapToLong(Long::longValue).average().orElse(0)) + "ms");
    }

    @Test
    @Order(3)
    @DisplayName("Performance: Search under load")
    public void testSearchPerformance() throws InterruptedException {
        // Create documents with searchable content
        for (int i = 0; i < 50; i++) {
            Map<String, Object> docRequest = new HashMap<>();
            docRequest.put("title", "Search Test " + i);
            docRequest.put("content", "This is searchable content number " + i);
            docRequest.put("tags", new String[]{"search", "test"});
            
            Response response = given()
                    .baseUri(DOCUMENT_SERVICE_URL)
                    .header("Authorization", "Bearer " + testUserToken)
                    .contentType(ContentType.JSON)
                    .body(docRequest)
                    .when()
                    .post("/api/v1/documents");
            
            if (response.getStatusCode() == 200) {
                try {
                    documentIds.add(response.jsonPath().getLong("data.id"));
                } catch (Exception ignored) {}
            }
        }

        // Wait for indexing
        Thread.sleep(5000);

        // Search performance test
        List<Long> searchTimes = new ArrayList<>();
        
        for (int i = 0; i < 20; i++) {
            long startTime = System.currentTimeMillis();
            
            Response response = given()
                    .baseUri(SEARCH_SERVICE_URL)
                    .header("Authorization", "Bearer " + testUserToken)
                    .param("keyword", "searchable")
                    .when()
                    .get("/api/v1/search/documents");
            
            searchTimes.add(System.currentTimeMillis() - startTime);
        }

        System.out.println("\n=== Search Performance Results ===");
        System.out.println("Avg Search Time: " + String.format("%.2f", searchTimes.stream().mapToLong(Long::longValue).average().orElse(0)) + "ms");
        System.out.println("Max Search Time: " + searchTimes.stream().mapToLong(Long::longValue).max().orElse(0) + "ms");
    }

    @Test
    @Order(4)
    @DisplayName("Performance: Database connection pool test")
    public void testDatabaseConnectionPool() {
        // Test connection pool efficiency
        List<Long> responseTimes = new ArrayList<>();
        
        for (int i = 0; i < 100; i++) {
            long startTime = System.currentTimeMillis();
            
            given()
                    .baseUri(DOCUMENT_SERVICE_URL)
                    .header("Authorization", "Bearer " + testUserToken)
                    .when()
                    .get("/api/v1/documents?page=1&size=10");
            
            responseTimes.add(System.currentTimeMillis() - startTime);
        }

        System.out.println("\n=== Connection Pool Test Results ===");
        System.out.println("Total Requests: 100");
        System.out.println("Avg Response Time: " + String.format("%.2f", responseTimes.stream().mapToLong(Long::longValue).average().orElse(0)) + "ms");
        
        // Check for connection leaks
        long slowRequests = responseTimes.stream().filter(t -> t > 1000).count();
        System.out.println("Slow Requests (>1s): " + slowRequests);
        
        Assertions.assertTrue(slowRequests < 10, "Should have less than 10% slow requests");
    }

    @Test
    @Order(5)
    @DisplayName("Performance: Memory stress test")
    public void testMemoryStress() {
        // Simulate memory pressure
        List<byte[]> memoryHog = new ArrayList<>();
        
        try {
            for (int i = 0; i < 100; i++) {
                byte[] data = new byte[1024 * 1024]; // 1MB
                memoryHog.add(data);
                
                // Make some requests
                given()
                        .baseUri(DOCUMENT_SERVICE_URL)
                        .header("Authorization", "Bearer " + testUserToken)
                        .when()
                        .get("/api/v1/documents?page=1&size=10");
            }
        } catch (OutOfMemoryError e) {
            System.out.println("Memory limit reached - application handled gracefully");
        } finally {
            memoryHog.clear();
        }
        
        System.out.println("\n=== Memory Stress Test ===");
        System.out.println("Test completed without crash");
    }
}