# 03. Resources 调用链（ResourceLoader → Resource → 读取）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：Resources 调用链（ResourceLoader → Resource → 读取）
    - 怎么使用：先跑 `SpringCoreResourcesLabTest`，把“不同资源前缀/加载方式”固化成断言，再按本文把 resource resolution 主线串起来。
    - 原理：资源不是字符串路径：Spring 用 `Resource` 抽象统一 classpath/file/url 等；pattern 扫描由 ResourcePatternResolver 负责。
    - 源码入口：`ResourceLoader#getResource` / `DefaultResourceLoader` / `PathMatchingResourcePatternResolver`
    - 推荐 Lab：`SpringCoreResourcesLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. 深挖指南（Spring Core Resources）](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[04. 断点地图（Resources Debugger Pack）](04-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章主题：**03. Resources 调用链（ResourceLoader → Resource → 读取）**
- 建议入口：优先运行 `SpringCoreResourcesLabTest`，以获得可回归的现象与断言入口。
- 阅读目标：资源不是字符串路径：Spring 用 `Resource` 抽象统一 classpath/file/url 等；pattern 扫描由 ResourcePatternResolver 负责。
- 源码入口：`ResourceLoader#getResource` / `DefaultResourceLoader` / `PathMatchingResourcePatternResolver`



## 最短调用链

1. `ResourceLoader#getResource(location)`
2. 根据前缀创建具体 Resource（classpath/file/url 等）
3. `Resource#getInputStream` 读取
4. （pattern）`ResourcePatternResolver#getResources(pattern)` 扫描匹配资源

证据链入口：

- `SpringCoreResourcesLabTest` / `SpringCoreResourcesMechanicsLabTest`

## 小结与下一章

- 小结：资源不是字符串路径：Spring 用 `Resource` 抽象统一 classpath/file/url 等；pattern 扫描由 ResourcePatternResolver 负责。
- 下一章：[第 140 章：02：断点地图](04-breakpoint-map.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreResourcesLabTest`
- Lab：`SpringCoreResourcesMechanicsLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](02-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/02-breakpoint-map.md](04-breakpoint-map.md)

<!-- BOOKIFY:END -->
