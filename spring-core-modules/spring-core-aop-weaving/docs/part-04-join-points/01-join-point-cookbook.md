# 01. Join Point & Pointcut Cookbook（速查）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕Join Point & Pointcut Cookbook（速查）展开，主线可以概括为：代理 vs 织入：选择 LTW/CTW → 定义切点（execution/call/...）→ weaving 生效取决于 classloader/agent/时机 → 用测试/断点验证。

    先运行 `AspectjCtwLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：当代理覆盖不了 join point（constructor/get/set/call）时，使用 AspectJ LTW/CTW 在类加载期/编译期织入；用可断言实验验证是否生效。

    需要下探源码时，可以从 `org.springframework.context.weaving.AspectJWeavingEnabler` / `org.springframework.instrument.classloading.LoadTimeWeaver` / `org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. CTW：Compile-Time Weaving（编译期织入）](../part-03-ctw/01-ctw-basics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 常见坑清单（LTW/CTW）](../appendix/01-common-pitfalls.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章围绕「04. Join Point & Pointcut Cookbook（速查）」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
优先运行 `AspectjCtwLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! example "本章配套实验（先运行实验，再阅读）"

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

## 最小可运行实验（Lab）

- Lab：`AspectjCtwLabTest` / `AspectjLtwLabTest`
- 建议命令：`mvn -pl :spring-core-aop-weaving test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

> 建议配合 Labs 跑：
> - LTW：`AspectjLtwLabTest`
> - CTW：`AspectjCtwLabTest`

本模块验证点：

本模块验证点：

## 常见坑与边界

下一章：[`90-common-pitfalls`](../appendix/01-common-pitfalls.md)

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`AspectjCtwLabTest` / `AspectjLtwLabTest`

上一章：[03-ctw-basics](../part-03-ctw/01-ctw-basics.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[90-common-pitfalls](../appendix/01-common-pitfalls.md)

<!-- BOOKIFY:END -->
