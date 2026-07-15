package com.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpServer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MainTest {

    private HttpServer server;
    private HttpClient client;
    private String baseUrl;

    @BeforeEach
    void setUp() throws Exception {
        server = Main.createServer(0);
        server.start();

        int port = server.getAddress().getPort();

        baseUrl = "http://127.0.0.1:" + port;
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void shouldReturnApplicationInformation() throws Exception {
        HttpResponse<String> response = sendRequest("/");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Inventory API"));
        assertTrue(response.body().contains("running"));
    }

    @Test
    void shouldReturnHealthStatus() throws Exception {
        HttpResponse<String> response = sendRequest("/health");

        assertEquals(200, response.statusCode());
        assertEquals("{\"status\":\"UP\"}", response.body());
    }

    @Test
    void shouldReturnProducts() throws Exception {
        HttpResponse<String> response = sendRequest("/products");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Laptop"));
        assertTrue(response.body().contains("\"quantity\":5"));
        assertTrue(response.body().contains("\"price\":1000.0"));
    }

    private HttpResponse<String> sendRequest(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .GET()
                .build();

        return client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
    }
}
