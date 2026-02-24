# Technical Design: spring-boot-async-scheduling 加深（二）：线程上下文传播（ThreadLocal/MDC）证据链

## Technical Solution

### Core Technologies

- Spring `@Async` + `ThreadPoolTaskExecutor`
- `org.springframework.core.task.TaskDecorator`
- 测试：JUnit 5 + AssertJ + `ApplicationContextRunner`

### Implementation Key Points

1. **用 ThreadLocal 作为最小模型**
   - MDC / SecurityContext 等都可视为 ThreadLocal 的具体实现或封装，先用最小模型建立可复现证据链。

2. **TaskDecorator 的“复制 + 恢复”必须成对**
   - 只复制不恢复 = 线程池复用时泄漏上下文（高危）。
   - 实现上在 decorator 内捕获调用方上下文，在执行线程设置，并在 finally 恢复旧值或 clear。

3. **断言同时覆盖“切线程”与“上下文值”**
   - 仅断言上下文值不足以证明线程切换；仅断言线程切换又不足以证明上下文传播正确。

## Security and Performance

- **Security:** 不涉及生产环境，不引入敏感信息。
- **Performance:** 使用单线程 executor + 短 timeout，避免测试慢与 flaky。

## Testing and Deployment

- `mvn -q -pl :spring-boot-async-scheduling test`（连续跑多次确认稳定）

