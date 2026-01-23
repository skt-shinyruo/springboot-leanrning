package com.learning.springboot.bootobservability.api;

import java.time.Duration;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @GetMapping("/api/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("/api/sleep")
    public String sleep() throws InterruptedException {
        Thread.sleep(Duration.ofMillis(50).toMillis());
        return "ok";
    }
}
