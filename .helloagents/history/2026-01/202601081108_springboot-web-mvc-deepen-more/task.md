# Task List: springboot-web-mvc deepen more（链路内核/条件请求/异步/排障闭环再深化）

Directory: `helloagents/plan/202601081108_springboot-web-mvc-deepen-more/`

---

## 1. springboot-web-mvc（Docs 再深化）
- [√] 1.1 增补知识地图（“主线 + 关键分支 + 证据链”映射表），更新 `docs/web-mvc/springboot-web-mvc/part-00-guide/01-knowledge-map.md`，verify why.md#core-scenarios
- [√] 1.2 新增/扩写 Internals：ExceptionResolvers 主线章节，更新 `docs/web-mvc/springboot-web-mvc/part-03-web-mvc-internals/*`，verify why.md#requirement-请求链路内核可解释--可复现
- [√] 1.3 新增 Interceptor/Filter 生命周期章节，更新 `docs/web-mvc/springboot-web-mvc/part-01-web-mvc/*`，verify why.md#requirement-interceptorfilter-顺序与生命周期可被验证
- [√] 1.4 新增条件请求章节（Last-Modified + ETag filter），更新 `docs/web-mvc/springboot-web-mvc/part-05-real-world-http/*`，verify why.md#requirement-条件请求缓存工程闭环补齐
- [√] 1.5 新增 DeferredResult 章节，更新 `docs/web-mvc/springboot-web-mvc/part-06-async-sse/*`，verify why.md#requirement-asyncdeferredresult补齐并可测试
- [√] 1.6 补齐常见坑与排障清单（强绑定测试入口），更新 `docs/web-mvc/springboot-web-mvc/appendix/90-common-pitfalls.md`，verify why.md#requirement-常见坑--排障清单与测试对齐
- [√] 1.7 更新模块 docs TOC：`docs/web-mvc/springboot-web-mvc/README.md`（新增章节链接），verify docs links

## 2. springboot-web-mvc（Labs：Interceptor/Filter 顺序 + Async lifecycle）
- [√] 2.1 新增 Trace 端点 + Filter/Interceptor（限定 `/api/advanced/trace/**`），实现事件记录，files in `springboot-web-mvc/src/main/java/.../part03_internals`，verify why.md#scenario-通过-labtest-固定-sync--async-lifecycle
- [√] 2.2 新增 LabTest 固定顺序与 async lifecycle（含 `afterConcurrentHandlingStarted`），file `springboot-web-mvc/src/test/java/.../BootWebMvcTraceLabTest.java`

## 3. springboot-web-mvc（Labs：条件请求与缓存）
- [√] 3.1 引入 ShallowEtagHeaderFilter（限定路径）并新增对应端点，verify why.md#scenario-通过测试验证-304-分支
- [√] 3.2 扩展 real-world HTTP Lab：静态资源 `Last-Modified/If-Modified-Since` → 304；filter ETag → 304，update `BootWebMvcRealWorldHttpLabTest`

## 4. springboot-web-mvc（Labs：DeferredResult）
- [√] 4.1 增加 DeferredResult 正常完成 + timeout/fallback 端点，update `AsyncDemoController`
- [√] 4.2 扩展 `BootWebMvcAsyncSseLabTest`：补齐 DeferredResult 的 asyncDispatch 断言

## 5. Security Check
- [√] 5.1 执行安全检查（G9）：输入校验/敏感信息/权限边界/避免扩大 Filter 影响面

## 6. Knowledge Base Update
- [√] 6.1 更新模块知识库：`helloagents/wiki/modules/springboot-web-mvc.md`（Docs/Labs 入口与变更记录）
- [√] 6.2 更新 `helloagents/CHANGELOG.md`（Unreleased - Added）
- [√] 6.3 更新 `helloagents/history/index.md`（新增本次方案包索引）

## 7. Testing
- [√] 7.1 运行：`mvn -pl springboot-web-mvc test`
