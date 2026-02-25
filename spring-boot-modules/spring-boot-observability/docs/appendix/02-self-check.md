# 02. 99 - Self Check（springboot-observability）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Self Check（springboot-observability）
    - 怎么使用：先跑 Book/Branch Matrix，再用自检问题检查你是否能从“指标”回到“链路节点”，并能用断点验证。
    - 原理：自检目标：你能回答“指标从哪里来、在哪里写入、如何验证、如何避免 tag 失控”。
    - 源码入口：`MeterRegistry` / `DispatcherServlet#doDispatch`
    - 推荐 Lab：`BootObservabilityLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 90 - Common Pitfalls（springboot-observability）](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**02. 99 - Self Check（springboot-observability）**
- 建议入口：优先运行 `BootObservabilityLabTest`，以获得可回归的现象与断言入口。
- 阅读目标：自检目标：你能回答“指标从哪里来、在哪里写入、如何验证、如何避免 tag 失控”。
- 源码入口：`MeterRegistry` / `DispatcherServlet#doDispatch`



## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityBookMatrixLabTest test`

## 从 Branch Matrix 进入（关键分支最小集合）

- `mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityBranchMatrixLabTest test`
- 配套资料：[`断点地图`](../part-00-guide/04-breakpoint-map.md) / [`关键分支矩阵`](../part-00-guide/05-branch-decision-matrix.md)

## 自检问题

1. 你如何用证据链证明 `http.server.requests` 指标存在？
2. 你会在哪里下断点确认“请求链路确实走到了 MVC”？
3. 为什么标签（tags）会带来风险？你如何控制？

## 小结与下一章

- 小结：自检目标：你能回答“指标从哪里来、在哪里写入、如何验证、如何避免 tag 失控”。
- 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootObservabilityLabTest`

上一章：[appendix/90-common-pitfalls.md](01-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
