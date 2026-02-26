# Task List: springboot-web-mvc 深入扩展（advanced deep dive）

Directory: `helloagents/plan/202601071635_springboot-web-mvc-advanced-deep-dive/`

---

## 1. Docs 目录与章节结构（SSOT）
- [√] 1.1 更新目录：在 `docs/web-mvc/springboot-web-mvc/README.md` 增加 Part 03–07 的章节链接（明确到文件名），包含：
  - Part 03：`part-03-web-mvc-internals/01-dispatcherservlet-call-chain.md`、`part-03-web-mvc-internals/02-argument-resolver-and-binder.md`、`part-03-web-mvc-internals/03-message-converters-and-return-values.md`
  - Part 04：`part-04-rest-contract/01-content-negotiation-406-415.md`、`part-04-rest-contract/02-jackson-objectmapper-controls.md`、`part-04-rest-contract/03-error-contract-hardening.md`
  - Part 05：`part-05-real-world-http/01-cors-preflight.md`、`part-05-real-world-http/02-multipart-upload.md`、`part-05-real-world-http/03-download-and-streaming.md`、`part-05-real-world-http/04-static-resources-and-cache.md`
  - Part 06：`part-06-async-sse/01-servlet-async-and-testing.md`、`part-06-async-sse/02-sse-emitter.md`
  - Part 07：`part-07-testing-debugging/01-webmvc-testing-and-troubleshooting.md`
  verify why.md#core-scenarios
- [√] 1.2 创建章节骨架：为上面所有 `.md` 文件创建最小可读骨架（沿用仓库现有章节结构），并在每章的 “E. 最小可运行实验（Lab）” 写入对应 `*LabTest` 类名，确保 `scripts/check-teaching-coverage.py --module springboot-web-mvc` 解析得到可跑入口（可以同一个 LabTest 被多个章节引用），verify why.md#core-scenarios
- [√] 1.3 更新导读：在 `docs/web-mvc/springboot-web-mvc/part-00-guide/00-deep-dive-guide.md` 的 “推荐阅读顺序” 中插入 Part 03–07，并补一句“先跑哪个 LabTest 再读哪几章”的建议（例如：先跑 Internals/Contract/RealWorld/Async 这 4 个 Lab），verify why.md#core-scenarios

## 2. Part 03 - Web MVC Internals（DispatcherServlet 全链路）
- [√] 2.1 新增参数注解：新增 `springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part03_internals/ClientIp.java`（用于标记 controller 方法参数），约束：仅支持 `String` 参数，verify why.md#dispatcherServlet-全链路可观察
- [√] 2.2 新增 ArgumentResolver：新增 `springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part03_internals/ClientIpArgumentResolver.java`，解析规则：优先取 `X-Forwarded-For` 第一个 IP；否则取 `HttpServletRequest#getRemoteAddr()`，verify why.md#dispatcherServlet-全链路可观察
- [√] 2.3 注册 resolver：新增 `springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part03_internals/WebMvcInternalsConfig.java`（实现 `WebMvcConfigurer`，在 `addArgumentResolvers` 注册 `ClientIpArgumentResolver`），verify why.md#dispatcherServlet-全链路可观察
- [√] 2.4 新增教学端点（internals）：新增 `springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part03_internals/WebMvcInternalsController.java`，提供 `GET /api/advanced/internals/whoami`：入参包含 `@ClientIp String clientIp` + `@RequestHeader("User-Agent") String userAgent`，返回 JSON `{ clientIp, userAgent }`，verify why.md#dispatcherServlet-全链路可观察
- [√] 2.5 新增 LabTest（internals）：新增 `springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part03_internals/BootWebMvcInternalsLabTest.java`（`@WebMvcTest(WebMvcInternalsController.class)` + `@Import(WebMvcInternalsConfig.class)`），至少覆盖：
  - 带 `X-Forwarded-For: 1.2.3.4, 5.6.7.8` 时 `clientIp == 1.2.3.4`
  - 不带 `X-Forwarded-For` 时，通过 `RequestPostProcessor` 设置 `remoteAddr=8.8.8.8` 并断言 `clientIp == 8.8.8.8`
  verify why.md#dispatcherServlet-全链路可观察
- [√] 2.6 补齐 docs：在 `docs/web-mvc/springboot-web-mvc/part-03-web-mvc-internals/01-dispatcherservlet-call-chain.md` 写清“从测试用例反推调用链”的观察点，并列出建议断点（`DispatcherServlet#doDispatch`、`RequestMappingHandlerMapping`、`RequestMappingHandlerAdapter`、`HandlerMethodArgumentResolverComposite`、`AbstractMessageConverterMethodProcessor`、`ExceptionHandlerExceptionResolver`），引用 `BootWebMvcInternalsLabTest`，verify why.md#dispatcherServlet-全链路可观察
- [√] 2.7 补齐 docs：在 `docs/web-mvc/springboot-web-mvc/part-03-web-mvc-internals/02-argument-resolver-and-binder.md` 写清 `ArgumentResolver` vs `@InitBinder`/`WebDataBinder` 的职责边界，并给出“什么时候写 resolver、什么时候写 converter”的判断标准（配合本模块现有 binding 章节），引用 `BootWebMvcInternalsLabTest` + `BootWebMvcLabTest`，verify why.md#dispatcherServlet-全链路可观察
- [√] 2.8 补齐 docs：在 `docs/web-mvc/springboot-web-mvc/part-03-web-mvc-internals/03-message-converters-and-return-values.md` 解释 `HttpMessageConverter` 在链路中的位置（与 `@RequestBody/@ResponseBody` 对应），并把 Part 04 的 strict media type 例子作为“可观察证据”链接过去，引用 `BootWebMvcContractJacksonLabTest`，verify why.md#dispatcherServlet-全链路可观察

## 3. Part 04 - REST Contract & Jackson（契约与序列化可控）
- [√] 3.1 新增 REST 契约端点：新增 `springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part04_contract/RestContractController.java`，至少包含：
  - `POST /api/advanced/contract/echo`（`consumes=application/json`、`produces=application/json`）：回显请求体中的 `message` 与 `createdAt`
  - `POST /api/advanced/contract/strict-echo`（`consumes=application/vnd.learning.strict+json`、`produces=application/json`）：用于演示“同一个 DTO 在不同 converter 下行为不同”
  - `GET /api/advanced/contract/ping`（`produces=application/json`）：用于制造 `406 Not Acceptable` 的稳定复现
  verify why.md#rest-契约与序列化可控httpmessageconverter--jackson
- [√] 3.2 新增 DTO：新增 `springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part04_contract/ContractEchoRequest.java` 与 `ContractEchoResponse.java`（包含 `String message`、`Instant createdAt`），并确保 JSON 字段名与测试断言一致，verify why.md#rest-契约与序列化可控httpmessageconverter--jackson
- [√] 3.3 新增 strict converter：新增 `springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part04_contract/StrictJsonMessageConverterConfig.java`，注册一个仅支持 `application/vnd.learning.strict+json` 的 `MappingJackson2HttpMessageConverter`，并使用 “严格模式 ObjectMapper”（启用 `FAIL_ON_UNKNOWN_PROPERTIES=true`），verify why.md#rest-契约与序列化可控httpmessageconverter--jackson
- [√] 3.4 新增异常映射（仅作用于 advanced REST）：新增 `springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part04_contract/AdvancedApiExceptionHandler.java`（`@RestControllerAdvice`，basePackages 限定为 `part03_internals/part04_contract/part05_real_world/part06_async_sse`），至少处理：
  - `HttpMessageNotReadableException` → `ApiError(message="malformed_json")`
  - `MethodArgumentNotValidException` → `ApiError(message="validation_failed", fieldErrors=...)`
  - `MethodArgumentTypeMismatchException` → `ApiError(message="type_mismatch", fieldErrors=...)`
  verify why.md#rest-契约与序列化可控httpmessageconverter--jackson
- [√] 3.5 新增 LabTest（contract/jackson）：新增 `springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part04_contract/BootWebMvcContractJacksonLabTest.java`（`@WebMvcTest(RestContractController.class)` + `@Import(StrictJsonMessageConverterConfig.class, AdvancedApiExceptionHandler.class)`），至少覆盖：
  - `415 Unsupported Media Type`：对 `/echo` 用 `Content-Type: text/plain` 发 body
  - `406 Not Acceptable`：对 `/ping` 用 `Accept: text/plain`
  - “严格 JSON”拒绝未知字段：对 `/strict-echo` 用 `Content-Type: application/vnd.learning.strict+json` 发送包含多余字段（如 `extra`），断言返回 400 + `ApiError.message == "malformed_json"`
  - JSON 时间字段可控：对 `/echo` 发送固定 `createdAt` 字符串并断言回显一致
  verify why.md#rest-契约与序列化可控httpmessageconverter--jackson
- [√] 3.6 补齐 docs：在 `docs/web-mvc/springboot-web-mvc/part-04-rest-contract/01-content-negotiation-406-415.md` 用本 Lab 的 406/415 用例讲清 `Accept`/`Content-Type`/`produces`/`consumes` 与异常类型（`HttpMediaTypeNotAcceptableException`/`HttpMediaTypeNotSupportedException`），引用 `BootWebMvcContractJacksonLabTest`，verify why.md#rest-契约与序列化可控httpmessageconverter--jackson
- [√] 3.7 补齐 docs：在 `docs/web-mvc/springboot-web-mvc/part-04-rest-contract/02-jackson-objectmapper-controls.md` 用 “strict media type → strict converter → strict ObjectMapper” 的例子讲清“配置如何做到不破坏默认 JSON 行为”，引用 `BootWebMvcContractJacksonLabTest`，verify why.md#rest-契约与序列化可控httpmessageconverter--jackson
- [√] 3.8 补齐 docs：在 `docs/web-mvc/springboot-web-mvc/part-04-rest-contract/03-error-contract-hardening.md` 用现有 `ApiError` 解释“解析失败 vs 校验失败 vs 类型不匹配”的分流点（异常类型/触发阶段），并明确哪些错误不建议用 200 包装，引用 `BootWebMvcLabTest` + `BootWebMvcContractJacksonLabTest`，verify why.md#rest-契约与序列化可控httpmessageconverter--jackson

## 4. Part 05 - Real World HTTP（CORS / Multipart / Download / Static）
- [√] 4.1 新增 CORS 配置：新增 `springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part05_real_world/AdvancedCorsConfig.java`（实现 `WebMvcConfigurer`），只对 `/api/advanced/cors/**` 生效，至少配置：允许 Origin `https://example.com`、允许方法 `GET`、允许 header `X-Request-Id`，verify why.md#真实-web-场景补齐corsmultipartdownloadstatic
- [√] 4.2 新增 CORS demo 端点：新增 `springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part05_real_world/CorsDemoController.java`，提供 `GET /api/advanced/cors/ping` 返回 JSON `{message:"pong"}`，verify why.md#真实-web-场景补齐corsmultipartdownloadstatic
- [√] 4.3 新增内存文件存储：新增 `springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part05_real_world/InMemoryFileStore.java`（组件/服务），提供：`save(MultipartFile)` → 返回 `long id`；`get(long id)` → 返回 bytes + filename + contentType，注意：不落盘，verify why.md#真实-web-场景补齐corsmultipartdownloadstatic
- [√] 4.4 新增上传/下载端点：新增 `springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part05_real_world/FileTransferController.java`，至少包含：
  - `POST /api/advanced/files/upload`：接收 `MultipartFile file`，返回 `{id,fileName,size,contentType}`
  - `GET /api/advanced/files/{id}`：返回文件 bytes，并设置 `Content-Disposition: attachment; filename="..."` 与正确 `Content-Type`
  verify why.md#真实-web-场景补齐corsmultipartdownloadstatic
- [√] 4.5 新增 LabTest（real world http）：新增 `springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part05_real_world/BootWebMvcRealWorldHttpLabTest.java`（`@WebMvcTest({CorsDemoController.class, FileTransferController.class})` + `@Import(AdvancedCorsConfig.class, InMemoryFileStore.class, AdvancedApiExceptionHandler.class)`），至少覆盖：
  - CORS 预检：对 `/api/advanced/cors/ping` 发送 OPTIONS（带 `Origin`、`Access-Control-Request-Method`、`Access-Control-Request-Headers`）并断言 `Access-Control-Allow-Origin`/`Allow-Methods`/`Allow-Headers`
  - CORS 实际请求：GET + `Origin`，断言响应头仍包含 `Access-Control-Allow-Origin`
  - 上传：MockMvc multipart 上传 `hello.txt`（内容 `hello`），断言返回 `id`、`fileName`、`size`
  - 下载：用上传返回的 `id` GET 下载，断言 `Content-Disposition` 与 body bytes 等于 `hello`
  - 静态资源：GET `/css/app.css`，断言 200 + `Content-Type` 包含 `text/css` + body 含 `:root`
  verify why.md#真实-web-场景补齐corsmultipartdownloadstatic
- [√] 4.6 补齐 docs：在 Part 05 的 4 篇章节中分别写清“浏览器视角为什么要这样做”，并引用同一个 `BootWebMvcRealWorldHttpLabTest` 作为可跑入口（确保每章都有可跑入口），verify why.md#真实-web-场景补齐corsmultipartdownloadstatic

## 5. Part 06 - Async & SSE（可选但深入）
- [√] 5.1 新增 Async 端点：新增 `springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part06_async_sse/AsyncDemoController.java`，提供 `GET /api/advanced/async/ping` 返回 `Callable<Map<String,Object>>`（返回 `{message:"pong", thread:...}`），verify why.md#async--sse可选但深入
- [√] 5.2 新增 SSE 端点：新增 `springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part06_async_sse/SseDemoController.java`，提供 `GET /api/advanced/sse/ping` 返回 `SseEmitter`，发送 2 条事件（例如 `data: ping-1`、`data: ping-2`）后 `complete()`，并设置合理超时（避免测试挂死），verify why.md#async--sse可选但深入
- [√] 5.3 新增 LabTest（async/sse）：新增 `springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part06_async_sse/BootWebMvcAsyncSseLabTest.java`（`@WebMvcTest({AsyncDemoController.class, SseDemoController.class})` + `@Import(AdvancedApiExceptionHandler.class)`），至少覆盖：
  - Async：`GET /api/advanced/async/ping` 断言 `asyncStarted`，随后 `asyncDispatch` 断言 200 + JSON `message=="pong"`
  - SSE：`GET /api/advanced/sse/ping` 断言 200 + `Content-Type` 为 `text/event-stream`（或兼容），并断言响应体包含 `data: ping-1`（至少一条）
  verify why.md#async--sse可选但深入
- [√] 5.4 补齐 docs：在 `docs/web-mvc/springboot-web-mvc/part-06-async-sse/01-servlet-async-and-testing.md` 解释“为什么 asyncDispatch 是必要的”，列出推荐断点（`WebAsyncManager`、`CallableMethodReturnValueHandler` 等），引用 `BootWebMvcAsyncSseLabTest`，verify why.md#async--sse可选但深入
- [√] 5.5 补齐 docs：在 `docs/web-mvc/springboot-web-mvc/part-06-async-sse/02-sse-emitter.md` 解释 SSE 协议片段（`data:`/空行）与为什么要“有限事件 + 快速完成”来避免 flaky，引用 `BootWebMvcAsyncSseLabTest`，verify why.md#async--sse可选但深入

## 6. Part 07 - Testing & Troubleshooting（测试与排障进阶）
- [√] 6.1 新增 LabTest（testing/debugging）：新增 `springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part07_testing/BootWebMvcTestingDebuggingLabTest.java`（`@WebMvcTest(RestContractController.class)` + 必要 `@Import`），至少覆盖：
  - 通过 `MvcResult#getResolvedException()` 断言 415 的异常类型是 `HttpMediaTypeNotSupportedException`
  - 通过 `MvcResult#getResolvedException()` 断言 406 的异常类型是 `HttpMediaTypeNotAcceptableException`
  - （可选）断言 `result.getHandler()` 是 `HandlerMethod` 且方法名匹配预期（帮助定位“到底走了哪个 handler”）
  verify why.md#测试与排障进阶体系化
- [√] 6.2 补齐章节：在 `docs/web-mvc/springboot-web-mvc/part-07-testing-debugging/01-webmvc-testing-and-troubleshooting.md` 写出“红测排障脚本”：
  - 先看 status/响应体，再看 `resolvedException`，最后打断点（给出每类状态码的断点清单）
  - slice vs full 的判断：什么时候 `@WebMvcTest` 不够，需要 `@SpringBootTest(webEnvironment=RANDOM_PORT)`
  引用 `BootWebMvcTestingDebuggingLabTest` + 现有 `BootWebMvcSpringBootLabTest`，verify why.md#测试与排障进阶体系化
- [√] 6.3 扩展 Exercises：在 `springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part00_guide/BootWebMvcExerciseTest.java` 增加 3 个（默认 `@Disabled`）练习题，具体题面建议：
  - 练习 A：实现 `@ClientIp` resolver（补全解析规则并写 MockMvc 测试）
  - 练习 B：为 strict media type 增加“错误信息更可读”的 `ApiError.fieldErrors`（例如把未知字段名写进去）并写测试
  - 练习 C：为上传下载增加“文件不存在 id 返回 404 + JSON 错误形状”的分支并写测试
  verify why.md#测试与排障进阶体系化

## 7. Security Check
- [√] 7.1 执行安全自检（按 G9）：不写入敏感信息、不落盘上传文件、避免不受控无限 SSE/Async，记录结论

## 8. Documentation & Knowledge Base Sync
- [√] 8.1 更新 `helloagents/wiki/modules/springboot-web-mvc.md`（补齐新增 Part 的覆盖范围与新增 Lab 入口）
- [√] 8.2 更新 `helloagents/CHANGELOG.md` 记录本次扩展

## 9. Verification
- [√] 9.1 运行 `mvn -pl springboot-web-mvc test` 并修复红测
