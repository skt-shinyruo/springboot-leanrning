# 第 9 章：IoC 容器主线（Beans）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：IoC 容器主线（Beans）
    - 怎么使用：本页为索引/工具页：按页面提示找到入口（章节/Lab/断点地图），再回到主线章节顺读。
    - 原理：本页不讲机制原理，负责把“入口与路径”整理成可检索的导航。
    - 源码入口：N/A（本页为索引/工具页）
    - 推荐 Lab：N/A
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 8 章：99 - Self Check（spring-boot-basics）](../basics/spring-boot-basics/appendix/008-99-self-check.md) ｜ 全书目录：[Book TOC](/) ｜ 下一章：[第 10 章：主线时间线：Spring Core Beans（IoC 容器）](../beans/spring-core-beans/part-00-guide/010-03-mainline-timeline.md)
<!-- GLOBAL-BOOK-NAV:END -->

如果你把 Spring Boot 当成“启动一个应用”，那 **IoC 容器**就是“把应用装配成一个可运行系统”的核心：它决定了 Bean 从哪里来、什么时候创建、怎么注入、有哪些扩展点、为什么会出现各种“看起来很诡异”的边界行为。

---

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：IoC 容器主线（Beans） —— 本页为索引/工具页：按页面提示找到入口（章节/Lab/断点地图），再回到主线章节顺读。
- 回到主线：本页不讲机制原理，负责把“入口与路径”整理成可检索的导航。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「IoC 容器主线（Beans）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 建议先带着问题顺读一遍正文，再按证据链回到源码/断点验证。
<!-- BOOKLIKE-V2:INTRO:END -->

## 主线（按时间线顺读）

你可以把容器的主线拆成两个层次：**定义层**与**实例层**。

1. 定义进入容器：扫描/注册/导入，把 `BeanDefinition` 收集起来
2. 定义被“加工”：`BeanFactoryPostProcessor`（BFPP）阶段改定义/改注册表
3. 实例被创建：实例化 → 填充属性（依赖注入）→ 初始化回调
4. 实例被“增强”：`BeanPostProcessor`（BPP）阶段包一层代理/注入额外能力
5. 生命周期与销毁：singleton/prototype 差异、destroy 语义、循环依赖与 early reference

---

## 深挖入口（模块 docs）

### 进阶入口（排障/关键分支）

- 断点地图：[`docs/beans/spring-core-beans/part-00-guide/013-02-breakpoint-map.md`](../beans/spring-core-beans/part-00-guide/013-02-breakpoint-map.md)
- 关键分支矩阵：[`docs/beans/spring-core-beans/part-00-guide/011-04-branch-decision-matrix.md`](../beans/spring-core-beans/part-00-guide/011-04-branch-decision-matrix.md)
- 排障 playbook：[`docs/beans/spring-core-beans/appendix/025-90-common-pitfalls.md`](../beans/spring-core-beans/appendix/025-90-common-pitfalls.md)
- 自检清单：[`docs/beans/spring-core-beans/appendix/026-99-self-check.md`](../beans/spring-core-beans/appendix/026-99-self-check.md)

- 模块目录页（超大模块，建议按入口顺读）：[`docs/beans/spring-core-beans/README.md`](../beans/spring-core-beans/README.md)
- 模块主线时间线（含可跑入口）：[`docs/beans/spring-core-beans/part-00-guide/03-mainline-timeline.md`](../beans/spring-core-beans/part-00-guide/010-03-mainline-timeline.md)

建议先跑的最小闭环（从“看见容器”开始）：

- `SpringCoreBeansLabTest` / `SpringCoreBeansContainerLabTest`

---

## 本章可跑入口（最小闭环）

- Lab：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansContainerLabTest test`（`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`）
- Lab（进阶：Book Matrix）：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBookMatrixLabTest test`（`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansBookMatrixLabTest.java`）
- Lab（进阶：Branch Matrix - IoC 分支）：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansIocBranchMatrixLabTest test`（`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansIocBranchMatrixLabTest.java`）
- Lab（进阶：Branch Matrix - 内部机制分支）：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansInternalsBranchMatrixLabTest test`（`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansInternalsBranchMatrixLabTest.java`）
- Exercise（动手练习，默认 `@Disabled`）：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part00_guide/SpringCoreBeansExerciseTest.java`

---

## 下一章怎么接

容器能把对象装配起来，但很多“横切能力”（事务/缓存/安全/日志）需要在方法调用边界做增强：这就是 AOP/代理登场的地方。

- 下一章：[第 27 章：AOP/代理主线](027-aop-proxy-mainline.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「IoC 容器主线（Beans）」的生效时机/顺序/边界；断点/入口：N；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「IoC 容器主线（Beans）」的生效时机/顺序/边界；断点/入口：A（本页为索引；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「IoC 容器主线（Beans）」的生效时机/顺序/边界；断点/入口：工具页）；断言：你能解释“为什么此处生效/为什么此处不生效”。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
