package com.learning.springboot.bootasyncscheduling.part01_async_scheduling;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

@Service
class AsyncCallerService {

    private final AsyncDemoService asyncDemoService;

    AsyncCallerService(AsyncDemoService asyncDemoService) {
        this.asyncDemoService = asyncDemoService;
    }

    CompletableFuture<String> callsAsyncThroughProxy() {
        return asyncDemoService.currentThreadName();
    }
}

