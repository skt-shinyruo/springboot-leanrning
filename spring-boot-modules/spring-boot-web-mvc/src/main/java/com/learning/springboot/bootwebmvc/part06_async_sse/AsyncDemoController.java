package com.learning.springboot.bootwebmvc.part06_async_sse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/api/advanced/async")
public class AsyncDemoController {

    @GetMapping(value = "/ping", produces = MediaType.APPLICATION_JSON_VALUE)
    public Callable<Map<String, Object>> ping() {
        return () -> Map.of(
                "message", "pong",
                "thread", Thread.currentThread().getName()
        );
    }

    @GetMapping(value = "/deferred", produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<Map<String, Object>> deferred() {
        DeferredResult<Map<String, Object>> result = new DeferredResult<>(1000L);

        CompletableFuture.runAsync(() -> {
            try {
                // 轻微延迟，确保测试中能稳定触发 asyncStarted → asyncDispatch 的链路。
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
            }
            result.setResult(Map.of(
                    "message", "pong",
                    "thread", Thread.currentThread().getName()
            ));
        });

        return result;
    }

    @GetMapping(value = "/deferred-timeout", produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<Map<String, Object>> deferredTimeout() {
        // 教学用：在 MockMvc 中不依赖 Servlet 容器的 timeout 事件，而是用可控的延迟回退，保证测试稳定。
        DeferredResult<Map<String, Object>> result = new DeferredResult<>(1000L);

        CompletableFuture.delayedExecutor(50, TimeUnit.MILLISECONDS)
                .execute(() -> result.setResult(Map.of("message", "timeout")));

        return result;
    }
}
