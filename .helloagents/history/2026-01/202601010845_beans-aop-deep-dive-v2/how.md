# Technical Design: spring-core-beans + spring-core-aop 深挖增强 v2

## Technical Solution

### Core Technologies
- Java 17
- Spring Boot 3.5.x（本仓库当前为 3.5.9）
- Spring Framework 6.x（随 Spring Boot 版本）
- Spring AOP（proxy-based AOP）
- JUnit 5 + AssertJ（Labs 断言闭环）

### Implementation Key Points

#### 1) AOP 作为 BPP 的源码主线（可落断点）
- 文档以“容器阶段 → 调用阶段 → 匹配阶段”的三段主线组织。
- 明确 AOP 基础设施在容器里的身份：AutoProxyCreator = `BeanPostProcessor`。
- 给出“短路径断点”与“固定观察点（watch list）”，避免读者在源码里迷路。
- Labs 采用 `AnnotationConfigApplicationContext` + 最小配置，减少 Boot 自动配置噪音，方便逐步断点。

#### 2) advice 链执行细节（proceed 嵌套 + 顺序）
- 新增/增强 Labs：用“可断言的顺序日志”证明 proceed 的嵌套结构（外层/内层 before/after）。
- 文档明确：排序发生在“链条组装”阶段；执行发生在 `MethodInvocation#proceed` 阶段。
- 给出 `DefaultAdvisorChainFactory#getInterceptorsAndDynamicInterceptionAdvice` 与 `ReflectiveMethodInvocation#proceed` 的断点闭环。

#### 3) pointcut 表达式系统（避免误判）
- 文档覆盖常见 pointcut：`execution/within/this/target/args/@annotation/@within/@target/...`。
- Labs 重点落在最易误判的 this vs target（与 JDK/CGLIB 代理类型的关系）。
- 同时补齐“表达式写太宽/太窄”的调试策略：从最小切点逐步放大，并用断言固化边界。

#### 4) 多代理叠加与顺序（单代理 vs 多层代理）
- 文档明确两种形态：
  - 单 proxy + 多 advisor（同一个 proxy 内部的 interceptor chain）
  - 多层 proxy（nested proxy，通常来自不同 BPP 的包装或显式创建）
- Labs 覆盖“如何看”：`Advised#getAdvisors()`、`AopProxyUtils.ultimateTargetClass(...)`、逐层拆解 TargetSource。
- 输出“真实项目排障 checklist”：先确认 call path，再确认 pointcut，再确认代理限制/代理形态。

#### 5) beans 模块的容器视角承接
- 在 beans 的 BPP/代理章节显式引入 AutoProxyCreator 作为典型实现，补齐“注册 → 排序 → 介入点（pre/early/after-init）”的时间线承接。
- 强化 beans → aop 的跳转路径：读者在 beans 里理解“代理什么时候出现”，在 aop 里理解“链条怎么执行、为什么没命中”。

## Security and Performance

- **Security:**
  - 不连接任何外部服务/生产环境资源；不处理任何真实 PII。
  - 若新增依赖，限定在学习模块内，避免引入密钥/配置项；不写入敏感信息到日志。
- **Performance:**
  - 新增 Labs 采用最小上下文（优先 `AnnotationConfigApplicationContext`），控制启动与执行时间。
  - 断言基于稳定语义，减少对内部实现细节的强绑定，降低维护成本。

## Testing and Deployment

- 目标验证命令：
  - `mvn -pl spring-core-aop test`
  - `mvn -pl spring-core-beans test`
- 单测设计原则：
  - 每个 Lab 都能“方法级运行 + 断点闭环 + 可断言结论”
  - 避免依赖容器内部处理器的全序列（版本变动易碎），只断言关键相对顺序或可观察行为
