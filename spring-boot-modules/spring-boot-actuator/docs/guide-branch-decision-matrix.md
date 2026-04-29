# 05. 关键分支矩阵
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕04：关键分支矩阵展开，主线可以概括为：exposure 配置决定 endpoint 是否被映射到 web；root links 是最便宜的“是否暴露”的证据链。

    把 Actuator 的关键分支（是否暴露、root links 是否出现）固化为矩阵表，并给出复现入口与观察点。

    对照入口：`BootActuatorBranchMatrixLabTest`。需要下探源码时，可以从 `WebEndpointDiscoverer` / `WebMvcEndpointHandlerMapping` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（Actuator）](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. 01 - Actuator 基础与暴露](actuator-basics.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

优先运行 `BootActuatorBranchMatrixLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：exposure 配置决定 endpoint 是否被映射到 web；root links 是最便宜的“是否暴露”的证据链。需要下探源码时，可以从 `WebEndpointDiscoverer` / `WebMvcEndpointHandlerMapping` 这些入口切入。


## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点 |
|---|---|---|---|---|
| 暴露 env endpoint | `management.endpoints.web.exposure.include` 包含 env | `/actuator/env` 可访问 | `BootActuatorExposureOverrideLabTest` | HTTP status / root links |
| root links 证据链 | 暴露成功 | `/actuator` 的 `_links` 包含 `env` | `BootActuatorExposureOverrideLabTest` | `_links/env/href` |
| env 响应结构 | endpoint 可用 | 返回包含 propertySources | `BootActuatorExposureOverrideLabTest` | JSON 字段存在性 |

## 运行命令

- `mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](appendix-self-check.md)

## 小结与下一章

exposure 配置决定 endpoint 是否被映射到 web；root links 是最便宜的“是否暴露”的证据链。

下一章见：[01：Actuator 基础：健康检查与端点暴露](actuator-basics.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`BootActuatorBranchMatrixLabTest`
- Lab：`BootActuatorExposureOverrideLabTest`

上一章：[guide-breakpoint-map.md](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[actuator-basics.md](actuator-basics.md)

<!-- BOOKIFY:END -->

