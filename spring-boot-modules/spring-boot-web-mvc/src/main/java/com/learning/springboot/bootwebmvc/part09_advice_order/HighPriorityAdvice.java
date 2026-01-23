package com.learning.springboot.bootwebmvc.part09_advice_order;

import java.util.Map;

import com.learning.springboot.bootwebmvc.part01_web_mvc.ApiError;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.learning.springboot.bootwebmvc.part09_advice_order")
@Order(1)
public class HighPriorityAdvice {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("high_priority_advice", Map.of("source", "high")));
    }
}

