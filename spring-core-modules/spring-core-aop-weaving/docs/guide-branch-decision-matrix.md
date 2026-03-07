# 05. 关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕04：关键分支矩阵（Branch Decision Matrix）展开，主线可以概括为：LTW/CTW 运行方式决定织入时机；join point 决定 advice 触发点。

    把织入的关键分支（LTW/CTW、join point 种类）整理成矩阵表；每行都能被测试复现并用断点验证。

    对照入口：`AspectjLtwBranchMatrixLabTest`。需要下探源码时，可以从 advice 方法 + target 方法/字段访问点 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（AspectJ Weaving Debugger Pack）](guide-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. 心智模型：Proxy vs Weaving](mental-model-proxy-vs-weaving.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `AspectjLtwBranchMatrixLabTest`，以获得可回归的现象与断言入口。

读完这一章，你应该能把这件事讲清楚：LTW/CTW 运行方式决定织入时机；join point 决定 advice 触发点。需要下探源码时，可以从 advice 方法 + target 方法/字段访问点 这些入口切入。


## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| LTW 需要 agent | JVM 带 `-javaagent:aspectjweaver.jar` | advice 生效（运行时织入） | `AspectjLtwLabTest` / `AspectjLtwBranchMatrixLabTest` | inputArgs / invocation log |
| CTW 不需要 agent | JVM 不带 agent | advice 仍生效（编译期织入） | `AspectjCtwLabTest` / `AspectjCtwBranchMatrixLabTest` | inputArgs 不含 agent |
| call vs execution | pointcut 使用 call/execution | 两类 join point 都可拦截 | `AspectjLtwLabTest` / `AspectjCtwLabTest` | log 中两类记录 |
| field get/set | field join points | 字段读写可被拦截 | `AspectjLtwLabTest` / `AspectjCtwLabTest` | field-get/field-set |
| cflow/withincode | 高级 pointcut | 受控制流/调用方限制 | `AspectjLtwLabTest` / `AspectjCtwLabTest` | log 条数差异 |

## 推荐运行命令

- LTW/CTW 推荐：`mvn -q -pl :spring-core-aop-weaving test`
- 单入口（需要时）：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`../appendix/01-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`../appendix/02-self-check.md`](appendix-self-check.md)

## 小结与下一章

LTW/CTW 运行方式决定织入时机；join point 决定 advice 触发点。

下一章见：[第 45 章：01：代理 vs 织入：边界、能力与成本](mental-model-proxy-vs-weaving.md)


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`AspectjLtwBranchMatrixLabTest` / `AspectjCtwBranchMatrixLabTest`
- Lab：`AspectjLtwLabTest` / `AspectjCtwLabTest`

上一章：[part-00-guide/02-breakpoint-map.md](guide-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-mental-model/01-proxy-vs-weaving.md](mental-model-proxy-vs-weaving.md)

<!-- BOOKIFY:END -->

