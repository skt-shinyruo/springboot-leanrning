# 第 204 章：03：主线时间线：springboot-observability
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：主线时间线：springboot-observability
    - 怎么使用：本页是导航页。建议先跑 `BootObservabilityLabTest`，把“请求后出现 http.server.requests”固化成断言，再按调用链定位到关键入口。
    - 原理：观测信号不是事后补丁，而是请求链路的一部分：FilterChain/MVC/Actuator 会在合适的阶段创建 observation 与 meter 记录。
    - 源码入口：`io.micrometer.core.instrument.MeterRegistry` / `io.micrometer.observation.ObservationRegistry` /（MVC）`DispatcherServlet#doDispatch`
    - 推荐 Lab：`BootObservabilityLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 205 章：00. 深挖导读](205-00-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :spring-boot-observability -Dtest=BootObservabilityBookMatrixLabTest test`

## 机制主线（你要建立的叙事）

1. 一次 HTTP 请求经过哪些阶段（FilterChain → MVC）
2. 观测对象在哪里创建/结束（Observation）
3. 指标在哪里落地（MeterRegistry）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootObservabilityLabTest`

上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/00-deep-dive-guide.md](205-00-deep-dive-guide.md)

<!-- BOOKIFY:END -->
