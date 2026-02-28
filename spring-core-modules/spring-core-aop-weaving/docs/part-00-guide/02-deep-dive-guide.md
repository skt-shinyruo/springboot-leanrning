# 02. 深挖指南：把 weaving 的“结论 → 实验 → 排障路径”跑通
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕深挖指南：把 weaving 的“结论 → 实验 → 排障路径”跑通展开，主线可以概括为：代理 vs 织入：选择 LTW/CTW → 定义切点（execution/call/...）→ weaving 生效取决于 classloader/agent/时机 → 用测试/断点验证。

    先运行 `AspectjCtwLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：当代理覆盖不了 join point（constructor/get/set/call）时，使用 AspectJ LTW/CTW 在类加载期/编译期织入；用可断言实验验证是否生效。

    需要下探源码时，可以从 `org.springframework.context.weaving.AspectJWeavingEnabler` / `org.springframework.instrument.classloading.LoadTimeWeaver` / `org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 主线时间线：AOP Weaving（织入：LTW/CTW）](01-mainline-timeline.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 心智模型：Proxy vs Weaving](../part-01-mental-model/01-proxy-vs-weaving.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

本章是「00. 深挖指南：把 weaving 的“结论 → 实验 → 排障路径”跑通」的深挖导读：说明如何阅读、如何验证、以及遇到分支时从哪里下断点更省时间。
建议先运行 `AspectjCtwLabTest` 获得可复现现象，再带着断言/观察点回到正文对照机制。

!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`AspectjCtwLabTest` / `AspectjLtwLabTest`

## 机制主线

本模块要解决的第一个问题是“分流”：

- **Spring AOP（proxy）**：改调用链，必须“走代理”，所以 self-invocation 是坑点
- **AspectJ Weaving（weaving）**：改字节码，不依赖 Spring 容器，也不依赖“走代理”，所以 self-invocation 不会绕过织入

本模块只讨论 AspectJ Weaving，并且拆成两条可回归路径：

1. **LTW（Load-Time Weaving）**：运行时通过 `-javaagent` 在类加载阶段织入
2. **CTW（Compile-Time Weaving）**：构建期直接把织入产物编译出来，运行时不需要 agent

### 1) 时间线：LTW（带 -javaagent）是怎么“把 advice 插进目标类”的

1. JVM 启动时携带 `-javaagent:aspectjweaver.jar`
2. 类加载过程中 weaver 生效，读取 classpath 上的 `META-INF/aop.xml`
3. 根据 aop.xml 的 include/pointcut 范围，对目标类字节码进行织入
4. 运行时调用目标方法，advice 以“字节码层面插入”的方式被触发

本模块的关键证据链（都能在测试里断言出来）：

- JVM 参数确实带了 `-javaagent`：`AspectjLtwLabTest#ltw_testJvmIsStartedWithJavaAgent`
- 普通对象（非 Spring bean）也会被织入：`AspectjLtwLabTest#ltw_canWeaveExecutionForNonSpringObjects`

**本模块的 `aop.xml` 放在：**`spring-core-modules/spring-core-aop-weaving/src/test/resources/META-INF/aop.xml`
（这也是为什么本模块的 LTW 实验主要用 test scope 来验证：需要“可控且可重复”的 classpath。）

### 2) 时间线：CTW（不带 agent）为什么也能拦截

1. 构建期由 `aspectj-maven-plugin` 执行 compile-time weaving
2. 织入后的 class 作为编译产物输出（运行时加载的就是“已经被改写的字节码”）
3. 测试 JVM 无需 `-javaagent`，advice 仍会触发

关键证据链：

- JVM 启动参数不包含 aspectjweaver agent：`AspectjCtwLabTest#ctw_testJvmIsNotStartedWithAspectjJavaAgent`
- weaving 在无 agent 情况下仍生效：`AspectjCtwLabTest#ctw_weavingWorksWithoutJavaAgent_forMethodExecutionAndCall`

### 3) 关键参与者（应当能解释它们的作用）

- `-javaagent:${project.build.directory}/aspectjweaver.jar`（LTW 开关，见 `spring-core-modules/spring-core-aop-weaving/pom.xml`）
- `META-INF/aop.xml`（LTW 织入配置：要织谁、怎么织）
- `aspectj-maven-plugin`（CTW 开关：构建期织入）
- advice 的“证据载体”：本模块用 `InvocationLog` 把 advice 触发变成可断言事件

### 4) 本模块的关键分支（2–5 条，默认可回归）

1. **LTW：带 agent 才会织入（启动参数证据）**
   - 验证：`AspectjLtwLabTest#ltw_testJvmIsStartedWithJavaAgent`
2. **CTW：不带 agent 也能织入（构建产物证据）**
   - 验证：`AspectjCtwLabTest#ctw_testJvmIsNotStartedWithAspectjJavaAgent` / `AspectjCtwLabTest#ctw_weavingWorksWithoutJavaAgent_forMethodExecutionAndCall`
3. **call vs execution：两种 join point 语义不同**
   - 验证：`AspectjLtwLabTest#ltw_callVsExecution_areDifferentJoinPointKinds`
4. **self-invocation 不会绕过 weaving（与 Spring AOP 的根本差异）**
   - 验证：`AspectjLtwLabTest#ltw_selfInvocationDoesNotBypassWeaving` / `AspectjCtwLabTest#ctw_selfInvocationIsStillIntercepted`
5. **高级 pointcut：constructor/field/withincode/cflow 都可断言**
   - 验证：`AspectjLtwLabTest#ltw_constructorCallAndExecution_canBeIntercepted` / `AspectjLtwLabTest#ltw_withincode_limitsJoinPointByCallerMethodBody`

## 源码与断点


建议断点（从“织入没发生”快速分流）：

- 先确认运行的是 LTW 还是 CTW：
  - LTW：确认 JVM 是否带 `-javaagent`（看 `AspectjLtwLabTest#ltw_testJvmIsStartedWithJavaAgent`）
  - CTW：确认构建期是否执行了 weaving（看 `aspectj-maven-plugin` 的 weave info 输出）
- 织入是否触发：
  - 在 `LtwWeavingAspect`/对应 CTW aspect 的 advice 方法处下断点（最直观）
- `aop.xml` 是否被加载：
  - 确认 `spring-core-modules/spring-core-aop-weaving/src/test/resources/META-INF/aop.xml` 在 test classpath（否则 include 范围再对也不会织）

## 最小可运行实验（Lab）

- Lab：`AspectjCtwLabTest` / `AspectjLtwLabTest`
- 建议命令：`mvn -pl :spring-core-aop-weaving test`（或在 IDE 直接运行上面的测试类）

### 验证补充（从实验现象出发）

> 验证入口（可跑）：
> - `AspectjLtwLabTest`
> - `AspectjCtwLabTest`

1. **LTW（Load-Time Weaving）**：通过 `-javaagent` 在类加载时织入（更接近“运行时开关”）
2. **CTW（Compile-Time Weaving）**：通过编译期织入（更接近“构建期产物”）

```bash
mvn -pl :spring-core-aop-weaving test
```

会看到两套 tests 都跑：

- `*Ltw*Test`：带 `-javaagent:.../aspectjweaver.jar`
- `*Ctw*Test`：不带 `-javaagent`（用于证明 CTW 独立于 agent）

LTW 是否生效，最常见的判断点不是“有没有写 @Aspect”，而是：

- `src/test/resources/META-INF/aop.xml`

因此它只影响测试运行，不影响 `spring-boot:run` 的默认启动。

- 确认构建是否真的执行了织入（插件是否生效）
- 确认织入范围是否正确（只织入目标包/目标类）
- 确认运行时 classpath 上使用的是“织入后的 class”（而不是未织入版本）

## 常见坑与边界

如果是带着线上问题来的，建议先对照本模块 Appendix（common pitfalls/self-check），再回到主线章节逐一核对。

## 小结与下一章

下一章：[`01-proxy-vs-weaving`](../part-01-mental-model/01-proxy-vs-weaving.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`AspectjCtwLabTest` / `AspectjLtwLabTest`

上一章：[Docs TOC](../README.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01-proxy-vs-weaving](../part-01-mental-model/01-proxy-vs-weaving.md)

<!-- BOOKIFY:END -->
