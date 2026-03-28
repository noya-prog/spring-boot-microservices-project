package com.noya.microservices.inventory;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.mysql.MySQLContainer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryServiceApplicationTests {

    @LocalServerPort
    int port;

    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        webTestClient = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void shouldReturnTrueWhenProductIsInStock() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/inventory")
                        .queryParam("skuCode", "iphone_13")
                        .queryParam("quantity", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .value(Boolean.TRUE::equals);
    }

    @Test
    void shouldReturnFalseWhenProductIsOutOfStock() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/inventory")
                        .queryParam("skuCode", "iphone_13")
                        .queryParam("quantity", 200)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .value(Boolean.FALSE::equals);
    }
}
