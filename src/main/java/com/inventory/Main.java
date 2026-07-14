package com.inventory;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080),
                0
        );

        server.createContext("/", exchange -> sendJson(
                exchange,
                200,
                "{\"application\":\"Inventory API\",\"status\":\"running\"}"
        ));

        server.createContext("/health", exchange -> sendJson(
                exchange,
                200,
                "{\"status\":\"UP\"}"
        ));

        server.createContext("/products", exchange -> sendJson(
                exchange,
                200,
                "[{\"name\":\"Laptop\",\"quantity\":5,\"price\":1000.0}]"
        ));

        server.setExecutor(null);
        server.start();

        System.out.println("Inventory API running on port 8080");
    }

    private static void sendJson(
            HttpExchange exchange,
            int statusCode,
            String body
    ) throws IOException {

        byte[] response = body.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
        );

        exchange.sendResponseHeaders(statusCode, response.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(response);
        }
    }
}
