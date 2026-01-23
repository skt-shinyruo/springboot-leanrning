# 02：Jackson ObjectMapper 可控（严格模式、未知字段、时间）

## 导读

- 本章主题：**02：Jackson ObjectMapper 可控（严格模式、未知字段、时间）**
- 目标：把“序列化/反序列化差异”收敛到可控边界：该严格时严格、该兼容时兼容。

!!! summary "本章要点"

    - 不要为了一个端点的严格校验去全局开启 `FAIL_ON_UNKNOWN_PROPERTIES`；更好的方式是：**自定义 media type + 专用 converter + 专用 ObjectMapper**。
    - 对日期时间（如 `Instant`）的约束要靠测试锁住：不要依赖“猜测默认行为”。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcContractJacksonLabTest`

## 机制主线

- 本章以 `BootWebMvcContractJacksonLabTest` 的 strict media type 用例为证据链：
  - `Content-Type: application/vnd.learning.strict+json`
  - 未知字段出现时，返回 400 且错误形状可控（由异常映射决定）

## 源码与断点

建议断点：
- `MappingJackson2HttpMessageConverter#readInternal`
- `com.fasterxml.jackson.databind.ObjectMapper#readValue`

## 最小可运行实验（Lab）

- Lab：`BootWebMvcContractJacksonLabTest`

## 常见坑与边界

- “严格模式”不是为了报错而报错，而是为了 **让契约在边界处明确失败**，避免吞字段导致的“静默数据丢失”。

## 小结与下一章

- 下一章把解析失败/校验失败/类型不匹配三类错误统一到一套错误契约里，便于调用方处理。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcContractJacksonLabTest`

上一章：[part-04-rest-contract/01-content-negotiation-406-415.md](077-01-content-negotiation-406-415.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-04-rest-contract/03-error-contract-hardening.md](03-error-contract-hardening.md)

<!-- BOOKIFY:END -->
