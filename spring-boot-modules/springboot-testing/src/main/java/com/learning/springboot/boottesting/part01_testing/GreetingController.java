package com.learning.springboot.boottesting.part01_testing;

import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    private final GreetingService greetingService;

    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GetMapping("/api/greeting")
    public Map<String, String> greeting(@RequestParam(defaultValue = "World") String name) {
        return Map.of("message", greetingService.greet(name));
    }

    @PostMapping("/api/echo")
    public Map<String, String> echo(@Valid @RequestBody EchoRequest request) {
        return Map.of("message", request.message());
    }
}
