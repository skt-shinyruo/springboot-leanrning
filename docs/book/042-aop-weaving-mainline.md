# 第 42 章：织入主线（LTW/CTW）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：织入主线（LTW/CTW）
    - 怎么使用：当代理覆盖不了 join point（constructor/get/set/call）时，使用 AspectJ LTW/CTW 在类加载期/编译期织入；用可断言实验验证是否生效。
    - 原理：代理 vs 织入：选择 LTW/CTW → 定义切点（execution/call/...）→ weaving 生效取决于 classloader/agent/时机 → 用测试/断点验证。
    - 源码入口：`org.springframework.context.weaving.AspectJWeavingEnabler` / `org.springframework.instrument.classloading.LoadTimeWeaver` / `org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter`
    - 推荐 Lab：`AspectjLtwLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 41 章：99. 自测题：你是否真的理解了 AOP？](../aop/spring-core-aop/appendix/041-99-self-check.md) ｜ 全书目录：[Book TOC](/) ｜ 下一章：[第 43 章：主线时间线：AOP Weaving（织入：LTW/CTW）](../aop/spring-core-aop-weaving/part-00-guide/043-03-mainline-timeline.md)
<!-- GLOBAL-BOOK-NAV:END -->

这一章解决的问题是：**代理做不到的增强怎么办**？当你需要在类加载期/编译期把增强“织进字节码”，AspectJ weaving（LTW/CTW）就会成为另一条主线。

---

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：织入主线（LTW/CTW） —— 当代理覆盖不了 join point（constructor/get/set/call）时，使用 AspectJ LTW/CTW 在类加载期/编译期织入；用可断言实验验证是否生效。
- 回到主线：代理 vs 织入：选择 LTW/CTW → 定义切点（execution/call/...）→ weaving 生效取决于 classloader/agent/时机 → 用测试/断点验证。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「织入主线（LTW/CTW）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。
<!-- BOOKLIKE-V2:INTRO:END -->

## 主线（按时间线顺读）

1. 明确目标：你要拦截的是 `execution` 还是 `call`？constructor/field get/set 还是方法？
2. 选择织入方式：
   - LTW：类加载时织入（类加载器/agent/织入器协作）
   - CTW：编译期织入（构建产物已被织入）
3. 验证织入是否生效：不是“看配置”，而是用可断言证据（测试/断点/可观测输出）
4. 排障主线：classloader、weaver 配置、切点表达式是否匹配、织入时机与目标类是否一致

---

## 深挖入口（模块 docs）

### 进阶入口（排障/关键分支）

- 断点地图：[`docs/aop/spring-core-aop-weaving/part-00-guide/044-02-breakpoint-map.md`](../aop/spring-core-aop-weaving/part-00-guide/044-02-breakpoint-map.md)
- 关键分支矩阵：[`docs/aop/spring-core-aop-weaving/part-00-guide/044-04-branch-decision-matrix.md`](../aop/spring-core-aop-weaving/part-00-guide/044-04-branch-decision-matrix.md)
- 排障 playbook：[`docs/aop/spring-core-aop-weaving/appendix/049-90-common-pitfalls.md`](../aop/spring-core-aop-weaving/appendix/049-90-common-pitfalls.md)
- 自检清单：[`docs/aop/spring-core-aop-weaving/appendix/050-99-self-check.md`](../aop/spring-core-aop-weaving/appendix/050-99-self-check.md)

- 模块目录页：[`docs/aop/spring-core-aop-weaving/README.md`](../aop/spring-core-aop-weaving/README.md)
- 模块主线时间线（含可跑入口）：[`docs/aop/spring-core-aop-weaving/part-00-guide/03-mainline-timeline.md`](../aop/spring-core-aop-weaving/part-00-guide/043-03-mainline-timeline.md)

---

## 本章可跑入口（最小闭环）

- Lab：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwLabTest test`（`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part02_ltw_fundamentals/AspectjLtwLabTest.java`）
- Lab（进阶：Book Matrix）：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjWeavingBookMatrixLabTest test`（`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part02_ltw_fundamentals/AspectjWeavingBookMatrixLabTest.java`）
- Lab（进阶：Branch Matrix - LTW）：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwBranchMatrixLabTest test`（`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part02_ltw_fundamentals/AspectjLtwBranchMatrixLabTest.java`；也可直接 `mvn -q -pl :spring-core-aop-weaving test`）
- Lab（进阶：Branch Matrix - CTW）：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjCtwBranchMatrixLabTest test`（`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part03_ctw_fundamentals/AspectjCtwBranchMatrixLabTest.java`；也可直接 `mvn -q -pl :spring-core-aop-weaving test`）
- Exercise（动手练习，默认 `@Disabled`）：`spring-core-modules/spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part00_guide/SpringCoreAopWeavingExerciseTest.java`

---

## 下一章怎么接

“方法边界增强”最常见的落点是事务：把业务写在一堆方法里，你需要一个可验证的事务边界。

- 下一章：[第 51 章：事务主线（Tx）](051-tx-mainline.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「织入主线（LTW/CTW）」的生效时机/顺序/边界；断点/入口：`org.springframework.context.weaving.AspectJWeavingEnabler`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「织入主线（LTW/CTW）」的生效时机/顺序/边界；断点/入口：`org.springframework.instrument.classloading.LoadTimeWeaver`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「织入主线（LTW/CTW）」的生效时机/顺序/边界；断点/入口：`org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``AspectjLtwLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
