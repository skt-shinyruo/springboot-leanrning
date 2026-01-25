# 第 53 章：04：关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Branch Decision Matrix）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在方法边界使用 `@Transactional` 声明事务；理解传播/回滚规则；排障时先确认是否真的走到代理与事务拦截器。
    - 原理：方法调用 → 事务拦截器 → 获取/创建事务（TransactionManager）→ 绑定资源到线程 → 正常提交/异常回滚；传播决定“加入还是新开”。
    - 源码入口：`org.springframework.transaction.interceptor.TransactionInterceptor#invoke` / `org.springframework.transaction.interceptor.TransactionAspectSupport#invokeWithinTransaction` / `org.springframework.transaction.PlatformTransactionManager`
    - 推荐 Lab：`SpringCoreTxRollbackRulesLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 53 章：02：断点地图（Spring Tx Debugger Pack）](053-02-breakpoint-map.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 54 章：01：事务边界：什么情况下“算在一个事务里”](../part-01-transaction-basics/054-01-transaction-boundary.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：04：关键分支矩阵（Branch Decision Matrix） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：在方法边界使用 `@Transactional` 声明事务；理解传播/回滚规则；排障时先确认是否真的走到代理与事务拦截器。
- 回到主线：方法调用 → 事务拦截器 → 获取/创建事务（TransactionManager）→ 绑定资源到线程 → 正常提交/异常回滚；传播决定“加入还是新开”。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「04：关键分支矩阵（Branch Decision Matrix）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。

验证入口（可直接跑）：
```bash
mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxRollbackRulesLabTest test
```
<!-- BOOKLIKE-V2:INTRO:END -->

## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| 默认 rollback 规则 | 抛出 RuntimeException | 默认回滚 | `SpringCoreTxRollbackRulesLabTest` | commit/rollback 路径 |
| checked exception | 抛出 checked exception | 默认不回滚（提交） | `SpringCoreTxRollbackRulesLabTest` | rollbackOnly=false |
| propagation 行为 | REQUIRED/REQUIRES_NEW 等 | 是否新开/挂起事务符合预期 | `SpringCoreTxPropagationMatrixLabTest` | `isNewTransaction` |
| 自调用坑 | 同 bean 内部调用 `@Transactional` 方法 | 绕过代理导致事务不生效 | `SpringCoreTxPitfallsBranchMatrixLabTest` | 调用栈是否进入 proxy |

## 推荐运行命令

- `mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`../appendix/060-90-common-pitfalls.md`](../appendix/060-90-common-pitfalls.md)
- 自检：[`../appendix/061-99-self-check.md`](../appendix/061-99-self-check.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：`org.springframework.transaction.interceptor.TransactionInterceptor#invoke`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：`org.springframework.transaction.interceptor.TransactionAspectSupport#invokeWithinTransaction`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：`org.springframework.transaction.PlatformTransactionManager`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``SpringCoreTxRollbackRulesLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreTxRollbackRulesLabTest` / `SpringCoreTxPropagationMatrixLabTest` / `SpringCoreTxPitfallsBranchMatrixLabTest` / `SpringCoreTxBranchMatrixLabTest`

上一章：[053-01-transaction-interceptor-call-chain.md](053-01-transaction-interceptor-call-chain.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[常见坑](../appendix/060-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
