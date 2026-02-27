# 01. 90 - Common Pitfalls（springboot-testing）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Common Pitfalls（springboot-testing）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：按目标选择测试切片（如 `@WebMvcTest`）或全量上下文（`@SpringBootTest`）；用 mock/替身把外部依赖固定成可断言证据。
    - 原理：测试注解决定上下文装配范围 → TestContext 缓存与复用 → slice/full context 的权衡 → 断言固化机制结论 → 快速定位失败。
    - 源码入口：`org.springframework.boot.test.context.SpringBootTest` / `org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest` / `org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate`
    - 推荐 Lab：`BootTestingMockBeanLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 01 - Slice 与 Mock（Testing）](../part-01-testing/01-slice-and-mocking.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 99 - Self Check（springboot-testing）](02-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

### 排障模板（统一结构）

当遇到“行为不符合预期 / 入口跑不通 / 断点不命中”时，建议按下面 6 步收敛（每一步都尽量可复现、可对照、可验证）：

1. 症状（Symptoms）：看到的错误/现象（保留关键错误信息）
2. 复现（Repro）：用最小可运行入口稳定复现（优先用测试入口，而不是手工点 UI）
   - Book Matrix：`mvn -q -pl :spring-boot-testing -Dtest=BootTestingBookMatrixLabTest test`
   - Branch Matrix：`mvn -q -pl :spring-boot-testing -Dtest=BootTestingBranchMatrixLabTest test`
3. 证据（Evidence）：对照断点地图，把断点/Watchpoints/关键日志收齐：[04-breakpoint-map.md](../part-00-guide/04-breakpoint-map.md)
4. 决策（Decision）：对照关键分支矩阵，把 If/Then 选路写清楚：[05-branch-decision-matrix.md](../part-00-guide/05-branch-decision-matrix.md)
5. 修复（Fix）：给出最小修复动作（配置/代码/调用方式）
6. 验证（Verify）：复跑入口 + 对照自检清单：[02-self-check.md](02-self-check.md)

- 本章主题：**01. 90 - Common Pitfalls（springboot-testing）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 读完本章，应当能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 如果只看一眼：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootTestingMockBeanLabTest` / `GreetingControllerSpringBootLabTest` / `GreetingControllerWebMvcLabTest`

## 机制主线

这页不展开完整机制主线；其定位更接近排障备忘录：把常见分支与可复现入口列出来，便于回到 tests 验证。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`BootTestingMockBeanLabTest` / `GreetingControllerSpringBootLabTest` / `GreetingControllerWebMvcLabTest`
- 建议命令：`mvn -pl :spring-boot-testing test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界

> 这页的坑大多数不是“代码写错了”，而是“选错了测试边界”，导致上下文装配范围与预期不一致。

## 坑 1：把 slice 当成全量上下文（装配范围误判）

- 会看到：`@WebMvcTest` 里缺 bean、缺 auto-config、行为和 `@SpringBootTest` 不一致，于是误判“功能没实现/框架坏了”。
- Root Cause：slice 的目标是“只测某一层”，它刻意不加载其它层的 bean。
- Fix：先明确需要验证的是“Controller 行为”还是“端到端链路”；需要全链路就用 `@SpringBootTest`，需要快速/聚焦就用 slice。
- 对照入口：`GreetingControllerWebMvcLabTest` vs `GreetingControllerSpringBootLabTest`

## 坑 2：`@MockBean` 过度使用（测试变成模拟驱动）

- 会看到：测试全绿，但线上仍然失败；因为把关键分支都 mock 掉了，只留下“按预期返回”的假世界。
- Fix：只 mock 边界（外部依赖/不可控系统），把核心分支留在真实实现里；并优先用断言固定“关心的契约”而不是固定实现细节。
- 对照入口：`BootTestingMockBeanLabTest`

## 坑 3：测试不稳定（flaky），但没有隔离策略

- 会看到：在本机偶发失败、CI 更容易失败；失败原因往往和随机端口/时间/并发时机有关。
- Fix：把不确定性变成可控输入（固定时钟/固定随机种子/可控并发），并在必要时把“时机/线程”写成可断言证据。

## 对应 Lab（可运行）

- `GreetingControllerWebMvcLabTest`
- `GreetingControllerSpringBootLabTest`
- `BootTestingMockBeanLabTest`

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootTestingMockBeanLabTest` / `GreetingControllerSpringBootLabTest` / `GreetingControllerWebMvcLabTest`

上一章：[part-01-testing/01-slice-and-mocking.md](../part-01-testing/01-slice-and-mocking.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/99-self-check.md](02-self-check.md)

<!-- BOOKIFY:END -->
