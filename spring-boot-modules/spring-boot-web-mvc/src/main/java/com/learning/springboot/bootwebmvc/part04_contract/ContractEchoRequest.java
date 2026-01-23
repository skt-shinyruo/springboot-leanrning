package com.learning.springboot.bootwebmvc.part04_contract;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContractEchoRequest(
        @NotBlank String message,
        @NotNull Instant createdAt
) {
}

