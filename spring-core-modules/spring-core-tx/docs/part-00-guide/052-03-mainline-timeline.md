# 第 52 章：主线时间线：Spring Core Tx（事务）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：主线时间线：Spring Core Tx（事务）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在方法边界使用 `@Transactional` 声明事务；理解传播/回滚规则；排障时先确认是否真的走到代理与事务拦截器。
    - 原理：方法调用 → 事务拦截器 → 获取/创建事务（TransactionManager）→ 绑定资源到线程 → 正常提交/异常回滚；传播决定“加入还是新开”。
    - 源码入口：`org.springframework.transaction.interceptor.TransactionInterceptor#invoke` / `org.springframework.transaction.interceptor.TransactionAspectSupport#invokeWithinTransaction` / `org.springframework.transaction.PlatformTransactionManager`
    - 推荐 Lab：`SpringCoreTxLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 51 章：事务主线（Tx）](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 53 章：深挖指南（Spring Core Tx）](053-00-deep-dive-guide.md)
<!-- GLOBAL-BOOK-NAV:END -->

!!! summary
    - 这一模块关注：@Transactional 如何通过 AOP 代理把“事务边界”织入方法调用，以及传播/回滚规则如何生效。
    - 读完你应该能复述：**方法调用 → 事务拦截器 → 事务管理器 → 提交/回滚** 这一条主线。
    - 推荐顺序：先读《深挖导读》→ 本章 → Part 01（事务基础）→ Part 02（模板与调试）→ 附录排坑。

!!! example "建议先跑的 Lab（把时间线变成证据）"

    - Lab：`SpringCoreTxLabTest`

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：主线时间线：Spring Core Tx（事务） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在方法边界使用 `@Transactional` 声明事务；理解传播/回滚规则；排障时先确认是否真的走到代理与事务拦截器。
- 回到主线：方法调用 → 事务拦截器 → 获取/创建事务（TransactionManager）→ 绑定资源到线程 → 正常提交/异常回滚；传播决定“加入还是新开”。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「主线时间线：Spring Core Tx（事务）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。
<!-- BOOKLIKE-V2:INTRO:END -->

## 在 Spring 主线中的位置

- 事务依赖 AOP：没有代理就没有“拦截器包裹方法调用”的事务边界。
- 事务的“看起来不对”通常来自三类分支：**没代理上**、**传播/回滚规则与预期不同**、**资源/管理器配置不一致**。

## 主线时间线（建议顺读）

1. 先把“事务边界”这件事讲清楚：哪里开始、哪里结束、谁决定提交/回滚
   - 阅读：[01. 事务边界](../part-01-transaction-basics/054-01-transaction-boundary.md)
2. 再把“@Transactional 为何生效”跑通：代理与拦截器
   - 阅读：[02. @Transactional 代理](../part-01-transaction-basics/055-02-transactional-proxy.md)
3. 回滚规则：哪些异常会回滚、哪些不会（以及怎么验证）
   - 阅读：[03. 回滚规则](../part-01-transaction-basics/056-03-rollback-rules.md)
4. 传播行为：嵌套调用如何决定复用/新建事务
   - 阅读：[04. 传播行为](../part-01-transaction-basics/057-04-propagation.md)
5. 当你不想要注解：TransactionTemplate 的“显式边界”
   - 阅读：[05. TransactionTemplate](../part-02-template-and-debugging/058-05-transaction-template.md)
6. 最后学会调试：先能把“有没有进拦截器、拿到的是什么事务”看见
   - 阅读：[06. 调试](../part-02-template-and-debugging/059-06-debugging.md)

## 排坑与自检

- 常见坑：[90-common-pitfalls.md](../appendix/060-90-common-pitfalls.md)
- 自检：[99-self-check.md](../appendix/061-99-self-check.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「主线时间线：Spring Core Tx（事务）」的生效时机/顺序/边界；断点/入口：`org.springframework.transaction.interceptor.TransactionInterceptor#invoke`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「主线时间线：Spring Core Tx（事务）」的生效时机/顺序/边界；断点/入口：`org.springframework.transaction.interceptor.TransactionAspectSupport#invokeWithinTransaction`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「主线时间线：Spring Core Tx（事务）」的生效时机/顺序/边界；断点/入口：`org.springframework.transaction.PlatformTransactionManager`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``SpringCoreTxLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
