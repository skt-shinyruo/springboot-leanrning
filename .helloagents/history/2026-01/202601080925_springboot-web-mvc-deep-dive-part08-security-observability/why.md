# Change Proposal: springboot-web-mvc 深入扩展 II（知识地图 + Security/Observability + 排障闭环）

## Requirement Background

- 当前 `springboot-web-mvc` 已具备一套可跑的 Web MVC 学习闭环：Part 01–02（主线/页面渲染）+ Part 03–07（机制内核/契约与 Jackson/真实 HTTP/Async/SSE/排障）与对应 Labs。
- 但在“更全面更深入”的目标下，仍有两类缺口：
  1) **知识地图与关键机制解释不足**：多个知识点分散在章节里，缺少“一张图”把请求链路关键阶段、关键分支、可观察证据（测试/断点）串起来。
  2) **真实工程视角（Security 与观测/性能）与排障闭环需要加强**：Filter/Interceptor/异常塑形/内容协商在接入 Security、Actuator/Micrometer 后常见分支与坑点需要可复现实验固化（401/403/CSRF、metrics、请求耗时观测、404/406/415/400 的定位套路等）。
- 目标：在不破坏现有主线与接口形状（`ApiError` 等）的前提下，以“增量 + 可验证（docs + tests）”方式补齐更系统的知识点与真实场景闭环。

## Change Content

1. 新增 **知识地图（Mindmap/索引）**：用“请求进入 → handler 选路 → 入参解析/绑定/校验 → 异常解析 → 返回值处理/序列化 → 响应写回”的主轴串联章节与可跑入口。
2. 对现有章节做“深入补齐”：补充关键分支解释与可复现实验入口（尤其是当前存在“坑点待补齐/兜底入口”的章节）。
3. 新增 **Part 08 - Security & Observability（安全与观测/性能）**：
   - Security：FilterChainProxy 与 MVC 链路的关系、401/403/CSRF、与统一错误塑形并存策略。
   - Observability：请求耗时观测、HTTP 指标（`http.server.requests`）、Actuator 端点的最小可用暴露策略与测试验证。
4. 扩展“常见坑 + 排障清单 + 对应可复现测试用例”，形成“遇到问题先写测试固化现象 → 看 resolvedException/handler → 再打断点”的可执行套路。
5. 自检与验收：
   - `mvn -pl springboot-web-mvc test` 全绿

## Impact Scope

- **Modules:** `springboot-web-mvc`
- **Files:** `docs/web-mvc/springboot-web-mvc/**`、`springboot-web-mvc/src/main/**`、`springboot-web-mvc/src/test/**`、`helloagents/wiki/modules/springboot-web-mvc.md`、`helloagents/CHANGELOG.md`
- **Dependencies:** 可能新增 `spring-boot-starter-security` / `spring-boot-starter-actuator`（以“最小接入、兼容既有行为”为原则）
- **APIs:** 仅新增教学用端点（建议使用独立前缀，例如 `/api/advanced/**`），不破坏既有接口与页面路由
- **Data:** 无持久化数据（示例以最小内存结构或纯演示为主）

## Core Scenarios

### Requirement: Web MVC 知识地图 + 调用链可验证
**Module:** springboot-web-mvc
把“知识点”升级为“可解释 + 可验证”的心智模型：每个关键阶段都有“证据”（测试断言/断点观察点）。

#### Scenario: 从测试反推调用链与关键分支
- 能从一次请求定位到：HandlerMapping/HandlerAdapter → ArgumentResolver/Binder/Validator → MessageConverter/ReturnValueHandler → ExceptionResolver
- 能用 MockMvc / SpringBootTest 给出稳定证据（状态码、响应形状、`resolvedException`、关键 header）

### Requirement: 绑定/转换/校验更深入（含 @InitBinder / @ModelAttribute vs @RequestBody）
**Module:** springboot-web-mvc
把“入参为什么会变成 400/校验失败/类型不匹配”讲清楚，并用可复现实验锁住边界。

#### Scenario: 绑定失败/校验失败/类型不匹配可被测试区分
- 能区分：JSON 解析失败 vs binding 失败 vs validation 失败
- 能通过测试固定：400 的不同分支与错误塑形策略

### Requirement: Content Negotiation / HttpMessageConverter 进阶边界
**Module:** springboot-web-mvc
覆盖 `Accept`/`Content-Type`/`produces`/`consumes`、converter 选择与 406/415 的关键分支，并能快速定位。

#### Scenario: 406/415/strict media type 分支可被测试锁住
- 能通过 `resolvedException` 证明分支入口
- 能解释 converter 顺序与 media type 匹配规则

### Requirement: 异常体系升级（ApiError + ProblemDetail 并存示例）
**Module:** springboot-web-mvc
在保留现有 `ApiError` 教学主线的同时，引入 `ProblemDetail` 的对照示例，说明各自适用边界与落地方式。

#### Scenario: 同一类错误的两种响应风格可对照验证
- `ApiError` 作为“强约束统一契约”
- `ProblemDetail` 作为“与框架/标准对齐的语义载体”

### Requirement: 缓存与 ETag（静态资源 + API 响应）
**Module:** springboot-web-mvc
补齐“304/缓存命中”的可复现实验：不仅解释 header，还能跑出行为。

#### Scenario: ETag/If-None-Match 触发 304 可被测试断言
- 静态资源链路（已有基础）+ API 响应链路（新增最小示例）

### Requirement: Security 集成（FilterChainProxy + MVC + 401/403/CSRF）
**Module:** springboot-web-mvc
把“加了 spring-security 后请求链路发生了什么变化”讲清楚，并提供最小可跑闭环。

#### Scenario: 401/403/CSRF 的典型分支可复现并可定位
- 未登录访问受保护资源 → 401
- 登录但权限不足 → 403
- POST 缺失 CSRF → 403（并解释何时应该关/开）

### Requirement: Observability/性能观测（Interceptor 计时 + Actuator metrics）
**Module:** springboot-web-mvc
补齐“如何观察请求耗时与请求指标”的最小工程实践：能看见、能验证、能解释。

#### Scenario: 指标可被验证并能解释观测点位置
- 能验证 `http.server.requests`（或等价指标）存在/可读取
- 能对照 Interceptor 计时与指标视角，解释观测点差异

### Requirement: 排障清单可复现（常见坑 + 断点 + 失败场景测试）
**Module:** springboot-web-mvc
把排障从“经验”变成“流程”：每类错误都有“先看什么、再看什么、断点打哪里”。

#### Scenario: 常见失败场景都有测试与定位手册
- 400/401/403/404/406/415/500 至少各 1 个“可复现 + 可定位”入口
- 文档与测试互相引用，形成闭环

## Risk Assessment

- **Risk:** 引入 Security/Actuator 可能改变默认行为、影响既有测试或暴露端点  
  **Mitigation:** 只保护新端点前缀（如 `/api/advanced/secure/**`），既有路径默认 `permitAll`；Actuator 只暴露最小端点集合并在文档中强调“示例≠生产默认”。
- **Risk:** Async/SSE/观测相关测试容易 flaky  
  **Mitigation:** SSE 使用“有限事件 + 快速完成”策略；async 使用 `asyncDispatch`；避免依赖时间窗口与线程竞态。

