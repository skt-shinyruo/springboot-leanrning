# Technical Design: spring-boot-async-scheduling 加深（三）——任务边界（事务 / SecurityContext / RequestContext）与 Boot `spring.task.*` 自动装配

## Technical Solution

### Core Technologies

- Java 17
- Spring Boot `3.5.9`
- Spring Framework `6.2.x`（随 Boot 版本）
- `org.springframework:spring-tx`：事务边界/`TransactionSynchronizationManager` 观测点
- `org.springframework.security:spring-security-core`：`SecurityContextHolder` 与 Delegating* executor
- `org.springframework:spring-web`（test scope）：`RequestContextHolder` / `ServletRequestAttributes`（用于真实“请求上下文”示例）

### Implementation Key Points

1. **Evidence-first（Labs-first）**
   - 所有关键结论先落在 `*LabTest#method` 的断言里，docs 只引用“证据入口”
2. **稳定性优先（anti-flaky）**
   - 不使用长时间 `Thread.sleep`
   - 异步：`CompletableFuture#get(timeout)`
   - 上下文泄漏：使用单线程池（core=max=1）保证线程复用可控
3. **观察点一致性**
   - 线程名使用固定前缀（`async-` / `sched-` / `boot-`）
   - 事务活跃性使用 `TransactionSynchronizationManager.isActualTransactionActive()`
   - SecurityContext 使用 `SecurityContextHolder.getContext().getAuthentication()`
   - RequestContext 使用 `RequestContextHolder.getRequestAttributes()`
4. **Boot 自动装配以“实际 bean 与行为”为 SSOT**
   - 用 `ApplicationContextRunner` + `AutoConfigurations` 拉起最小 Boot 自动装配
   - 不背诵 bean 名字（可能随版本调整）；以“类型 + 行为”断言为主

## Architecture Decision ADR

### ADR-01: 事务演示采用 `ResourcelessTransactionManager`（不引入数据库）

**Context:** 本模块定位是“机制手册 + 可回归证据链”，不希望因为数据源/SQL/连接池把注意力从 `@Async/@Transactional` 的线程边界上拉走。

**Decision:** 使用 `ResourcelessTransactionManager` 与 `TransactionTemplate` / `@EnableTransactionManagement` / `@Transactional` 组合，证明事务“在哪个线程存在/不存在”。

**Rationale:**
- 足以表达“事务上下文属于线程”的核心边界
- 不引入 H2/JDBC，降低依赖与 flaky 风险

**Alternatives:** 引入 H2 + `DataSourceTransactionManager`
- Rejection reason: 会把一半篇幅变成数据源搭建与 SQL 细节，偏离本模块主线

**Impact:** 事务证据链聚焦“边界与拦截器顺序”，不覆盖真实数据库提交/回滚的可观察副作用。

### ADR-02: Security 只引入 `spring-security-core`，用 Delegating* executor 展示传播语义

**Context:** 我们需要一个真实的“上下文对象”，其语义明确、API 稳定、且与线程边界强相关；同时不希望引入完整 Web Security 过滤链。

**Decision:** 引入 `spring-security-core`，使用 `SecurityContextHolder` 与 `DelegatingSecurityContextAsyncTaskExecutor`（或等价 delegating wrapper）证明：
- 默认不传播
- wrapper 可传播且会清理，避免线程池泄漏

**Alternatives:** 引入 `spring-boot-starter-security`
- Rejection reason: 引入过滤链与认证配置成本过高，不符合本模块“非 web + 最小可复现”定位

**Impact:** 证明机制边界足够，且对真实工程具有直接迁移价值。

### ADR-03: RequestContext 采用 spring-web（test scope）+ mock request，避免引入 web server

**Context:** RequestContext 是常见 ThreadLocal 上下文，但该模块默认 `web-application-type=none`，不应启动 servlet 容器。

**Decision:** 仅在 tests 中引入 `spring-web`，使用 `RequestContextHolder` + `ServletRequestAttributes(MockHttpServletRequest)` 演示“默认不传播/可传播/泄漏风险”。

**Impact:** runnable demo 保持非 web；request 上下文的证据链完整保留在 Labs + docs。

## Security and Performance

- **Security:** 不处理真实凭证；SecurityContext 示例使用测试 token；避免输出敏感数据。
- **Performance:** 所有并发相关测试均为毫秒级；避免不确定性等待；线程池容量控制为最小复现。

## Testing and Deployment

- **Tests:** `mvn -q -pl :spring-boot-async-scheduling test`（至少连续 3 次）
- **Run demo:** `mvn -pl :spring-boot-async-scheduling spring-boot:run`
