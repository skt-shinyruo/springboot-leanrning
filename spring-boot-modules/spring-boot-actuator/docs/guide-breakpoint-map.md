# 04. 断点地图（Actuator）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕02：断点地图（Actuator）展开，主线可以概括为：Actuator endpoint 注册（discover）→ exposure 配置决定是否暴露 → web handler mapping 映射到 `/actuator/**`。

    先跑 `BootActuatorBranchMatrixLabTest` 固化“exposure include 能否生效”的断言，再用断点确认 endpoint 的注册、暴露与 handler mapping 的分支。

    需要下探源码时，可以从 `org.springframework.boot.actuate.endpoint.web.annotation.WebEndpointDiscoverer` / `org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[深挖导读：Spring Boot Actuator](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[05. 关键分支矩阵](guide-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

- Actuator 排障核心问题：**endpoint 是否注册**、**是否暴露**、**是否被安全/网关拦截**。
- 证据链：先用测试证明“是否可访问（HTTP 200/404/401）”，再用断点定位分支发生点。

## 运行入口（先运行）

- Book Matrix：`BootActuatorBookMatrixLabTest`
- Branch Matrix：`BootActuatorBranchMatrixLabTest`

运行命令：

- `mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBranchMatrixLabTest test`

## 断点（Endpoint 注册与暴露）

- `org.springframework.boot.actuate.endpoint.web.annotation.WebEndpointDiscoverer#discoverEndpoints`
- `org.springframework.boot.actuate.endpoint.web.EndpointLinksResolver#resolveLinks`
- `org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping#getHandlerInternal`

## 观察点

- `management.endpoints.web.exposure.include`（最终值）
- `/actuator` root 的 `_links` 是否包含目标 endpoint
- 请求是否被安全链拦截（若启用 security，优先确认 FilterChain 分支）

## 排障入口（Playbook）

- 常见坑：[`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](appendix-self-check.md)

## 小结与下一章

Actuator endpoint 注册（discover）→ exposure 配置决定是否暴露 → web handler mapping 映射到 `/actuator/**`。

下一章见：[04：关键分支矩阵](guide-branch-decision-matrix.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`BootActuatorBranchMatrixLabTest`
- Lab：`BootActuatorExposureOverrideLabTest`

上一章：[guide-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[guide-branch-decision-matrix.md](guide-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

