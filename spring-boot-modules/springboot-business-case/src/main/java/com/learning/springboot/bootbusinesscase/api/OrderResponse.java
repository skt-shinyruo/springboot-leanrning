package com.learning.springboot.bootbusinesscase.api;

import com.learning.springboot.bootbusinesscase.domain.PurchaseOrder;

public class OrderResponse {

    private final Long id;
    private final String customer;
    private final String sku;
    private final int quantity;
    private final String status;

    public OrderResponse(Long id, String customer, String sku, int quantity, String status) {
        this.id = id;
        this.customer = customer;
        this.sku = sku;
        this.quantity = quantity;
        this.status = status;
    }

    public static OrderResponse from(PurchaseOrder order) {
        return new OrderResponse(order.getId(), order.getCustomer(), order.getSku(), order.getQuantity(), order.getStatus().name());
    }

    public Long getId() {
        return id;
    }

    public String getCustomer() {
        return customer;
    }

    public String getSku() {
        return sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }
}

