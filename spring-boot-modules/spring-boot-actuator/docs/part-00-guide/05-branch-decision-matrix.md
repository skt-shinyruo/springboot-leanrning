# 05. 关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Branch Decision Matrix）
    - 怎么使用：把 Actuator 的关键分支（是否暴露、root links 是否出现）固化为矩阵表，并给出复现入口与观察点。
    - 原理：exposure 配置决定 endpoint 是否被映射到 web；root links 是最便宜的“是否暴露”的证据链。
    - 源码入口：`WebEndpointDiscoverer` / `WebMvcEndpointHandlerMapping`
    - 推荐 Lab：`BootActuatorBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（Actuator Debugger Pack）](04-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 01 - Actuator 基础与暴露](../part-01-actuator/01-actuator-basics.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**05. 关键分支矩阵（Branch Decision Matrix）**
- 建议入口：优先运行 `BootActuatorBranchMatrixLabTest`，以获得可回归的现象与断言入口。
- 阅读目标：exposure 配置决定 endpoint 是否被映射到 web；root links 是最便宜的“是否暴露”的证据链。
- 源码入口：`WebEndpointDiscoverer` / `WebMvcEndpointHandlerMapping`



## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| 暴露 env endpoint | `management.endpoints.web.exposure.include` 包含 env | `/actuator/env` 可访问 | `BootActuatorExposureOverrideLabTest` | HTTP status / root links |
| root links 证据链 | 暴露成功 | `/actuator` 的 `_links` 包含 `env` | `BootActuatorExposureOverrideLabTest` | `_links/env/href` |
| env 响应结构 | endpoint 可用 | 返回包含 propertySources | `BootActuatorExposureOverrideLabTest` | JSON 字段存在性 |

## 推荐运行命令

- `mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`../appendix/01-common-pitfalls.md`](../appendix/01-common-pitfalls.md)
- 自检：[`../appendix/02-self-check.md`](../appendix/02-self-check.md)

## 小结与下一章

- 小结：exposure 配置决定 endpoint 是否被映射到 web；root links 是最便宜的“是否暴露”的证据链。
- 下一章：[第 169 章：01：Actuator 基础：健康检查与端点暴露](../part-01-actuator/01-actuator-basics.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`BootActuatorBranchMatrixLabTest`
- Lab：`BootActuatorExposureOverrideLabTest`

上一章：[part-00-guide/02-breakpoint-map.md](04-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-actuator/01-actuator-basics.md](../part-01-actuator/01-actuator-basics.md)

<!-- BOOKIFY:END -->

