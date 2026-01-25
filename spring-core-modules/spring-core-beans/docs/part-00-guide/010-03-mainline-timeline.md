# 第 10 章：主线时间线：Spring Core Beans（IoC 容器）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：主线时间线：Spring Core Beans（IoC 容器）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`
    - 推荐 Lab：`SpringCoreBeansContainerLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 9 章：IoC 容器主线（Beans）](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 11 章：00. 深挖指南：把“Bean 三层模型”落到源码与断点](011-00-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

!!! summary
    - 这一模块关注：ApplicationContext/BeanFactory 如何把“定义”变成“对象”，并在过程中提供可插拔的扩展点。
    - 读完你应该能复述：**注册 BeanDefinition → 执行 Post-Processor → 实例化/注入 → 生命周期回调** 这一条主线。
    - 推荐顺序：先读《深挖导读》→ 本章（建立时间线）→ 顺读 Part 01 的主线章节 → 再进入 Part 03/04 深挖内部机制与边界。

!!! example "建议先跑的 Lab（把时间线变成证据）"

    - Lab：`SpringCoreBeansContainerLabTest`

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：主线时间线：Spring Core Beans（IoC 容器） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
- 回到主线：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「主线时间线：Spring Core Beans（IoC 容器）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。
<!-- BOOKLIKE-V2:INTRO:END -->

## 在 Spring 主线中的位置

- 这是整个 Spring 生态的“发动机”：AOP、事务、WebMVC 等最终都要落在 **Bean 的创建、注入与增强** 上。
- 绝大多数“为什么我的 Bean 行为不对”的问题，本质都能映射到：**定义阶段**或**创建阶段**的某个关键分支。

## 主线时间线（推荐顺读：先把容器跑通）

1. 建立 Bean 的心智模型：容器到底在管理什么
   - 阅读：[深挖导读](011-00-deep-dive-guide.md)
   - 主线章节：[01. Bean 心智模型与注册入口](../part-01-ioc-container/020-01-bean-mental-model.md)
2. 把“依赖是怎么被解析与注入的”跑通（按类型/按名称/泛型/候选集）
   - 主线章节：[03. 依赖注入解析](../part-01-ioc-container/014-03-dependency-injection-resolution.md)
3. 把“作用域与生命周期”接起来（尤其是 prototype 的边界）
   - 主线章节：[04. 作用域与 prototype](../part-01-ioc-container/015-04-scope-and-prototype.md)
   - 主线章节：[05. 生命周期与回调](../part-01-ioc-container/016-05-lifecycle-and-callbacks.md)
4. 进入扩展点：Post-Processor 如何改变“定义”与“对象”
   - 主线章节：[06. Post-Processor](../part-01-ioc-container/017-06-post-processors.md)
   - 主线章节：[07. 配置类增强](../part-01-ioc-container/018-07-configuration-enhancement.md)
5. 把 Spring Boot 接上来：自动配置如何把条件装配落到容器里
   - 阅读：[10. 自动配置主线](../part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md)
   - 调试：[11. 调试与观测](../part-02-boot-autoconfig/019-11-debugging-and-observability.md)

## 深挖路线（按需）

- 想理解容器内部“按顺序发生了什么”：从 [12. 容器启动与基础设施处理器](../part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md) 开始，再顺读 13–17。
- 想系统掌握“边界与坑点”（候选集/别名/覆盖/类型匹配/代理时机）：从 [18. Lazy 语义](../part-04-wiring-and-boundaries/023-18-lazy-semantics.md) 开始按目录推进。
- 想看 AOT 与真实世界输入形态：从 [40. AOT 与 Native 概览](../part-05-aot-and-real-world/024-40-aot-and-native-overview.md) 开始按目录推进。

## 排坑与自检

- 常见坑：[90-common-pitfalls.md](../appendix/025-90-common-pitfalls.md)
- 自检：[99-self-check.md](../appendix/026-99-self-check.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「主线时间线：Spring Core Beans（IoC 容器）」的生效时机/顺序/边界；断点/入口：`org.springframework.context.support.AbstractApplicationContext#refresh`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「主线时间线：Spring Core Beans（IoC 容器）」的生效时机/顺序/边界；断点/入口：`org.springframework.beans.factory.support.DefaultListableBeanFactory`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「主线时间线：Spring Core Beans（IoC 容器）」的生效时机/顺序/边界；断点/入口：`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``SpringCoreBeansContainerLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
