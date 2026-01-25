# 第 171 章：99 - Self Check（springboot-actuator）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Self Check（springboot-actuator）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 Actuator endpoints 暴露健康检查/信息/指标；用 exposure 控制可见范围，并在生产环境结合鉴权与安全边界。
    - 原理：引入 Actuator → 端点注册与 discover → exposure 决定暴露 → Web 层映射为 HTTP 端点 → 结合安全策略与可观测信号使用。
    - 源码入口：`org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration` / `org.springframework.boot.actuate.endpoint.annotation.Endpoint` / `org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties`
    - 推荐 Lab：`BootActuatorExposureOverrideLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 170 章：90 - Common Pitfalls（springboot-actuator）](170-90-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 172 章：Web Client 主线](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

## 从 Book Matrix 进入（主线最小集合）

- `mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBookMatrixLabTest test`

## 从 Branch Matrix 进入（关键分支最小集合）

- `mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBranchMatrixLabTest test`
- 配套资料：[`断点地图`](../part-00-guide/168-02-breakpoint-map.md) / [`关键分支矩阵`](../part-00-guide/168-04-branch-decision-matrix.md)

- 本章主题：**99 - Self Check（springboot-actuator）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootActuatorExposureOverrideLabTest` / `BootActuatorLabTest`

## 机制主线

这一章不是新增概念，而是用“可断言证据”复盘 Actuator 的三段式分流：

1. 端点是否存在（Registered）
2. 端点是否暴露（Exposed）
3. 端点是否可访问（Accessible：401/403/404 的分流）

## 自测题
1. exposure 的 include/exclude 与端点实际可访问性之间是什么关系？
2. 如何快速判断一个配置值来自哪里（哪个 PropertySource）？

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章未显式引用 LabTest，先注入模块默认 LabTest 作为“合规兜底入口”（后续可逐章细化）。
- Lab：`BootActuatorExposureOverrideLabTest` / `BootActuatorLabTest`
- 建议命令：`mvn -pl :spring-boot-actuator test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 对应 Exercise（可运行）

- `BootActuatorExerciseTest`

## 常见坑与边界

### 坑点 1：把 404 当成“端点不存在”，忽略了 exposure 分流

- Symptom：你访问 `/actuator/env` 得到 404，于是以为 env endpoint 没有注册/没生效
- Root Cause：`getResource` 类比：**“有句柄”不等于“可访问”**。对 Actuator 来说，端点是否“存在”与是否“暴露到 HTTP”是两回事
- Verification：
  - 默认不暴露：`BootActuatorLabTest#envEndpointIsNotExposedByDefault`
  - 显式 include 后可访问：`BootActuatorExposureOverrideLabTest#envEndpointCanBeExposedViaProperties`
  - 根路径 links 只列出“暴露端点”：`BootActuatorLabTest#actuatorRootListsExposedEndpoints` / `BootActuatorExposureOverrideLabTest#actuatorRootIncludesEnvLinkWhenExposed`
- Fix：先用 `/actuator` 的 `_links` 与 exposure 配置确认“暴露集合”，再谈安全策略（401/403）

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootActuatorExposureOverrideLabTest` / `BootActuatorLabTest`
- Exercise：`BootActuatorExerciseTest`

上一章：[appendix/90-common-pitfalls.md](170-90-common-pitfalls.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Docs TOC](../README.md)

<!-- BOOKIFY:END -->
