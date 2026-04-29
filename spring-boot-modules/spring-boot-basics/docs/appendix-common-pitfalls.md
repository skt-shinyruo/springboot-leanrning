# 01. 常见坑清单（排查时对照）
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口（排障短文）"

    这页不是“把坑列完”的目录，而是更偏项目里的排障备忘录：每个坑尽量给一个最小复现入口、一个最常见根因，以及可以立刻验证的观察点。

    - 排障入口：`BootBasicsBranchMatrixLabTest` + [断点地图](guide-breakpoint-map.md)
    - 主线入口：`BootBasicsBookMatrixLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[02. `@ConfigurationProperties` 绑定与类型转换](boot-basics-configuration-properties-binding.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[自检题](appendix-self-check.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 本页路线图

本页不是新的主线章节，而是把已读过的机制拿回来验证、排障和自检。读法如下：

1. 先运行 Book Matrix、Branch Matrix 或本页列出的最小 Lab，把现象固定成可重复结果。
2. 再按现象、题目或坑点定位对应章节、断点和关键变量。
3. 最后用对应实验/测试 收束答案；如果答案仍然只停留在概念层面，再回到正文补齐机制。

## 导读

优先运行 `BootBasicsBranchMatrixLabTest`（见文末“对应实验/测试”），从分支矩阵把“最终值/最终实现”先钉住，再回到本章逐条对照根因与修法。


## “配置没生效”的排查路径

这模块的配置问题，大多可以用三步收敛：

1. **先确认事实**：active profiles 是什么？最终属性值是什么？（用断言/断点看 `Environment`）
2. **再分线排查**：这是“属性覆盖”的问题，还是“Bean 注册/切换”的问题？
3. **最后固化结论**：把现象写成测试断言（否则两周后还会再掉一次坑）

## 配置没生效

### 坑点 1：以为是 dev，本质上根本没激活 profile

现象：

- 明明在本地“开了 dev”，但看到的还是默认 greeting/默认 Bean

最短验证：

- `BootBasicsDefaultLabTest#activeProfilesDoNotContainDevByDefault`
- `BootBasicsDevLabTest#activeProfilesContainDev`

一眼观察点：

- `environment.getActiveProfiles()` 是否包含 `dev`

### 坑点 2：被测试覆盖了，但一直盯着配置文件

现象：

- 文件里写的是“好，默认配置”，测试里却变成了 “Hello from test override”

最短验证：

- `BootBasicsOverrideLabTest#testPropertiesOverrideApplicationProperties`

一眼观察点：

- `@SpringBootTest(properties = ...)` 是否覆盖了同名 key

## `@ConfigurationProperties` 没绑定 / 值不对

### 坑点 3：prefix 或 key 写错（以及 kebab-case 映射误判）

现象：

- “表面上写了配置”，但绑定对象里一直是默认值

这类问题优先检查两件事：

- prefix 是否匹配（`@ConfigurationProperties(prefix = "app")`）
- `featureEnabled` 对应 `feature-enabled`（kebab-case）

练习入口（用它构造一个错误样例，形成肌肉记忆）：

- `BootBasicsExerciseTest#exercise_addNewPropertyField`

### 坑点 4：类型转换失败（别指望调用方“自动兜底”）

现象：

- 启动失败，报 “Failed to bind properties ...”

练习入口：

- `BootBasicsExerciseTest#exercise_invalidPropertyType`

## Bean 没切换 / 条件不生效

### 坑点 5：把“属性覆盖”误当成“Bean 一定会切换”

现象：

- 属性变了，但注入的实现类没变（或反过来）

最短验证：

- 属性 + Bean 一起变化：`BootBasicsDevLabTest#loadsDevProfileConfigurationAndBean`
- 只覆盖属性：`BootBasicsOverrideLabTest#beansSeeOverriddenProperties`

## 小结与下一章

- 如果能把这页的 5 个坑用自己的话讲清楚，并能指出每个坑的最小复现入口，那么这个模块的“配置主线”就算吃透了。下一章的 Self-check 会负责一次复盘。

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`BootBasicsDefaultLabTest` / `BootBasicsDevLabTest` / `BootBasicsOverrideLabTest`

上一章：[boot-basics-configuration-properties-binding.md](boot-basics-configuration-properties-binding.md) ｜ 目录：[模块目录](../README.md) ｜ 下一章：[appendix-self-check.md](appendix-self-check.md)

<!-- BOOKIFY:END -->
