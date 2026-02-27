# 04. ProblemDetail vs 自定义错误体（ApiError：契约的两种路线）

## 导读

本章围绕「04：ProblemDetail vs 自定义错误体（ApiError：契约的两种路线）」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
建议优先运行 `BootWebMvcContractJacksonLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! summary "本章要点"

    - **ApiError（自定义契约）**：适合“强约束统一契约”，前后端协作成本低，但需要持续维护语义与兼容性。
    - **ProblemDetail（框架语义）**：更接近标准化错误载体（RFC 7807 风格），语义清晰、可扩展，但需要团队对字段含义达成共识。
    - 关键不是“选哪个更高级”，而是：**想把稳定性放在“字段形状”还是“语义表达”上**。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcContractJacksonLabTest`

## 机制主线（两条路线怎么落地）

### 路线 1：ApiError（统一错误契约）

- 典型入口：`@ControllerAdvice` + `@ExceptionHandler` 返回 `ApiError`
- 优势：字段固定（`message` / `fieldErrors`），调用方处理简单
- 风险：语义需要自定义并维护（例如 message 值、错误码体系）

本模块示例：
- `ApiError`：`spring-boot-modules/spring-boot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part01_web_mvc/ApiError.java`
- `AdvancedApiExceptionHandler`：统一错误塑形（Part 04/05/06）

### 路线 2：ProblemDetail（更语义化/更标准）

- 典型入口：显式返回 `ProblemDetail`，或通过 `@ExceptionHandler` 映射异常到 `ProblemDetail`
- 优势：字段语义更清晰（`status/title/detail/instance` + 扩展字段）
- 风险：如果同时存在多套错误体，需要在文档中把“选择边界”写清楚

本模块示例：
- `ProblemDetailDemoController`：显式返回 + 抛异常两种方式
- `ProblemDetailDemoExceptionHandler`：用 `@ExceptionHandler` 把异常映射成 ProblemDetail

## 源码与断点

- ApiError 路线：优先看 `ExceptionHandlerExceptionResolver` 如何选择 `@ExceptionHandler`
- ProblemDetail 路线：同样从 `ExceptionHandlerExceptionResolver` 入手，但返回体是 `ProblemDetail`

## 最小可运行实验（Lab）

- Lab：`BootWebMvcContractJacksonLabTest`（ApiError 路线的契约与分支）
- Lab：`BootWebMvcProblemDetailLabTest`（ProblemDetail 路线的对照）

## 常见坑与边界

- 如果在同一模块里同时存在两套错误体，一定要写清楚“各自负责哪些端点/哪些场景”，否则调用方会被迫做大量兼容分支。
- `Content-Type` 建议显式使用 `application/problem+json`（ProblemDetail）以避免误解为普通 JSON。

## 小结与下一章

- 下一章进入真实 HTTP 场景：从浏览器视角理解 CORS、上传下载、缓存等“线上最常见”的分支。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcContractJacksonLabTest`
- Lab：`BootWebMvcProblemDetailLabTest`

上一章：[part-04-rest-contract/03-error-contract-hardening.md](03-error-contract-hardening.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-05-real-world-http/01-cors-preflight.md](../part-05-real-world-http/01-cors-preflight.md)

<!-- BOOKIFY:END -->

