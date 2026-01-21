package com.learning.springboot.bootwebmvc.part04_contract;

import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/advanced/contract")
public class RestContractController {

    @PostMapping(
            value = "/echo",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ContractEchoResponse echo(@Valid @RequestBody ContractEchoRequest request) {
        return new ContractEchoResponse(request.message(), request.createdAt());
    }

    @PostMapping(
            value = "/strict-echo",
            consumes = StrictJsonMessageConverterConfig.STRICT_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ContractEchoResponse strictEcho(@Valid @RequestBody ContractEchoRequest request) {
        return new ContractEchoResponse(request.message(), request.createdAt());
    }

    @GetMapping(value = "/ping", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> ping() {
        return Map.of("message", "pong");
    }
}

