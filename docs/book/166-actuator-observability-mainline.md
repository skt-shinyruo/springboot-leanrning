# 第 166 章：Actuator/Observability 主线
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Actuator/Observability 主线
    - 怎么使用：本页为索引/工具页：按页面提示找到入口（章节/Lab/断点地图），再回到主线章节顺读。
    - 原理：本页不讲机制原理，负责把“入口与路径”整理成可检索的导航。
    - 源码入口：N/A（本页为索引/工具页）
    - 推荐 Lab：N/A
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 165 章：自测题（Spring Core Validation）](../validation/spring-core-validation/appendix/165-99-self-check.md) ｜ 全书目录：[Book TOC](/) ｜ 下一章：[第 167 章：主线时间线：Spring Boot Actuator](../actuator/spring-boot-actuator/part-00-guide/167-03-mainline-timeline.md)
<!-- GLOBAL-BOOK-NAV:END -->

这一章解决的问题是：**应用怎么暴露健康检查、指标、信息端点；为什么 exposure 配置与安全边界很关键；如何把“看不见的运行状态”变成可观测信号**。

---

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：Actuator/Observability 主线 —— 本页为索引/工具页：按页面提示找到入口（章节/Lab/断点地图），再回到主线章节顺读。
- 回到主线：本页不讲机制原理，负责把“入口与路径”整理成可检索的导航。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「Actuator/Observability 主线」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 建议先带着问题顺读一遍正文，再按证据链回到源码/断点验证。
<!-- BOOKLIKE-V2:INTRO:END -->

## 主线（按时间线顺读）

1. Actuator 端点注册：health/info/metrics 等
2. exposure 决定哪些端点对外可见（运维需要 vs 安全边界）
3. 自定义 health/indicator：把关键依赖状态可视化
4. metrics 采集：把“请求量/耗时/错误”等固化成指标
5. 常见坑：端点暴露过多、生产环境未做鉴权、误把 actuator 当业务 API

---

## 深挖入口（模块 docs）

### 进阶入口（排障/关键分支）

- 断点地图：[`docs/actuator/spring-boot-actuator/part-00-guide/168-02-breakpoint-map.md`](../actuator/spring-boot-actuator/part-00-guide/168-02-breakpoint-map.md)
- 关键分支矩阵：[`docs/actuator/spring-boot-actuator/part-00-guide/168-04-branch-decision-matrix.md`](../actuator/spring-boot-actuator/part-00-guide/168-04-branch-decision-matrix.md)
- 排障 playbook：[`docs/actuator/spring-boot-actuator/appendix/170-90-common-pitfalls.md`](../actuator/spring-boot-actuator/appendix/170-90-common-pitfalls.md)
- 自检清单：[`docs/actuator/spring-boot-actuator/appendix/171-99-self-check.md`](../actuator/spring-boot-actuator/appendix/171-99-self-check.md)

- 模块目录页：[`docs/actuator/spring-boot-actuator/README.md`](../actuator/spring-boot-actuator/README.md)
- 模块主线时间线（含可跑入口）：[`docs/actuator/spring-boot-actuator/part-00-guide/03-mainline-timeline.md`](../actuator/spring-boot-actuator/part-00-guide/167-03-mainline-timeline.md)

---

## 本章可跑入口（最小闭环）

- Lab：`mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorLabTest test`（`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorLabTest.java`）
- Lab（进阶：Book Matrix）：`mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBookMatrixLabTest test`（`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorBookMatrixLabTest.java`）
- Lab（进阶：Branch Matrix）：`mvn -q -pl :spring-boot-actuator -Dtest=BootActuatorBranchMatrixLabTest test`（`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorBranchMatrixLabTest.java`）
- Exercise（动手练习，默认 `@Disabled`）：`spring-boot-modules/spring-boot-actuator/src/test/java/com/learning/springboot/bootactuator/part00_guide/BootActuatorExerciseTest.java`

---

## 下一章怎么接

当你需要向外部服务发请求时，WebClient 是现代 Spring 体系里的核心客户端。我们把“客户端主线”串一遍。

- 下一章：[第 172 章：Web Client 主线](172-web-client-mainline.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「Actuator/Observability 主线」的生效时机/顺序/边界；断点/入口：N；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「Actuator/Observability 主线」的生效时机/顺序/边界；断点/入口：A（本页为索引；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「Actuator/Observability 主线」的生效时机/顺序/边界；断点/入口：工具页）；断言：你能解释“为什么此处生效/为什么此处不生效”。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
