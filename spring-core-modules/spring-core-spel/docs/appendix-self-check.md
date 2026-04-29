# 99 自检：Spring Core SpEL
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（复盘出口）"

    - 主线入口：`SpringCoreSpelBookMatrixLabTest`
    - 分支入口：`SpringCoreSpelBranchMatrixLabTest`
    - 入口：`SpringCoreSpelLabTest`
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

- Book Matrix（主线入口）：`mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelBookMatrixLabTest test`
- Branch Matrix（关键分支入口）：`mvn -q -pl :spring-core-spel -Dtest=SpringCoreSpelBranchMatrixLabTest test`

配套资料（排障更快）：

- [断点地图](guide-breakpoint-map.md)
- [关键分支矩阵](guide-branch-decision-matrix.md)
- 常见坑清单（索引页，不在本页重复）：[01-common-pitfalls.md](appendix-common-pitfalls.md)

## 自检题

1. SpEL 的最小闭环是什么？（parse → evaluate → 类型转换）如何用断言把它固定下来？
   - 证据入口：`SpringCoreSpelLabTest#parsesAndEvaluatesSimpleExpression`
2. root object 与 variables 各解决什么问题？为什么 `#var` 与 `property` 不是一回事？
   - 证据入口：`SpringCoreSpelLabTest#evaluatesAgainstRootObjectAndVariables`
3. 同一条表达式在不同 EvaluationContext 下结果为什么可能不同？会用什么最小对照用例证明？
   - 证据入口：`SpringCoreSpelLabTest#evaluatesAgainstRootObjectAndVariables`
4. 如何为 SpEL 增加“变量 + 函数”，并把它固化成可回归断言？
   - 证据入口：`SpringCoreSpelExerciseSolutionTest#solution_addVariablesAndFunctions`
5. 表达式对象（`Expression`）与求值上下文（`EvaluationContext`）在并发下能不能共享？如何用并发实验把边界写成结论？
   - 证据入口：`SpringCoreSpelConcurrencyLabTest#parsedExpressionCanBeEvaluatedConcurrently_whenEvaluationContextIsPerThread`
6. 会把断点下在什么位置观察：parse 产物是什么、getValue 如何走到读取属性/变量？（写出 1 个入口点即可）
   - 证据导航：[`guide-breakpoint-map.md`](guide-breakpoint-map.md)
7. 为什么 SpEL 需要安全边界？会如何限制“可访问的属性/方法/类型”来降低风险？
   - 对照：[`01-common-pitfalls.md`](appendix-common-pitfalls.md)
8. 练习：完成“变量/函数”练习题，并用断言锁定行为（不要靠 println）。
   - 入口：`SpringCoreSpelExerciseTest#exercise_addVariablesAndFunctions`

## 退出条件（完成标准）

- 能区分：表达式本身（可复用）与 EvaluationContext（承载变量/函数/根对象，通常按线程/请求创建）。
- 能用 1–2 个断言用例把一个表达式的行为固定下来（避免靠“表面上能跑”）。

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreSpelLabTest`

上一章：[appendix-common-pitfalls.md](appendix-common-pitfalls.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[模块目录](../README.md)

<!-- BOOKIFY:END -->
