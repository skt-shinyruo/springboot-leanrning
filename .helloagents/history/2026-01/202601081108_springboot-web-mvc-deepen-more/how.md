# Technical Design: springboot-web-mvc deepen more（链路内核/条件请求/异步/排障闭环再深化）

## Technical Solution

### Core Technologies

- Spring Boot + Spring Web MVC
- MockMvc（slice Labs）+ SpringBootTest/TestRestTemplate（端到端 Labs）
- Spring Security（FilterChain 对照）
- Spring Actuator/Micrometer（观测入口）

### Implementation Key Points

1. **Internals 章节深化**
   - 在 docs 中补齐 `DispatcherServlet#doDispatch` 的主线伪代码与“关键分支 → 现象 → 对应异常/断点”映射。
   - 增补 ExceptionResolvers（`ExceptionHandlerExceptionResolver`/`ResponseStatusExceptionResolver`/`DefaultHandlerExceptionResolver`）的链路位置与优先级。

2. **Interceptor/Filter 顺序与 async lifecycle Lab**
   - 新增一组教学端点（限定在 `/api/advanced/trace/**`），通过 request attribute 记录事件序列。
   - 新增 Filter + AsyncHandlerInterceptor：记录 sync 顺序与 async 的 `afterConcurrentHandlingStarted` + 二次 dispatch（asyncDispatch）。
   - LabTest 通过 `MvcResult#getRequest().getAttribute(...)` 断言事件序列（包含 Filter finally 的“after”事件）。

3. **条件请求对照（Last-Modified + ShallowEtagHeaderFilter）**
   - 扩展 real-world HTTP Lab：
     - 静态资源：读取 `Last-Modified` 并用 `If-Modified-Since` 验证 304
     - Filter ETag：引入 `ShallowEtagHeaderFilter`（限定路径），验证 `ETag` + `If-None-Match` 的 304

4. **DeferredResult 补齐**
   - 在 async controller 中增加 DeferredResult 正常完成与 timeout/fallback 两条分支。
   - LabTest：`asyncStarted` → `asyncDispatch` 固定行为，分别断言正常与 fallback 输出。

## Security and Performance

- **Security:**
  - 新增端点统一放在 `/api/advanced/**`，并遵循现有 `SecurityConfig` 的“默认放行、局部保护”策略，不扩大保护面以免影响既有 Labs。
  - 不引入任何敏感信息与外部依赖；不记录用户隐私数据。
- **Performance:**
  - 新增 Filter/Interceptor 均限制 path pattern，避免对全局请求带来额外开销。
  - 仅作为教学示例，不引入复杂的 buffer/包装响应实现。

## Testing and Verification

- **Module tests:** `mvn -pl springboot-web-mvc test`
- **重点回归：**
  - 406/415（契约分支）
  - 绑定/校验（binder 分支）
  - CORS/multipart/download/static resources/ETag（真实 HTTP 分支）
  - Async（Callable/SSE + DeferredResult）
  - Security/Observability（FilterChain + metrics）

