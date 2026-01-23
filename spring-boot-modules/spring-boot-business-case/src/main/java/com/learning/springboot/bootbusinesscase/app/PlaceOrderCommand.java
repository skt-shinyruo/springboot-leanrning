package com.learning.springboot.bootbusinesscase.app;

public record PlaceOrderCommand(String customer, String sku, int quantity) {
}

