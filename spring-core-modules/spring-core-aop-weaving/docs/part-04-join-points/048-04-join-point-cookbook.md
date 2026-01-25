# 第 48 章：04. Join Point & Pointcut Cookbook（速查）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Join Point & Pointcut Cookbook（速查）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：当代理覆盖不了 join point（constructor/get/set/call）时，使用 AspectJ LTW/CTW 在类加载期/编译期织入；用可断言实验验证是否生效。
    - 原理：代理 vs 织入：选择 LTW/CTW → 定义切点（execution/call/...）→ weaving 生效取决于 classloader/agent/时机 → 用测试/断点验证。
    - 源码入口：`org.springframework.context.weaving.AspectJWeavingEnabler` / `org.springframework.instrument.classloading.LoadTimeWeaver` / `org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter`
    - 推荐 Lab：`AspectjCtwLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 47 章：03. CTW：Compile-Time Weaving（编译期织入）](../part-03-ctw/047-03-ctw-basics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 49 章：90. 常见坑清单（LTW/CTW）](../appendix/049-90-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**04. Join Point & Pointcut Cookbook（速查）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，你应该能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`AspectjCtwLabTest` / `AspectjLtwLabTest`

## 机制主线

这一章不追求“完整列举”，只追求“够用 + 不容易误判”。

---

## 1. `call` vs `execution`

- `call(...)`：命中 **调用点**（caller 的代码位置）
- `execution(...)`：命中 **被调用体**（callee 的方法体入口）

本模块在事件记录里用 `JoinPoint#getStaticPart().getKind()` 做断言：

- `method-call`
- `method-execution`

---

## 2. constructor

常见写法：

- `call(com.xxx.Foo.new(..))`
- `execution(com.xxx.Foo.new(..))`

对应 kind：

- `constructor-call`
- `constructor-execution`

---

## 3. field get/set（不是 getter/setter）

常见写法：

- `get(int com.xxx.Foo.value)`
- `set(int com.xxx.Foo.value)`

对应 kind：

- `field-get`
- `field-set`

这经常用来解释：

- 为什么“字段访问”不是“方法拦截”能覆盖的范围（proxy AOP 做不到）

---

## 4. `withincode`（限定方法体）

常见写法（只在 callerA 方法体内发生的 call 才命中）：

```text
call(* Foo.callee(..)) && withincode(* Foo.callerA(..))
```

- callerA 命中
- callerB 不命中

---

## 5. `cflow`（限定控制流）

常见写法（只在 entry 的控制流下发生的 deep 执行才命中）：

```text
execution(* Foo.deep(..)) && cflow(execution(* Foo.entry(..)))
```

- `entry()` 触发 deep → 命中
- `otherEntry()` 触发 deep → 不命中

---

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`AspectjCtwLabTest` / `AspectjLtwLabTest`
- 建议命令：`mvn -pl :spring-core-aop-weaving test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

> 建议配合 Labs 跑：  
> - LTW：`AspectjLtwLabTest`  
> - CTW：`AspectjCtwLabTest`

本模块验证点：

本模块验证点：

## 常见坑与边界

下一章：[`90-common-pitfalls`](../appendix/049-90-common-pitfalls.md)

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`AspectjCtwLabTest` / `AspectjLtwLabTest`

上一章：[03-ctw-basics](../part-03-ctw/047-03-ctw-basics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[90-common-pitfalls](../appendix/049-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
