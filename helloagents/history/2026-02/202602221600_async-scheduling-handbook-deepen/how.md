# Technical Design: spring-boot-async-scheduling 手册级内容加深（Async + Scheduling）

## Technical Solution

### Core Technologies

- Java 17 / Spring Boot 3.5.x / Spring Framework 6.x
- `@Async`（`AsyncAnnotationBeanPostProcessor` / `AsyncExecutionInterceptor`）
- `@Scheduled`（`ScheduledAnnotationBeanPostProcessor` / `ScheduledTaskHolder`）
- 测试：JUnit 5 + AssertJ + `ApplicationContextRunner`

### Implementation Key Points

1. **测试优先，文档引用证据入口**
   - 先把“关键结论”固化为可运行断言（LabTest），再在 docs 章节引用对应的 `*LabTest#method`。

2. **稳定观测点优先**
   - 以线程名前缀、注册任务类型、handler 收集结果作为稳定断言点，减少对真实时间触发与 race condition 的依赖。

3. **拆分测试集合，避免重复与不必要耗时**
   - `BookMatrix` 聚合主线最小集合。
   - `BranchMatrix` 聚合关键分支/坑位/异常语义/饱和拒绝等。

4. **示例代码尽量放到 main 源码侧，测试显式装配**
   - 示例类放在 `src/main/java/.../partXX`，便于文档跳转与断点。
   - 测试侧用 `ApplicationContextRunner.withUserConfiguration(...)` 明确启用条件（`@EnableAsync/@EnableScheduling`）与 executor/scheduler 配置，避免依赖扫描与外部环境。

## Security and Performance

- **Security:** 不涉及生产环境操作，不引入敏感信息，不新增危险命令。
- **Performance:** 新增测试全部保持短超时 + 轮询/等待工具；避免长 sleep 与无限等待。

## Testing and Deployment

- **Testing:**
  - `mvn -q -pl :spring-boot-async-scheduling test`
  - 关键入口：
    - `BootAsyncSchedulingBookMatrixLabTest`
    - `BootAsyncSchedulingBranchMatrixLabTest`
- **Deployment:** 本模块不发布服务（`spring.main.web-application-type=none`），无需部署步骤。
