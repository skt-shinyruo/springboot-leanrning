# 第 47 章：03. CTW：Compile-Time Weaving（编译期织入）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：CTW：Compile-Time Weaving（编译期织入）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：当代理覆盖不了 join point（constructor/get/set/call）时，使用 AspectJ LTW/CTW 在类加载期/编译期织入；用可断言实验验证是否生效。
    - 原理：代理 vs 织入：选择 LTW/CTW → 定义切点（execution/call/...）→ weaving 生效取决于 classloader/agent/时机 → 用测试/断点验证。
    - 源码入口：`org.springframework.context.weaving.AspectJWeavingEnabler` / `org.springframework.instrument.classloading.LoadTimeWeaver` / `org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter`
    - 推荐 Lab：`AspectjCtwLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 46 章：02. LTW：Load-Time Weaving（-javaagent）](../part-02-ltw/046-02-ltw-basics.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 48 章：04. Join Point & Pointcut Cookbook（速查）](../part-04-join-points/048-04-join-point-cookbook.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**03. CTW：Compile-Time Weaving（编译期织入）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。

!!! example "本章配套实验（先跑再读）"

    - Lab：`AspectjCtwLabTest`

## 机制主线

CTW 的一句话定义：

> 在 **构建期** 使用 ajc 把 advice 织入到 class 字节码里，运行时不需要 `-javaagent`。

因此本模块提供两个关键断言：

- JVM 启动参数里没有 `aspectjweaver.jar`：`AspectjCtwLabTest#ctw_testJvmIsNotStartedWithAspectjJavaAgent`
- 但 join point 仍会被拦截（说明字节码已被织入）：`AspectjCtwLabTest#ctw_weavingWorksWithoutJavaAgent_forMethodExecutionAndCall`

---

## 2. 为什么 CTW 仍然需要谨慎

CTW 的代价主要在于：

- 构建过程更复杂（插件配置、织入范围、与 IDE 的一致性）
- 织入范围不当会让“全局行为”变得难以预期

因此本模块默认把 CTW 的织入范围控制在：

- `com.learning.springboot.springcoreaopweaving.ctwtargets..*`

---

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`AspectjCtwLabTest`
- 建议命令：`mvn -pl :spring-core-aop-weaving test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

> 在 **构建期** 把横切逻辑织入字节码，运行时不需要 `-javaagent`。

## 1. CTW 的“验证点”与 LTW 不同

LTW 的验证点是“JVM 启动参数 + aop.xml + include 范围”。  
CTW 的验证点是“构建产物是否已被织入”。

- `AspectjCtwLabTest#ctw_testJvmIsNotStartedWithAspectjJavaAgent`

> ⚠️ 版本提示：本模块为了兼容 `aspectj-maven-plugin` 内置的 ajc，把编译目标设置为 `--release 16`（运行仍需 JDK 17+）。  
> 这不影响 weaving 关键结论，但会影响你在本模块里使用 Java 17+ 语法特性。

并用 Labs 验证“只织入应该织入的类”。

## 常见坑与边界

### 坑点 1：IDE 里“单跑某个测试/类”却拿到未织入的字节码，导致误判 CTW 失效

- Symptom：命令行 `mvn test` 一切正常，但你在 IDE 单跑时 advice 不触发
- Root Cause：CTW 的关键是“构建产物是否被织入”；IDE 的编译/运行配置如果绕过了 maven weaving，可能跑的是未织入 class
- Verification：
  - 命令行下 JVM 无 agent 仍生效：`AspectjCtwLabTest#ctw_weavingWorksWithoutJavaAgent_forMethodExecutionAndCall`
  - 若你在 IDE 单跑看不到效果，优先对比“是否使用了 maven 编译产物”
- Fix：以 maven 构建产物为准（先跑 `mvn -pl :spring-core-aop-weaving test`）；IDE 场景需要确保复用 maven 编译输出或启用相同 weaving 配置

## 小结与下一章

下一章：[`04-join-point-cookbook`](../part-04-join-points/048-04-join-point-cookbook.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`AspectjCtwLabTest`

上一章：[LTW 基础](../part-02-ltw/046-02-ltw-basics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[Join Point 菜谱](../part-04-join-points/048-04-join-point-cookbook.md)

<!-- BOOKIFY:END -->
