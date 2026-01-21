# 第 4 章：02：断点地图（Boot Basics Debugger Pack）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：02：断点地图（Boot Basics Debugger Pack）
    - 怎么使用：先跑 `BootBasicsBranchMatrixLabTest` 固化“Profile/覆盖”的现象，再按本页断点从 `@ConfigurationProperties` 绑定一路追到最终 bean 选择与业务结果。
    - 原理：PropertySources → Environment 覆盖/分流（Profile）→ Binder 绑定（`@ConfigurationProperties`）→ 影响条件装配与运行期行为。
    - 源码入口：`org.springframework.core.env.Environment` / `org.springframework.boot.context.properties.bind.Binder` / `org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor`
    - 推荐 Lab：`BootBasicsBranchMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 4 章：00 - Deep Dive Guide（springboot-basics）](004-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 4 章：04：关键分支矩阵（Branch Decision Matrix）](004-04-branch-decision-matrix.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 导读

- 本章目标：把“配置从哪来、谁覆盖谁、Profile 到底影响什么”变成一组可复制的断点与观察点（Watchpoints）。
- 最推荐的调试路线：**先用测试固定分支，再用断点确认分支发生点**（不要直接靠猜配置优先级）。

## 运行入口（建议先跑）

- Book Matrix（主线最小集合）：`BootBasicsBookMatrixLabTest`
- Branch Matrix（关键分支最小集合）：`BootBasicsBranchMatrixLabTest`

推荐命令：

- `mvn -q -pl :springboot-basics -Dtest=BootBasicsBranchMatrixLabTest test`

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

- Matrix：`BootBasicsBranchMatrixLabTest`
- Matrix：`BootBasicsBookMatrixLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](004-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/04-branch-decision-matrix.md](004-04-branch-decision-matrix.md)

<!-- BOOKIFY:END -->

