package com.learning.springboot.bootwebmvc.part01_web_mvc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AtomicLong idSequence = new AtomicLong(0);
    private final Map<Long, UserResponse> store = new ConcurrentHashMap<>();

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable long id) {
        UserResponse user = store.get(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        long id = idSequence.incrementAndGet();
        UserResponse response = new UserResponse(id, request.getName(), request.getEmail());
        store.put(id, response);
        return response;
    }

    @PostMapping("/no-valid")
    public UserResponse createUserWithoutValid(@RequestBody CreateUserRequest request) {
        long id = idSequence.incrementAndGet();
        UserResponse response = new UserResponse(id, request.getName(), request.getEmail());
        store.put(id, response);
        return response;
    }
}
