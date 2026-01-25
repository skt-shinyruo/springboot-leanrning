# 第 195 章：01：AutoConfiguration 调用链（imports → 条件决策 → 产出 bean）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：AutoConfiguration 调用链（imports → 条件决策 → 产出 bean）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootAutoConfigurationLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 195 章：00. 深挖导读](195-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 195 章：02：断点地图](195-02-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「01：AutoConfiguration 调用链（imports → 条件决策 → 产出 bean）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。

验证入口（可直接跑）：
```bash
mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationLabTest test
```
<!-- BOOKLIKE-V2:INTRO:END -->

## 你要能复述的“最短调用链”

### 第 0 段：AutoConfiguration 的候选集合从哪里来？

核心机制是：Spring Boot 会从 classpath 上读取 auto-config 列表（imports），再把它们当作“配置类”交给 Spring 的配置类处理链。

关键入口（按排障优先级）：

1. `AutoConfigurationImportSelector#selectImports`
2. `AutoConfigurationImports#load`（读取 imports 文件并聚合）

### 第 1 段：这些候选什么时候变成 BeanDefinition？

auto-config 最终会进入 Spring 的“配置类解析与注册”主线（你可以把它理解成：配置类也会被解析成 BeanDefinition）。

关键入口：

1. `ConfigurationClassPostProcessor#processConfigBeanDefinitions`
2. `ConfigurationClassParser#parse`
3. `ConfigurationClassBeanDefinitionReader#loadBeanDefinitions`

### 第 2 段：为什么它会/不会注册？

条件装配的核心抓手是：

- `ConditionEvaluator#shouldSkip`

当你看到 `@ConditionalOnProperty/@ConditionalOnClass/@ConditionalOnBean/@ConditionalOnMissingBean` 时，不要把它当成“注解魔法”，把它当成：

> 配置类/`@Bean` 方法在注册前的一次 if 判断。

### 第 3 段：backoff（让位）发生在什么时候？

最常见的 backoff 是：

- `@ConditionalOnMissingBean`

它会在“容器已有/将有某个 bean”时决定不再注册默认 bean，最终表现为：

- 你能拿到用户自定义 bean，但拿不到默认 bean

本模块的证据链入口：

- `BootAutoConfigurationLabTest#userBeanOverridesAutoConfig_backoffOccurs`

## 小结与下一章

- 本章把 imports/condition/backoff 串成了一条可复述调用链；下一章给出“断点/观察点清单”，用于真实项目排障。

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「01：AutoConfiguration 调用链（imports → 条件决策 → 产出 bean）」的生效时机/顺序/边界；断点/入口：（以本章正文“源码；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「01：AutoConfiguration 调用链（imports → 条件决策 → 产出 bean）」的生效时机/顺序/边界；断点/入口：断点”小节为准）；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootAutoConfigurationLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAutoConfigurationLabTest`

上一章：[深挖导读](195-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[断点地图（排障优先）](195-02-breakpoint-map.md)

<!-- BOOKIFY:END -->
