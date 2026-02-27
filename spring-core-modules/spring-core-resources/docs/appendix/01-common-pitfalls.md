# 01. 常见坑清单（建议反复对照）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：常见坑清单（建议反复对照）
    - 怎么使用：先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ResourceLoader`/`ApplicationContext` 获取 `Resource`；读取优先走 `getInputStream()`；pattern 扫描使用 `PathMatchingResourcePatternResolver`。
    - 原理：定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。
    - 源码入口：`org.springframework.core.io.Resource` / `org.springframework.core.io.ResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver`
    - 推荐 Lab：`SpringCoreResourcesLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[06. jar vs filesystem：为什么在 IDE 里 OK，打包后就不行？](../part-01-resource-abstraction/06-jar-vs-filesystem.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[02. 自测题（Spring Core Resources）](02-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

### 排障骨架（统一结构）

当遇到“行为不符合预期 / 入口跑不通 / 断点不命中”时，可以按下面 6 步收敛问题（每一步都尽量可复现、可对照、可验证）：

1. 症状（Symptoms）：看到的错误/现象（保留关键错误信息）
2. 复现（Repro）：用最小可运行入口稳定复现（优先用测试入口，而不是手工点 UI）
   - Book Matrix：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBookMatrixLabTest test`
   - Branch Matrix：`mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesBranchMatrixLabTest test`
3. 证据（Evidence）：对照断点地图，把断点/Watchpoints/关键日志收齐：[04-breakpoint-map.md](../part-00-guide/04-breakpoint-map.md)
4. 决策（Decision）：对照关键分支矩阵，把 If/Then 选路写清楚：[05-branch-decision-matrix.md](../part-00-guide/05-branch-decision-matrix.md)
5. 修复（Fix）：给出最小修复动作（配置/代码/调用方式）
6. 验证（Verify）：复跑入口 + 对照自检清单：[02-self-check.md](02-self-check.md)

- 本章主题：**01. 常见坑清单（建议反复对照）**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - 本章结束后，应能用 2–3 句话复述“它解决什么问题 / 关键约束是什么 / 常见坑在哪里”。
    - 速读路径：请先跑一次本章的最小实验，再回到主线对照阅读。


!!! example "本章配套实验（先运行实验，再阅读）"

    - Lab：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`

## 机制主线

这页不展开完整机制主线；其定位更接近排障备忘录：把常见分支与可复现入口列出来，便于回到 tests 验证。

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`
- 建议命令：`mvn -pl :spring-core-resources test`（或在 IDE 直接运行上面的测试类）

## 常见坑与边界

> 验证入口（可跑）：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`

## 坑 1：把 classpath 资源当成 File

- 现象：本地（IDE）OK，打包后失败（典型是 `getFile()` 抛异常或路径根本不存在）。
- Verification：`SpringCoreResourcesMechanicsLabTest#classpathResourceCanBeReadAsBytes`（对比：读 bytes 是稳定的；拿 file 路径不是）
- Fix：优先 `getInputStream()`，不要依赖 `getFile()`；需要背景可回看 [06. jar vs filesystem](../part-01-resource-abstraction/06-jar-vs-filesystem.md)。

## 坑 2：以为 `getResource(...)` 会在不存在时返回 null

- 事实：它返回的是“句柄（handle）”，需要 `exists()` 判断（见 [04. exists-and-handles](../part-01-resource-abstraction/04-exists-and-handles.md)）。
- Verification：`SpringCoreResourcesMechanicsLabTest#getResourceReturnsAHandle_evenIfTheResourceDoesNotExist`

## 坑 3：pattern 扫描结果顺序不稳定导致 tests 抖动

- Verification：`SpringCoreResourcesMechanicsLabTest#classpathStarPatternLoadsResourcesFromClasspath`
- Fix：把结果映射成稳定的描述（`getDescription()`/文件名）并排序后再断言（本模块 service 已经这么做）。

## 坑 4：忽略编码导致内容乱码

- 建议：读取文本时显式使用 UTF-8（见 [05. reading-and-encoding](../part-01-resource-abstraction/05-reading-and-encoding.md)）

## 坑 5：错误处理太粗糙

- 建议：区分“资源不存在”与“资源不可读”（Exercise 里会练）

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`

上一章：[06-jar-vs-filesystem](../part-01-resource-abstraction/06-jar-vs-filesystem.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[99-self-check](02-self-check.md)

<!-- BOOKIFY:END -->
