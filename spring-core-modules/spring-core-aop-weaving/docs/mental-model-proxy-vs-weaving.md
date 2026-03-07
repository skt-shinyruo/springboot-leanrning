# 01. 心智模型：Proxy vs Weaving
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕心智模型：Proxy vs Weaving展开，主线可以概括为：代理 vs 织入：选择 LTW/CTW → 定义切点（execution/call/...）→ weaving 生效取决于 classloader/agent/时机 → 用测试/断点验证。

    先运行 `AspectjCtwLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：当代理覆盖不了 join point（constructor/get/set/call）时，使用 AspectJ LTW/CTW 在类加载期/编译期织入；用可断言实验验证是否生效。

    需要下探源码时，可以从 `org.springframework.context.weaving.AspectJWeavingEnabler` / `org.springframework.instrument.classloading.LoadTimeWeaver` / `org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖指南：把 weaving 的“结论 → 实验 → 排障路径”跑通](guide-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. LTW：Load-Time Weaving（-javaagent）](ltw-basics.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「01. 心智模型：Proxy vs Weaving」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `AspectjCtwLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`AspectjCtwLabTest` / `AspectjLtwLabTest`

## 机制主线

这一章的目标是把一句话讲清楚：

> **Spring AOP（proxy）是“改调用链”，AspectJ（weaving）是“改字节码”。**

---

## 1. Proxy AOP：必须走代理（call path）

- 只有“通过 Spring 容器拿到的 proxy 对象”去调用方法，advice 才可能触发
- 同一个类里 `this.inner()` 这种**自调用**不会经过 proxy，因此可能“不生效”

它的优势是：对项目侵入性低、配置简单、风险可控。
它的上限是：只能拦截 method execution（且必须走 proxy）。

---

## 2. Weaving：不依赖 proxy，可以覆盖更多 join point

weaving 的核心是：**修改目标类/调用点的字节码**，在编译期或类加载期把横切逻辑织入进去。

因此它可以覆盖很多 proxy 做不到的 join point，例如：

- `call`：方法调用点（谁调用了谁）
- constructor：构造器调用/执行
- field `get/set`：字段读写（不是 getter/setter）
- `withincode`：限定“在哪个方法体里发生的 join point”
- `cflow`：限定“在某条控制流链路里发生的 join point”

代价也很明显：

---

## 3. `call` vs `execution`（必须掌握）

同一个业务动作里，`call` 与 `execution` 不是替代关系，而是两个视角：

- `call`：发生在 **caller（调用者）** 的代码位置
- `execution`：发生在 **callee（被调用者）** 的方法体入口

因此它们的触发次数、来源位置、以及能拿到的上下文信息都可能不同。

---

## 最小可运行实验（Lab）

- Lab：`AspectjCtwLabTest` / `AspectjLtwLabTest`
- 建议命令：`mvn -pl :spring-core-aop-weaving test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

在 `spring-core-aop` 里已经验证过两件事：

- 构建/运行方式更复杂（尤其是 LTW 的 `-javaagent`）
- 织入范围不当会造成不可控影响（性能/兼容性/排障成本）

本模块用两套 Labs 固化这个差异：

- LTW：`AspectjLtwLabTest#ltw_callVsExecution_areDifferentJoinPointKinds`
- CTW：`AspectjCtwLabTest#ctw_weavingWorksWithoutJavaAgent_forMethodExecutionAndCall`

## 常见坑与边界

因此它们的能力边界完全不同。

## 小结与下一章

下一章：[`02-ltw-basics`](ltw-basics.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`AspectjCtwLabTest` / `AspectjLtwLabTest`

上一章：[00-deep-dive-guide](guide-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02-ltw-basics](ltw-basics.md)

<!-- BOOKIFY:END -->
