# 05. 关键分支矩阵
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕04：关键分支矩阵展开，主线可以概括为：测试注解决定“加载哪些 auto-config 与 bean”，mock 决定“用哪个实现参与注入”。

    把测试里最常见的“边界条件”写成矩阵表（slice、mock、上下文范围），并为每个分支提供复现入口。

    对照入口：`BootTestingBranchMatrixLabTest`。需要下探源码时，可以从 `ContextBootstrapper` / `MockitoTestExecutionListener` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（Testing）](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. 01 - Slice 与 Mock（Testing）](testing-slice-and-mocking.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

优先运行 `BootTestingBranchMatrixLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：测试注解决定“加载哪些 auto-config 与 bean”，mock 决定“用哪个实现参与注入”。需要下探源码时，可以从 `ContextBootstrapper` / `MockitoTestExecutionListener` 这些入口切入。


## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点 |
|---|---|---|---|---|
| WebMvc slice | 使用 `@WebMvcTest` | 只加载 MVC 相关 bean | `GreetingControllerWebMvcLabTest` | bean 列表明显更小 |
| Full Boot | 使用 `@SpringBootTest` | 完整启动上下文（更接近生产） | `GreetingControllerSpringBootLabTest` | auto-config 生效 |
| @MockBean 替换 | 使用 `@MockBean` | 注入点拿到 mock | `BootTestingMockBeanLabTest` | 注入实例类型变化 |

## 运行命令

- `mvn -q -pl :spring-boot-testing -Dtest=BootTestingBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](appendix-self-check.md)

## 小结与下一章

测试注解决定“加载哪些 auto-config 与 bean”，mock 决定“用哪个实现参与注入”。

下一章见：[01：Slice Test 与 Mocking：把边界变成可断言](testing-slice-and-mocking.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`BootTestingBranchMatrixLabTest`
- Lab：`GreetingControllerWebMvcLabTest` / `GreetingControllerSpringBootLabTest` / `BootTestingMockBeanLabTest`

上一章：[guide-breakpoint-map.md](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[testing-slice-and-mocking.md](testing-slice-and-mocking.md)

<!-- BOOKIFY:END -->

