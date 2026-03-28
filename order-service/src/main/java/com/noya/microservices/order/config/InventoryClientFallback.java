package com.noya.microservices.order.config;


import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class InventoryClientFallback {
    private static final Logger log =
            LoggerFactory.getLogger(InventoryClientFallback.class);

    public Boolean inventoryFallback(String skuCode,
                                     Integer quantity,
                                     Throwable throwable) {

        log.info("Cannot get inventory for skuCode {}, reason: {}",
                skuCode,
                throwable.getMessage());

        return false;
    }
}
