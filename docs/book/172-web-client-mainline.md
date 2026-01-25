# 第 172 章：Web Client 主线
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Web Client 主线
    - 怎么使用：本页为索引/工具页：按页面提示找到入口（章节/Lab/断点地图），再回到主线章节顺读。
    - 原理：本页不讲机制原理，负责把“入口与路径”整理成可检索的导航。
    - 源码入口：N/A（本页为索引/工具页）
    - 推荐 Lab：N/A
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 171 章：99 - Self Check（spring-boot-actuator）](../actuator/spring-boot-actuator/appendix/171-99-self-check.md) ｜ 全书目录：[Book TOC](/) ｜ 下一章：[第 173 章：主线时间线：Spring Boot Web Client](../web-client/spring-boot-web-client/part-00-guide/173-03-mainline-timeline.md)
<!-- GLOBAL-BOOK-NAV:END -->

这一章解决的问题是：**为什么 WebClient 既能同步也能异步、过滤器链怎么工作、错误处理怎么写才不会“吞掉根因”**。

---

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：Web Client 主线 —— 本页为索引/工具页：按页面提示找到入口（章节/Lab/断点地图），再回到主线章节顺读。
- 回到主线：本页不讲机制原理，负责把“入口与路径”整理成可检索的导航。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「Web Client 主线」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 建议先带着问题顺读一遍正文，再按证据链回到源码/断点验证。
<!-- BOOKLIKE-V2:INTRO:END -->

## 主线（按时间线顺读）

1. 构建 client：`WebClient.builder()`
2. 组装过滤器链：exchange filters（日志/鉴权/重试/指标）
3. 发起请求：request → exchange → decode
4. 错误处理：状态码分支（4xx/5xx）、body 解析失败、超时/取消
5. 常见坑：block 的位置、线程模型、超时配置、错误链路的“丢栈/丢 body”

---

## 深挖入口（模块 docs）

### 进阶入口（排障/关键分支）

- 断点地图：[`docs/web-client/spring-boot-web-client/part-00-guide/174-02-breakpoint-map.md`](../web-client/spring-boot-web-client/part-00-guide/174-02-breakpoint-map.md)
- 关键分支矩阵：[`docs/web-client/spring-boot-web-client/part-00-guide/174-04-branch-decision-matrix.md`](../web-client/spring-boot-web-client/part-00-guide/174-04-branch-decision-matrix.md)
- 排障 playbook：[`docs/web-client/spring-boot-web-client/appendix/180-90-common-pitfalls.md`](../web-client/spring-boot-web-client/appendix/180-90-common-pitfalls.md)
- 自检清单：[`docs/web-client/spring-boot-web-client/appendix/181-99-self-check.md`](../web-client/spring-boot-web-client/appendix/181-99-self-check.md)

- 模块目录页：[`docs/web-client/spring-boot-web-client/README.md`](../web-client/spring-boot-web-client/README.md)
- 模块主线时间线（含可跑入口）：[`docs/web-client/spring-boot-web-client/part-00-guide/03-mainline-timeline.md`](../web-client/spring-boot-web-client/part-00-guide/173-03-mainline-timeline.md)

---

## 本章可跑入口（最小闭环）

- Lab：`mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientWebClientLabTest test`（`spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientWebClientLabTest.java`）
- Lab（进阶：Book Matrix）：`mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBookMatrixLabTest test`（`spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientBookMatrixLabTest.java`）
- Lab（进阶：Branch Matrix）：`mvn -q -pl :spring-boot-web-client -Dtest=BootWebClientBranchMatrixLabTest test`（`spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientBranchMatrixLabTest.java`）
- Exercise（动手练习，默认 `@Disabled`）：`spring-boot-modules/spring-boot-web-client/src/test/java/com/learning/springboot/bootwebclient/part00_guide/BootWebClientExerciseTest.java`

---

## 下一章怎么接

当模块越来越多，最重要的工程能力之一就是测试：怎么选 slice、怎么控制上下文、怎么写出可维护的可断言证据链。

- 下一章：[第 182 章：Testing 主线](182-testing-mainline.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「Web Client 主线」的生效时机/顺序/边界；断点/入口：N；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「Web Client 主线」的生效时机/顺序/边界；断点/入口：A（本页为索引；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「Web Client 主线」的生效时机/顺序/边界；断点/入口：工具页）；断言：你能解释“为什么此处生效/为什么此处不生效”。
<!-- BOOKLIKE-V2:EVIDENCE:END -->
