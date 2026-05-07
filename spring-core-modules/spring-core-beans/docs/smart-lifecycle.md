# SmartLifecycle：start/stop 时机与 phase 顺序
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 使用方式：先运行章首 Lab，把现象固化为断言；排查真实问题时，按“定义层/实例层/最终暴露对象”分层，再用断点与观察清单收敛原因。

    观察对象：27. SmartLifecycle：start/stop 时机与 phase 顺序。
    主线位置：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansSmartLifecycleLabTest`。需要下探源码时，可以从 `LifecycleProcessor#onRefresh` / `DefaultLifecycleProcessor#startBeans` / `DefaultLifecycleProcessor#stopBeans` 这些入口切入。

<!-- CHAPTER-CARD:END -->


## 问题：容器启动后哪些组件会自动 start，关闭时按什么顺序 stop

先运行本章 Lab，把核心现象固定为可复现事实；随后围绕入口方法、关键分支和可观察变量阅读正文。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansSmartLifecycleLabTest`
    - 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartLifecycleLabTest.java`


## 机制主线

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

`SmartLifecycle` 是容器提供的“启动/停止阶段”扩展点。

它适合表达：

- 希望在容器 refresh 完成后自动 start
- 希望在容器 close 时 stop
- 希望多个组件之间按 phase 排序

### 机制边界：条件、分支与结果

**条件**：bean 实现 `SmartLifecycle`，且 `isAutoStartup()` 为 `true`
**分支**：`LifecycleProcessor#onRefresh` → `DefaultLifecycleProcessor#startBeans` 按 phase 升序启动
**结果**：refresh 结束自动 start；close 阶段按 phase 反序 stop
**断点入口**：`DefaultLifecycleProcessor#startBeans` / `DefaultLifecycleProcessor#stopBeans`

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

## 现象：start 按 phase 升序，stop 反向

对应测试：

- A：phase=0
- B：phase=1

可以观察到：

- refresh 时：`start:A` → `start:B`
- close 时：`stop:B` → `stop:A`

读者还应该补齐两个“真实项目更常见”的边界：

- **autoStartup=false**：refresh 不会自动 start（否则很多“为什么它在启动后即开始运行”难以解释清楚）
- **stop(callback)**：容器为什么要 callback（否则 shutdown 可能卡住）

对应实验（本仓库已补齐）：

- `SpringCoreBeansSmartLifecycleLabTest#smartLifecycleDoesNotAutoStart_whenIsAutoStartupIsFalse`
- `SpringCoreBeansSmartLifecycleLabTest#containerStopsSmartLifecycle_viaStopCallbackMethod_notStopMethod`

## 机制：LifecycleProcessor 统一管理

容器内部通过 `LifecycleProcessor`（默认 `DefaultLifecycleProcessor`）来：

- 在 refresh 阶段触发 `onRefresh()` → start
- 在 close 阶段触发 `onClose()` → stop

所以它不是“读者手动调用 start/stop”，而是容器生命周期的一部分。

- `AbstractApplicationContext#finishRefresh`：refresh 收尾阶段（触发 `LifecycleProcessor#onRefresh`）
- `LifecycleProcessor#onRefresh`：生命周期统一入口（默认实现是 `DefaultLifecycleProcessor`）
- `DefaultLifecycleProcessor#startBeans`：start 的排序与触发点（phase 升序）
- `LifecycleProcessor#onClose`：close 阶段入口（触发 stop）
- `DefaultLifecycleProcessor#stopBeans`：stop 的排序与触发点（phase 反序）

补充一个关键但容易忽略的事实：

- `DefaultLifecycleProcessor` 的 stop 逻辑会按 phase 分组，并等待 `stop(callback)` 的回调完成（用于支持异步 stop）
- 如果回调永远不触发，容器会等待直到 `timeoutPerShutdownPhase` 超时（这就是“关闭卡住”的来源之一）

入口：

- 入口测试（方法级）：`SpringCoreBeansSmartLifecycleLabTest#smartLifecycleStartsInPhaseOrder_andStopsInReverseOrder`
- 运行方式：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansSmartLifecycleLabTest#smartLifecycleStartsInPhaseOrder_andStopsInReverseOrder test`

## 排障分流：这是定义层问题还是实例层问题？
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


- “SmartLifecycle 没自动 start” → **实例层（生命周期触发条件）**：`isAutoStartup()` 是否为 true？context 是否 refresh 完成？（看 `finishRefresh`）
- “start/stop 顺序不符合预期” → **实例层（phase 语义）**：检查 `getPhase()` 值与依赖关系（本章第 1 节）
- “close 卡住/stop 不返回” → **实例层（异步 stop）**：`stop(Runnable)` 必须调用 callback（本章第 3 节）
- “把它当业务逻辑入口导致复杂副作用” → **设计风险**：它更适合作为基础设施 start/stop 钩子（本章第 3 节）

## 面试常问（SmartLifecycle / phase）

1. SmartLifecycle 的 start/stop 触发点分别落在 refresh/close 的哪个阶段？（提示：LifecycleProcessor）
2. 为什么 start 按 phase 升序，而 stop 按 phase 反序？（提示：依赖关系与安全停机）
3. `stop(Runnable)` 为什么必须调用 callback？如果不调用，会出现什么现象？

## 实验：把现象固定成断言

本章可复核的实验入口：
- Lab：`SpringCoreBeansSmartLifecycleLabTest`
- 命令：`mvn -pl :spring-core-beans test`（亦可在 IDE 中运行上述测试类）

### 从实验现象看边界

## 运行入口

- 入口测试（先运行通过，再设置断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartLifecycleLabTest.java`
  - `mvn -pl :spring-core-beans -Dtest=SpringCoreBeansSmartLifecycleLabTest test`

对应实验：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartLifecycleLabTest.java`

- `SpringCoreBeansSmartLifecycleLabTest.smartLifecycleStartsInPhaseOrder_andStopsInReverseOrder()`

该实验中注册了两个 lifecycle：

## 源码锚点：从这里设置断点

- `AbstractApplicationContext#finishRefresh`：refresh 末尾阶段（触发生命周期处理器）
- `DefaultLifecycleProcessor#onRefresh`：容器刷新完成后的生命周期启动入口
- `DefaultLifecycleProcessor#startBeans`：按 phase 分组并启动的主算法
- `SmartLifecycle#getPhase` / `isAutoStartup` / `start`：phase 与自动启动语义的关键接口

## 断点闭环（用本仓库实验/测试运行一次）

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartLifecycleLabTest.java`
  - `smartLifecycleStartsInPhaseOrder_andStopsInReverseOrder()`

断点入口：

1. `DefaultLifecycleProcessor#startBeans`：观察 start 为什么按 phase 升序
2. `SmartLifecycle#start`（在 Lab 里的实现）：观察实际 start 调用顺序（A → B）
3. `DefaultLifecycleProcessor#stopBeans`：观察 stop 为什么按 phase 反序
4. `SmartLifecycle#stop` / `stop(Runnable)`：观察容器为什么需要 callback（否则可能卡关闭）

- 需要解释清楚：为什么 stop 顺序是反向的吗？（提示：避免先停掉依赖者）
对应实验/测试：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartLifecycleLabTest.java`
断点入口：`DefaultLifecycleProcessor#startBeans`、`DefaultLifecycleProcessor#stopBeans`、`SmartLifecycle#start`

## 边界：SmartLifecycle：start/stop 时机与 phase 顺序

### 误判点：不要把外层现象当成根因

- **误区 1：把 SmartLifecycle 当成业务逻辑入口**
  - 其定位更接近基础设施启动/停止钩子。

- **误区 2：stop(Runnable) 不调用 callback**
  - 容器会等待 callback，用于支持异步 stop；若不调用 callback，关闭可能卡住。

## 验收口径：SmartLifecycle：start/stop 时机与 phase 顺序
需要解释清楚：

1. **phase 的排序规则是什么？为什么 phase 会影响“启动/停止顺序”？**
2. **autoStartup/isRunning 的边界是什么？什么时候会出现“看似没启动/没停止”？**
3. **SmartLifecycle 与 init/destroy 的关系是什么？各自解决的是哪个阶段的问题？**

<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreBeansSmartLifecycleLabTest`
- 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansSmartLifecycleLabTest.java`

<!-- BOOKIFY:END -->
