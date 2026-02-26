# Technical Design: spring-core-aop 真实多代理叠加（Tx/Cache/Security）+ AOP 内核深化 + 学习路线

## Technical Solution

### Core Technologies
- Java 17
- Spring Boot 3.5.x（依赖管理）
- Spring AOP（proxy-based）
- Spring Tx（`@Transactional` + `PlatformTransactionManager`）
- Spring Cache（`@Cacheable` + `CacheManager`）
- Spring Security Method Security（`@PreAuthorize`）
- JUnit 5 + AssertJ（可断言闭环）

### Implementation Key Points

#### 1) 真实叠加集成实验放在 `spring-core-aop` 内（你选择的 Solution 1）

目标：同一方法上同时叠加 `@Traced/@Transactional/@Cacheable/@PreAuthorize`，并且做到：

- 行为可断言（阻断/缓存短路/事务激活）
- 结构可观察（proxy/advisors/interceptor chain）
- 断点可复现（适合方法级运行）

实现策略：

- 使用 `AnnotationConfigApplicationContext` 构建最小上下文（避免 Boot 自动装配噪音）
- 显式开启：
  - `@EnableAspectJAutoProxy`
  - `@EnableTransactionManagement`
  - `@EnableCaching`
  - `@EnableMethodSecurity`（pre/post）
- 显式提供基础设施：
  - `PlatformTransactionManager`：优先 `ResourcelessTransactionManager`（避免引入真实 DB 依赖）
  - `CacheManager`：使用 `ConcurrentMapCacheManager`（稳定、无外部依赖）
  - `TracingAspect` + `InvocationLog`（现有 AOP 观察点继续复用）
- 认证上下文在测试内手工设置（`SecurityContextHolder`），避免引入 Web/MockMvc 依赖

#### 2) 链条观察工具化（AOP 内核继续加深）

新增一个测试侧工具（或内嵌在 Lab 中）：

- 输入：proxy bean + 目标方法
- 输出：该方法的 advisors 与组装后的 method-level interceptor chain（按执行顺序）

核心实现思路：

- 通过 `Advised#getAdvisors()` 获取 Advisor 列表
- 通过 `DefaultAdvisorChainFactory#getInterceptorsAndDynamicInterceptionAdvice`（或等价公开 API）组装链条
- 断言只聚焦关键类型（tx/cache/security）存在，不对内部全序列做强绑定

#### 3) 学习路线与断点清单（输出为文档）

新增一个“真实叠加 Debug Playbook”章节，内容结构：

- 3 段路线：
  1) 单 AOP（proxy/入口/自调用）
  2) 多 advisor（proceed 嵌套、pointcut 误判、Advised 观察）
  3) 真实叠加（Tx/Cache/Security/AOP）+ 分流排障 checklist
- 每段包含：
  - 推荐阅读章节（docs）
  - 推荐测试入口（类/方法）
  - 推荐断点与 watch list

## Architecture Decision ADR

### ADR-001: 集成实验放在 spring-core-aop，并使用最小 Spring 容器而非 Boot 自动装配

**Context:** 需要同时展示 Tx/Cache/Security/AOP 的真实叠加，但又要尽量避免“上下文噪音”破坏学习体验与断点可控性。

**Decision:** 在 `spring-core-aop` 内新增集成 Lab，并使用 `AnnotationConfigApplicationContext` 显式开启与显式提供基础设施（TxManager/CacheManager/SecurityContext）。

**Rationale:**
- 学习体验更可控：断点命中更少、调用链更短
- 可复现性更高：不依赖 Boot 的条件装配与环境差异
- 维护成本更低：断言聚焦行为与关键类型，不依赖内部全序列

**Alternatives:**
- Solution 2（新模块隔离）→ 拒绝原因：引入新模块改动面更大
- Solution 3（分散到 Tx/Cache/Security 模块）→ 拒绝原因：难以形成“同一 bean 真实叠加”的一体化闭环

**Impact:**
- `spring-core-aop` 将新增少量学习依赖（Tx/Security config）
- 新增 Labs 与 docs，不影响对外 API

## Security and Performance

- **Security:**
  - 不连接任何外部服务/生产环境资源；不处理真实 PII
  - Security 实验使用内存 Authentication，不引入明文密钥
  - 依赖变更记录在 changelog，并保持范围最小
- **Performance:**
  - 集成实验使用最小上下文，避免 `@SpringBootTest` 全量启动
  - 测试断言聚焦稳定语义，降低维护成本

## Testing and Deployment

- 验证命令：
  - `mvn -pl spring-core-aop test`
- 验证目标：
  - 新增集成 Lab 全通过
  - 现有 AOP Labs 不受依赖变更影响（仍能通过）
