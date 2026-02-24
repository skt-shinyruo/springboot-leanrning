package com.learning.springboot.bootasyncscheduling.part01_async_scheduling;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class VoidFailureAsyncService {

    @Async
    public void failsAsVoid(String arg) {
        throw new IllegalStateException("boom_void:" + arg);
    }
}
