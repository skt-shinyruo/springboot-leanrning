# 第 140 章：04：关键分支矩阵（Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Branch Decision Matrix）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ResourceLoader`/`ApplicationContext` 获取 `Resource`；读取优先走 `getInputStream()`；pattern 扫描使用 `PathMatchingResourcePatternResolver`。
    - 原理：定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。
    - 源码入口：`org.springframework.core.io.Resource` / `org.springframework.core.io.ResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver`
    - 推荐 Lab：`SpringCoreResourcesMechanicsLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 140 章：02：断点地图（Resources Debugger Pack）](140-02-breakpoint-map.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 141 章：01：Resource 抽象：你拿到的到底是什么](../part-01-resource-abstraction/141-01-resource-abstraction.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：04：关键分支矩阵（Branch Decision Matrix） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `ResourceLoader`/`ApplicationContext` 获取 `Resource`；读取优先走 `getInputStream()`；pattern 扫描使用 `PathMatchingResourcePatternResolver`。
- 回到主线：定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「04：关键分支矩阵（Branch Decision Matrix）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。

验证入口（可直接跑）：
```bash
mvn -q -pl :spring-core-resources -Dtest=SpringCoreResourcesMechanicsLabTest test
```
<!-- BOOKLIKE-V2:INTRO:END -->

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

- 常见坑：[`../appendix/147-90-common-pitfalls.md`](../appendix/147-90-common-pitfalls.md)
- 自检：[`../appendix/148-99-self-check.md`](../appendix/148-99-self-check.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：`org.springframework.core.io.Resource`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：`org.springframework.core.io.ResourceLoader`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「04：关键分支矩阵（Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：`org.springframework.core.io.support.PathMatchingResourcePatternResolver`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``SpringCoreResourcesMechanicsLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreResourcesMechanicsLabTest` / `SpringCoreResourcesLabTest` / `SpringCoreResourcesBranchMatrixLabTest`

上一章：[140-02-breakpoint-map.md](140-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[常见坑](../appendix/147-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
