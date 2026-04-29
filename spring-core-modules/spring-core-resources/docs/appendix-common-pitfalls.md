# 01. 常见坑清单（排查时对照）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕常见坑清单（排查时对照）展开，主线可以概括为：定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。

    先运行 `SpringCoreResourcesLabTest`，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ResourceLoader`/`ApplicationContext` 获取 `Resource`；读取优先走 `getInputStream()`；pattern 扫描使用 `PathMatchingResourcePatternResolver`。

    需要下探源码时，可以从 `org.springframework.core.io.Resource` / `org.springframework.core.io.ResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[06. jar vs filesystem：为什么在 IDE 里 OK，打包后就不行？](resource-abstraction-jar-vs-filesystem.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[02. 自测题（Spring Core Resources）](appendix-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页不是新的主线章节，而是把已读过的机制拿回来验证、排障和自检。读法如下：

1. 先运行 Book Matrix、Branch Matrix 或本页列出的最小 Lab，把现象固定成可重复结果。
2. 再按现象、题目或坑点定位对应章节、断点和关键变量。
3. 最后用对应实验/测试 收束答案；如果答案仍然只停留在概念层面，再回到正文补齐机制。

### 排障骨架（统一结构）

当遇到“行为不符合预期 / 入口跑不通 / 断点不命中”时，可以按下面 6 步收敛问题（每一步都尽量可复现、可对照、可验证）：

1. 症状（Symptoms）：看到的错误/现象（保留关键错误信息）
2. 复现（Repro）：用最小可运行入口稳定复现（优先用测试入口，而不是手工点 UI）
   - Book Matrix：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBookMatrixLabTest test`
   - Branch Matrix：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBranchMatrixLabTest test`
3. 证据（Evidence）：对照断点地图，把断点/观察点/关键日志收齐：[04-breakpoint-map.md](guide-breakpoint-map.md)
4. 决策（Decision）：对照关键分支矩阵，把 If/Then 选路写清楚：[05-branch-decision-matrix.md](guide-branch-decision-matrix.md)
5. 修复（Fix）：给出最小修复动作（配置/代码/调用方式）
6. 验证（Verify）：复跑入口 + 对照自检清单：[02-self-check.md](appendix-self-check.md)


!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`
## 最小可运行实验（Lab）

- Lab：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`
- 运行命令：`mvn -pl :spring-core-resources test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界

> 验证入口（可跑）：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`

## 坑 1：把 classpath 资源当成 File

- 现象：本地（IDE）OK，打包后失败（典型是 `getFile()` 抛异常或路径根本不存在）。
`SpringCoreResourcesMechanicsLabTest#classpathResourceCanBeReadAsBytes`（对比：读 bytes 是稳定的；拿 file 路径不是）

优先 `getInputStream()`，不要依赖 `getFile()`；需要背景可回看 [06. jar vs filesystem](resource-abstraction-jar-vs-filesystem.md)。

## 坑 2：以为 `getResource(...)` 会在不存在时返回 null

- 事实：它返回的是“句柄（handle）”，需要 `exists()` 判断（见 [04. exists-and-handles](resource-abstraction-exists-and-handles.md)）。
`SpringCoreResourcesMechanicsLabTest#getResourceReturnsAHandle_evenIfTheResourceDoesNotExist`

## 坑 3：pattern 扫描结果顺序不稳定导致 tests 抖动

`SpringCoreResourcesMechanicsLabTest#classpathStarPatternLoadsResourcesFromClasspath`

把结果映射成稳定的描述（`getDescription()`/文件名）并排序后再断言（本模块 service 已经这么做）。

## 坑 4：忽略编码导致内容乱码

- 动作：读取文本时显式使用 UTF-8（见 [05. reading-and-encoding](resource-abstraction-reading-and-encoding.md)）

## 坑 5：错误处理太粗糙

- 动作：区分“资源不存在”与“资源不可读”（Exercise 里会练）

## 小结与下一章


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`

上一章：[06-jar-vs-filesystem](resource-abstraction-jar-vs-filesystem.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[99-self-check](appendix-self-check.md)

<!-- BOOKIFY:END -->
