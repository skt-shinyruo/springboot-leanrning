package com.learning.springboot.bootcache.part01_cache;

import java.time.Duration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.annotation.EnableCaching;

@Configuration
@EnableCaching
class CacheConfig {

    @Bean
    @ConditionalOnMissingBean(Ticker.class)
    Ticker ticker() {
        return Ticker.systemTicker();
    }

    @Bean
    CacheManager cacheManager(Ticker ticker) {
        CaffeineCacheManager manager = new CaffeineCacheManager(
                "greetings",
                "conditionalGreetings",
                "unlessGreetings",
                "syncGreetings"
        );

        manager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(5))
                .ticker(ticker)
                .recordStats());

        return manager;
    }
}
