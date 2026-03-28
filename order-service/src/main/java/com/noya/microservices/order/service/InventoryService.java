package com.noya.microservices.order.service;

import com.noya.microservices.order.client.InventoryClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;



@Service
public class InventoryService {
    private final InventoryClient inventoryClient;
    private static final Logger log =
            LoggerFactory.getLogger(InventoryService.class);

    public InventoryService(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    @CircuitBreaker(name = "inventory", fallbackMethod = "fallback")
    @
            Retry(name = "inventory")
    public Boolean checkInventory(String skuCode, Integer quantity) {
        return inventoryClient.isInStock(skuCode, quantity);
    }

    public Boolean fallback(String skuCode,
                            Integer quantity,
                            Throwable throwable) {

        log.info("Cannot get inventory for skuCode {}, reason: {}",
                skuCode,
                throwable.getMessage());

        return false;
    }
}
