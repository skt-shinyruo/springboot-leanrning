# 99 自检：Spring Boot Testing
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（复盘出口）"

    - 主线入口：`BootTestingBookMatrixLabTest`
    - 分支入口：`BootTestingBranchMatrixLabTest`
    - 入口：`GreetingControllerWebMvcLabTest` / `GreetingControllerSpringBootLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[常见坑清单](appendix-common-pitfalls.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[模块目录](../README.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 先跑入口（把现象跑成事实）

- Book Matrix（主线入口）：`mvn -q -pl :spring-boot-testing -Dtest=BootTestingBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：`mvn -q -pl :spring-boot-testing -Dtest=BootTestingBranchMatrixLabTest test`

配套资料（排障更快）：

- [断点地图](guide-breakpoint-map.md)
- [关键分支矩阵](guide-branch-decision-matrix.md)
- 常见坑清单（索引页，不在本页重复）：[appendix-common-pitfalls.md](appendix-common-pitfalls.md)

## 自检题

1. `@WebMvcTest` 是什么边界？它为什么通常需要显式 `@MockBean` controller 依赖？
   - 证据入口：`GreetingControllerWebMvcLabTest`（观察其 `@WebMvcTest(...)` 与 `@MockBean`）
2. `@SpringBootTest(webEnvironment=RANDOM_PORT)` 与 `@WebMvcTest` 的差异是什么？它们分别证明了什么、不能证明什么？
   - 证据入口：`GreetingControllerSpringBootLabTest#returnsGreetingFromRealService` + `GreetingControllerWebMvcLabTest#returnsGreetingFromMockedService`
3. MockMvc 与 TestRestTemplate 的本质差异是什么？如何用“是否经过真实网络栈/容器”解释它们的取舍？
   - 证据入口：`GreetingControllerWebMvcLabTest` / `GreetingControllerSpringBootLabTest`
4. `@MockBean` 的“替换边界”是什么？为什么它能影响一次真实 HTTP 调用的返回结果？
   - 证据入口：`BootTestingMockBeanLabTest#mockBeanOverridesRealBeanInFullContext`
5. `@MockBean` 是否会影响默认参数流转（比如缺省 name=World）？如何把它写成可回归结论？
   - 证据入口：`BootTestingMockBeanLabTest#mockBeanAlsoAffectsDefaultParamFlow`
6. `@WebMvcTest` 中请求参数是如何被解析/传递到 service 的？如何验证它“确实传了 Bob”？
   - 证据入口：`GreetingControllerWebMvcLabTest#callsServiceWithTheResolvedNameParameter`
7. 如何固定“响应 shape”而不是只断言某个具体字符串？会选择断言哪一层结构？
   - 证据入口：`GreetingControllerWebMvcLabTest#returnsJsonResponseShape` + `GreetingControllerSpringBootLabTest#responseContainsMessageKey`
8. Unicode 参数在测试里如何保证不出乱码？如何把它写成一个长期回归用例？
   - 证据入口：`GreetingControllerWebMvcLabTest#supportsUnicodeNames`
9. 练习：写一组“slice vs full”的对照用例，证明某个 bean 在 `@WebMvcTest` 中不会加载，但在 `@SpringBootTest` 会。
   - 入口：`BootTestingExerciseTest#exercise_sliceVsFull`

## 退出条件（完成标准）

- 能基于目标选择测试类型（slice/full），并能用一个反例说明“选错会得到假绿/假红”。
- 能把 mock 的边界说清楚：Mockito 字段 mock vs Spring 容器 bean 替换（`@MockBean`）。

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootTestingMockBeanLabTest` / `GreetingControllerSpringBootLabTest`
- Exercise：`BootTestingExerciseTest`

上一章：[appendix-common-pitfalls.md](appendix-common-pitfalls.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[模块目录](../README.md)

<!-- BOOKIFY:END -->
