package com.noya.microservices.order;

import com.noya.microservices.order.client.InventoryClient;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.mysql.MySQLContainer;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

class OrderServiceApplicationTests {
    @ServiceConnection
    static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.3.0");

    @LocalServerPort
    private Integer port;
    @MockitoBean
    private InventoryClient inventoryClient;
    @BeforeEach
    void setUp(){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        when(inventoryClient.isInStock(anyString(), anyInt())).thenReturn(true);
    }
    static {
        mySQLContainer.start();
    }

	@Test
	void shouldCreateOrder() {
        String requestBody = """
                {
                "skuCode": "iphone_13",
                    "quantity": 1,
                    "price": 999
                }
                """;
        RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/order")
                .then()
                .statusCode(201)
                .body("id", Matchers.notNullValue())
                .body("orderNumber", Matchers.notNullValue())
                .body("skuCode", Matchers.equalTo("iphone_13"))
                .body("quantity", Matchers.equalTo(1))
                .body("price", Matchers.equalTo(999));

	}

}
