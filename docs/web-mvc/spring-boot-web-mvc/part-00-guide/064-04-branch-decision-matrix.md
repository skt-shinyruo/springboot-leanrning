# 第 64 章：04：关键分支矩阵（Web MVC Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Web MVC Branch Decision Matrix）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootWebMvcErrorBranchMatrixLabTest#branch415_whenContentTypeIsNotSupported`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 64 章：00 - Deep Dive Guide（springboot-web-mvc）](064-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 65 章：01：知识地图（Web MVC Deep Dive Map）](065-01-knowledge-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：04：关键分支矩阵（Web MVC Branch Decision Matrix） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
- 回到主线：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

本页目标：给你一张“从 status 回到根因”的最小矩阵表——每一行都对应一个：

- 可复现入口（测试方法）
- 可观察证据（resolvedException 类型）
- 可定位断点（分支发生点）

## 关键分支矩阵（最小集合：HTTP 错误分支）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| 415 Unsupported Media Type | Content-Type 不支持 | 415 + `HttpMediaTypeNotSupportedException` | `BootWebMvcErrorBranchMatrixLabTest#branch415_whenContentTypeIsNotSupported` | resolvedException 类型 |
| 406 Not Acceptable | Accept 不支持 | 406 + `HttpMediaTypeNotAcceptableException` | `BootWebMvcErrorBranchMatrixLabTest#branch406_whenAcceptIsNotSupported` | selectedMediaType |
| 400 Malformed JSON | JSON 解析失败 | 400 + `HttpMessageNotReadableException` | `BootWebMvcErrorBranchMatrixLabTest#branch400_whenJsonIsMalformed` | converter/readWithMessageConverters |
| 400 Validation | Bean Validation 失败 | 400 + `MethodArgumentNotValidException` | `BootWebMvcErrorBranchMatrixLabTest#branch400_whenValidationFails` | BindingResult errors |
| 400 Type Mismatch | 参数类型不匹配 | 400 + `MethodArgumentTypeMismatchException` | `BootWebMvcErrorBranchMatrixLabTest#branch400_whenRequestParamTypeMismatch` | argument resolver/binder |

## 推荐运行命令

- `mvn -q -pl :spring-boot-web-mvc -Dtest=BootWebMvcErrorBranchMatrixLabTest test`

## 推荐断点（从错误分支回到机制）

- 入口：`DispatcherServlet#doDispatch`
- body 分支：`AbstractMessageConverterMethodArgumentResolver#readWithMessageConverters`
- binder 分支：`WebDataBinder#bind` / `DataBinder#validate`
- 异常收敛：`DispatcherServlet#processHandlerException` / `HandlerExceptionResolverComposite#resolveException`

## 与断点地图/Playbook 的关系

- 断点地图（总入口）：[`066-02-breakpoint-map.md`](066-02-breakpoint-map.md)
- 常见坑：[`../appendix/082-90-common-pitfalls.md`](../appendix/082-90-common-pitfalls.md)
- 自检：[`../appendix/083-99-self-check.md`](../appendix/083-99-self-check.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「04：关键分支矩阵（Web MVC Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：（以本章正文“源码；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「04：关键分支矩阵（Web MVC Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：断点”小节为准）；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootWebMvcErrorBranchMatrixLabTest#branch415_whenContentTypeIsNotSupported`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcErrorBranchMatrixLabTest`

上一章：[064-01-webmvc-request-call-chain.md](064-01-webmvc-request-call-chain.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[常见坑](../appendix/082-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
