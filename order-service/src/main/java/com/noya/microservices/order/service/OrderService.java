package com.noya.microservices.order.service;

import com.noya.microservices.order.dto.OrderRequest;
import com.noya.microservices.order.dto.OrderResponse;
import com.noya.microservices.order.event.OrderPlacedEvent;
import com.noya.microservices.order.model.Order;
import com.noya.microservices.order.repo.OrderRepository;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    public OrderResponse placeOrder(OrderRequest orderRequest) {

        var isProductInStock = inventoryService.checkInventory(
                orderRequest.skuCode(),
                orderRequest.quantity()
        );

        if (isProductInStock) {
            Order order = Order.builder()
                    .orderNumber(UUID.randomUUID().toString())
                    .skuCode(orderRequest.skuCode())
                    .quantity(orderRequest.quantity())
                    .price(orderRequest.price())
                    .build();

            orderRepository.save(order);
            OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent();
            orderPlacedEvent.setOrderNumber(order.getOrderNumber());
            orderPlacedEvent.setEmail(orderRequest.userDetails().email());
            orderPlacedEvent.setFirsName(orderRequest.userDetails().firstname());
            orderPlacedEvent.setLastName(orderRequest.userDetails().lastname());
            log.info("Start - Sending OrderPlacedEvent {} to Kafka topic order-placed", orderPlacedEvent);
            kafkaTemplate.send("order-placed", orderPlacedEvent);
            log.info("End - Sending OrderPlacedEvent {} to Kafka topic order-placed", orderPlacedEvent);
            return new OrderResponse(
                    order.getId(),
                    order.getOrderNumber(),
                    order.getSkuCode(),
                    order.getQuantity(),
                    order.getPrice()
            );
        } else {
            throw new RuntimeException(
                    "Product with SKU code " + orderRequest.skuCode() + " is not in stock."
            );
        }
    }
}
