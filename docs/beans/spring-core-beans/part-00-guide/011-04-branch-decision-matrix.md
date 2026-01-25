# 第 11 章：04：关键分支矩阵（Beans Branch Decision Matrix）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：04：关键分支矩阵（Beans Branch Decision Matrix）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`
    - 推荐 Lab：`SpringCoreBeansCircularDependencyBoundaryLabTest`
<!-- CHAPTER-CARD:END -->


<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 11 章：00 - Deep Dive Guide（spring-core-beans）](011-00-deep-dive-guide.md) ｜ 全书目录：[Book TOC](../../../book/index.md) ｜ 下一章：[第 12 章：01：30 分钟快速闭环（Minimal Mainline）](012-01-quickstart-30min.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：04：关键分支矩阵（Beans Branch Decision Matrix） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
- 回到主线：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：见页尾导航（顺读不迷路）。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「04：关键分支矩阵（Beans Branch Decision Matrix）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。

验证入口（可直接跑）：
```bash
mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansCircularDependencyBoundaryLabTest test
```
<!-- BOOKLIKE-V2:INTRO:END -->

## 关键分支矩阵（最小集合）

| 分支（Branch） | 触发条件（Trigger） | 期望行为（Expected） | 最小复现入口（Repro） | 观察点（Watchpoints） |
|---|---|---|---|---|
| 注册入口差异 | BeanFactory vs ApplicationContext | 能解释“多出来的基础设施 bean” | `SpringCoreBeansBeanFactoryVsApplicationContextLabTest` | beanDefinition 数量 |
| 扫描 vs 导入 | @ComponentScan / @Import | 注册路径不同但结果可对照 | `SpringCoreBeansIocBranchMatrixLabTest` | BeanDefinition 注册点 |
| 循环依赖边界 | 构造器循环依赖 | 失败并给出可定位异常 | `SpringCoreBeansCircularDependencyBoundaryLabTest` | 创建链路/异常栈 |
| early reference | 提前暴露引用 | 能解释“为什么能解开某些循环” | `SpringCoreBeansEarlyReferenceLabTest` | singletonFactories |
| BPP 顺序 | 多个 PostProcessor | 顺序影响行为/最终 bean | `SpringCoreBeansPostProcessorOrderingLabTest` | postProcess 调用顺序 |

## 推荐运行命令

- IoC 分支：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansIocBranchMatrixLabTest test`
- Internals 分支：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansInternalsBranchMatrixLabTest test`

## 推荐断点（与断点地图配合）

- 断点地图（总入口）：[`013-02-breakpoint-map.md`](013-02-breakpoint-map.md)
- refresh 主线：`AbstractApplicationContext#refresh`
- 创建 bean：`AbstractAutowireCapableBeanFactory#doCreateBean`

## 排障 Playbook（对应模块）

- 常见坑：[`../appendix/025-90-common-pitfalls.md`](../appendix/025-90-common-pitfalls.md)
- 自检：[`../appendix/026-99-self-check.md`](../appendix/026-99-self-check.md)

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「04：关键分支矩阵（Beans Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：`org.springframework.context.support.AbstractApplicationContext#refresh`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「04：关键分支矩阵（Beans Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：`org.springframework.beans.factory.support.DefaultListableBeanFactory`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「04：关键分支矩阵（Beans Branch Decision Matrix）」的生效时机/顺序/边界；断点/入口：`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``SpringCoreBeansCircularDependencyBoundaryLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansBeanFactoryVsApplicationContextLabTest` / `SpringCoreBeansIocBranchMatrixLabTest` / `SpringCoreBeansEarlyReferenceLabTest` / `SpringCoreBeansPostProcessorOrderingLabTest` / `SpringCoreBeansInternalsBranchMatrixLabTest`

上一章：[013-01-applicationcontext-refresh-call-chain.md](013-01-applicationcontext-refresh-call-chain.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[常见坑](../appendix/025-90-common-pitfalls.md)

<!-- BOOKIFY:END -->
