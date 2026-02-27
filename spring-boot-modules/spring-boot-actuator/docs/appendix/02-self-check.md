# 99 自检：Spring Boot Actuator
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（复盘出口）"

    这页不新增概念，只负责把 Actuator 的关键分流“跑成事实 → 对照机制 → 自检复盘”。

    - 主线入口：`BootActuatorBookMatrixLabTest`
    - 分支入口：`BootActuatorBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

## 这页怎么用（建议 15–30 分钟完成一次闭环）

- 先跑入口：用 Book/Branch Matrix 把现象固定为断言（不要只背结论）。
- 再走证据链：对照断点地图/分支矩阵，把“404/401/403”拆成可验证的子问题。
- 最后做题：每题都要能指回一个测试方法或关键类/配置项。

## 先跑入口（把现象跑成事实）

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：`mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBranchMatrixLabTest test`

## 证据链导航（排障加速）

- 模块目录（Docs TOC）：[`../README.md`](../README.md)
- 断点地图：[`../part-00-guide/04-breakpoint-map.md`](../part-00-guide/04-breakpoint-map.md)
- 关键分支矩阵：[`../part-00-guide/05-branch-decision-matrix.md`](../part-00-guide/05-branch-decision-matrix.md)
- 常见坑清单（只做索引，不在本页重复）：[`01-common-pitfalls.md`](01-common-pitfalls.md)

## 自检题（每题都能落到 tests）

1. `/actuator/env` 默认返回 404：这意味着“端点未注册”还是“端点未暴露”？用哪两条证据把它说清楚？
   - 证据入口：`BootActuatorLabTest#envEndpointIsNotExposedByDefault` + `BootActuatorLabTest#actuatorRootListsExposedEndpoints`
2. 如何证明 `management.endpoints.web.exposure.include=...` 会改变“根路径 `/actuator` 的 `_links` 集合”？
   - 证据入口：`BootActuatorExposureOverrideLabTest#actuatorRootIncludesEnvLinkWhenExposed`
3. health 输出里的 `components.learning` 来自哪里？它的 `details` 是如何被写进去的？
   - 证据入口：`BootActuatorLabTest#healthIncludesCustomIndicator` + `BootActuatorLabTest#learningIndicatorHasExpectedDetailsHint`
4. `/actuator/info` 的内容来自哪里？能指出“配置 → 端点输出”的最短链路吗？
   - 证据入口：`BootActuatorLabTest#infoEndpointContainsConfiguredInfoProperties`
5. 为什么 health 的 HTTP 状态码通常是 200，但 JSON 的 `status` 仍然有语义？如何解释“HTTP 与业务状态分离”的边界？
   - 证据入口：`BootActuatorLabTest#healthReturnsHttp200WhenUp`
6. 如何验证 health 的响应确实是 JSON（而不是默认字符串/HTML）？
   - 证据入口：`BootActuatorLabTest#healthResponseIsJson`
7. `env` 端点暴露后，它的响应结构里至少包含什么“可用于排障的材料”？
   - 证据入口：`BootActuatorExposureOverrideLabTest#envResponseContainsPropertySources`
8. 给一个线上需求：只对外暴露 health/info，但希望内部排障时能访问 env。如何设计“暴露策略 + 最小安全边界”，并写出一条可回归的验证路径？
   - 对照：[`01-common-pitfalls.md`](01-common-pitfalls.md)

## 退出条件（完成标准）

- 能在不看文档的情况下复述：Registered / Exposed / Accessible 三段式分流，并用 1–2 个测试把它固定下来。
- 能用 `_links` + exposure 配置解释“为什么有些端点看起来不存在”（其实是不暴露）。
- 能把“端点暴露”与“安全边界”分开讨论：先决定暴露集合，再决定鉴权策略。

## 下一步（回到主线）

- 推荐顺读：[`../part-00-guide/02-deep-dive-guide.md`](../part-00-guide/02-deep-dive-guide.md)
- 看完后回到这页再做一遍：[`01-common-pitfalls.md`](01-common-pitfalls.md)

