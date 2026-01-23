package com.learning.springboot.bootasyncscheduling.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class BootAsyncSchedulingExerciseTest {

    @Test
    @Disabled("练习：自定义一个 @Async executor（不同线程名 prefix），并用测试证明它被使用了")
    void exercise_customExecutor() {
        assertThat(true)
                .as("""
                        练习：自定义一个 @Async executor（不同线程名 prefix），并用测试证明它被使用了。

                        提示：
                        - 提供一个 `TaskExecutor` bean（例如 ThreadPoolTaskExecutor）。
                        - 通过断言 threadName 前缀证明：调用确实跑在你提供的 executor 上。
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：增加一个 @Async 方法返回值为 ListenableFuture/CompletableFuture 的对比实验")
    void exercise_futureTypes() {
        assertThat(true)
                .as("""
                        练习：增加一个 @Async 方法返回值为 ListenableFuture/CompletableFuture 的对比实验。

                        目标：
                        - 你能解释：不同 future 类型的 API 与异常传播差异。
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：构造一个“线程池饱和”的场景（queue 满/拒绝策略），并用断言固化现象")
    void exercise_executorSaturation() {
        assertThat(true)
                .as("""
                        练习：构造一个“线程池饱和”的场景（queue 满/拒绝策略），并用断言固化现象。

                        提示：
                        - 使用很小的 poolSize/queueCapacity；
                        - 提交多次 async 任务，观察是否抛出拒绝异常。
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：把 self-invocation 的坑写成一个更直观的 Web/Service 链路（可选）")
    void exercise_selfInvocationInChain() {
        assertThat(true)
                .as("""
                        练习：把 self-invocation 的坑写成一个更直观的链路（可选）。

                        示例：
                        - Controller/Runner 调用 outer()
                        - outer() 内部 this.asyncMethod()
                        - 通过断言证明并没有异步切线程
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：增加一个可测试的 @Scheduled(fixedRate=...) 与 fixedDelay 的差异实验")
    void exercise_fixedRateVsFixedDelay() {
        assertThat(true)
                .as("""
                        练习：增加一个可测试的 @Scheduled(fixedRate=...) 与 fixedDelay 的差异实验。

                        提示：
                        - 可以在任务里引入可控的“执行耗时”（但避免 Thread.sleep 过长）；
                        - 用计数/时间窗口去做稳定断言。
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：增加一个 cron 表达式示例，并用测试证明它被解析/注册了")
    void exercise_cronBasics() {
        assertThat(true)
                .as("""
                        练习：增加一个 cron 表达式示例，并用测试证明它被解析/注册了。

                        提示：
                        - 你可以从 ScheduledTaskHolder 或 ScheduledAnnotationBeanPostProcessor 的注册结果入手做断言。
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：为 void @Async 异常增加自定义 AsyncUncaughtExceptionHandler，并记录 method + 参数")
    void exercise_uncaughtHandlerDetails() {
        assertThat(true)
                .as("""
                        练习：为 void @Async 异常增加自定义 AsyncUncaughtExceptionHandler，并记录 method + 参数。

                        目标：
                        - 你能解释：为什么 void 异常不会传回调用方，以及异常最终去哪了。
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：写一个“稳定的等待工具”替代 Thread.sleep（例如轮询 + 超时），用于异步/调度测试")
    void exercise_waitingUtility() {
        assertThat(true)
                .as("""
                        练习：写一个“稳定的等待工具”替代 Thread.sleep（例如轮询 + 超时），用于异步/调度测试。

                        目标：
                        - 让你能写出不 flaky 的 async/scheduling tests。
                        """)
                .isFalse();
    }
}

