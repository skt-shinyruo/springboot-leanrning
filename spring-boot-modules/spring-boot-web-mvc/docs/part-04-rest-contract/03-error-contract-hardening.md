# 03. 错误契约加固（解析失败 vs 校验失败 vs 类型不匹配）

## 导读

本章围绕「03：错误契约加固（解析失败 vs 校验失败 vs 类型不匹配）」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
建议优先运行 `BootWebMvcLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! summary "本章要点"

    - 解析失败（malformed JSON）：通常是 `HttpMessageNotReadableException`
    - 校验失败（@Valid）：通常是 `MethodArgumentNotValidException`
    - 类型不匹配（query/path 无法转型）：通常是 `MethodArgumentTypeMismatchException`


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcLabTest` / `BootWebMvcContractJacksonLabTest`

## 机制主线

- 本章以两组证据链对照：
  - Part 01：`BootWebMvcLabTest`（校验失败、malformed JSON 的基本形状）
  - Part 04：`BootWebMvcContractJacksonLabTest`（strict media type 下未知字段触发解析失败）

## 源码与断点

建议断点：
- `RequestResponseBodyMethodProcessor#resolveArgument`（解析失败）
- `MethodValidationAdapter`/`DataBinder#validate`（校验失败）
- `ExceptionHandlerExceptionResolver#doResolveHandlerMethodException`（异常映射）

## 最小可运行实验（Lab）

- Lab：`BootWebMvcLabTest` / `BootWebMvcContractJacksonLabTest`

## 常见坑与边界

- 不要把所有错误都映射成同一个 message：调用方需要区分“修 payload”还是“修字段约束”还是“修类型”。

## 小结与下一章

- 下一章进入真实 HTTP 场景：从浏览器/前端视角看 CORS、上传下载与静态资源。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcLabTest` / `BootWebMvcContractJacksonLabTest`

上一章：[part-04-rest-contract/02-jackson-objectmapper-controls.md](02-jackson-objectmapper-controls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-04-rest-contract/04-problemdetail-vs-custom-error.md](04-problemdetail-vs-custom-error.md)

<!-- BOOKIFY:END -->
