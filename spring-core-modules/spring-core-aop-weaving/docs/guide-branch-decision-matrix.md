# 05. 关键分支矩阵
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕04：关键分支矩阵展开，主线可以概括为：LTW/CTW 运行方式决定织入时机；join point 决定 advice 触发点。

    把织入的关键分支（LTW/CTW、join point 种类）整理成矩阵表；每行都能被测试复现并用断点验证。

    对照入口：`AspectjLtwBranchMatrixLabTest`。需要下探源码时，可以从 advice 方法 + target 方法/字段访问点 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（AspectJ Weaving）](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. 心智模型：Proxy vs Weaving](mental-model-proxy-vs-weaving.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

优先运行 `AspectjLtwBranchMatrixLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：LTW/CTW 运行方式决定织入时机；join point 决定 advice 触发点。需要下探源码时，可以从 advice 方法 + target 方法/字段访问点 这些入口切入。


## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点 |
|---|---|---|---|---|
| LTW 需要 agent | JVM 带 `-javaagent:aspectjweaver.jar` | advice 生效（运行时织入） | `AspectjLtwLabTest` / `AspectjLtwBranchMatrixLabTest` | inputArgs / invocation log |
| CTW 不需要 agent | JVM 不带 agent | advice 仍生效（编译期织入） | `AspectjCtwLabTest` / `AspectjCtwBranchMatrixLabTest` | inputArgs 不含 agent |
| call vs execution | pointcut 使用 call/execution | 两类 join point 都可拦截 | `AspectjLtwLabTest` / `AspectjCtwLabTest` | log 中两类记录 |
| field get/set | field join points | 字段读写可被拦截 | `AspectjLtwLabTest` / `AspectjCtwLabTest` | field-get/field-set |
| cflow/withincode | 高级 pointcut | 受控制流/调用方限制 | `AspectjLtwLabTest` / `AspectjCtwLabTest` | log 条数差异 |

## 运行命令

- LTW/CTW 可选：`mvn -q -pl :spring-core-aop-weaving test`
- 单入口（需要时）：`mvn -q -pl :spring-core-aop-weaving -Dtest=AspectjLtwBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](appendix-self-check.md)

## 小结与下一章

LTW/CTW 运行方式决定织入时机；join point 决定 advice 触发点。

下一章见：[01：代理 vs 织入：边界、能力与成本](mental-model-proxy-vs-weaving.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`AspectjLtwBranchMatrixLabTest` / `AspectjCtwBranchMatrixLabTest`
- Lab：`AspectjLtwLabTest` / `AspectjCtwLabTest`

上一章：[guide-breakpoint-map.md](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[mental-model-proxy-vs-weaving.md](mental-model-proxy-vs-weaving.md)

<!-- BOOKIFY:END -->

