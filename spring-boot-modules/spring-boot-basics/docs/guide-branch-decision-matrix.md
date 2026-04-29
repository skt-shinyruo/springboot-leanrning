# 05. 关键分支矩阵
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（五问闭环）"
    本章围绕04：关键分支矩阵展开，主线可以概括为：Profile/优先级的差异最终体现在 `Environment` 与 `@ConfigurationProperties` 的绑定结果上。

    把“配置覆盖/Profile 分流”的关键分支写成矩阵表；每一行都对应一个可运行入口 + 可观察点（断点/变量）。

    对照入口：`BootBasicsBranchMatrixLabTest`。需要下探源码时，可以从 `Environment#getProperty` / `Binder#bind` / `ConfigurationPropertiesBindingPostProcessor` 这些入口切入。

<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[04. 断点地图（Boot Basics）](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[01. 配置来源（PropertySources）与 Profile 覆盖](boot-basics-property-sources-and-profiles.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页把阅读顺序、源码入口与可运行实验放在同一处。读法如下：

1. 先看导读和机制主线，确认本页要解释的现象。
2. 再运行“最小可运行实验（Lab）”，把主线或分支固定成断言。
3. 最后回到源码与断点、常见坑或自检题，把结论落到可复述证据链。

## 导读

本页把“配置到底怎么生效”拆成 3 条最常见、最容易踩坑的分支；处理方式：

1. 先跑 `BootBasicsBranchMatrixLabTest`
2. 断点停在 `AppProperties#setGreeting`，观察最终绑定值
3. 回到框架断点确认来源（命中哪个 PropertySource）

## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点 |
|---|---|---|---|---|
| Default Profile | 未显式激活 `dev` | greeting 来自默认配置，使用 default bean | `BootBasicsBookMatrixLabTest` / `BootBasicsDefaultLabTest` | `activeProfiles` / `app.greeting` / `DefaultGreetingProvider` |
| Dev Profile 生效 | 激活 `dev` profile | greeting 来自 dev 配置，使用 dev bean | `BootBasicsBranchMatrixLabTest` / `BootBasicsDevLabTest` | `getActiveProfiles()` / `DevGreetingProvider` |
| 测试覆盖优先级 | 测试通过 properties 覆盖 `app.greeting` | greeting 以测试覆盖为准（优先级最高） | `BootBasicsBranchMatrixLabTest` / `BootBasicsOverrideLabTest` | `Environment#getProperty("app.greeting")` 命中哪个 source |

## 运行命令

- `mvn -q -pl :spring-boot-basics -Dtest=BootBasicsBranchMatrixLabTest test`

## 调试路线

- 第 1 站：`AppProperties#setGreeting`（确认绑定结果）
- 第 2 站：`ConfigurationPropertiesBindingPostProcessor#postProcessBeforeInitialization`（确认绑定发生点）
- 第 3 站：`PropertySourcesPropertyResolver#getProperty`（确认命中的 PropertySource）

## 排障 Playbook（对应模块）

- 常见坑：[`appendix-common-pitfalls.md`](appendix-common-pitfalls.md)
- 自检：[`appendix-self-check.md`](appendix-self-check.md)

## 小结与下一章

Profile/优先级的差异最终体现在 `Environment` 与 `@ConfigurationProperties` 的绑定结果上。

下一章见：[01：配置来源（PropertySources）与 Profile 覆盖](boot-basics-property-sources-and-profiles.md)


<!-- BOOKIFY:START -->

### 对应实验/测试

- Matrix：`BootBasicsBranchMatrixLabTest`
- Lab：`BootBasicsDevLabTest` / `BootBasicsOverrideLabTest`

上一章：[guide-breakpoint-map.md](guide-breakpoint-map.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[boot-basics-property-sources-and-profiles.md](boot-basics-property-sources-and-profiles.md)

<!-- BOOKIFY:END -->

