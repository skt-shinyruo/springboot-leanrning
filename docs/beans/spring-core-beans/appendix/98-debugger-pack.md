# Debugger Pack（断点包总入口）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：Debugger Pack（断点包总入口）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`org.springframework.context.support.AbstractApplicationContext#refresh` / `org.springframework.beans.factory.support.DefaultListableBeanFactory` / `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` / `org.springframework.context.support.PostProcessorRegistrationDelegate`
    - 推荐 Lab：`SpringCoreBeansMainlineCallChainLabTest`
<!-- CHAPTER-CARD:END -->


> 目标：用最少的入口测试，把“主线时间线 / 关键分支 / 排障策略 / 性能并发”串成可运行的断点闭环。

## 小结与下一章

<!-- BOOKLIKE-V2:SUMMARY:START -->
- 一句话总结：Debugger Pack（断点包总入口） —— 建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过配置类/扫描/导入注册 Bean；用注入机制（类型/名称/限定符）组装依赖；需要增强时依赖 Post-Processor 体系。
- 回到主线：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
- 关键分支提示：当行为不符合预期时，优先回到“原理/主线”找分支判断条件，再用推荐入口复现与验证。
- 下一章：建议按模块目录/全书目录继续顺读。
<!-- BOOKLIKE-V2:SUMMARY:END -->

## 导读

<!-- BOOKLIKE-V2:INTRO:START -->
这一章围绕「Debugger Pack（断点包总入口）」展开：先把边界说清楚，再沿主线推进到关键分支，最后用可运行入口把结论验证出来。

阅读建议：
- 先看章首的“章节学习卡片/本章要点”，建立预期；
- 推荐先跑一遍本章 Lab，再带着问题回到正文。

验证入口（可直接跑）：
```bash
mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansMainlineCallChainLabTest test
```
<!-- BOOKLIKE-V2:INTRO:END -->

## 推荐入口（从这里开始）

1. 主线调用链入口（refresh → doCreateBean）  
   - 运行：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansMainlineCallChainLabTest test`
2. 断点包入口（高频分支与排障）  
   - 运行：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBreakpointPackLabTest test`
3. 排障 Playbook 入口（现象 → 根因 → 验证）  
   - 运行：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansTroubleshootingPlaybookLabTest test`
4. 性能与并发入口（缓存/并发 getBean）  
   - 运行：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansPerformanceConcurrencyLabTest test`

## 断点包索引（文档 ↔ 入口）

- 主线时间线：`part-00-guide/010-03-mainline-timeline.md`  
  入口测试：`SpringCoreBeansMainlineCallChainLabTest`
- 断点地图：`part-00-guide/013-02-breakpoint-map.md`  
  入口测试：`SpringCoreBeansBreakpointPackLabTest`
- 关键分支矩阵：`part-00-guide/011-04-branch-decision-matrix.md`  
  入口测试：`SpringCoreBeansBreakpointPackLabTest`
- 排障 Playbook：`appendix/025-90-common-pitfalls.md`  
  入口测试：`SpringCoreBeansTroubleshootingPlaybookLabTest`
- 并发与性能：`../../book/performance-and-concurrency.md`  
  入口测试：`SpringCoreBeansPerformanceConcurrencyLabTest`

## 关键断点建议（主线优先）

- `AbstractApplicationContext#refresh`
- `DefaultListableBeanFactory#preInstantiateSingletons`
- `AbstractAutowireCapableBeanFactory#doCreateBean`
- `AbstractAutowireCapableBeanFactory#populateBean`
- `AbstractAutowireCapableBeanFactory#initializeBean`
- `DefaultListableBeanFactory#resolveDependency`

## 使用策略（1-2 次跳转定位问题）

1. 先定位阶段：看异常/现象属于“注册 / 注入解析 / 创建 / 初始化 / 代理替换”。  
2. 再定位分支：按“关键分支矩阵”缩小候选路径。  
3. 最后进入入口测试：从对应 Lab 运行并打断点确认。

## 证据链（如何验证你真的理解了）

<!-- BOOKLIKE-V2:EVIDENCE:START -->
- 观察点 1：运行本章推荐入口后，聚焦「Debugger Pack（断点包总入口）」的生效时机/顺序/边界；断点/入口：`org.springframework.context.support.AbstractApplicationContext#refresh`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 2：运行本章推荐入口后，聚焦「Debugger Pack（断点包总入口）」的生效时机/顺序/边界；断点/入口：`org.springframework.beans.factory.support.DefaultListableBeanFactory`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 观察点 3：运行本章推荐入口后，聚焦「Debugger Pack（断点包总入口）」的生效时机/顺序/边界；断点/入口：`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`；断言：你能解释“为什么此处生效/为什么此处不生效”。
- 建议：跑完 ``SpringCoreBeansMainlineCallChainLabTest`` 后，把上述观察点逐条对照，写出你自己的 1–2 句结论（可复述）。
<!-- BOOKLIKE-V2:EVIDENCE:END -->

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansMainlineCallChainLabTest` / `SpringCoreBeansBreakpointPackLabTest` / `SpringCoreBeansTroubleshootingPlaybookLabTest` / `SpringCoreBeansPerformanceConcurrencyLabTest`

上一章：[013-02-breakpoint-map.md](../part-00-guide/013-02-breakpoint-map.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[013-01-applicationcontext-refresh-call-chain.md](../part-00-guide/013-01-applicationcontext-refresh-call-chain.md)

<!-- BOOKIFY:END -->
