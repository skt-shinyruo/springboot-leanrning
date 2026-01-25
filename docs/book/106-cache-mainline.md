# 第 106 章：Cache 主线
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Cache 主线
    - 怎么使用：本页为索引/工具页：按页面提示找到入口（章节/Lab/断点地图），再回到主线章节顺读。
    - 原理：本页不讲机制原理，负责把“入口与路径”整理成可检索的导航。
    - 源码入口：N/A（本页为索引/工具页）
    - 推荐 Lab：N/A
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 105 章：99 - Self Check（spring-boot-data-jpa）](../data-jpa/spring-boot-data-jpa/appendix/105-99-self-check.md) ｜ 全书目录：[Book TOC](/) ｜ 下一章：[第 107 章：主线时间线：Spring Boot Cache](../cache/spring-boot-cache/part-00-guide/107-03-mainline-timeline.md)
<!-- GLOBAL-BOOK-NAV:END -->

这一章解决的问题是：**为什么加上 `@Cacheable` 就能缓存、缓存 key 怎么算、为什么同一个方法有时命中有时不命中**。核心仍然是：AOP 代理 + Cache 抽象。

---

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：Cache 主线 —— 本页为索引/工具页：按页面提示找到入口（章节/Lab/断点地图），再回到主线章节顺读。
- 回到主线：本页不讲机制原理，负责把“入口与路径”整理成可检索的导航。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「Cache 主线」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 建议先带着问题顺读一遍正文，再按证据链回到源码/断点验证。
<!-- BOOKLIKE-V2:INTRO:END -->

## 主线（按时间线顺读）

1. 你声明缓存意图：`@Cacheable/@CachePut/@CacheEvict`
2. 容器为目标 Bean 创建代理（和事务类似）
3. 调用进入缓存拦截器：
   - 计算 key（key generator / SpEL）
   - 查询 Cache（命中则短路返回）
   - 不命中则调用目标方法并写回
4. 常见坑：自调用绕过缓存、key 设计不稳定、null 值策略、cache manager 配置差异

---

## 深挖入口（模块 docs）

### 进阶入口（排障/关键分支）

- 断点地图：[`docs/cache/spring-boot-cache/part-00-guide/108-02-breakpoint-map.md`](../cache/spring-boot-cache/part-00-guide/108-02-breakpoint-map.md)
- 关键分支矩阵：[`docs/cache/spring-boot-cache/part-00-guide/108-04-branch-decision-matrix.md`](../cache/spring-boot-cache/part-00-guide/108-04-branch-decision-matrix.md)
- 排障 playbook：[`docs/cache/spring-boot-cache/appendix/114-90-common-pitfalls.md`](../cache/spring-boot-cache/appendix/114-90-common-pitfalls.md)
- 自检清单：[`docs/cache/spring-boot-cache/appendix/115-99-self-check.md`](../cache/spring-boot-cache/appendix/115-99-self-check.md)

- 模块目录页：[`docs/cache/spring-boot-cache/README.md`](../cache/spring-boot-cache/README.md)
- 模块主线时间线（含可跑入口）：[`docs/cache/spring-boot-cache/part-00-guide/03-mainline-timeline.md`](../cache/spring-boot-cache/part-00-guide/107-03-mainline-timeline.md)

---

## 本章可跑入口（最小闭环）

- Lab：`mvn -q -pl :spring-boot-cache -Dtest=BootCacheLabTest test`（`spring-boot-modules/spring-boot-cache/src/test/java/com/learning/springboot/bootcache/part01_cache/BootCacheLabTest.java`）
- Lab（进阶：Book Matrix）：`mvn -q -pl :spring-boot-cache -Dtest=BootCacheBookMatrixLabTest test`（`spring-boot-modules/spring-boot-cache/src/test/java/com/learning/springboot/bootcache/part01_cache/BootCacheBookMatrixLabTest.java`）
- Lab（进阶：Branch Matrix）：`mvn -q -pl :spring-boot-cache -Dtest=BootCacheBranchMatrixLabTest test`（`spring-boot-modules/spring-boot-cache/src/test/java/com/learning/springboot/bootcache/part01_cache/BootCacheBranchMatrixLabTest.java`）
- Exercise（动手练习，默认 `@Disabled`）：`spring-boot-modules/spring-boot-cache/src/test/java/com/learning/springboot/bootcache/part00_guide/BootCacheExerciseTest.java`

---

## 下一章怎么接

缓存解决“读多写少”的成本，但很多业务还有“异步与定时任务”的需求：我们进入 Async/Scheduling 主线。

- 下一章：[第 116 章：Async/Scheduling 主线](116-async-scheduling-mainline.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「Cache 主线」的生效时机/顺序/边界；断点/入口：N；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「Cache 主线」的生效时机/顺序/边界；断点/入口：A（本页为索引；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「Cache 主线」的生效时机/顺序/边界；断点/入口：工具页）；断言：你能解释“为什么此处生效/为什么此处不生效”。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
