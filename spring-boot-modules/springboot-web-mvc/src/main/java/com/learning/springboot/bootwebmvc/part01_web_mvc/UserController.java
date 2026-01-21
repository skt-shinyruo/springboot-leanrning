package com.learning.springboot.bootwebmvc.part01_web_mvc;

import java.util.concurrent.atomic.AtomicLong;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AtomicLong idSequence = new AtomicLong(0);

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        long id = idSequence.incrementAndGet();
        return new UserResponse(id, request.getName(), request.getEmail());
    }

    @PostMapping("/no-valid")
    public UserResponse createUserWithoutValid(@RequestBody CreateUserRequest request) {
        long id = idSequence.incrementAndGet();
        return new UserResponse(id, request.getName(), request.getEmail());
    }
}
