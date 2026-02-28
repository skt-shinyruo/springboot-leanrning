# 01. 主线时间线：Spring Core AOP
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕主线时间线：Spring Core AOP展开，主线可以概括为：目标 Bean → `AbstractAutoProxyCreator` 判断 → 生成代理（JDK/CGLIB）→ advisor/interceptor 链 → `proceed()` 形成嵌套调用。

    先运行 `SpringCoreAopLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过切点表达式与通知声明横切意图；在 Spring 中多数能力（Tx/Cache/Validation/Method Security）都以代理方式织入。

    需要下探源码时，可以从 `org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization` / `org.springframework.aop.framework.ProxyFactory` / `org.springframework.aop.framework.ReflectiveMethodInvocation#proceed` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 27 章：AOP/代理主线](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 深挖指南：把“代理是怎么来的、advice 链怎么跑”落到源码与断点](02-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

!!! summary
    - 这一模块关注：AOP 在 Spring 中如何以“代理”的方式织入横切逻辑，以及 AutoProxy 这条主线如何工作。
    - 读完应当能复述：**目标 Bean → AutoProxyCreator 判断 → 生成代理 → 代理链执行** 这一条主线。
    - 推荐顺序：先读《深挖导读》→ 本章 → Part 01（代理基础）→ Part 02（自动代理与切点）→ Part 03（多层代理叠加）。

!!! example "先运行的 Lab（把时间线变成证据）"

    - Lab：`SpringCoreAopLabTest`

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：主线时间线：Spring Core AOP —— 先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过切点表达式与通知声明横切意图；在 Spring 中多数能力（Tx/Cache/Validation/Method Security）都以代理方式织入。
- 回到主线：目标 Bean → `AbstractAutoProxyCreator` 判断 → 生成代理（JDK/CGLIB）→ advisor/interceptor 链 → `proceed()` 形成嵌套调用。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

本章是「第 28 章：主线时间线：Spring Core AOP」的路线图：先给出主线顺序与关键分支，再把每一段落到可运行入口。
先运行 `SpringCoreAopLabTest` 作为主线证据，再回到正文理解“为什么章节按这个顺序组织”。

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「主线时间线：Spring Core AOP」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。
<!-- BOOKLIKE-V2:INTRO:END -->

## 在 Spring 主线中的位置

- AOP 基于 Beans 的创建过程：**BeanPostProcessor 在创建阶段“包一层代理”** 是主线。
- 事务（@Transactional）、异步（@Async）、方法校验等很多能力，本质都依赖这条“代理主线”。

## 主线时间线（建议顺读）

1. 先把“代理 = AOP”的心智模型建立起来
   - 阅读：[01. 代理心智模型](../part-01-proxy-fundamentals/01-aop-proxy-mental-model.md)
2. 选择代理技术：JDK vs CGLIB（以及它们决定的边界）
   - 阅读：[02. JDK vs CGLIB](../part-01-proxy-fundamentals/02-jdk-vs-cglib.md)
3. 把最常见的坑先打掉：self-invocation（自调用）为什么不生效
   - 阅读：[03. self-invocation](../part-01-proxy-fundamentals/03-self-invocation.md)
4. 理解“哪些情况不能被代理”（final 等限制）与临时兜底方案（exposeProxy）
   - 阅读：[04. final 与代理限制](../part-01-proxy-fundamentals/04-final-and-proxy-limits.md)
   - 阅读：[05. exposeProxy](../part-01-proxy-fundamentals/05-expose-proxy.md)
5. 学会调试代理：先能把代理链看清楚
   - 阅读：[06. 调试代理](../part-01-proxy-fundamentals/06-debugging.md)
6. 进入主线：AutoProxyCreator 是怎么决定“要不要代理”的
   - 阅读：[07. AutoProxyCreator 主线](../part-02-autoproxy-and-pointcuts/01-autoproxy-creator-mainline.md)
7. 再进入“选择切点”的系统：表达式与匹配规则
   - 阅读：[08. 切点表达式系统](../part-02-autoproxy-and-pointcuts/02-pointcut-expression-system.md)
8. 最后处理真实世界：多个代理/多个增强如何叠加与排查
   - 阅读：[09. 多层代理叠加](../part-03-proxy-stacking/01-multi-proxy-stacking.md)
   - 阅读：[10. 叠加排障手册](../part-03-proxy-stacking/02-real-world-stacking-playbook.md)

## 排坑与自检

- 常见坑：[90-common-pitfalls.md](../appendix/01-common-pitfalls.md)
- 自检：[99-self-check.md](../appendix/02-self-check.md)

## 证据链（如何验证理解成立）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「主线时间线：Spring Core AOP」的生效时机/顺序/边界；断点/入口：`org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization`；断言：能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「主线时间线：Spring Core AOP」的生效时机/顺序/边界；断点/入口：`org.springframework.aop.framework.ProxyFactory`；断言：能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「主线时间线：Spring Core AOP」的生效时机/顺序/边界；断点/入口：`org.springframework.aop.framework.ReflectiveMethodInvocation#proceed`；断言：能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``SpringCoreAopLabTest`` 后，把上述观察点逐条对照，写出 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
