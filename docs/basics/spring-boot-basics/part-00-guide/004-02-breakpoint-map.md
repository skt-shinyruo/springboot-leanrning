# 第 4 章：02：断点地图（Boot Basics Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Boot Basics Debugger Pack）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
    - 原理：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
    - 源码入口：（以本章正文“源码/断点”小节为准）
    - 推荐 Lab：`BootBasicsBookMatrixLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 4 章：00 - Deep Dive Guide（springboot-basics）](004-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 4 章：04：关键分支矩阵（Branch Decision Matrix）](004-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：02：断点地图（Boot Basics Debugger Pack） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文主线理解用法。
- 回到主线：主线与关键分支以本章正文为准（先抓住“入口 → 关键分支 → 可观察证据”）。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

- 本章目标：把“配置从哪来、谁覆盖谁、Profile 到底影响什么”变成一组可复制的断点与观察点（Watchpoints）。
- 最推荐的调试路线：**先用测试固定分支，再用断点确认分支发生点**（不要直接靠猜配置优先级）。

## 运行入口（建议先跑）

- Book Matrix（主线最小集合）：`BootBasicsBookMatrixLabTest`
- Branch Matrix（关键分支最小集合）：`BootBasicsBranchMatrixLabTest`

推荐命令：

- `mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBranchMatrixLabTest test`

## 入口断点（从“现象”回到“绑定”）

> 优先在业务侧断点，先拿到“最终值”，再回到框架侧看“为什么是它”。

- `com.learning.springboot.bootbasics.part01_boot_basics.AppProperties#setGreeting`
- `com.learning.springboot.bootbasics.part01_boot_basics.AppProperties#setFeatureEnabled`
- `com.learning.springboot.bootbasics.part01_boot_basics.DefaultGreetingProvider#provideGreeting`
- `com.learning.springboot.bootbasics.part01_boot_basics.DevGreetingProvider#provideGreeting`

## 框架侧断点（从“绑定”回到“来源”）

> 这组断点用于回答：**值是从哪个 PropertySource 来的**、**Profile 是否参与分流**。

- `org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor#postProcessBeforeInitialization`
- `org.springframework.boot.context.properties.bind.Binder#bind`
- `org.springframework.core.env.PropertySourcesPropertyResolver#getProperty`

## Watchpoints（建议加入 IDE Watch/Expression）

- `context.getEnvironment().getActiveProfiles()`：当前 Profile
- `context.getEnvironment().getProperty("app.greeting")`：最终 greeting 值
- `context.getEnvironment().getProperty("app.featureEnabled")`：最终开关值
- `properties.getGreeting()` / `properties.isFeatureEnabled()`：绑定后的类型安全值

## 常见分支定位（与“关键分支矩阵”配合）

- Profile 没生效：先看 `getActiveProfiles()`，再看是否通过测试属性/系统属性把 profile 覆盖掉了。
- “以为覆盖了但没覆盖”：优先在 `PropertySourcesPropertyResolver#getProperty` 看命中的 PropertySource（先命中谁）。

## 排障入口（Playbook）

- 常见坑：[`../appendix/007-90-common-pitfalls.md`](../appendix/007-90-common-pitfalls.md)
- 自检：[`../appendix/008-99-self-check.md`](../appendix/008-99-self-check.md)

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootBasicsBookMatrixLabTest` / `BootBasicsBranchMatrixLabTest`

上一章：[配置绑定（@ConfigurationProperties）](../part-01-boot-basics/006-02-configuration-properties-binding.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[004-04-branch-decision-matrix.md](004-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->
