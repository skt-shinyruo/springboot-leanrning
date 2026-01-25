# 第 169 章：01 - Actuator 基础与暴露
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Actuator 基础与暴露
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 Actuator endpoints 暴露健康检查/信息/指标；用 exposure 控制可见范围，并在生产环境结合鉴权与安全边界。
    - 原理：引入 Actuator → 端点注册与 discover → exposure 决定暴露 → Web 层映射为 HTTP 端点 → 结合安全策略与可观测信号使用。
    - 源码入口：`org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration` / `org.springframework.boot.actuate.endpoint.annotation.Endpoint` / `org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties`
    - 推荐 Lab：`BootActuatorExposureOverrideLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 168 章：00 - Deep Dive Guide（springboot-actuator）](../part-00-guide/168-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 170 章：90 - Common Pitfalls（springboot-actuator）](../appendix/170-90-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**01 - Actuator 基础与暴露**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootActuatorExposureOverrideLabTest` / `BootActuatorLabTest`
    - Test file：`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorLabTest.java` / `spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorExposureOverrideLabTest.java`

## 机制主线

本章用最小心智模型把 Actuator 的“端点是否可用”拆成三段式分流：

1. **Registered**：端点是否存在（是否有 endpoint bean）
2. **Exposed**：端点是否暴露到 HTTP（include/exclude/base-path）
3. **Accessible**：端点是否可访问（401/403/404 分流）

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootActuatorExposureOverrideLabTest` / `BootActuatorLabTest`
- 建议命令：`mvn -pl :spring-boot-actuator test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 最小可复现入口
- `BootActuatorLabTest`：`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorLabTest.java`
- `BootActuatorExposureOverrideLabTest`：`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorExposureOverrideLabTest.java`

## 常见坑与边界

### 坑点 1：把 401/403/404 混为一谈，导致排障方向完全错误

- Symptom：访问 `/actuator/env` 失败后，只盯着安全配置或只盯着 exposure 配置，反复试错
- Root Cause：三段式分流没有先做：
  - 401：通常是认证问题（Authentication）
  - 403：通常是鉴权/CSRF 等安全策略问题（Authorization/CSRF）
  - 404：可能是没暴露（Exposed 集合不包含它）或路径/base-path 不对
- Verification：
  - 404（默认不暴露 env）：`BootActuatorLabTest#envEndpointIsNotExposedByDefault`
  - 200（include env 后可访问）：`BootActuatorExposureOverrideLabTest#envEndpointCanBeExposedViaProperties`
  - `/actuator` links 作为“暴露事实来源”：`BootActuatorLabTest#actuatorRootListsExposedEndpoints`
- Fix：先用 `/actuator` + exposure 配置固定“暴露集合”，再根据 401/403 分流到安全层排障

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootActuatorExposureOverrideLabTest` / `BootActuatorLabTest`
- Test file：`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorLabTest.java` / `spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorExposureOverrideLabTest.java`

上一章：[part-00-guide/00-deep-dive-guide.md](../part-00-guide/168-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/90-common-pitfalls.md](../appendix/170-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
