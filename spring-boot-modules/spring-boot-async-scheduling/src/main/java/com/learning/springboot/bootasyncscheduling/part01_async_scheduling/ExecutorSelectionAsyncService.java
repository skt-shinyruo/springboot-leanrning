package com.learning.springboot.bootasyncscheduling.part01_async_scheduling;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ExecutorSelectionAsyncService {

    @Async
    public CompletableFuture<String> defaultExecutorThreadName() {
        return CompletableFuture.completedFuture(Thread.currentThread().getName());
    }

    @Async("specialExecutor")
    public CompletableFuture<String> specialExecutorThreadName() {
        return CompletableFuture.completedFuture(Thread.currentThread().getName());
    }
}
