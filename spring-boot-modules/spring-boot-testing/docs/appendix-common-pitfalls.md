# 01. 90 - Common Pitfalls（springboot-testing）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    这页的坑大多不是“断言写错”，而是“启动了什么上下文，因此哪些 bean 本来就不会出现”。一旦把 slice 当成全量上下文，或者把 mock 当成真实实现，测试就会变成“看起来全绿但线上照样炸”的错觉制造机。

    建议先跑 `BootTestingMockBeanLabTest`，再对照 `GreetingControllerWebMvcLabTest` 与 `GreetingControllerSpringBootLabTest` 的差异，把“边界选择”跑成事实。需要下探时，入口通常落在 `@WebMvcTest/@SpringBootTest` 的装配范围与 TestContext 缓存复用上。
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[01. 01 - Slice 与 Mock（Testing）](testing-slice-and-mocking.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 99 - Self Check（springboot-testing）](appendix-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 先确认“边界是什么”，再改代码（测试排障的常见逆序）

在测试里，最省时间的排障顺序往往和生产代码相反：先确认测试边界（slice/full context、是否启用 filters、哪些 bean 会被自动装配），再谈业务逻辑。否则会出现“在测试里修了半天，实际上只是选错了注解”的空转。

如果想把这种边界选择跑成事实，建议先跑两组矩阵测试，把常见分支固化为断言：

- `mvn -q -pl :spring-boot-testing -Dtest=BootTestingBookMatrixLabTest test`
- `mvn -q -pl :spring-boot-testing -Dtest=BootTestingBranchMatrixLabTest test`

需要进一步解释“为什么这个 bean 没装配/为什么上下文被复用”时，再对照本模块的断点地图与关键分支矩阵去下探，会比单纯看日志更收敛：[04-breakpoint-map.md](guide-breakpoint-map.md) / [05-branch-decision-matrix.md](guide-branch-decision-matrix.md)。


!!! example "本章配套实验（先跑再读）"

    - Lab：`BootTestingMockBeanLabTest` / `GreetingControllerSpringBootLabTest` / `GreetingControllerWebMvcLabTest`

## 最小可运行实验（Lab）

- Lab：`BootTestingMockBeanLabTest` / `GreetingControllerSpringBootLabTest` / `GreetingControllerWebMvcLabTest`
- 建议命令：`mvn -pl :spring-boot-testing test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界

> 这页的坑大多数不是“代码写错了”，而是“选错了测试边界”，导致上下文装配范围与预期不一致。

## 坑 1：把 slice 当成全量上下文（装配范围误判）

`@WebMvcTest` 里缺 bean、缺 auto-config、行为和 `@SpringBootTest` 不一致，于是误判“功能没实现/框架坏了”——这通常不是框架问题，而是 slice 的边界被误解了。slice 的目标是“只测某一层”，它刻意不加载其它层的 bean。

先明确需要验证的是“Controller 行为”还是“端到端链路”；需要全链路就用 `@SpringBootTest`，需要快速/聚焦就用 slice。

- 对照入口：`GreetingControllerWebMvcLabTest` vs `GreetingControllerSpringBootLabTest`

## 坑 2：`@MockBean` 过度使用（测试变成模拟驱动）

测试全绿，但线上仍然失败；因为把关键分支都 mock 掉了，只留下“按预期返回”的假世界。更稳妥的策略是：只 mock 边界（外部依赖/不可控系统），把核心分支留在真实实现里；并优先用断言固定“关心的契约”，而不是固定实现细节。

- 对照入口：`BootTestingMockBeanLabTest`

## 坑 3：测试不稳定（flaky），但没有隔离策略

- 会看到：在本机偶发失败、CI 更容易失败；失败原因往往和随机端口/时间/并发时机有关。
把不确定性变成可控输入（固定时钟/固定随机种子/可控并发），并在必要时把“时机/线程”写成可断言证据。

## 对应 Lab（可运行）

- `GreetingControllerWebMvcLabTest`
- `GreetingControllerSpringBootLabTest`
- `BootTestingMockBeanLabTest`

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootTestingMockBeanLabTest` / `GreetingControllerSpringBootLabTest` / `GreetingControllerWebMvcLabTest`

上一章：[part-01-testing/01-slice-and-mocking.md](testing-slice-and-mocking.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[appendix/99-self-check.md](appendix-self-check.md)

<!-- BOOKIFY:END -->
