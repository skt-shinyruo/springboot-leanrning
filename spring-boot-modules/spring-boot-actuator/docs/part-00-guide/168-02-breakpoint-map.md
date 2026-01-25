# 第 168 章：02：断点地图（Actuator Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Actuator Debugger Pack）
    - 怎么使用：先跑 `BootActuatorBranchMatrixLabTest` 固化“exposure include 能否生效”的断言，再用断点确认 endpoint 的注册、暴露与 handler mapping 的分支。
    - 原理：Actuator endpoint 注册（discover）→ exposure 配置决定是否暴露 → web handler mapping 映射到 `/actuator/**`。
    - 源码入口：`org.springframework.boot.actuate.endpoint.web.annotation.WebEndpointDiscoverer` / `org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping`
    - 推荐 Lab：`BootActuatorBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 168 章：00 - Deep Dive Guide（springboot-actuator）](168-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 168 章：04：关键分支矩阵（Branch Decision Matrix）](168-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- Actuator 排障核心问题：**endpoint 是否注册**、**是否暴露**、**是否被安全/网关拦截**。
- 推荐证据链：先用测试证明“是否可访问（HTTP 200/404/401）”，再用断点定位分支发生点。

## 运行入口（建议先跑）

- Book Matrix：`BootActuatorBookMatrixLabTest`
- Branch Matrix：`BootActuatorBranchMatrixLabTest`

推荐命令：

- `mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBranchMatrixLabTest test`

## 断点（Endpoint 注册与暴露）

- `org.springframework.boot.actuate.endpoint.web.annotation.WebEndpointDiscoverer#discoverEndpoints`
- `org.springframework.boot.actuate.endpoint.web.EndpointLinksResolver#resolveLinks`
- `org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping#getHandlerInternal`

## Watchpoints

- `management.endpoints.web.exposure.include`（最终值）
- `/actuator` root 的 `_links` 是否包含目标 endpoint
- 请求是否被安全链拦截（若启用 security，优先确认 FilterChain 分支）

## 排障入口（Playbook）

- 常见坑：[`../appendix/170-90-common-pitfalls.md`](../appendix/170-90-common-pitfalls.md)
- 自检：[`../appendix/171-99-self-check.md`](../appendix/171-99-self-check.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`BootActuatorBranchMatrixLabTest`
- Lab：`BootActuatorExposureOverrideLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](168-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/04-branch-decision-matrix.md](168-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

