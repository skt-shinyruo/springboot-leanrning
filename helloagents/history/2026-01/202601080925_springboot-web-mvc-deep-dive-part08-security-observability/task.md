# Task List: springboot-web-mvc 深入扩展 II（知识地图 + Security/Observability + 排障闭环）

Directory: `helloagents/plan/202601080925_springboot-web-mvc-deep-dive-part08-security-observability/`

---

## 1. Docs - Knowledge Map（知识地图）
- [√] 1.1 新增知识地图章节：新增 `docs/web-mvc/springboot-web-mvc/part-00-guide/01-knowledge-map.md`（主轴 + 关键分支 + 可跑入口索引），verify why.md#web-mvc-知识地图--调用链可验证
- [√] 1.2 更新文档索引：更新 `docs/web-mvc/springboot-web-mvc/README.md` 与 `docs/web-mvc/springboot-web-mvc/part-00-guide/00-deep-dive-guide.md`，把知识地图纳入 Start Here + 推荐阅读路径，verify why.md#web-mvc-知识地图--调用链可验证

## 2. Binding/Validation Deep Dive（绑定/转换/校验深入）
- [√] 2.1 补齐 docs：更新 `docs/web-mvc/springboot-web-mvc/part-01-web-mvc/03-binding-and-converters.md`，补充 `@RequestBody` vs `@ModelAttribute`、`@InitBinder`、错误分类与断点建议，verify why.md#绑定转换校验更深入含-initbinder--modelattribute-vs-requestbody
- [√] 2.2 扩展 Lab：更新 `springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcLabTest.java` 或新增同包 `*LabTest`，补齐“解析失败/类型不匹配/校验失败”的分支断言，verify why.md#绑定转换校验更深入含-initbinder--modelattribute-vs-requestbody

## 3. Content Negotiation & Converters Advanced（内容协商进阶）
- [√] 3.1 增强 docs：更新 `docs/web-mvc/springboot-web-mvc/part-04-rest-contract/01-content-negotiation-406-415.md` 与 `docs/web-mvc/springboot-web-mvc/part-03-web-mvc-internals/03-message-converters-and-return-values.md`，补齐 converter 选择“关键分支 + 断点”，verify why.md#content-negotiation--httpmessageconverter-进阶边界
- [√] 3.2 扩展 Lab：更新 `springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part07_testing/BootWebMvcTestingDebuggingLabTest.java`（或新增用例），用 `resolvedException` 固化 406/415 分支证据链，verify why.md#content-negotiation--httpmessageconverter-进阶边界

## 4. ProblemDetail 对照示例（不破坏 ApiError）
- [√] 4.1 新增对照端点：新增 `springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part04_contract/ProblemDetailDemoController.java`（或新 package），提供最小 `ProblemDetail` 响应示例，verify why.md#异常体系升级apierror--problemdetail-并存示例
- [√] 4.2 新增对照文档与测试：新增 `docs/web-mvc/springboot-web-mvc/part-04-rest-contract/04-problemdetail-vs-custom-error.md` 与对应 `springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part04_contract/*LabTest.java`，对比两种错误形状并给出选择边界，verify why.md#异常体系升级apierror--problemdetail-并存示例

## 5. ETag/缓存（静态资源 + API）
- [√] 5.1 新增 API ETag 示例：新增 `springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part05_real_world/ApiEtagDemoController.java`（或同包类）与必要配置，verify why.md#缓存与-etag静态资源--api-响应
- [√] 5.2 新增 ETag Lab：更新 `springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part05_real_world/BootWebMvcRealWorldHttpLabTest.java`（或新增 test），断言 `ETag` + `If-None-Match` → 304，verify why.md#缓存与-etag静态资源--api-响应

## 6. Part 08 - Security Integration（与 MVC 链路对照）
- [√] 6.1 引入依赖：更新 `springboot-web-mvc/pom.xml` 增加 `spring-boot-starter-security`（以及必要测试依赖/配置），verify why.md#security-集成filterchainproxy--mvc--401403csrf
- [√] 6.2 新增最小安全配置与端点：新增 `springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part08_security_observability/SecurityConfig.java` 与 `SecureDemoController.java`（仅保护新端点前缀，既有默认放行），verify why.md#security-集成filterchainproxy--mvc--401403csrf
- [√] 6.3 新增安全 Lab：新增 `springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part08_security_observability/BootWebMvcSecurityLabTest.java`，至少覆盖 401/403/CSRF 分支并给出定位建议，verify why.md#security-集成filterchainproxy--mvc--401403csrf
- [√] 6.4 新增 Security 文档：新增 `docs/web-mvc/springboot-web-mvc/part-08-security-observability/01-security-filterchain-and-mvc.md`，解释 Filter/Interceptor/SecurityFilterChain 的相对位置与断点清单，verify why.md#security-集成filterchainproxy--mvc--401403csrf

## 7. Part 08 - Observability/性能观测（Actuator + 指标）
- [√] 7.1 引入依赖：更新 `springboot-web-mvc/pom.xml` 增加 `spring-boot-starter-actuator`，并更新 `springboot-web-mvc/src/main/resources/application.properties` 暴露最小端点集合，verify why.md#observability性能观测interceptor-计时--actuator-metrics
- [√] 7.2 新增观测组件：新增 `springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part08_security_observability/TimingInterceptor.java` 与对应 MVC 配置（仅对新端点或示例端点生效），verify why.md#observability性能观测interceptor-计时--actuator-metrics
- [√] 7.3 新增观测 Lab：新增 `springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part08_security_observability/BootWebMvcObservabilityLabTest.java`（建议 `@SpringBootTest(webEnvironment=RANDOM_PORT)`），验证 actuator metrics 可读取并能解释观测点，verify why.md#observability性能观测interceptor-计时--actuator-metrics
- [√] 7.4 新增 Observability 文档：新增 `docs/web-mvc/springboot-web-mvc/part-08-security-observability/02-observability-and-metrics.md`，写清“看见什么/怎么验证/常见误区”，verify why.md#observability性能观测interceptor-计时--actuator-metrics

## 8. Pitfalls & Troubleshooting（常见坑 + 排障清单）
- [√] 8.1 补齐坑点：更新 `docs/web-mvc/springboot-web-mvc/appendix/90-common-pitfalls.md` 与 `docs/web-mvc/springboot-web-mvc/part-07-testing-debugging/01-webmvc-testing-and-troubleshooting.md`，覆盖 400/401/403/404/406/415/500 的定位套路与断点清单，verify why.md#排障清单可复现常见坑--断点--失败场景测试
- [√] 8.2 更新自测题：更新 `docs/web-mvc/springboot-web-mvc/appendix/99-self-check.md` 补齐 Security/观测/ETag 的自测题与实验入口引用，verify why.md#排障清单可复现常见坑--断点--失败场景测试

## 9. Security Check
- [√] 9.1 执行安全自检（按 G9）：避免硬编码密钥、限制 actuator 暴露、上传下载不落盘敏感内容、SSE/Async 不无限阻塞，记录结论
  > Note: `SecurityConfig` 仅用于教学演示（demo 用户使用 `{noop}`），Actuator 仅最小暴露 `health/info/metrics`，未引入真实密钥/Token，也未将上传内容落盘。

## 10. Documentation & Knowledge Base Sync
- [√] 10.1 更新 `helloagents/wiki/modules/springboot-web-mvc.md`（新增 Part 08 覆盖范围与 Lab 入口），并更新 `helloagents/CHANGELOG.md` 记录本次增强

## 11. Verification
- [√] 11.1 运行 `mvn -pl springboot-web-mvc test` 并修复红测
