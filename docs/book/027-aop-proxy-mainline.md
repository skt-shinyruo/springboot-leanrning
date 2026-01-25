# 第 27 章：AOP/代理主线
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：AOP/代理主线
    - 怎么使用：本页为索引/工具页：按页面提示找到入口（章节/Lab/断点地图），再回到主线章节顺读。
    - 原理：本页不讲机制原理，负责把“入口与路径”整理成可检索的导航。
    - 源码入口：N/A（本页为索引/工具页）
    - 推荐 Lab：N/A
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 26 章：99. 自测题：你是否真的理解了？](../beans/spring-core-beans/appendix/026-99-self-check.md) ｜ 全书目录：[Book TOC](/) ｜ 下一章：[第 28 章：主线时间线：Spring Core AOP](../aop/spring-core-aop/part-00-guide/028-03-mainline-timeline.md)
<!-- GLOBAL-BOOK-NAV:END -->

这一章解决的问题是：**为什么你没写任何“拦截器代码”，却能获得事务/缓存/安全/审计**？答案通常不是魔法，而是代理：在“方法调用边界”插入一段可观察、可调试、可组合的增强逻辑。

---

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：AOP/代理主线 —— 本页为索引/工具页：按页面提示找到入口（章节/Lab/断点地图），再回到主线章节顺读。
- 回到主线：本页不讲机制原理，负责把“入口与路径”整理成可检索的导航。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「AOP/代理主线」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 建议先带着问题顺读一遍正文，再按证据链回到源码/断点验证。
<!-- BOOKLIKE-V2:INTRO:END -->

## 主线（按时间线顺读）

1. 你声明切面意图：切点（pointcut）+ 通知（advice）
2. 容器在创建 Bean 时识别“需要增强”的目标（典型是 AutoProxyCreator）
3. 选择代理策略：JDK 动态代理 or CGLIB（final/接口/可见性等边界）
4. 调用发生：进入代理 → 组装拦截链（advisors）→ `proceed()` 形成嵌套
5. 常见坑：自调用绕过代理、多个代理叠加顺序、`@Order` 与优先级

---

## 深挖入口（模块 docs）

### 进阶入口（排障/关键分支）

- 断点地图：[`docs/aop/spring-core-aop/part-00-guide/029-02-breakpoint-map.md`](../aop/spring-core-aop/part-00-guide/029-02-breakpoint-map.md)
- 关键分支矩阵：[`docs/aop/spring-core-aop/part-00-guide/029-04-branch-decision-matrix.md`](../aop/spring-core-aop/part-00-guide/029-04-branch-decision-matrix.md)
- 排障 playbook：[`docs/aop/spring-core-aop/appendix/040-90-common-pitfalls.md`](../aop/spring-core-aop/appendix/040-90-common-pitfalls.md)
- 自检清单：[`docs/aop/spring-core-aop/appendix/041-99-self-check.md`](../aop/spring-core-aop/appendix/041-99-self-check.md)

- 模块目录页：[`docs/aop/spring-core-aop/README.md`](../aop/spring-core-aop/README.md)
- 模块主线时间线（含可跑入口）：[`docs/aop/spring-core-aop/part-00-guide/03-mainline-timeline.md`](../aop/spring-core-aop/part-00-guide/028-03-mainline-timeline.md)

建议先跑的最小闭环：

- `SpringCoreAopLabTest`（最小 advice 闭环 + 自调用陷阱）

---

## 本章可跑入口（最小闭环）

- Lab：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopLabTest test`（`spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopLabTest.java`）
- Lab（进阶：Book Matrix）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopBookMatrixLabTest test`（`spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopBookMatrixLabTest.java`）
- Lab（进阶：Branch Matrix - Proxy 基础）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopProxyBranchMatrixLabTest test`（`spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part01_proxy_fundamentals/SpringCoreAopProxyBranchMatrixLabTest.java`）
- Lab（进阶：Branch Matrix - AutoProxy）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopAutoProxyBranchMatrixLabTest test`（`spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part02_autoproxy_and_pointcuts/SpringCoreAopAutoProxyBranchMatrixLabTest.java`）
- Lab（进阶：Branch Matrix - 多代理叠加）：`mvn -q -pl :spring-core-aop -Dtest=SpringCoreAopStackingBranchMatrixLabTest test`（`spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part03_proxy_stacking/SpringCoreAopStackingBranchMatrixLabTest.java`）
- Exercise（动手练习，默认 `@Disabled`）：`spring-core-modules/spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part00_guide/SpringCoreAopExerciseTest.java`

---

## 下一章怎么接

当“代理”解决不了你的需求（例如拦截 constructor/field access），你需要织入（weaving）这条线。

- 下一章：[第 42 章：织入主线（LTW/CTW）](042-aop-weaving-mainline.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「AOP/代理主线」的生效时机/顺序/边界；断点/入口：N；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「AOP/代理主线」的生效时机/顺序/边界；断点/入口：A（本页为索引；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「AOP/代理主线」的生效时机/顺序/边界；断点/入口：工具页）；断言：你能解释“为什么此处生效/为什么此处不生效”。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
