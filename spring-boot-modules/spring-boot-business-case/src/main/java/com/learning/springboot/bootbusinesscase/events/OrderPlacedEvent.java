package com.learning.springboot.bootbusinesscase.events;

public record OrderPlacedEvent(Long orderId, String customer) {
}

