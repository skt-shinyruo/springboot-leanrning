package com.learning.springboot.springcoreevents.part00_guide;

import com.learning.springboot.springcoreevents.part01_event_basics.InMemoryAuditLog;
import com.learning.springboot.springcoreevents.part01_event_basics.UserRegistrationService;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringCoreEventsExerciseTest {

    @Autowired
    private UserRegistrationService userRegistrationService;

    @Autowired
    private InMemoryAuditLog auditLog;

    @Test
    @Disabled("练习：新增第二个 listener 写入不同的 entry，并断言两条 entry 都存在")
    void exercise_multipleListeners() {
        auditLog.clear();

        userRegistrationService.register("Alice");

        List<String> entries = auditLog.entries();
        assertThat(entries)
                .as("""
                        练习：新增第二个 listener，并验证多个监听器都能收到同一事件。

                        下一步：
                        1) 新建一个 `@EventListener`（监听 `UserRegisteredEvent`）。
                        2) 在新 listener 里写入一条不同的 auditLog entry。
                        3) 让本测试断言两条 entry 都存在（可自由调整 entry 的文本）。

                        建议阅读：
                        - `docs/events/spring-core-events/part-01-event-basics/02-multiple-listeners-and-order.md`
                        """)
                .contains("userRegistered:Alice");
        assertThat(entries)
                .as("""
                        建议的第二条 entry 文本：`userRegisteredSecondary:Alice`（你也可以选择自己的命名，但要同步更新断言）。
                        """)
                .contains("userRegisteredSecondary:Alice");
    }

    @Test
    @Disabled("练习：给 listeners 加 @Order，并写一个确定性的顺序断言")
    void exercise_ordering() {
        assertThat(true)
                .as("""
                        练习：给 listeners 加 @Order，并写一个确定性的顺序断言。

                        下一步：
                        1) 给两个 listener 分别加 `@Order`（例如 1 和 2）。
                        2) 用一个“可断言的观察点”记录顺序（例如把 entry 按执行顺序写入 list）。
                        3) 断言 list 的顺序与你的 @Order 一致。

                        常见坑：
                        - 默认顺序不保证；如果没有 @Order，很容易学到错误结论
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：创建一个 @Async listener，并证明它跑在不同线程")
    void exercise_asyncListener() {
        assertThat(true)
                .as("""
                        练习：创建一个 @Async listener，并证明它跑在不同线程。

                        下一步：
                        1) 新建一个 `@Async` 的 listener。
                        2) 启用 `@EnableAsync`（否则 @Async 可能被忽略）。
                        3) 用稳定的方式断言异步：记录线程名 + CountDownLatch 等待，而不是靠日志时序。

                        建议阅读：
                        - `docs/events/spring-core-events/part-02-async-and-transactional/05-async-listener.md`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：让事件默认异步分发（自定义 multicaster），并更新测试")
    void exercise_asyncMulticaster() {
        assertThat(true)
                .as("""
                        练习：让事件默认异步分发（自定义 multicaster），并更新测试。

                        下一步：
                        1) 提供一个自定义 `ApplicationEventMulticaster`。
                        2) 配置它使用 task executor。
                        3) 用测试证明：即使 listener 没标 @Async，也会异步执行。

                        建议阅读：
                        - `docs/events/spring-core-events/part-02-async-and-transactional/06-async-multicaster.md`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：增加一个 condition listener（SpEL），并证明它只在匹配时触发")
    void exercise_conditionalListener() {
        assertThat(true)
                .as("""
                        练习：增加一个 condition listener（SpEL），并证明它只在匹配时触发。

                        下一步：
                        1) 写一个 `@EventListener(condition = \"...\")`。
                        2) 发布两次事件：一次满足条件，一次不满足。
                        3) 用断言证明：只有满足条件时才会写入 auditLog。

                        建议阅读：
                        - `docs/events/spring-core-events/part-01-event-basics/03-condition-and-payload.md`
                        """)
                .isFalse();
    }
}
