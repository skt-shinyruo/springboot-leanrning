# 03. AspectJ Weaving 调用链（CTW/LTW：织入发生在哪里）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕01：AspectJ Weaving 调用链（CTW/LTW：织入发生在哪里）展开，主线可以概括为：代理只能拦截“走代理入口”的调用；织入是“改字节码”。LTW 通过 javaagent + Instrumentation 在类加载时改字节码；CTW 在编译期/构建期改字节码。

    先跑 `AspectjLtwLabTest` / `AspectjCtwLabTest`，把“哪些 join point 能/不能被织入”固化为断言，再按本文理解 CTW 与 LTW 的织入入口。

    需要下探源码时，可以从 （LTW）`java.lang.instrument.Instrumentation` / `ClassFileTransformer` /（AspectJ）weaver 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖指南：把 weaving 的“结论 → 实验 → 排障路径”跑通](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04. 断点地图（AspectJ Weaving Debugger Pack）](04-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `AspectjLtwLabTest`，以获得可回归的现象与断言入口。

读完这一章，你应该能把这件事讲清楚：代理只能拦截“走代理入口”的调用；织入是“改字节码”。LTW 通过 javaagent + Instrumentation 在类加载时改字节码；CTW 在编译期/构建期改字节码。需要下探源码时，可以从 （LTW）`java.lang.instrument.Instrumentation` / `ClassFileTransformer` /（AspectJ）weaver 这些入口切入。


## 最短调用链（应能复述）

### 1) CTW（Compile-Time Weaving）

1. 构建阶段由 AspectJ 编译器/织入器处理 class
2. 输出的 class 已经包含织入后的字节码
3. 运行期不需要代理也能命中 `call/get/set/constructor` 等 join point

### 2) LTW（Load-Time Weaving）

1. JVM 启动加载 javaagent（`-javaagent:...`）
2. agent 注册 `ClassFileTransformer`
3. 类加载时 transformer 接到原始字节码并执行织入
4. JVM 定义类（defineClass）时使用“织入后的字节码”

证据链入口：

- `AspectjLtwLabTest` / `AspectjCtwLabTest`

## 小结与下一章

代理只能拦截“走代理入口”的调用；织入是“改字节码”。LTW 通过 javaagent + Instrumentation 在类加载时改字节码；CTW 在编译期/构建期改字节码。

下一章见：[第 44 章：02：断点地图](04-breakpoint-map.md)


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`AspectjLtwLabTest`
- Lab：`AspectjCtwLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/02-breakpoint-map.md](04-breakpoint-map.md)

<!-- BOOKIFY:END -->
