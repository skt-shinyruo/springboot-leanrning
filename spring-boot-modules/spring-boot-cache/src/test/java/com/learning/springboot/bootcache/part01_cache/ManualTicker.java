package com.learning.springboot.bootcache.part01_cache;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import com.github.benmanes.caffeine.cache.Ticker;

final class ManualTicker implements Ticker {

    private final AtomicLong nanos = new AtomicLong();

    @Override
    public long read() {
        return nanos.get();
    }

    void advance(Duration duration) {
        nanos.addAndGet(duration.toNanos());
    }
}

