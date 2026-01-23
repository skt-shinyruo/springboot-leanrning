# Technical Design: solutions_perf_concurrency_batch01

## Technical Solution

### Core Technologies

- Java 17
- Spring Boot 3.5.9 / Spring Framework 6.2.15（由父 POM 管理）
- JUnit 5 / AssertJ
-（可选）Logback 测试能力：ListAppender（用于稳定断言 MDC，而不是依赖 console pattern）

### Implementation Key Points

1. **Solutions 的实现策略：测试内自包含（优先）**
   - 每个 `*ExerciseSolutionTest` 用独立的 `ApplicationContextRunner` 或 `AnnotationConfigApplicationContext` 或 `@SpringBootTest(classes=...)` 来搭建最小上下文。
   - 避免修改主线示例（`*LabTest` / `src/main`）造成行为漂移。

2. **并发/性能 Labs 的“可复现范式”**
   - 用 `CountDownLatch` 做开始/释放信号，确保“任务确实在跑且占住线程”。
   - 断言观察点优先级：异常类型（TaskRejectedException）> 线程名前缀 > 计数/可观测对象（MeterRegistry/Logback event）
   - 避免以耗时阈值做断言（性能波动会导致 flaky）；如果必须等待，使用短超时（1s）+ 确保可释放退出。

3. **专题沉淀：Book 级指南 + 模块内入口链接**
   - 新增 `docs/book/performance-and-concurrency.md` 作为“写法规范 + 样板索引”。
   - 在相关模块的 README 或 guide 中加入口链接，保证读者能快速找到“怎么写不 flaky 的并发实验”。

## Architecture Design

无新增运行时模块/服务；仅新增测试与文档。结构保持：

- 代码模块：`spring-boot-modules/`、`spring-core-modules/`
- 文档：`docs/`（由 `docs/SUMMARY.md` 作为站点导航 SSOT）
- 知识库：`helloagents/wiki/**`

## Security and Performance

- **Security:** 不引入外部服务调用、不写入明文密钥/Token；测试仅使用内存结构/本地上下文。
- **Performance:** 并发测试避免长时间 sleep；线程池任务必须可释放退出，避免卡死 surefire。

## Testing and Deployment

- **Testing（本滚动包）**
  - 新增 Solution/LabTest 后：
    - 运行 `python3 scripts/generate-book-labs-index.py` 更新 `docs/book/labs-index.md`
    - 运行全仓：`mvn -q test`
    - 文档门禁：`bash scripts/check-docs.sh`
    - 站点构建：`bash scripts/docs-site-build.sh`
- **Deployment:** 无部署动作（学习工程）

