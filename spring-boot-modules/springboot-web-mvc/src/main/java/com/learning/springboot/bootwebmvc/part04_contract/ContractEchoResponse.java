package com.learning.springboot.bootwebmvc.part04_contract;

import java.time.Instant;

public record ContractEchoResponse(
        String message,
        Instant createdAt
) {
}

