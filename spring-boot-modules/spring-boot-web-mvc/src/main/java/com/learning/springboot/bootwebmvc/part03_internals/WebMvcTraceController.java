package com.learning.springboot.bootwebmvc.part03_internals;

import java.util.Map;
import java.util.concurrent.Callable;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/advanced/trace")
public class WebMvcTraceController {

    @GetMapping(value = "/sync", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> sync(HttpServletRequest request) {
        WebMvcTraceSupport.record(request, "handler:sync[" + WebMvcTraceSupport.dispatch(request) + "]");
        return Map.of(
                "message", "ok",
                "events", WebMvcTraceSupport.snapshot(request)
        );
    }

    @GetMapping(value = "/async", produces = MediaType.APPLICATION_JSON_VALUE)
    public Callable<Map<String, Object>> async(HttpServletRequest request) {
        WebMvcTraceSupport.record(request, "handler:async[" + WebMvcTraceSupport.dispatch(request) + "]");
        return () -> Map.of(
                "message", "ok",
                "events", WebMvcTraceSupport.snapshot(request)
        );
    }
}

