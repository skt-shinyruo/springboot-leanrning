# Technical Design: solutions_all_remaining_modules（剩余模块 Solutions 全量补齐）

## Technical Solution

### Core Technologies

- Java 17
- JUnit Jupiter（JUnit 5）
- Spring Boot Test / Spring TestContext Framework
- AssertJ
- 按模块使用既有测试工具（优先不新增依赖）：
  - Web MVC：MockMvc / `@WebMvcTest`（如合适）
  - WebClient：优先使用 `WebClient` 的可替换 `ExchangeFunction` 或 Spring Test 能力（避免真实网络）
  - Data JPA：H2（已有依赖时）或测试内存库
  - Security：MockMvc + Security Test（若已引入）
  - Actuator：`TestRestTemplate` / `WebTestClient`（RANDOM_PORT）读取 endpoint/metrics（只在本地进程内）

### Implementation Key Points

1. **每个模块 1 个 `*ExerciseSolutionTest`**
   - 与对应 `*ExerciseTest` 同包名（通常 `part00_guide`），文件名保持一致的 `...ExerciseSolutionTest` 后缀。
2. **Solutions 默认参与回归，但不破坏教学体验**
   - 不修改原 `*ExerciseTest`（保持 `@Disabled`）。
   - Solutions 测试使用独立 `@TestConfiguration` 或独立 `@SpringBootTest(classes=...)` 组合，避免影响既有 Labs。
3. **稳定性优先（不 flaky）**
   - 不用 “sleep + 耗时阈值” 来断言。
   - 对异步/并发：使用 latch/屏障/线程名前缀/明确异常路径作为可观测事实。
   - 对 Web/HTTP：只使用本地随机端口（RANDOM_PORT）或 mock server，不访问外网。
4. **最小变更面**
   - 优先只新增测试文件，不改 main 代码。
   - 如 Exercise 要求必须新增少量示例类/配置，则严格控制在模块内，且避免影响既有 Labs。
5. **Docs 同步（Solutions 可发现性）**
   - 更新 `docs/book/exercises-and-solutions.md`：补齐 Solutions 的命名约定、运行方式与“Exercise ↔ Solution”的对照入口。
   - 更新各模块 `docs/<topic>/<module>/README.md`：增加 Solution 的可跑入口命令，并与 Book 工具页互链。
6. **并发/性能可复现实验（全量推广）**
   - 每个模块新增至少 1 个并发/性能样板 Lab（建议放入 `part02_perf_concurrency`），并保持“可断言、可复现、不 flaky”。
   - 断言优先级：异常路径（拒绝/超时） > 线程边界（线程名前缀/切换） > 指标/日志（MeterRegistry/ListAppender） > 耗时（禁止用耗时阈值做主断言）。

## Security and Performance

- **Security:**
  - 不接入生产环境；不访问外网；不引入明文密钥/Token；不添加破坏性脚本命令。
  - WebClient/HTTP 场景使用 mock 或本地端口，避免真实网络依赖。
- **Performance:**
  - Solutions 测试保证可回归与可复现，不追求 micro-benchmark 跑分。
  - 并发相关断言以确定性为先，避免对 CI 环境敏感。
  - 并发/性能专题的目标是“机制边界证据链”，不是跑分；如需真实性能数据，建议独立引入 JMH（不进入默认单测回归）。

## Testing and Deployment

- **Testing:**
  - 逐模块验证（优先）：`mvn -q -pl :<artifactId> test`
  - 全仓回归（必须）：`mvn -q test`
- **Deployment:** N/A（教学仓库，不涉及部署发布流程）
