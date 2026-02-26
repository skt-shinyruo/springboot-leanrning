# Technical Design: 全模块更深一层对标（V2 / Batch 01）

> Batch 01 目标模块：`springboot-security` / `springboot-data-jpa` / `spring-core-events` / `springboot-web-client`

## Technical Solution

### Core Technologies

- Java 17 / Maven 多模块
- Spring Boot Test / JUnit 5 / AssertJ
-（按需）Spring Security Test、Spring Data JPA、Hibernate（通过 Statistics 做可断言证据链）

### Implementation Key Points

#### 1) Guide 源码级深化（断点锚点 + call chain sketch + playbook）

在每个目标模块的 `docs/part-00-guide/00-deep-dive-guide.md` 内：

- 为每个“关键分支”补齐：
  - **call chain sketch**（建议 6–12 行，能复述即可）
  - **断点锚点**（class/method 级别；优先选择“最稳定入口”）
  - **对应默认 Lab/Test**（类名 + 方法名）
- 新增/强化 **Debug Playbook** 段落：
  - 按“现象 → 分支 → 断点 → 修复建议”写 5–8 条分流路径
  - 每条路径必须绑定：至少 1 个默认 Lab/Test + 1–3 个断点锚点

#### 2) 默认 Lab 增量（每模块新增 ≥1）

为每个目标模块新增 1 个默认 `*LabTest`，并控制为：

- **尽量单文件闭环**（优先用测试内 static class / 内部配置，避免新增 main 代码）
- **断言稳定**（避免 sleep、真实网络与真实时间依赖）

建议新增 Lab 方向（Batch 01）：

1) `springboot-security`
   - 主题：多 `SecurityFilterChain` 的 matcher 与 order 分流（“为什么 public 也被拦？”）
   - 断言信号：对不同 path 选择不同 chain（可通过 FilterChainProxy 选择结果/行为断言）
   - 断点锚点：`FilterChainProxy#doFilterInternal`、`DefaultSecurityFilterChain#matches`

2) `springboot-data-jpa`
   - 主题：merge/managed/detached 的对象语义差异（“为什么改了对象却没落库/为什么多了一次 select？”）
   - 断言信号：`EntityManager#contains`、对象引用是否同一、Hibernate Statistics 的 SQL/实体事件计数
   - 断点锚点：`SimpleJpaRepository#save`、Hibernate merge/persist 入口（以 `SimpleJpaRepository` 为稳定锚点）

3) `spring-core-events`
   - 主题：Generic/Smart listener 的 supportsEventType/sourceType 过滤分支（“为什么 listener 没触发？”）
   - 断言信号：发布不同 event/source 时触发与否（计数/日志 list）
   - 断点锚点：`SimpleApplicationEventMulticaster#multicastEvent`、`AbstractApplicationEventMulticaster#getApplicationListeners`

4) `springboot-web-client`
   - 主题：WebClient filter 链顺序与“必须调用 next.exchange”边界（“为什么请求没发出去/为什么没有按预期重试？”）
   - 断言信号：使用纯内存 `ExchangeFunction` stub 记录调用顺序（无真实网络）
   - 断点锚点：`DefaultWebClient#exchange`、`ExchangeFilterFunction#filter`

#### 3) 章节坑点“第二层对照”

在每个目标模块的每个章节 doc 中（`docs/part-01-*/` 与 `docs/appendix/99-self-check.md`）：

- 在已有坑点基础上新增 ≥1 条“对照坑点/边界”
- 每条新增坑点必须指向一个默认 Lab/Test 的方法名作为证据链

#### 4) 知识库同步（SSOT）

批次完成后同步更新：

- `helloagents/wiki/modules/<module>.md`：补齐本批次新增的“更深一层”要求、关键分支与默认 Lab 入口
- `helloagents/CHANGELOG.md`：记录本批次新增的默认 Lab 与深挖升级范围

## Security and Performance

- **Security:** 不引入生产环境连接；不写入任何 token/密钥到文档与日志；文档示例使用占位字符串；避免在测试日志输出敏感信息（例如 Authorization header 全量）。
- **Performance:** 新增测试避免长时间等待；异步类测试使用固定超时上限与 CountDownLatch；WebClient 测试使用内存 stub 避免外部 IO。

## Testing and Deployment

- **模块级回归（批次内）：**
  - `mvn -pl springboot-security test`
  - `mvn -pl springboot-data-jpa test`
  - `mvn -pl spring-core-events test`
  - `mvn -pl springboot-web-client test`
- **全仓库回归：** `mvn test`

