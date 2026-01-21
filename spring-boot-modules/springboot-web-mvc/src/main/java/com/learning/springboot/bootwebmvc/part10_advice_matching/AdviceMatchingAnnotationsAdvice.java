package com.learning.springboot.bootwebmvc.part10_advice_matching;

import java.util.Map;

import com.learning.springboot.bootwebmvc.part01_web_mvc.ApiError;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(
        annotations = AdviceMatchingTagged.class
)
@Order(2)
public class AdviceMatchingAnnotationsAdvice {

    @ExceptionHandler(AdviceMatchingDemoException.class)
    public ResponseEntity<ApiError> handle(AdviceMatchingDemoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("advice_annotations", Map.of("selector", "annotations")));
    }
}
