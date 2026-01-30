# 27. SmartLifecycle：start/stop 时机与 phase 顺序
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：27. SmartLifecycle：start/stop 时机与 phase 顺序
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里优先按“定义层/实例层/最终暴露对象”分层，再用断点与 watch list 收敛原因。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`LifecycleProcessor#onRefresh` / `DefaultLifecycleProcessor#startBeans` / `DefaultLifecycleProcessor#stopBeans`
    - 推荐 Lab：`SpringCoreBeansSmartLifecycleLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[26. SmartInitializingSingleton：容器就绪后回调](26-smart-initializing-singleton.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[28. 自定义 scope 与 scoped proxy：线程 scope 复现](28-custom-scope-and-scoped-proxy.md)
<!-- GLOBAL-BOOK-NAV:END -->



## 导读

- 本章主题：**27. SmartLifecycle：start/stop 时机与 phase 顺序**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - `SmartLifecycle` 把 start/stop 变成“容器生命周期的一部分”：refresh 收尾自动 start、close 自动 stop（由 `LifecycleProcessor` 统一触发）。
    - start 的顺序按 `phase` 升序；stop 的顺序按 `phase` 反序（更符合依赖停机的安全性）。
    - `isAutoStartup()` 决定是否自动 start：false 时 refresh 不会自动启动它（本仓库 Lab 已补齐对照）。
    - 对 `SmartLifecycle`，容器通常走 `stop(Runnable callback)`（支持异步 stop）：不调用 callback 可能导致关闭卡住直到超时（本仓库 Lab 已补齐“调用的是 stop(callback)”的证据）。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansSmartLifecycleLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartLifecycleLabTest.java`

<!-- AE-DEEPENING:START -->
!!! tip "内容级再加深（A–E 维度）"

    - A（证据链）： phase 排序算法的证据链与关键列表快照。
    - B（边界反例）：反例：autoStartup 与 isRunning 误判、stop 未执行导致资源泄漏。
    - C（排障 SOP）：排障：为什么组件没启动/没停止？如何从 phase/依赖/状态收敛。
    - D（断点观察）：断点：LifecycleProcessor、start/stop 调度点与观察变量。
    - E（面试复述）：面试追问：SmartLifecycle 与普通 init/destroy 的边界与适用场景。
<!-- AE-DEEPENING:END -->
## 机制主线

`SmartLifecycle` 是容器提供的“启动/停止阶段”扩展点。

它非常适合表达：

- 我希望在容器 refresh 完成后自动 start
- 我希望在容器 close 时 stop
- 并且我希望多个组件之间按 phase 排序

### 机制讲透：条件 → 分支 → 结果

**条件**：bean 实现 `SmartLifecycle`，且 `isAutoStartup()` 为 `true`  
**分支**：`LifecycleProcessor#onRefresh` → `DefaultLifecycleProcessor#startBeans` 按 phase 升序启动  
**结果**：refresh 结束自动 start；close 阶段按 phase 反序 stop  
**断点建议**：`DefaultLifecycleProcessor#startBeans` / `DefaultLifecycleProcessor#stopBeans`

## 回调来源分型：它和其他回调有什么层级差异？

- **bean 内部回调**（`@PostConstruct` / `afterPropertiesSet` / `init-method`）  
  发生在“单个 bean 创建”阶段  
- **容器生命周期回调**（`SmartLifecycle`）  
  发生在“容器 refresh/close 关键节点”  

因此它适合“基础设施启动/停止”，而不适合承载复杂业务逻辑。

## 回调与代理交织：start/stop 执行在 proxy 还是 target 上？

容器触发的是 `getBean(beanName)` 返回的最终实例：

- 如果 BPP 把 bean 替换为 proxy（AOP 常见），start/stop 调用落在 **proxy** 上  
- 如果没有替换，调用落在 **目标对象** 上  

排障时请先确认：观察到的是哪种对象，避免误判“start 没执行”。

## 1. 现象：start 按 phase 升序，stop 反向

对应测试：

- A：phase=0
- B：phase=1

可以观察到：

- refresh 时：`start:A` → `start:B`
- close 时：`stop:B` → `stop:A`

读者还应该补齐两个“真实项目更常见”的边界：

- **autoStartup=false**：refresh 不会自动 start（否则很多“为什么它启动就跑起来了”讲不清）
- **stop(callback)**：容器为什么要 callback（否则 shutdown 可能卡住）

对应实验（本仓库已补齐）：

- `SpringCoreBeansSmartLifecycleLabTest#smartLifecycleDoesNotAutoStart_whenIsAutoStartupIsFalse`
- `SpringCoreBeansSmartLifecycleLabTest#containerStopsSmartLifecycle_viaStopCallbackMethod_notStopMethod`

## 2. 机制：LifecycleProcessor 统一管理

容器内部通过 `LifecycleProcessor`（默认 `DefaultLifecycleProcessor`）来：

- 在 refresh 阶段触发 `onRefresh()` → start
- 在 close 阶段触发 `onClose()` → stop

所以它不是“读者手动调用 start/stop”，而是容器生命周期的一部分。

- `AbstractApplicationContext#finishRefresh`：refresh 收尾阶段（触发 `LifecycleProcessor#onRefresh`）
- `LifecycleProcessor#onRefresh`：生命周期统一入口（默认实现是 `DefaultLifecycleProcessor`）
- `DefaultLifecycleProcessor#startBeans`：start 的排序与触发点（phase 升序）
- `LifecycleProcessor#onClose`：close 阶段入口（触发 stop）
- `DefaultLifecycleProcessor#stopBeans`：stop 的排序与触发点（phase 反序）

补充一个非常重要但容易忽略的事实：

- `DefaultLifecycleProcessor` 的 stop 逻辑会按 phase 分组，并等待 `stop(callback)` 的回调完成（用于支持异步 stop）
- 如果回调永远不触发，容器会等待直到 `timeoutPerShutdownPhase` 超时（这就是“关闭卡住”的来源之一）

入口：

- 入口测试（方法级）：`SpringCoreBeansSmartLifecycleLabTest#smartLifecycleStartsInPhaseOrder_andStopsInReverseOrder`
- 推荐跑法：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansSmartLifecycleLabTest#smartLifecycleStartsInPhaseOrder_andStopsInReverseOrder test`

## 排障分流：这是定义层问题还是实例层问题？

- “SmartLifecycle 没自动 start” → **实例层（生命周期触发条件）**：`isAutoStartup()` 是否为 true？context 是否 refresh 完成？（看 `finishRefresh`）
- “start/stop 顺序不符合预期” → **实例层（phase 语义）**：检查 `getPhase()` 值与依赖关系（本章第 1 节）
- “close 卡住/stop 不返回” → **实例层（异步 stop）**：`stop(Runnable)` 必须调用 callback（本章第 3 节）
- “把它当业务逻辑入口导致复杂副作用” → **设计风险**：它更适合作为基础设施 start/stop 钩子（本章第 3 节）

## 4. 面试常问（SmartLifecycle / phase）

1) SmartLifecycle 的 start/stop 触发点分别落在 refresh/close 的哪个阶段？（提示：LifecycleProcessor）
2) 为什么 start 按 phase 升序，而 stop 按 phase 反序？（提示：依赖关系与安全停机）
3) `stop(Runnable)` 为什么必须调用 callback？如果不调用，会出现什么现象？

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansSmartLifecycleLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

## 0. 复现入口（可运行）

- 入口测试（推荐先跑通再下断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartLifecycleLabTest.java`
- 推荐运行命令：
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansSmartLifecycleLabTest test`

对应实验：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartLifecycleLabTest.java`

- `SpringCoreBeansSmartLifecycleLabTest.smartLifecycleStartsInPhaseOrder_andStopsInReverseOrder()`

该实验中注册了两个 lifecycle：

## 源码锚点（建议从这里下断点）

- `AbstractApplicationContext#finishRefresh`：refresh 末尾阶段（触发生命周期处理器）
- `DefaultLifecycleProcessor#onRefresh`：容器刷新完成后的生命周期启动入口
- `DefaultLifecycleProcessor#startBeans`：按 phase 分组并启动的主算法
- `SmartLifecycle#getPhase` / `isAutoStartup` / `start`：phase 与自动启动语义的关键接口

## 断点闭环（用本仓库 Lab/Test 跑一遍）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartLifecycleLabTest.java`
  - `smartLifecycleStartsInPhaseOrder_andStopsInReverseOrder()`

建议断点：

1) `DefaultLifecycleProcessor#startBeans`：观察 start 为什么按 phase 升序
2) `SmartLifecycle#start`（在 Lab 里的实现）：观察实际 start 调用顺序（A → B）
3) `DefaultLifecycleProcessor#stopBeans`：观察 stop 为什么按 phase 反序
4) `SmartLifecycle#stop` / `stop(Runnable)`：观察容器为什么需要 callback（否则可能卡关闭）

- 应能够解释清楚：为什么 stop 顺序是反向的吗？（提示：避免先停掉依赖者）
对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartLifecycleLabTest.java`
推荐断点：`DefaultLifecycleProcessor#startBeans`、`DefaultLifecycleProcessor#stopBeans`、`SmartLifecycle#start`

## 常见误区与边界

### 常见误区

- **误区 1：把 SmartLifecycle 当成业务逻辑入口**
  - 它更像基础设施启动/停止钩子。

- **误区 2：stop(Runnable) 不调用 callback**
  - 容器会等待 callback，用于支持异步 stop；若不调用 callback，关闭可能卡住。

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

## 自检要点
应能够解释清楚：

1) **phase 的排序规则是什么？为什么 phase 会影响“启动/停止顺序”？**
2) **autoStartup/isRunning 的边界是什么？什么时候会出现“看似没启动/没停止”？**
3) **SmartLifecycle 与 init/destroy 的关系是什么？各自解决的是哪个阶段的问题？**

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansSmartLifecycleLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartLifecycleLabTest.java`

上一章：[26. SmartInitializingSingleton：容器就绪后回调](26-smart-initializing-singleton.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[28. 自定义 scope 与 scoped proxy：线程 scope 复现](28-custom-scope-and-scoped-proxy.md)

<!-- BOOKIFY:END -->
