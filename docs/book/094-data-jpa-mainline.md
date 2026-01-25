# 第 94 章：Data JPA 主线
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Data JPA 主线
    - 怎么使用：本页为索引/工具页：按页面提示找到入口（章节/Lab/断点地图），再回到主线章节顺读。
    - 原理：本页不讲机制原理，负责把“入口与路径”整理成可检索的导航。
    - 源码入口：N/A（本页为索引/工具页）
    - 推荐 Lab：N/A
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 93 章：99 - Self Check（spring-boot-security）](../security/spring-boot-security/appendix/093-99-self-check.md) ｜ 全书目录：[Book TOC](/) ｜ 下一章：[第 95 章：主线时间线：Spring Boot Data JPA](../data-jpa/spring-boot-data-jpa/part-00-guide/095-03-mainline-timeline.md)
<!-- GLOBAL-BOOK-NAV:END -->

你会在真实项目里频繁遇到三类“看起来像魔法”的现象：

- 你只写了 Repository 接口，却能 CRUD；
- 同一个实体查两次不一定打两次 SQL；
- N+1 往往不是“突然出现”，而是“你没意识到它正在发生”。

本章的目标不是背 API，而是把 Data JPA 的行为压缩成一条能复现、能调试、能解释的主线：

**Repository 代理 → EntityManager/持久化上下文 → 事务边界 → SQL 发出时机（flush）**。

---

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：Data JPA 主线 —— 本页为索引/工具页：按页面提示找到入口（章节/Lab/断点地图），再回到主线章节顺读。
- 回到主线：本页不讲机制原理，负责把“入口与路径”整理成可检索的导航。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「Data JPA 主线」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 建议先带着问题顺读一遍正文，再按证据链回到源码/断点验证。
<!-- BOOKLIKE-V2:INTRO:END -->

## 你将学到什么（本章目标）

读完本章，你应该能做到：

1. 解释 Repository 接口如何变成可运行对象（代理 + 实现策略）
2. 画出 Persistence Context 的“一级缓存/identity map”心智模型，并能解释“为什么看起来没打 SQL”
3. 说清 flush/脏检查/事务提交之间的关系：什么时候会发 SQL、为什么“没 save 也 update”
4. 知道 N+1 通常在什么触发点出现，以及常见的抑制手段（fetch join / EntityGraph 等）

---

## 主线（按时间线顺读）

把 Data JPA 当成“在事务边界里管理实体状态的系统”，主线如下：

1. Repository 代理生成：接口 → 代理 → 运行期实现（CRUD / derived query / 自定义实现）
2. 获取与管理实体：EntityManager 维护 Persistence Context（managed entity 的身份与状态）
3. 写入语义：修改 managed entity 并不立刻发 SQL，SQL 往往在 flush/commit 发生（也可能因为查询触发自动 flush）
4. 读取语义：同一事务内重复读取可能命中一级缓存，因此你会得到“看起来没打 SQL”的假象
5. fetching 与边界：lazy association、`getReferenceById`、fetch join / EntityGraph 决定 N+1 的触发条件与可见性

---

## 读书式的“证据链”：你该观察什么

建议你在跑 Lab 时刻意观察三件事（这会让你之后排障更快）：

- **SQL 是什么时候发出的**：是调用 repository 时、还是事务结束前、还是查询前的自动 flush？
- **同一个 entity 的“身份”是否稳定**：同一事务内两次查询拿到的是不是同一个 Java 对象（`==`）？
- **N+1 的触发点在哪里**：是遍历集合时触发 lazy load，还是序列化/日志打印时触发？

这三点一旦跑过一次，就会从“概念”变成“经验”，而不是靠背诵。

---

## 深挖入口（模块 docs）

### 进阶入口（排障/关键分支）

- 断点地图：[`docs/data-jpa/spring-boot-data-jpa/part-00-guide/096-02-breakpoint-map.md`](../data-jpa/spring-boot-data-jpa/part-00-guide/096-02-breakpoint-map.md)
- 关键分支矩阵：[`docs/data-jpa/spring-boot-data-jpa/part-00-guide/096-04-branch-decision-matrix.md`](../data-jpa/spring-boot-data-jpa/part-00-guide/096-04-branch-decision-matrix.md)
- 排障 playbook：[`docs/data-jpa/spring-boot-data-jpa/appendix/104-90-common-pitfalls.md`](../data-jpa/spring-boot-data-jpa/appendix/104-90-common-pitfalls.md)
- 自检清单：[`docs/data-jpa/spring-boot-data-jpa/appendix/105-99-self-check.md`](../data-jpa/spring-boot-data-jpa/appendix/105-99-self-check.md)

- 模块目录页：[`docs/data-jpa/spring-boot-data-jpa/README.md`](../data-jpa/spring-boot-data-jpa/README.md)
- 模块主线时间线（含可跑入口）：[`docs/data-jpa/spring-boot-data-jpa/part-00-guide/03-mainline-timeline.md`](../data-jpa/spring-boot-data-jpa/part-00-guide/095-03-mainline-timeline.md)

建议先跑的最小闭环：

- `BootDataJpaLabTest`

---

## 本章可跑入口（最小闭环）

- Lab：`mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaLabTest test`（`spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaLabTest.java`）
- Lab（进阶：Book Matrix）：`mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBookMatrixLabTest test`（`spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaBookMatrixLabTest.java`）
- Lab（进阶：Branch Matrix）：`mvn -q -pl :spring-boot-data-jpa -Dtest=BootDataJpaBranchMatrixLabTest test`（`spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaBranchMatrixLabTest.java`）
- Exercise（动手练习，默认 `@Disabled`）：`spring-boot-modules/spring-boot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part00_guide/BootDataJpaExerciseTest.java`

---

## 下一章怎么接

当你把数据访问跑通后，下一层经常要考虑性能与降本：缓存是最常见的横切能力之一。

- 下一章：[第 106 章：Cache 主线](106-cache-mainline.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「Data JPA 主线」的生效时机/顺序/边界；断点/入口：N；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「Data JPA 主线」的生效时机/顺序/边界；断点/入口：A（本页为索引；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「Data JPA 主线」的生效时机/顺序/边界；断点/入口：工具页）；断言：你能解释“为什么此处生效/为什么此处不生效”。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
