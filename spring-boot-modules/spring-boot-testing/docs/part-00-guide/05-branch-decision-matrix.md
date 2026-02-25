# 05. 关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Branch Decision Matrix）
    - 怎么使用：把测试里最常见的“边界条件”写成矩阵表（slice、mock、上下文范围），并为每个分支提供复现入口。
    - 原理：测试注解决定“加载哪些 auto-config 与 bean”，mock 决定“用哪个实现参与注入”。
    - 源码入口：`ContextBootstrapper` / `MockitoTestExecutionListener`
    - 推荐 Lab：`BootTestingBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（Testing Debugger Pack）](04-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 01 - Slice 与 Mock（Testing）](../part-01-testing/01-slice-and-mocking.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**05. 关键分支矩阵（Branch Decision Matrix）**
- 建议入口：优先运行 `BootTestingBranchMatrixLabTest`，以获得可回归的现象与断言入口。
- 阅读目标：测试注解决定“加载哪些 auto-config 与 bean”，mock 决定“用哪个实现参与注入”。
- 源码入口：`ContextBootstrapper` / `MockitoTestExecutionListener`



## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| WebMvc slice | 使用 `@WebMvcTest` | 只加载 MVC 相关 bean | `GreetingControllerWebMvcLabTest` | bean 列表明显更小 |
| Full Boot | 使用 `@SpringBootTest` | 完整启动上下文（更接近生产） | `GreetingControllerSpringBootLabTest` | auto-config 生效 |
| @MockBean 替换 | 使用 `@MockBean` | 注入点拿到 mock | `BootTestingMockBeanLabTest` | 注入实例类型变化 |

## 推荐运行命令

- `mvn -q -pl :spring-boot-testing -Dtest=BootTestingBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`../appendix/01-common-pitfalls.md`](../appendix/01-common-pitfalls.md)
- 自检：[`../appendix/02-self-check.md`](../appendix/02-self-check.md)

## 小结与下一章

- 小结：测试注解决定“加载哪些 auto-config 与 bean”，mock 决定“用哪个实现参与注入”。
- 下一章：[第 185 章：01：Slice Test 与 Mocking：把边界变成可断言](../part-01-testing/01-slice-and-mocking.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`BootTestingBranchMatrixLabTest`
- Lab：`GreetingControllerWebMvcLabTest` / `GreetingControllerSpringBootLabTest` / `BootTestingMockBeanLabTest`

上一章：[part-00-guide/02-breakpoint-map.md](04-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-testing/01-slice-and-mocking.md](../part-01-testing/01-slice-and-mocking.md)

<!-- BOOKIFY:END -->

