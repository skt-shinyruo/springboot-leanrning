package com.learning.springboot.bootbusinesscase.api;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learning.springboot.bootbusinesscase.app.OrderService;
import com.learning.springboot.bootbusinesscase.app.PlaceOrderCommand;
import com.learning.springboot.bootbusinesscase.domain.PurchaseOrder;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        PurchaseOrder order = orderService.placeOrder(new PlaceOrderCommand(request.getCustomer(), request.getSku(), request.getQuantity()));
        return OrderResponse.from(order);
    }

    @PostMapping("/fail")
    public OrderResponse createThenFail(@Valid @RequestBody CreateOrderRequest request) {
        PurchaseOrder order = orderService.placeOrderThenFail(new PlaceOrderCommand(request.getCustomer(), request.getSku(), request.getQuantity()));
        return OrderResponse.from(order);
    }
}

