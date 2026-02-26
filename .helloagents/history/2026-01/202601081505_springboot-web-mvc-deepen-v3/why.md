# Change Proposal: springboot-web-mvc deepen v3（Advice 匹配 + Binder suppressedFields + Converter 可观测）

## Requirement Background

在 `springboot-web-mvc` 模块前两轮 “更深一层” 已经补齐了：

- ExceptionResolvers 主线与错误流（机制内核）
- Filter/Interceptor 顺序 + sync/async lifecycle（可断言证据链）
- 条件请求（静态资源 If-Modified-Since/304 + ShallowEtagHeaderFilter）与 ETag Filter
- DeferredResult（timeout/fallback）与 SSE（可运行 Labs）
- 工程边界：mass assignment 防护（`@InitBinder#setAllowedFields`）与 `@ControllerAdvice @Order` 优先级（可复现）

但用户明确要求“更深一层、全都要、源码阅读课 + 工程落地课都要”，要把关键知识点落到：
- **调用链 + 关键分支解释**
- **可运行 Labs + 测试闭环**
- **常见坑 + 排障清单 + 对应可复现测试**

当前仍有 3 个“深水区缺口”，会导致学习/排障停留在“概念”或“靠猜”：

1) **`@ControllerAdvice` 的“匹配规则”缺口（不仅是 @Order）**
   - 真实工程里更常见的问题不是“有两个 advice 谁先执行”，而是：
     - 为什么某个 advice 没生效？
     - basePackages / annotations / assignableTypes 到底匹配了谁？
   - 需要把“可适用性（applicable）”与“优先级（order）”拆开，并提供可复现对照。

2) **binder 的“被阻止字段”缺口（suppressedFields）**
   - 仅断言 `admin` 没被绑定还不够：需要能“看见证据”：
     - 哪些字段被禁止绑定？
     - 这是否能在排障时给出可观测信号？
   - Spring Framework 6.2+ 提供 `BindingResult#getSuppressedFields()`，但目前没有在 Labs 中落地展示。

3) **HttpMessageConverter 的“选择结果证据链”缺口（selectedConverterType/selectedContentType）**
   - docs 已列出关键断点，但工程排障里常见问题是：
     - 到底选了哪个 converter？是 String / Jackson / byte[]？
     - negotiated 的 content-type 到底是什么？
   - 需要一个“可观测”机制把选择结果落到 response headers，并用测试固化差异（String/JSON/bytes/custom media type）。

## Change Content

1. 新增一组 `@ControllerAdvice` 匹配规则对照 Labs（annotations / assignableTypes / basePackages + order 叠加）。
2. 新增 binder suppressedFields 可视化 Labs：在 mass assignment 防护场景输出 `suppressedFields` 并可断言。
3. 新增 HttpMessageConverter 选择可观测 Labs：用 `ResponseBodyAdvice` 记录 `selectedConverterType/selectedContentType` 到响应头，并用多端点/多媒体类型测试固化。
4. 同步更新 docs（新增章节 + 断点/观察点 + 排障清单）与 helloagents 知识库/变更历史。

## Impact Scope

- **Modules:** `springboot-web-mvc`、知识库 `helloagents/*`
- **APIs:** 仅新增教学端点（限定 `/api/advanced/**`），不破坏既有契约
- **Risk:** 误伤全站行为（Advice/Filter 全局生效）
  - **Mitigation:** Advice 使用 basePackages 限定；ResponseBodyAdvice/Filter 通过 URI 前缀限定；新增端点统一放在 `/api/advanced/**`

