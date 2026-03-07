# 01. 01 - Actuator 基础与暴露
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕Actuator 基础与暴露展开，主线可以概括为：引入 Actuator → 端点注册与 discover → exposure 决定暴露 → Web 层映射为 HTTP 端点 → 结合安全策略与可观测信号使用。

    阅读时可以先跑 `BootActuatorExposureOverrideLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 Actuator endpoints 暴露健康检查/信息/指标；用 exposure 控制可见范围，并在生产环境结合鉴权与安全边界。

    需要下探源码时，可以从 `org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration` / `org.springframework.boot.actuate.endpoint.annotation.Endpoint` / `org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 00 - Deep Dive Guide（springboot-actuator）](guide-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 90 - Common Pitfalls（springboot-actuator）](appendix-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「01 - Actuator 基础与暴露」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
建议优先运行 `BootActuatorExposureOverrideLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootActuatorExposureOverrideLabTest` / `BootActuatorLabTest`
    - Test file：`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorLabTest.java` / `spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorExposureOverrideLabTest.java`

## 机制主线

本章用最小心智模型把 Actuator 的“端点是否可用”拆成三段式分流：

1. **Registered**：端点是否存在（是否有 endpoint bean）
2. **Exposed**：端点是否暴露到 HTTP（include/exclude/base-path）
3. **Accessible**：端点是否可访问（401/403/404 分流）

## 最小可运行实验（Lab）

- Lab：`BootActuatorExposureOverrideLabTest` / `BootActuatorLabTest`
- 建议命令：`mvn -pl :spring-boot-actuator test`（或在 IDE 直接运行上面的测试类）


## 最小可复现入口
- `BootActuatorLabTest`：`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorLabTest.java`
- `BootActuatorExposureOverrideLabTest`：`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorExposureOverrideLabTest.java`

## 常见坑与边界

### 坑点 1：把 401/403/404 混为一谈，导致排障方向完全错误

访问 `/actuator/env` 失败后，只盯着安全配置或只盯着 exposure 配置，反复试错

三段式分流没有先做：

- 401：通常是认证问题（Authentication）
- 403：通常是鉴权/CSRF 等安全策略问题（Authorization/CSRF）
- 404：可能是没暴露（Exposed 集合不包含它）或路径/base-path 不对

- 404（默认不暴露 env）：`BootActuatorLabTest#envEndpointIsNotExposedByDefault`
- 200（include env 后可访问）：`BootActuatorExposureOverrideLabTest#envEndpointCanBeExposedViaProperties`
- `/actuator` links 作为“暴露事实来源”：`BootActuatorLabTest#actuatorRootListsExposedEndpoints`

先用 `/actuator` + exposure 配置固定“暴露集合”，再根据 401/403 分流到安全层排障

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootActuatorExposureOverrideLabTest` / `BootActuatorLabTest`
- Test file：`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorLabTest.java` / `spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorExposureOverrideLabTest.java`

上一章：[part-00-guide/00-deep-dive-guide.md](guide-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/90-common-pitfalls.md](appendix-common-pitfalls.md)

<!-- BOOKIFY:END -->
