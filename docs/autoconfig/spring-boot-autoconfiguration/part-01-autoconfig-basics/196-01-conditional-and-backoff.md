# 第 196 章：01：条件装配与 backoff（为什么它“有时生效、有时不生效”）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：条件装配与 backoff（为什么它“有时生效、有时不生效”）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootAutoConfigurationLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 195 章：04：关键分支矩阵](../part-00-guide/195-04-branch-decision-matrix.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 197 章：90 - Common Pitfalls（springboot-autoconfiguration）](../appendix/197-90-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「01：条件装配与 backoff（为什么它“有时生效、有时不生效”）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。

验证入口（可直接跑）：
```bash
mvn -q -pl :spring-boot-autoconfiguration -Dtest=BootAutoConfigurationLabTest test
```
<!-- BOOKLIKE-V2:INTRO:END -->

## 1. 条件装配：它不是魔法，是“启动期 if”

你可以把下面这些注解理解成“在注册 BeanDefinition 之前的一次判断”：

- `@ConditionalOnProperty`
- `@ConditionalOnClass`
- `@ConditionalOnBean`

在真实项目里，当你遇到“为什么加了 starter 但功能不出现”，第一反应应该是：

1) 相关 auto-config 有没有被导入（imports）？  
2) 导入后是不是被 condition skip 了？

## 2. backoff：默认配置为什么要让位给用户配置？

核心目标是：让框架提供“默认好用”，但不阻止你自定义。

最常见策略：

- `@ConditionalOnMissingBean`：当用户提供了同类型 bean，就不再创建默认 bean

你应该能用断言证明这件事，而不是凭感觉：

- `BootAutoConfigurationLabTest#userBeanOverridesAutoConfig_backoffOccurs`

## 3. 顺序与叠加：为什么最终注入的是“那个”bean？

当存在多个同类型 bean 时，最终注入对象受以下因素影响：

- 是否有 `@Primary`
- 是否有 `@Qualifier`
- 注入点是否按 name 注入

## 小结与下一章

下一章进入常见坑：把最容易误判的点列出来，并绑定到可跑入口与断点锚点。

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「01：条件装配与 backoff（为什么它“有时生效、有时不生效”）」的生效时机/顺序/边界；断点/入口：（以本章正文“源码；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「01：条件装配与 backoff（为什么它“有时生效、有时不生效”）」的生效时机/顺序/边界；断点/入口：断点”小节为准）；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``BootAutoConfigurationLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootAutoConfigurationLabTest`

上一章：[关键分支矩阵（If/Then 收敛）](../part-00-guide/195-04-branch-decision-matrix.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[常见坑](../appendix/197-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
