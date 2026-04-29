# 05. 关键分支矩阵
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕04：关键分支矩阵展开，主线可以概括为：分支来自 location 语义与底层 URL 形态（jar:file vs file）。

    把 Resource 的关键分支（classpath pattern、jar vs fs、encoding）整理成矩阵表；每行都有复现入口与观察点。

    对照入口：`SpringCoreResourcesBranchMatrixLabTest`。需要下探源码时，可以从 `PathMatchingResourcePatternResolver` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（Resources）](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. `Resource` 抽象：为什么 Spring 不直接使用 `File`？](resource-abstraction.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

优先运行 `SpringCoreResourcesBranchMatrixLabTest`，以获得可回归的现象与断言入口。

本章完成后，应能复述：分支来自 location 语义与底层 URL 形态（jar:file vs file）。需要下探源码时，可以从 `PathMatchingResourcePatternResolver` 这些入口切入。


## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点 |
|---|---|---|---|---|
| 单资源 location | `classpath:...` | 返回单个 Resource | `SpringCoreResourcesLabTest` | URL/description |
| pattern 扫描 | `classpath*:.../*.properties` | 返回多个 Resource | `SpringCoreResourcesMechanicsLabTest` | resources 数量 |
| jar vs filesystem | 运行环境不同 | URL 协议不同，读取方式不同 | `SpringCoreResourcesMechanicsLabTest` | `resource.getURL()` |
| encoding | 读取文本资源 | encoding 决定内容正确性 | `SpringCoreResourcesMechanicsLabTest` | 字符串内容对比 |

## 运行命令

- `mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBranchMatrixLabTest test`

## 排障 Playbook（对应模块）

- 常见坑：[`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](appendix-self-check.md)

## 小结与下一章

分支来自 location 语义与底层 URL 形态（jar:file vs file）。

下一章见：[01：Resource 抽象：拿到的到底是什么](resource-abstraction.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`SpringCoreResourcesBranchMatrixLabTest`
- Lab：`SpringCoreResourcesMechanicsLabTest`

上一章：[guide-breakpoint-map.md](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[resource-abstraction.md](resource-abstraction.md)

<!-- BOOKIFY:END -->

