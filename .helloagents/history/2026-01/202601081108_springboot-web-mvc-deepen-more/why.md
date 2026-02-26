# Change Proposal: springboot-web-mvc deepen more（链路内核/条件请求/异步/排障闭环再深化）

## Requirement Background

当前 `springboot-web-mvc` 模块已经具备：

- docs Part 00–08 的主线结构（REST + View + Internals + Contract + Real World HTTP + Async/SSE + Testing/Debugging + Security/Observability）
- 多组可运行 Labs（MockMvc slice + SpringBootTest 端到端）用于固定 406/415、绑定/校验、CORS、上传下载、ETag、Async/SSE、Security、metrics 等关键分支

但在“更深一层”的教学目标下，还存在以下缺口：

1. **请求链路内核的可解释性仍偏“点状”**：已有断点清单与章节，但缺少“从 `DispatcherServlet` 到 ExceptionResolvers 的主线伪代码 + 关键分支定位”的整合视图。
2. **Filter/Interceptor 的顺序与生命周期缺少可断言证据链**：目前章节偏概念说明，缺少“可观察/可回归”的 order + async lifecycle（`afterConcurrentHandlingStarted`）验证。
3. **条件请求（Last-Modified / If-Modified-Since / ShallowEtagHeaderFilter）缺少工程落地闭环**：已有 API 侧显式 ETag/304，但缺少“框架级 ETag filter”与“静态资源条件请求”的对照与测试证据。
4. **异步模型需要覆盖 `DeferredResult`**：现有 Callable/SSE 已覆盖，但 DeferredResult（尤其是 timeout/fallback 与测试方式）仍需补齐。
5. **“常见坑 + 排障清单”仍有占位内容**：需要把“坑点”与“可复现测试用例”强绑定，形成可执行清单。

## Change Content

1. 深化 Internals：补齐 MVC 请求链路内核的“主线伪代码 + 分支定位 + 断点锚点 + 对应 Labs”。
2. 新增可运行闭环：增加 Filter/Interceptor 顺序与 async lifecycle 的 LabTest，做到可断言、可回归。
3. 增强真实 HTTP：补齐条件请求对照（静态资源 Last-Modified/304 + ShallowEtagHeaderFilter 计算 ETag/304）。
4. 扩展 Async：补齐 DeferredResult（含 timeout/fallback）与对应 MockMvc asyncDispatch 测试闭环。
5. 补齐常见坑与排障清单：把“坑点 → 复现方式 → 对应测试类/断点入口”体系化整理。

## Impact Scope

- **Modules:** `springboot-web-mvc`（docs + src/main + src/test）、知识库 `helloagents/*`
- **Files:** 多文件新增/更新（docs 章节新增、LabTest 新增、少量示例代码新增/增强）
- **APIs:** 仅新增教学端点（限定在 `/api/advanced/**`），不破坏现有对外契约
- **Data:** 无

## Core Scenarios

### Requirement: 请求链路内核可解释 + 可复现
**Module:** springboot-web-mvc
把一次请求从 Filter → DispatcherServlet → HandlerMapping/HandlerAdapter → 参数解析/绑定/校验 → 返回值写回 → ExceptionResolvers 的关键分支串成“可运行的证据链”。

#### Scenario: 能从测试反推关键分支位置
- 给定一个失败场景（如 406/415/400/401），可以通过 `resolvedException` 与断点定位到链路中的确切分支。

### Requirement: Interceptor/Filter 顺序与生命周期可被验证
**Module:** springboot-web-mvc
补齐“入口在哪里、顺序怎么理解、async 时回调怎么变”的机制解释，并提供可断言的 LabTest。

#### Scenario: 通过 LabTest 固定 sync + async lifecycle
- sync：能证明 Filter 与 Interceptor 的相对位置与回调顺序
- async：能证明 `afterConcurrentHandlingStarted` 与二次 dispatch（asyncDispatch）的回调差异

### Requirement: 条件请求（缓存）工程闭环补齐
**Module:** springboot-web-mvc
新增静态资源 If-Modified-Since/304 与 ShallowEtagHeaderFilter 的 ETag/304 对照，用测试固化行为。

#### Scenario: 通过测试验证 304 分支
- 静态资源：第一次请求拿到 `Last-Modified`，第二次带 `If-Modified-Since` 命中 304
- Filter ETag：第一次请求拿到 `ETag`，第二次带 `If-None-Match` 命中 304

### Requirement: Async（DeferredResult）补齐并可测试
**Module:** springboot-web-mvc
新增 DeferredResult + timeout/fallback 演示，补齐 MockMvc asyncDispatch 测试证据链。

#### Scenario: 通过测试固定 DeferredResult 的 async 行为与 timeout 分支
- 正常完成：asyncStarted → asyncDispatch → 200 + JSON body
- 超时回退：asyncDispatch → 200 + fallback body（体现 timeout 分支可控）

### Requirement: 常见坑 + 排障清单与测试对齐
**Module:** springboot-web-mvc
把常见坑（406/415/400/401/403/CORS/multipart/async 等）整理为可执行排障清单，并为每类坑点绑定可运行测试入口。

#### Scenario: 看到现象后能按清单逐步收敛
- 以“先固化现象（测试）→ 再定位分支（resolvedException/handler）→ 再断点验证（源码）”为固定套路。

## Risk Assessment

- **Risk:** 新增 Filter/Interceptor/Async 示例可能影响既有测试稳定性
  - **Mitigation:** 将示例能力限定到 `/api/advanced/**` 的特定子路径；新增 Filter 使用路径白名单（避免影响其他端点）；新增测试使用独立 controller 与显式 `@Import`，降低耦合。
- **Risk:** Async/timeout 测试存在时序不稳定
  - **Mitigation:** 使用可控 timeout 与 fallback，避免依赖真实线程调度的偶然性；测试侧使用 `asyncDispatch` 固定行为。

