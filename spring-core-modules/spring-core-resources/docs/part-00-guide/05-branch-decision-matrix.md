# 05. 关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    本章围绕04：关键分支矩阵（Branch Decision Matrix）展开，主线可以概括为：分支来自 location 语义与底层 URL 形态（jar:file vs file）。

    把 Resource 的关键分支（classpath pattern、jar vs fs、encoding）整理成矩阵表；每行都有复现入口与观察点。

    对照入口：`SpringCoreResourcesBranchMatrixLabTest`。需要下探源码时，可以从 `PathMatchingResourcePatternResolver` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（Resources Debugger Pack）](04-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[01. `Resource` 抽象：为什么 Spring 不直接使用 `File`？](../part-01-resource-abstraction/01-resource-abstraction.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

建议优先运行 `SpringCoreResourcesBranchMatrixLabTest`，以获得可回归的现象与断言入口。

读完这一章，你应该能把这件事讲清楚：分支来自 location 语义与底层 URL 形态（jar:file vs file）。需要下探源码时，可以从 `PathMatchingResourcePatternResolver` 这些入口切入。


## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| 单资源 location | `classpath:...` | 返回单个 Resource | `SpringCoreResourcesLabTest` | URL/description |
| pattern 扫描 | `classpath*:.../*.properties` | 返回多个 Resource | `SpringCoreResourcesMechanicsLabTest` | resources 数量 |
| jar vs filesystem | 运行环境不同 | URL 协议不同，读取方式不同 | `SpringCoreResourcesMechanicsLabTest` | `resource.getURL()` |
| encoding | 读取文本资源 | encoding 决定内容正确性 | `SpringCoreResourcesMechanicsLabTest` | 字符串内容对比 |

## 推荐运行命令

- `mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`../appendix/01-common-pitfalls.md`](../appendix/01-common-pitfalls.md)
- 自检：[`../appendix/02-self-check.md`](../appendix/02-self-check.md)

## 小结与下一章

分支来自 location 语义与底层 URL 形态（jar:file vs file）。

下一章见：[第 141 章：01：Resource 抽象：拿到的到底是什么](../part-01-resource-abstraction/01-resource-abstraction.md)


<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Matrix：`SpringCoreResourcesBranchMatrixLabTest`
- Lab：`SpringCoreResourcesMechanicsLabTest`

上一章：[part-00-guide/02-breakpoint-map.md](04-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-01-resource-abstraction/01-resource-abstraction.md](../part-01-resource-abstraction/01-resource-abstraction.md)

<!-- BOOKIFY:END -->

