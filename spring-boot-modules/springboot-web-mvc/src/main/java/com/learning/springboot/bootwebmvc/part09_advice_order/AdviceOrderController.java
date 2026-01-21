package com.learning.springboot.bootwebmvc.part09_advice_order;

// 本 controller 仅用于演示 @ControllerAdvice 的匹配与优先级（@Order）。

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/advanced/advice-order")
public class AdviceOrderController {

    @GetMapping(value = "/boom", produces = MediaType.APPLICATION_JSON_VALUE)
    public String boom() {
        throw new IllegalStateException("boom");
    }
}

