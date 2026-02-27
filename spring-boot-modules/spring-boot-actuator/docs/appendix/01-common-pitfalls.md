# 01. 90 - Common Pitfalls（springboot-actuator）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Common Pitfalls（springboot-actuator）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 Actuator endpoints 暴露健康检查/信息/指标；用 exposure 控制可见范围，并在生产环境结合鉴权与安全边界。
    - 原理：引入 Actuator → 端点注册与 discover → exposure 决定暴露 → Web 层映射为 HTTP 端点 → 结合安全策略与可观测信号使用。
    - 源码入口：`org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration` / `org.springframework.boot.actuate.endpoint.annotation.Endpoint` / `org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties`
    - 推荐 Lab：`BootActuatorExposureOverrideLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 01 - Actuator 基础与暴露](../part-01-actuator/01-actuator-basics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 99 - Self Check（springboot-actuator）](02-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

### 排障模板（统一结构）

当遇到“行为不符合预期 / 入口跑不通 / 断点不命中”时，建议按下面 6 步收敛（每一步都尽量可复现、可对照、可验证）：

1. 症状（Symptoms）：看到的错误/现象（保留关键错误信息）
2. 复现（Repro）：用最小可运行入口稳定复现（优先用测试入口，而不是手工点 UI）
   - Book Matrix：`mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBookMatrixLabTest test`
   - Branch Matrix：`mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBranchMatrixLabTest test`
3. 证据（Evidence）：对照断点地图，把断点/Watchpoints/关键日志收齐：[04-breakpoint-map.md](../part-00-guide/04-breakpoint-map.md)
4. 决策（Decision）：对照关键分支矩阵，把 If/Then 选路写清楚：[05-branch-decision-matrix.md](../part-00-guide/05-branch-decision-matrix.md)
5. 修复（Fix）：给出最小修复动作（配置/代码/调用方式）
6. 验证（Verify）：复跑入口 + 对照自检清单：[02-self-check.md](02-self-check.md)

- 本章主题：**01. 90 - Common Pitfalls（springboot-actuator）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，应当能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootActuatorExposureOverrideLabTest` / `BootActuatorLabTest`

## 机制主线

这页不展开完整机制主线；其定位更接近排障备忘录：把常见分支与可复现入口列出来，便于回到 tests 验证。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootActuatorExposureOverrideLabTest` / `BootActuatorLabTest`
- 建议命令：`mvn -pl :spring-boot-actuator test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界

这一模块的排障最怕把现象混在一起：**端点存在 / 端点暴露 / 端点可访问** 是三件不同的事（见 Deep Dive Guide 的“三段式分流”）。

## 坑 1：把 404 当成“端点不存在”，忽略了 exposure 分流

- 会看到：访问 `/actuator/env` 得到 404，于是以为 env endpoint 没注册/没生效。
- Root Cause：端点存在 ≠ 端点暴露到 HTTP；默认 exposure 并不会把所有端点都映射出来。
- Verification：
  - 默认不暴露：`BootActuatorLabTest#envEndpointIsNotExposedByDefault`
  - 显式 include 后可访问：`BootActuatorExposureOverrideLabTest#envEndpointCanBeExposedViaProperties`
  - 根路径 links 只列出暴露端点：`BootActuatorLabTest#actuatorRootListsExposedEndpoints`
  - 暴露后 links 才会出现：`BootActuatorExposureOverrideLabTest#actuatorRootIncludesEnvLinkWhenExposed`
- Fix：先看 `/actuator` 的 `_links`（它只列“暴露端点”），再核对 include/exclude/base-path，而不是上来就怀疑“端点没注册”。

## 坑 2：环境差异把带偏（profile/配置来源）

- 会看到：本地可以、线上不行；或者 IDE 里 OK、命令行不行。
- Root Cause：Actuator 的行为高度依赖配置来源与覆盖顺序（profile/环境变量/外部配置）。
- Fix：先确认“当前生效的配置值是什么、来自哪个 PropertySource”，再讨论“配置写没写对”。

## 坑 3：暴露端点不等于允许匿名访问（401/403）

- 会看到：端点已暴露，但访问返回 401/403。
- Root Cause：exposure 决定“有没有路由”，安全策略决定“能不能访问”。401/403/404 三种状态码要先分流清楚。
- Fix：先把 401/403/404 分清：404 多半是没暴露/路径不对，401/403 才是安全边界（鉴权/CSRF/网络隔离等）。

## 对应 Lab（可运行）

- `BootActuatorLabTest`
- `BootActuatorExposureOverrideLabTest`

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootActuatorExposureOverrideLabTest` / `BootActuatorLabTest`

上一章：[part-01-actuator/01-actuator-basics.md](../part-01-actuator/01-actuator-basics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/99-self-check.md](02-self-check.md)

<!-- BOOKIFY:END -->
