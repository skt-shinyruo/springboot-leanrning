package com.learning.springboot.bootbusinesscase.api;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learning.springboot.bootbusinesscase.app.CheckedOrderException;
import com.learning.springboot.bootbusinesscase.app.OrderService;
import com.learning.springboot.bootbusinesscase.app.PlaceOrderCommand;
import com.learning.springboot.bootbusinesscase.domain.PurchaseOrder;
import com.learning.springboot.bootbusinesscase.domain.PurchaseOrderRepository;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final PurchaseOrderRepository repository;

    public OrderController(OrderService orderService, PurchaseOrderRepository repository) {
        this.orderService = orderService;
        this.repository = repository;
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

    @PostMapping("/fail-checked-default")
    public OrderResponse createThenFailCheckedDefault(@Valid @RequestBody CreateOrderRequest request) throws CheckedOrderException {
        PurchaseOrder order = orderService.placeOrderThenFailChecked_defaultRule(
                new PlaceOrderCommand(request.getCustomer(), request.getSku(), request.getQuantity()));
        return OrderResponse.from(order);
    }

    @PostMapping("/fail-checked-rollback")
    public OrderResponse createThenFailCheckedRollback(@Valid @RequestBody CreateOrderRequest request) throws CheckedOrderException {
        PurchaseOrder order = orderService.placeOrderThenFailChecked_rollbackFor(
                new PlaceOrderCommand(request.getCustomer(), request.getSku(), request.getQuantity()));
        return OrderResponse.from(order);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable("id") long id) {
        return repository.findById(id)
                .map(OrderResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<OrderResponse> list() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(OrderResponse::from)
                .toList();
    }
}
