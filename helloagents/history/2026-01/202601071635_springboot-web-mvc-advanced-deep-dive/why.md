# Change Proposal: springboot-web-mvc 深入扩展（机制内核 / REST 契约 / 真实场景）

## Requirement Background

- 当前 `springboot-web-mvc` 已覆盖 REST 基础链路（校验/异常/绑定/拦截器）与传统 MVC（Thymeleaf/表单/错误页/内容协商）。
- 但对“请求如何在 Spring MVC 内部被分派/解析/序列化/异常映射”的机制内核，以及真实 HTTP 场景（CORS、上传下载、静态资源）和异步/长连接、测试排障体系仍缺少系统化闭环。
- 目标：在不破坏现有示例的前提下，补齐更全面、更深入的可复现实验（docs + tests），形成可持续扩展的教学骨架。

## Change Content

1. 新增 docs Part（机制内核、契约与序列化、真实 Web 场景、Async/SSE、测试与排障），并在每章提供可跑入口（LabTest）。
2. 为每个专题补齐最小可复现实验：新增 Controller/Config/Resolver/Converter 等最小代码 + 对应 `*LabTest` 固定行为。
3. 统一约束：
   - 默认不引入新依赖（优先用现有 `spring-boot-starter-web` + `spring-boot-starter-test` 能覆盖的方式）。
   - 尽量只做“增量/可并存”的改动，避免破坏现有 `/api/ping`、`/api/users`、页面路由与 `ApiError` 形状。

## Impact Scope

- **Modules:** `springboot-web-mvc`
- **Files:** `docs/web-mvc/springboot-web-mvc/**`、`springboot-web-mvc/src/main/**`、`springboot-web-mvc/src/test/**`、`helloagents/wiki/modules/springboot-web-mvc.md`、`helloagents/CHANGELOG.md`
- **APIs:** 仅新增教学用端点（建议使用独立前缀，例如 `/api/advanced/**`），不破坏现有接口
- **Data:** 无持久化数据（仅内存结构/示例用）

## Core Scenarios

### Requirement: DispatcherServlet 全链路可观察
**Module:** springboot-web-mvc
建立“请求进入 MVC 内部的关键阶段”心智模型，并能通过断点/测试验证关键分支。

#### Scenario: 自定义 ArgumentResolver/ReturnValueHandler 可被测试固定
- 能证明 resolver/handler 被调用
- 能解释其在链路中的位置与与 binding/validation 的关系

### Requirement: REST 契约与序列化可控（HttpMessageConverter + Jackson）
**Module:** springboot-web-mvc
把 JSON 输入/输出与错误形状纳入工程可控范围（内容协商、媒体类型、序列化规则、解析失败分支）。

#### Scenario: JSON 解析失败/字段策略/日期格式 可被测试稳定复现
- 解析失败走统一错误响应（不依赖默认错误页）
- 能通过测试锁住 Jackson 配置效果（例如日期格式/命名策略/未知字段策略）

### Requirement: 真实 Web 场景补齐（CORS/Multipart/Download/Static）
**Module:** springboot-web-mvc
覆盖浏览器/HTTP 的常见真实交互点，并能用测试复现关键 header 与响应行为。

#### Scenario: 预检请求/上传下载/静态资源 可被测试断言
- OPTIONS 预检返回 `Access-Control-*` 头
- multipart 上传可解析并返回预期
- download/静态资源可访问并带合理 header

### Requirement: Async + SSE（可选但深入）
**Module:** springboot-web-mvc
展示 Servlet 线程模型、异步请求生命周期与 SSE 的最小实现与测试策略。

#### Scenario: asyncDispatch 与 event-stream 可复现
- 能断言异步完成/超时分支（尽量避免 flaky）
- SSE 至少能验证 `Content-Type: text/event-stream` 与首个事件发送

### Requirement: 测试与排障进阶体系化
**Module:** springboot-web-mvc
把“写得出/解释得清/调得明白”落到可执行的测试与断点策略。

#### Scenario: slice vs full context 与排障手册化
- 能对比 `@WebMvcTest` 与 `@SpringBootTest` 的边界与速度
- 能定位常见失败：415/406/400/404/500 的分支入口

## Risk Assessment

- **Risk:** 新增配置（如 CORS/Jackson）可能影响既有行为或导致测试不稳定  
  **Mitigation:** 新能力尽量限定在新端点/新配置类；测试避免依赖时间/线程竞态；所有改动以“可并存、可回滚”为原则。
