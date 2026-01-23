package com.learning.springboot.bootwebmvc.part03_internals;

import java.util.Map;

import com.learning.springboot.bootwebmvc.part04_contract.StrictJsonMessageConverterConfig;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/advanced/message-converters")
public class MessageConverterTraceController {

    @GetMapping(value = "/string", produces = MediaType.TEXT_PLAIN_VALUE)
    public String stringBody() {
        return "hello";
    }

    @GetMapping(value = "/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> jsonBody() {
        return Map.of("message", "hello");
    }

    @GetMapping(value = "/bytes", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] bytesBody() {
        return new byte[] {1, 2, 3};
    }

    @GetMapping(value = "/strict-json", produces = StrictJsonMessageConverterConfig.STRICT_JSON_VALUE)
    public Map<String, Object> strictJsonBody() {
        return Map.of("message", "hello");
    }
}

