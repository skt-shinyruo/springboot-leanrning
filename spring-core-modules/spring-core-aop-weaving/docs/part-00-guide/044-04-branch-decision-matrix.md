# 第 44 章：04：关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Branch Decision Matrix）
    - 怎么使用：把织入的关键分支（LTW/CTW、join point 种类）整理成矩阵表；每行都能被测试复现并用断点验证。
    - 原理：LTW/CTW 运行方式决定织入时机；join point 决定 advice 触发点。
    - 源码入口：advice 方法 + target 方法/字段访问点
    - 推荐 Lab：`AspectjLtwBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 44 章：02：断点地图（AspectJ Weaving Debugger Pack）](044-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 45 章：01：代理 vs 织入：边界、能力与成本](../part-01-mental-model/045-01-proxy-vs-weaving.md)
<!-- GLOBAL-BOOK-NAV:END -->

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

- 常见坑：[`../appendix/049-90-common-pitfalls.md`](../appendix/049-90-common-pitfalls.md)
- 自检：[`../appendix/050-99-self-check.md`](../appendix/050-99-self-check.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`AspectjLtwBranchMatrixLabTest` / `AspectjCtwBranchMatrixLabTest`
- Lab：`AspectjLtwLabTest` / `AspectjCtwLabTest`

上一章：[part-00-guide/02-breakpoint-map.md](044-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-mental-model/01-proxy-vs-weaving.md](../part-01-mental-model/045-01-proxy-vs-weaving.md)

<!-- BOOKIFY:END -->

