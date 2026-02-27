# 01. LTW：Load-Time Weaving（-javaagent）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：LTW：Load-Time Weaving（-javaagent）
    - 怎么使用：先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：当代理覆盖不了 join point（constructor/get/set/call）时，使用 AspectJ LTW/CTW 在类加载期/编译期织入；用可断言实验验证是否生效。
    - 原理：代理 vs 织入：选择 LTW/CTW → 定义切点（execution/call/...）→ weaving 生效取决于 classloader/agent/时机 → 用测试/断点验证。
    - 源码入口：`org.springframework.context.weaving.AspectJWeavingEnabler` / `org.springframework.instrument.classloading.LoadTimeWeaver` / `org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter`
    - 推荐 Lab：`AspectjLtwLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 心智模型：Proxy vs Weaving](../part-01-mental-model/01-proxy-vs-weaving.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. CTW：Compile-Time Weaving（编译期织入）](../part-03-ctw/01-ctw-basics.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「02. LTW：Load-Time Weaving（-javaagent）」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `AspectjLtwLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! summary "本章要点"

    - 本章结束后，应能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 速读路径：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`AspectjLtwLabTest`

## 机制主线

LTW 的一句话定义：

> 在 **类加载** 时织入（不改源码），通过 `-javaagent:aspectjweaver.jar` 启动 JVM。

---

## 1. LTW 的三个“生效前提”

### 1.1 JVM 必须带 `-javaagent`

- `-javaagent:${project.build.directory}/aspectjweaver.jar`

### 1.2 classpath 必须有 `META-INF/aop.xml`

本模块的 `aop.xml` 放在：

- `spring-core-modules/spring-core-aop-weaving/src/test/resources/META-INF/aop.xml`

放在 test resources 的好处是：

- 织入配置只影响测试运行（学习用闭环更可控）
- 不会“意外影响”其他模块/应用里的默认启动

### 1.3 织入范围必须覆盖到目标类

`aop.xml` 里的 `<weaver><include within="..."/></weaver>` 决定了 weaving 的范围。
范围太大：风险与噪声大；范围太小：会误判“没生效”。

---

## 2. LTW 的学习闭环（本模块提供）

- 非 Spring Bean（`new`）也可拦截：`ltw_canWeaveExecutionForNonSpringObjects`
- 自调用不绕过：`ltw_selfInvocationDoesNotBypassWeaving`
- `call vs execution`：`ltw_callVsExecution_areDifferentJoinPointKinds`
- constructor / field get/set：`ltw_constructorCallAndExecution_canBeIntercepted`、`ltw_fieldGetAndSet_canBeIntercepted`
- 高级表达式：`withincode` / `cflow`

---

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`AspectjLtwLabTest`
- 建议命令：`mvn -pl :spring-core-aop-weaving test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

本模块通过 surefire 执行 `*Ltw*Test` 时自动附带：

- agent jar：`spring-core-modules/spring-core-aop-weaving/target/aspectjweaver.jar`（由 `maven-dependency-plugin` 在 `process-test-classes` 复制）
- JVM 参数：`-javaagent:${project.build.directory}/aspectjweaver.jar`（见 `spring-core-modules/spring-core-aop-weaving/pom.xml` 的 surefire execution）

对应的 Lab 断言：

- `AspectjLtwLabTest#ltw_testJvmIsStartedWithJavaAgent`

- `src/test/resources/META-INF/aop.xml`

因此它只影响测试运行（学习用），不会影响 `spring-boot:run` 的默认启动。

## 常见坑与边界

### 坑点 1：把 “织入没发生” 误判成 “pointcut 写错了”

- Symptom：改了 pointcut/切面代码，但 advice 依然不触发，于是怀疑表达式
- Root Cause：LTW 的前提没满足（缺 agent / 缺 aop.xml / include 范围没覆盖），pointcut 再对也没用
- Verification：
  - JVM 是否带 agent：`AspectjLtwLabTest#ltw_testJvmIsStartedWithJavaAgent`
  - aop.xml 是否在 classpath：确认 `spring-core-modules/spring-core-aop-weaving/src/test/resources/META-INF/aop.xml`
- Fix：先按“三前提”分流确认 LTW 环境，再讨论 join point/pointcut

## 小结与下一章

下一章：[`03-ctw-basics`](../part-03-ctw/01-ctw-basics.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`AspectjLtwLabTest`

上一章：[01-proxy-vs-weaving](../part-01-mental-model/01-proxy-vs-weaving.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[03-ctw-basics](../part-03-ctw/01-ctw-basics.md)

<!-- BOOKIFY:END -->
