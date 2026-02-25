# springboot-web-mvc

## Purpose

学习 Spring MVC 的两条主线：

- REST API（JSON）：`@RestController`、校验、统一错误响应
- 传统 MVC（HTML）：`@Controller`、Thymeleaf 页面渲染、表单提交（绑定/校验/回显/PRG）、错误页与内容协商

## Module Overview

- **Responsibility:** 提供可运行 Web 示例与测试（MockMvc 等），帮助理解请求处理链路。
- **Status:** 🚧In Development
- **Last Updated:** 2026-01-23

## Start Here（路线图 / 断点地图 / 第一个可运行入口）

- 路线图：`helloagents/wiki/learning-path.md`
- Docs Start Here：`spring-boot-modules/spring-boot-web-mvc/docs/README.md`
- 断点地图（Part 01 Debugger Pack）：`spring-boot-modules/spring-boot-web-mvc/docs/part-00-guide/06-breakpoint-map.md`
- 机制内核主线（建议先读这一章再扩展分支）：`spring-boot-modules/spring-boot-web-mvc/docs/part-03-web-mvc-internals/01-dispatcherservlet-call-chain.md`（FilterChain → DispatcherServlet → ExceptionResolvers → Boot `/error`；async 两次 dispatch 时间线 + 证据链；ERROR vs ASYNC 对照 + 分支决策表）
- 第一个可运行入口（3 分钟开跑）：
  - `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcLabTest#pingEndpointReturnsPong test`
  - 对应测试类：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcLabTest.java`

## Specifications

### Requirement: Web MVC 学习闭环
**Module:** springboot-web-mvc
覆盖请求处理、参数绑定与异常处理策略，并补齐传统 MVC（HTML）渲染与错误页机制。

#### Scenario: 请求处理链路可被测试验证
- 通过 MockMvc 等方式断言响应与错误处理

### Requirement: 传统 MVC 页面渲染学习闭环
**Module:** springboot-web-mvc
覆盖 `@Controller`、Thymeleaf、表单提交（绑定/校验/回显/PRG）与错误页/Accept 内容协商，并提供可复现测试入口。

### Requirement: Advanced Deep Dive（机制内核 / 契约可控 / 真实场景 / Async / 排障）
**Module:** springboot-web-mvc
覆盖 Web MVC 的核心内部链路（DispatcherServlet/HandlerMapping/HandlerAdapter）、契约与序列化（HttpMessageConverter/Jackson/ProblemDetail）、真实 HTTP 场景（CORS/上传下载/静态资源/ETag）、Async/SSE，以及测试与排障的体系化套路，并补齐 Security 与观测（metrics）相关分支。

#### Scenario: 关键分支可被测试验证
- 通过 `@WebMvcTest` 的 Lab 固定关键行为：406/415、strict media type、CORS preflight、multipart upload/download、asyncDispatch、SSE content-type 等

## Dependencies

- 与安全/测试模块有学习路径关联（可选）

## Docs & 复现入口

- **Docs Index:** `spring-boot-modules/spring-boot-web-mvc/docs/README.md`
- **Docs Guide:** `spring-boot-modules/spring-boot-web-mvc/docs/part-00-guide/02-deep-dive-guide.md`
- **Breakpoint Map:** `spring-boot-modules/spring-boot-web-mvc/docs/part-00-guide/06-breakpoint-map.md`
- **Branch Decision Matrix:** `spring-boot-modules/spring-boot-web-mvc/docs/part-00-guide/04-branch-decision-matrix.md`
- **Playbook:** `spring-boot-modules/spring-boot-web-mvc/docs/appendix/01-common-pitfalls.md`
- **Self-check:** `spring-boot-modules/spring-boot-web-mvc/docs/appendix/02-self-check.md`

- **Book Matrix（进阶入口）：**
  - `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcBookMatrixLabTest test`
  - 对应测试类：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcBookMatrixLabTest.java`
- **Branch Matrix（关键分支入口）：**
  - `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcErrorBranchMatrixLabTest test`
  - 对应测试类：`spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part07_testing/BootWebMvcErrorBranchMatrixLabTest.java`
- **Labs:**
  - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcLabTest.java`
  - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcSpringBootLabTest.java`
  - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcBindingDeepDiveLabTest.java`
  - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewLabTest.java`
  - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcErrorViewLabTest.java`
  - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_view_mvc/BootWebMvcViewSpringBootLabTest.java`
  - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part03_internals/BootWebMvcInternalsLabTest.java`
  - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part03_internals/BootWebMvcTraceLabTest.java`
  - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part03_internals/BootWebMvcMessageConverterTraceLabTest.java`
  - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part03_internals/BootWebMvcExceptionResolverChainLabTest.java`
  - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part04_contract/BootWebMvcContractJacksonLabTest.java`
  - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part04_contract/BootWebMvcProblemDetailLabTest.java`
  - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part09_advice_order/BootWebMvcAdviceOrderLabTest.java`
  - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part10_advice_matching/BootWebMvcAdviceMatchingLabTest.java`
  - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part05_real_world/BootWebMvcRealWorldHttpLabTest.java`
  - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part06_async_sse/BootWebMvcAsyncSseLabTest.java`
  - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part07_testing/BootWebMvcTestingDebuggingLabTest.java`
  - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part08_security_observability/BootWebMvcSecurityLabTest.java`
  - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part08_security_observability/BootWebMvcSecurityVsMvcExceptionBoundaryLabTest.java`
  - `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part08_security_observability/BootWebMvcObservabilityLabTest.java`
- **Exercises:** `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part00_guide/BootWebMvcExerciseTest.java`
- **Solution（Exercises 对应答案回归）：** `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part00_guide/BootWebMvcExerciseSolutionTest.java`
- **Lab（并发/性能：RequestScope 隔离 / 并发请求边界）：** `spring-boot-modules/spring-boot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part02_perf_concurrency/BootWebMvcRequestScopeIsolationLabTest.java`

## Source Layout（与 docs Part 对齐）

- `src/main/java`：入口类包名不变；示例按 Part 分组：
  - REST 主线：`com.learning.springboot.bootwebmvc.part01_web_mvc`
  - 页面主线：`com.learning.springboot.bootwebmvc.part02_view_mvc`
  - 机制内核：`com.learning.springboot.bootwebmvc.part03_internals`
  - 契约与序列化：`com.learning.springboot.bootwebmvc.part04_contract`
  - 真实 HTTP：`com.learning.springboot.bootwebmvc.part05_real_world`
  - Async/SSE：`com.learning.springboot.bootwebmvc.part06_async_sse`
  - Security/Observability：`com.learning.springboot.bootwebmvc.part08_security_observability`
  - Advice 优先级：`com.learning.springboot.bootwebmvc.part09_advice_order`
  - Advice 匹配规则：`com.learning.springboot.bootwebmvc.part10_advice_matching`
- `src/test/java`：按 `part00_guide`（Exercises/Solutions）+ `part01_*`（Labs）+ `part02_perf_concurrency`（并发/性能 Labs）+ `part07_testing`（排障）分包

## Change History

- [202601222155_solutions_all_remaining_modules](../../history/2026-01/202601222155_solutions_all_remaining_modules/) - ✅ 已执行：补齐 Solutions/Labs 文档入口，并新增并发/性能可复现实验（RequestScope 隔离 / 并发请求边界）
- [202601182033_beans_branch_decision_table_webmvc_error_async_deepen](../../history/2026-01/202601182033_beans_branch_decision_table_webmvc_error_async_deepen/) - ✅ 已执行：深化 DispatcherServlet 主链路：补齐“异常未被 resolver 处理 → 回落到 Boot `/error`”完整叙事；补齐 async 两次 dispatch 时间线与可断言证据链
- [202601182117_beans_bootstrap_guide_webmvc_deepen](../../history/2026-01/202601182117_beans_bootstrap_guide_webmvc_deepen/) - ✅ 已执行：继续深化 067 主链路：补齐 ERROR dispatch（DispatcherType.ERROR）与 ASYNC dispatch 的对照时间线，并新增“现象→阶段→关键方法→证据链”分支决策表
- [202601081505_springboot-web-mvc-deepen-v3](../../history/2026-01/202601081505_springboot-web-mvc-deepen-v3/) - ✅ 已执行：深化 v3：新增 `@ControllerAdvice` 匹配规则可复现 Labs（basePackages/annotations/assignableTypes + selector OR 语义 + @Order 叠加）、binder `suppressedFields` 证据链、HttpMessageConverter 选择可观测（selectedConverterType/selectedContentType 响应头），并新增 Part 03 章节与排障/自测升级
- [202601081308_springboot-web-mvc-deepen-even-more](../../history/2026-01/202601081308_springboot-web-mvc-deepen-even-more/) - ✅ 已执行：补齐导读/自测/坑点占位，并新增“mass assignment 防护（InitBinder allowedFields）”与“ControllerAdvice @Order 优先级”两组可复现 Labs
- [202601081108_springboot-web-mvc-deepen-more](../../history/2026-01/202601081108_springboot-web-mvc-deepen-more/) - ✅ 已执行：补齐 ExceptionResolvers 主线、Interceptor/Filter sync+async lifecycle Lab、条件请求（Last-Modified + ETag filter）与 DeferredResult（timeout/fallback），并把坑点清单与测试入口强绑定
- [202601080925_springboot-web-mvc-deep-dive-part08-security-observability](../../history/2026-01/202601080925_springboot-web-mvc-deep-dive-part08-security-observability/) - ✅ 已执行：新增知识地图、ProblemDetail 对照、ETag/304、Part 08（Security/Observability）与对应 Labs/排障升级
- [202601071635_springboot-web-mvc-advanced-deep-dive](../../history/2026-01/202601071635_springboot-web-mvc-advanced-deep-dive/) - ✅ 已执行：扩展 advanced deep dive（机制内核/契约与 Jackson/真实 HTTP/Async/SSE/排障），新增 docs Part 03–07 与对应 Labs
- [202601062218_all_modules_docs_bookify](../../history/2026-01/202601062218_all_modules_docs_bookify/) - ✅ 已执行：以 docs/<topic>/<module>/README.md 为 SSOT，对全部章节 upsert 统一尾部区块（### 对应 Lab/Test + 上一章｜目录｜下一章）
- [202601041358_springboot-part-structure-sync](../../history/2026-01/202601041358_springboot-part-structure-sync/) - ✅ 已执行：对齐 docs Part 与 src/main/src/test 分包，并修复 README/docs 引用
- [202601061309_springboot-web-mvc-thymeleaf-view-rendering](../../history/2026-01/202601061309_springboot-web-mvc-thymeleaf-view-rendering/) - ✅ 已执行：补齐传统 MVC（Thymeleaf/表单/错误页/Accept）+ docs 与 tests 闭环
