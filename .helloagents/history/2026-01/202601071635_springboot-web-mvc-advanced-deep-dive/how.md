# Technical Design: springboot-web-mvc 深入扩展（advanced deep dive）

## Technical Solution

### Core Technologies

- Spring Boot 3.x（当前仓库 parent 版本）
- Spring Web MVC（Servlet 栈）+ Thymeleaf（已存在）
- Jackson（`HttpMessageConverter` 默认 JSON 处理）
- 测试：MockMvc（slice）/ TestRestTemplate（端到端）
- Servlet Async（Callable/DeferredResult）与 SSE（SseEmitter）

### Implementation Key Points

1. **以 docs/README.md 为 SSOT 扩展章节结构**
   - 在 `docs/web-mvc/springboot-web-mvc/README.md` 增加新 Part 的目录链接。

2. **机制内核（DispatcherServlet 全链路）用“可观察点 + 最小实验”讲清楚**
   - 通过自定义 `HandlerMethodArgumentResolver`、（可选）自定义 `HttpMessageConverter` 或 return value handler，提供“能被测试断言”的证据链。
   - 章节中给出推荐断点：`DispatcherServlet#doDispatch`、`RequestMappingHandlerMapping`、`RequestMappingHandlerAdapter`、`ExceptionHandlerExceptionResolver`、`AbstractMessageConverterMethodProcessor` 等。

3. **REST 契约可控（HttpMessageConverter + Jackson）坚持增量、不破坏现状**
   - 保持现有 `ApiError` 形状与既有 `/api/users` 行为不破坏。
   - 新增“教学用端点”与/或“仅对新 DTO 生效”的 Jackson 配置（例如日期格式、命名策略），用测试锁住行为。
   - 补齐 `415 Unsupported Media Type`、`406 Not Acceptable`、`HttpMessageNotReadableException` 等分支的可复现案例。

4. **真实 Web 场景补齐：CORS / Multipart / Download / Static**
   - CORS：提供最小 `CorsConfigurationSource` 或 MVC 配置；用测试断言 preflight（OPTIONS）与响应头。
   - Multipart：新增上传端点（`MultipartFile`/`@RequestPart`），用 MockMvc multipart 测试固定解析行为。
   - Download：新增下载端点，演示 `Content-Disposition`/`Content-Type`/streaming 的边界。
   - Static：补齐静态资源访问与（可选）缓存头策略的说明与断言。

5. **Async + SSE：避免 flaky 的最小闭环**
   - Async：使用 MockMvc 的 async 支持（`asyncStarted` + `asyncDispatch`）验证异步生命周期。
   - SSE：使用 `SseEmitter` 发送有限事件并快速完成；测试至少固定 `Content-Type` 与首个事件片段（不做无限流）。

6. **测试与排障：把“怎么调”写成可执行脚本**
   - 对比 `@WebMvcTest` 与 `@SpringBootTest` 的边界：哪些 bean 需要 `@Import`/mock，哪些问题只能端到端暴露。
   - 提供排障 checklist：状态码 400/404/405/406/415/500 的入口与常见成因。

## Docs Plan（建议新增 Part 结构）

- Part 03 - Web MVC Internals（机制内核）
  - 01 DispatcherServlet 主链路与关键组件
  - 02 ArgumentResolver / Binder / ConversionService
  - 03 ReturnValueHandler / MessageConverter（序列化发生在哪里）
- Part 04 - REST Contract & Jackson（契约与序列化）
  - 01 Content Negotiation：`Accept`/`Content-Type`/`produces`/`consumes`（406/415）
  - 02 Jackson 可控：未知字段/命名策略/日期时间
  - 03 错误契约加固：解析失败 vs 校验失败 vs 类型不匹配
- Part 05 - Real World HTTP（真实场景）
  - 01 CORS 与预检（OPTIONS）
  - 02 Multipart Upload
  - 03 Download & Streaming + Header
  - 04 Static Resources + Cache Headers
- Part 06 - Async & SSE
  - 01 Servlet Async 生命周期与测试（asyncDispatch）
  - 02 SSE：`text/event-stream` 最小闭环与测试策略
- Part 07 - Testing & Troubleshooting
  - 01 测试策略与排障工具箱（slice/full、resolvedException、打印链路）

## API Design（仅教学端点，避免破坏现有）

- `GET /api/advanced/internals/ping`：用于展示 resolver/handler 链路的可观察点
- `POST /api/advanced/contract/echo`：用于演示 message converter、content negotiation、Jackson 配置效果
- `OPTIONS/GET /api/advanced/cors/ping`：用于演示 preflight 与 CORS 响应头
- `POST /api/advanced/files/upload`：multipart 上传
- `GET /api/advanced/files/{id}`：下载与 header
- `GET /api/advanced/async/ping`：异步返回（Callable/DeferredResult）
- `GET /api/advanced/sse/stream`：SSE（有限事件、快速完成）

> 具体路径/命名以实现阶段为准，但原则是：新增内容隔离在 `/api/advanced/**`，避免影响既有章节与测试。

## Security and Performance

- **Security:** 不引入任何真实鉴权/敏感信息；上传仅用于内存示例；避免把文件写入未知路径。
- **Performance:** 以测试稳定性优先；SSE/Async 事件数量受控，避免超时与竞态导致 flaky。

## Testing and Deployment

- 单模块测试：`mvn -pl springboot-web-mvc test`
