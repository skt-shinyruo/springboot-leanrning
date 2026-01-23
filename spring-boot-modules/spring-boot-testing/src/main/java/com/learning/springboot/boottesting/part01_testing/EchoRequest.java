package com.learning.springboot.boottesting.part01_testing;

import jakarta.validation.constraints.NotBlank;

public record EchoRequest(@NotBlank(message = "message 不能为空") String message) {}

