# 第 51 章：事务主线（Tx）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：事务主线（Tx）
    - 怎么使用：在方法边界使用 `@Transactional` 声明事务；理解传播/回滚规则；排障时先确认是否真的走到代理与事务拦截器。
    - 原理：方法调用 → 事务拦截器 → 获取/创建事务（TransactionManager）→ 绑定资源到线程 → 正常提交/异常回滚；传播决定“加入还是新开”。
    - 源码入口：`org.springframework.transaction.interceptor.TransactionInterceptor#invoke` / `org.springframework.transaction.interceptor.TransactionAspectSupport#invokeWithinTransaction` / `org.springframework.transaction.PlatformTransactionManager`
    - 推荐 Lab：`SpringCoreTxLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 50 章：99. 自测题：你是否真的理解了 weaving？](../aop/spring-core-aop-weaving/appendix/050-99-self-check.md) ｜ 全书目录：[Book TOC](/) ｜ 下一章：[第 52 章：主线时间线：Spring Core Tx（事务）](../tx/spring-core-tx/part-00-guide/052-03-mainline-timeline.md)
<!-- GLOBAL-BOOK-NAV:END -->

事务这条线解决的问题是：**一次业务操作里，哪些数据库操作应该“同生共死”**？你要能讲清楚：边界在哪、回滚规则是什么、传播行为为什么会让你“以为开启了事务但其实没有”。

---

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：事务主线（Tx） —— 在方法边界使用 `@Transactional` 声明事务；理解传播/回滚规则；排障时先确认是否真的走到代理与事务拦截器。
- 回到主线：方法调用 → 事务拦截器 → 获取/创建事务（TransactionManager）→ 绑定资源到线程 → 正常提交/异常回滚；传播决定“加入还是新开”。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「事务主线（Tx）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。
<!-- BOOKLIKE-V2:INTRO:END -->

## 主线（按时间线顺读）

把事务理解成“在方法边界包一层”：

1. 识别事务边界：通常来自 `@Transactional`
2. 选择事务管理器：`PlatformTransactionManager`（JDBC/JPA 等）
3. 方法调用进入拦截器：开启/加入事务、绑定资源到线程
4. 方法正常返回：提交事务
5. 方法抛异常：按规则回滚（Runtime vs Checked、rollbackFor/noRollbackFor）
6. 传播与嵌套：REQUIRED/REQUIRES_NEW/NESTED 等决定“加入还是新开”

---

## 深挖入口（模块 docs）

### 进阶入口（排障/关键分支）

- 断点地图：[`docs/tx/spring-core-tx/part-00-guide/053-02-breakpoint-map.md`](../tx/spring-core-tx/part-00-guide/053-02-breakpoint-map.md)
- 关键分支矩阵：[`docs/tx/spring-core-tx/part-00-guide/053-04-branch-decision-matrix.md`](../tx/spring-core-tx/part-00-guide/053-04-branch-decision-matrix.md)
- 排障 playbook：[`docs/tx/spring-core-tx/appendix/060-90-common-pitfalls.md`](../tx/spring-core-tx/appendix/060-90-common-pitfalls.md)
- 自检清单：[`docs/tx/spring-core-tx/appendix/061-99-self-check.md`](../tx/spring-core-tx/appendix/061-99-self-check.md)

- 模块目录页：[`docs/tx/spring-core-tx/README.md`](../tx/spring-core-tx/README.md)
- 模块主线时间线（含可跑入口）：[`docs/tx/spring-core-tx/part-00-guide/03-mainline-timeline.md`](../tx/spring-core-tx/part-00-guide/052-03-mainline-timeline.md)

建议先跑的最小闭环：

- `SpringCoreTxLabTest`
- `SpringCoreTxSelfInvocationPitfallLabTest`（自调用绕过事务的最小复现）

---

## 本章可跑入口（最小闭环）

- Lab：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxLabTest test`（`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxLabTest.java`）
- Lab（进阶：Book Matrix）：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBookMatrixLabTest test`（`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxBookMatrixLabTest.java`）
- Lab（进阶：Branch Matrix - 事务主分支）：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxBranchMatrixLabTest test`（`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxBranchMatrixLabTest.java`）
- Lab（进阶：Branch Matrix - 常见坑聚合）：`mvn -q -pl :spring-core-tx -Dtest=SpringCoreTxPitfallsBranchMatrixLabTest test`（`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/appendix/SpringCoreTxPitfallsBranchMatrixLabTest.java`）
- Exercise（动手练习，默认 `@Disabled`）：`spring-core-modules/spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part00_guide/SpringCoreTxExerciseTest.java`

---

## 下一章怎么接

事务与 AOP 是底座，最终很多问题会落到“一个 HTTP 请求进来发生了什么”：我们从 Web MVC 请求主线开始把上层串起来。

- 下一章：[第 62 章：Web MVC 请求主线](062-webmvc-mainline.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「事务主线（Tx）」的生效时机/顺序/边界；断点/入口：`org.springframework.transaction.interceptor.TransactionInterceptor#invoke`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「事务主线（Tx）」的生效时机/顺序/边界；断点/入口：`org.springframework.transaction.interceptor.TransactionAspectSupport#invokeWithinTransaction`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「事务主线（Tx）」的生效时机/顺序/边界；断点/入口：`org.springframework.transaction.PlatformTransactionManager`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``SpringCoreTxLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
