# 01. 常见坑清单（LTW/CTW）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕常见坑清单（LTW/CTW）展开，主线可以概括为：代理 vs 织入：选择 LTW/CTW → 定义切点（execution/call/...）→ weaving 生效取决于 classloader/agent/时机 → 用测试/断点验证。

    先运行 `AspectjCtwLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：当代理覆盖不了 join point（constructor/get/set/call）时，使用 AspectJ LTW/CTW 在类加载期/编译期织入；用可断言实验验证是否生效。

    需要下探源码时，可以从 `org.springframework.context.weaving.AspectJWeavingEnabler` / `org.springframework.instrument.classloading.LoadTimeWeaver` / `org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. Join Point & Pointcut Cookbook（速查）](../part-04-join-points/01-join-point-cookbook.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 自测题：是否真正理解了 weaving？](02-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

### 排障骨架（统一结构）

当遇到“行为不符合预期 / 入口跑不通 / 断点不命中”时，可以按下面 6 步收敛问题（每一步都尽量可复现、可对照、可验证）：

1. 症状（Symptoms）：看到的错误/现象（保留关键错误信息）
2. 复现（Repro）：用最小可运行入口稳定复现（优先用测试入口，而不是手工点 UI）
   - Book Matrix：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjWeavingBookMatrixLabTest test`
   - Branch Matrix - LTW/CTW：建议直接 `mvn -q -pl :spring-core-aop-weaving test`（让 Surefire 自动区分 execution）；或分别：
     - `mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwBranchMatrixLabTest test`
     - `mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjCtwBranchMatrixLabTest test`
3. 证据（Evidence）：对照断点地图，把断点/Watchpoints/关键日志收齐：[04-breakpoint-map.md](../part-00-guide/04-breakpoint-map.md)
4. 决策（Decision）：对照关键分支矩阵，把 If/Then 选路写清楚：[05-branch-decision-matrix.md](../part-00-guide/05-branch-decision-matrix.md)
5. 修复（Fix）：给出最小修复动作（配置/代码/调用方式）
6. 验证（Verify）：复跑入口 + 对照自检清单：[02-self-check.md](02-self-check.md)


!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`AspectjCtwLabTest` / `AspectjLtwLabTest`

## 最小可运行实验（Lab）

- Lab：`AspectjCtwLabTest` / `AspectjLtwLabTest`
- 建议命令：`mvn -pl :spring-core-aop-weaving test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界

> 验证入口（可跑）：`AspectjLtwLabTest` / `AspectjCtwLabTest`

## LTW 常见坑

- 只写了 `@Aspect`，但 JVM 没带 `-javaagent`（永远不生效）
- JVM 带了 `-javaagent`，但 classpath 上没有 `META-INF/aop.xml`
- `aop.xml` 有，但 `<include within="...">` 没覆盖到目标类（或包名写错）
- IDE 运行配置与 Maven 不一致：命令行能复现、IDE 不能（或反之）

!!! warning "风险提示（最容易误判的 4 件事）"

    - “看起来配置了”：但构建根本没执行织入（CTW 常见）
    - “织入范围越大越稳”：其实会让排障噪声爆炸（所有类都被织入）
    - “织入没生效”：也可能是范围太小（include 没覆盖到目标类/包名写错）
    - “LTW + CTW 一起上”：范围没隔离会造成重复织入/重复触发

## `call` 误判

- 以为 `call` 会拦截“方法体执行”，但它拦的是“调用点”
- 当把 `call` 用在库代码/框架代码上时，很容易造成不可控影响（因为调用点太多）

## 建议的排障顺序（速查）

1. 先判断：proxy 还是 weaving？
2. LTW：确认 `-javaagent` → 确认 `aop.xml` → 确认 include 范围
3. CTW：确认构建是否织入 → 确认织入范围 → 确认运行时使用的是织入产物

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`AspectjCtwLabTest` / `AspectjLtwLabTest`

上一章：[04-join-point-cookbook](../part-04-join-points/01-join-point-cookbook.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[99-self-check](02-self-check.md)

<!-- BOOKIFY:END -->
