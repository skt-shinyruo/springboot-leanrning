# 99 自检：Spring Boot Web Client
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（复盘出口）"

    - 主线入口：`BootWebClientBookMatrixLabTest`
    - 分支入口：`BootWebClientBranchMatrixLabTest`
    - 推荐先跑：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 常见坑清单（Web Client）](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 先跑入口（把现象跑成事实）

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：`mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBranchMatrixLabTest test`

配套资料（排障更快）：

- [断点地图](../part-00-guide/04-breakpoint-map.md)
- [关键分支矩阵](../part-00-guide/05-branch-decision-matrix.md)
- 常见坑清单（索引页，不在本页重复）：[01-common-pitfalls.md](01-common-pitfalls.md)

## 自检题（每题都能落到 tests）

1. RestClient 的最小闭环是什么？（构建请求 → 反序列化 → 返回领域对象）如何把它写成断言？
   - 证据入口：`BootWebClientRestClientLabTest#restClientGetsGreeting`
2. 如何证明“发了直觉里的那条请求”（method/path/headers）？为什么这比只断言 response 更可靠？
   - 证据入口：`BootWebClientRestClientLabTest#restClientSendsExpectedPathAndHeaders`
3. 反序列化时未知字段会怎样？如何把“兼容性策略”固定成回归用例？
   - 证据入口：`BootWebClientRestClientLabTest#restClientIgnoresUnknownJsonFieldsByDefault`
4. 4xx/5xx 应该如何映射成“领域异常”（包含 status）？如何让调用方不必解读 HTTP 细节？
   - 证据入口：`BootWebClientRestClientLabTest#restClientMaps400ToDomainException` + `BootWebClientRestClientLabTest#restClientMaps500ToDomainException`
5. read timeout 触发时，如何证明它是“可重复失败”（而不是偶发现象）？
   - 证据入口：`BootWebClientRestClientLabTest#restClientReadTimeoutFailsFast`
6. retry 的边界是什么？如何用断言证明“失败一次 → 重试成功”，并把请求次数固定下来？
   - 证据入口：`BootWebClientRestClientLabTest#restClientRetriesOn5xxAndEventuallySucceeds`
7. WebClient（响应式）下如何写断言？为什么这里用 StepVerifier（或 block+timeout）更合适？
   - 证据入口：`BootWebClientWebClientLabTest#webClientGetsGreeting`
8. WebClient 的超时失败与 RestClient 的超时失败有何差异？如何把“失败模式”写成可回归用例？
   - 证据入口：`BootWebClientWebClientLabTest#webClientResponseTimeoutFailsFast`
9. WebClient 的 filter 链：请求阶段与响应阶段的执行顺序为什么相反？如何用一条用例把顺序固定下来？
   - 证据入口：`BootWebClientWebClientFilterOrderLabTest#webClientFilters_requestOrderAndResponseOrder_areDifferent`
10. 对比 RestClient 与 WebClient：当需要做“可测试的超时/重试/错误映射”时，两者的取舍点分别是什么？
    - 对照：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest`

## 退出条件（完成标准）

- 能把客户端行为写成“可控下游 + 可回归断言”的证据链（MockWebServer + 断言 + 请求计数）。
- 能区分：状态码分支、超时分支、重试分支，并为每条分支提供一个稳定入口用例。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebClientRestClientLabTest` / `BootWebClientWebClientLabTest` / `BootWebClientWebClientFilterOrderLabTest`
- Exercise：`BootWebClientExerciseTest`

上一章：[appendix/90-common-pitfalls.md](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
