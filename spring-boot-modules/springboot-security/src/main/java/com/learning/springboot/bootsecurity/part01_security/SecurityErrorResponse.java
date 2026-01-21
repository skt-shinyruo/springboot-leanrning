package com.learning.springboot.bootsecurity.part01_security;

public record SecurityErrorResponse(String message, int status, String path) {}

