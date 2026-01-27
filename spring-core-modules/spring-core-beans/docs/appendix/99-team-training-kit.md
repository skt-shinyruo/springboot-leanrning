# 99. 团队内训讲义（Training Kit）：可直接开讲的课时脚本

## 导读

- 本章主题：**团队内训讲义（可直接开讲的课时脚本）**
- 使用方式建议：把本章当成“讲师用的导航页”。你不需要从头讲完 `spring-core-beans` 的所有机制，而是按课时选一条路线：**60/90/120 分钟**，每条路线都给出：
  - 课时安排（分段与讲解目标）
  - 可运行的 Lab 入口（用断言固化现象）
  - 建议断点与 watch list（用证据链收敛结论）
  - 课堂互动问题与课后练习（用于面试复盘/内化）

!!! summary "本章要点"

    - 内训目标不是“讲完知识点”，而是把团队共同的排障语言统一起来：**阶段（refresh 哪一段）→ 入口（关键方法）→ 观察点（最小 watch list）→ 结论（可复述）**。
    - 课堂节奏建议：先让学员“跑起来”（Lab 断言通过），再让学员“看见”（断点观察），最后让学员“说出来”（面试式复述）。
    - 你可以把本讲义当成“课程骨架”，需要补细节时再跳到知识地图/断点包/对应章节。

!!! example "讲师建议先跑的入口（上课前自检）"

    - 断点包总入口（确保环境能跑）：`SpringCoreBeansBreakpointPackLabTest`
    - 30 分钟快启（课堂最常用的 3 个实验入口）：`part-00-guide/012-01-quickstart-30min.md`
    - 知识地图（遇到问题直接定位章节/断点/Lab）：`appendix/92-knowledge-map.md`

## 0. 讲师准备清单（上课前 10 分钟）

1) **准备环境**

- JDK：17
- IDE：IntelliJ IDEA（建议）或 VS Code + Java 插件
- Maven：可用（建议提前跑一次测试让依赖落地）

2) **提前 warm up（避免课堂上下载依赖）**

- `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansBreakpointPackLabTest test`

3) **调试准备（可选，但强烈建议）**

- 方式 A：IDE 直接 Debug 单测（最省心）
- 方式 B：Maven surefire debug（适合演示“如何在真实项目 attach”）
  - `mvn -pl :spring-core-beans -Dmaven.surefire.debug -Dtest=SpringCoreBeansBreakpointPackLabTest test`

4) **统一课堂约定（建议投屏写在白板上）**

- 所有问题先回答 2 句：
  1) 它发生在 `refresh` 的哪一段？（定义层 / 注册 BPP / 创建单例 / 注入 / 初始化 / 代理替换）
  2) 第一断点下在哪个方法？（只说 1 个方法名）

## 1. 60 分钟速成（适合团队统一“排障语言”）

> 目标：让所有人用同一套“阶段感 + 断点入口 + watch list”描述问题，不再停留在“感觉像是 Spring 的锅”。

### 1.1 课时安排（60 分钟）

- 0–10：目标与规则（阶段感/证据链/最小观察点）
- 10–25：实验 1（多候选注入如何收敛：Qualifier）
- 25–40：实验 2（prototype 注入 singleton 的坑：ObjectProvider）
- 40–55：实验 3（为什么拿到 proxy：BPP 替换发生点）
- 55–60：总结与作业（面试复述模板）

### 1.2 课堂实验（按顺序跑）

实验入口直接复用“30 分钟快启”：

- 入口：`part-00-guide/012-01-quickstart-30min.md`

建议讲师只强调三件事：

1) **能跑**：命令能执行、断言能通过
2) **能看见**：断点命中、watch list 有变化
3) **能复述**：每个实验 2 句话讲清“为什么这样/如何证明”

### 1.3 本场必须统一的三组断点（不求多，求稳定）

- 候选解析：
  - `DefaultListableBeanFactory#doResolveDependency`
  - `DefaultListableBeanFactory#determineAutowireCandidate`
- 属性填充与注入发生点：
  - `AbstractAutowireCapableBeanFactory#populateBean`
- 代理替换发生点：
  - `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`

### 1.4 课堂互动问题（建议当场问）

1) 为什么 `@Qualifier` 能“收敛”多候选？（你观察到了哪个候选集合？）
2) 为什么 prototype 注入 singleton 会“像单例”？（你在 `doGetBean` 看到了什么分支？）
3) 为什么你拿到的是 proxy？（`result != bean` 在哪一段第一次成立？）

### 1.5 课后作业（10 分钟）

- 用面试式复述写 6 句总结（每题 2 句）：
  1) “注入失败”你先看哪一段？第一断点在哪？
  2) “代理不生效”你先分成哪两类问题？各自入口在哪？
  3) “循环依赖”你如何判断是否存在 early reference 窗口？

参考：`appendix/93-interview-playbook.md`

## 2. 90 分钟进阶（适合“源码进阶 + 排障套路”）

> 目标：让学员能把“概念”放回 `refresh()` 主线，建立可复用的时间线视角。

### 2.1 课时安排（90 分钟）

- 0–10：回顾 60 分钟的三组断点
- 10–35：主线时间线（把 BFPP/BDRPP 与 BPP 放回 refresh）
- 35–55：单例创建主线（doCreateBean 的关键阶段与窗口）
- 55–75：循环依赖分型（constructor vs setter vs proxy 介入）
- 75–90：排障演练（从异常到断点入口）

### 2.2 推荐讲解路线（课堂跳读顺序）

1) 主线时间线（阶段感）：`part-00-guide/010-03-mainline-timeline.md`
2) refresh 调用链（锚点）：`part-00-guide/013-01-applicationcontext-refresh-call-chain.md`
3) 断点地图（收敛）：`part-00-guide/013-02-breakpoint-map.md`
4) 循环依赖与 early reference：
   - `part-01-ioc-container/09-circular-dependencies.md`
   - `part-03-container-internals/16-early-reference-and-circular.md`

### 2.3 本场必须跑的 2 个 Lab（建议讲师提前熟悉）

- `SpringCoreBeansMainlineCallChainLabTest`（主线时间线）
- `SpringCoreBeansCircularDependencyBoundaryLabTest`（循环依赖边界）

## 3. 120 分钟深挖（适合“面试 + 源码 + 真实排障闭环”）

> 目标：学员能把一个真实问题压缩成“现象→阶段→关键方法→关键变量→修复策略”，并能复述成面试答案。

### 3.1 课时安排（120 分钟）

- 0–15：主线复述（refresh 三段论 + doCreateBean 四段论）
- 15–45：处理器体系（BDRPP/BFPP/BPP 顺序与能力边界）
- 45–70：注入发生点（populateBean + AABPP/CABPP）
- 70–95：FactoryBean 与类型匹配边界（`&`、getObjectType、缓存语义）
- 95–120：值解析与类型转换（占位符/SpEL/转换链路的排障套路）

### 3.2 课堂演练建议（每组 1 个现象）

你可以直接用知识地图选题：

- `appendix/92-knowledge-map.md`（按现象选题：注入失败/代理/循环依赖/占位符/转换）

### 3.3 面试复述模板（课堂最后 10 分钟统一输出）

- 参考：`appendix/93-interview-playbook.md`
- 课堂要求：每人任选一个主题，按模板写出“主线 + 边界 + 证据链（断点/变量/Lab）”

## 4. 常见课堂翻车点（讲师备忘）

1) **学员直接跑全量测试/直接跑应用，断点命中爆炸**
   - 对策：强制从 `*LabTest` 入口开始（噪音最少）
2) **断点打了很多，但 watch list 没有统一**
   - 对策：课堂只允许看 3–5 个变量（beanName/mbd/exposedObject/候选集合/三层缓存）
3) **把“现象”当“结论”**
   - 对策：要求每个结论必须对应一个可复现 Lab（断言）与一个关键断点（证据链）

## 5. 课程扩展（把内训变成长期资产）

- 章节补齐/排障固化：`appendix/025-90-common-pitfalls.md`
- Debugger Pack：`appendix/98-debugger-pack.md`
- 自测题：`appendix/026-99-self-check.md`
- 深挖指南：`part-00-guide/011-00-deep-dive-guide.md`

## 6. 课后验收（把“听懂了”变成“能复述/能排障”）

建议用一个统一的验收口径收尾（不看感觉，只看证据）：

1) 每人任选 1 个真实问题（或知识地图里的 1 个现象）：写出“阶段 → 入口方法 → 观察点 → 修复策略”四行结论
2) 按 `appendix/93-interview-playbook.md` 的结构写出标准答案（包含方法级证据链）
3) 用本仓库一个 LabTest 跑出可复现证据（断言 + 条件断点命中截图/口头描述均可）

<!-- BOOKIFY:START -->

上一章：[98. Debugger Pack（断点包总入口）](98-debugger-pack.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[99. 自测题：你是否真的理解了？](026-99-self-check.md)

<!-- BOOKIFY:END -->
