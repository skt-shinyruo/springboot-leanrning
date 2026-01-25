# 第 44 章：04：关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Branch Decision Matrix）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：当代理覆盖不了 join point（constructor/get/set/call）时，使用 AspectJ LTW/CTW 在类加载期/编译期织入；用可断言实验验证是否生效。
    - 原理：代理 vs 织入：选择 LTW/CTW → 定义切点（execution/call/...）→ weaving 生效取决于 classloader/agent/时机 → 用测试/断点验证。
    - 源码入口：`org.springframework.context.weaving.AspectJWeavingEnabler` / `org.springframework.instrument.classloading.LoadTimeWeaver` / `org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter`
    - 推荐 Lab：`AspectjLtwLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 44 章：02：断点地图（AspectJ Weaving Debugger Pack）](044-02-breakpoint-map.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 45 章：01：代理 vs 织入：边界、能力与成本](../part-01-mental-model/045-01-proxy-vs-weaving.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：04：关键分支矩阵（Branch Decision Matrix） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：当代理覆盖不了 join point（constructor/get/set/call）时，使用 AspectJ LTW/CTW 在类加载期/编译期织入；用可断言实验验证是否生效。
- 回到主线：代理 vs 织入：选择 LTW/CTW → 定义切点（execution/call/...）→ weaving 生效取决于 classloader/agent/时机 → 用测试/断点验证。
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
mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwLabTest test
```
<!-- BOOKLIKE-V2:INTRO:END -->

## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| LTW 需要 agent | JVM 带 `-javaagent:aspectjweaver.jar` | advice 生效（运行时织入） | `AspectjLtwLabTest` / `AspectjLtwBranchMatrixLabTest` | inputArgs / invocation log |
| CTW 不需要 agent | JVM 不带 agent | advice 仍生效（编译期织入） | `AspectjCtwLabTest` / `AspectjCtwBranchMatrixLabTest` | inputArgs 不含 agent |
| call vs execution | pointcut 使用 call/execution | 两类 join point 都可拦截 | `AspectjLtwLabTest` / `AspectjCtwLabTest` | log 中两类记录 |
| field get/set | field join points | 字段读写可被拦截 | `AspectjLtwLabTest` / `AspectjCtwLabTest` | field-get/field-set |
| cflow/withincode | 高级 pointcut | 受控制流/调用方限制 | `AspectjLtwLabTest` / `AspectjCtwLabTest` | log 条数差异 |

## 推荐运行命令

- LTW/CTW 推荐：`mvn -q -pl :spring-core-aop-weaving test`
- 单入口（需要时）：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`../appendix/049-90-common-pitfalls.md`](../appendix/049-90-common-pitfalls.md)
- 自检：[`../appendix/050-99-self-check.md`](../appendix/050-99-self-check.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：`org.springframework.context.weaving.AspectJWeavingEnabler`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：`org.springframework.instrument.classloading.LoadTimeWeaver`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：`org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``AspectjLtwLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`AspectjLtwLabTest` / `AspectjLtwBranchMatrixLabTest` / `AspectjCtwLabTest` / `AspectjCtwBranchMatrixLabTest`

上一章：[044-02-breakpoint-map.md](044-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[常见坑](../appendix/049-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
