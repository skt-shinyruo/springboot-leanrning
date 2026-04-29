# BeanDefinition 覆盖（overriding）：同名 bean 是“最后一个赢”还是“直接失败”？
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 使用方式：先运行章首 Lab，把现象固化为断言；排查真实问题时，按“定义层/实例层/最终暴露对象”分层，再用断点与观察清单 收敛原因。

    观察对象：24. BeanDefinition 覆盖（overriding）：同名 bean 是“最后一个赢”还是“直接失败”？。
    主线位置：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansBeanDefinitionOverridingLabTest`。需要下探源码时，可以从 `DefaultListableBeanFactory#setAllowBeanDefinitionOverriding(...)` / `DefaultListableBeanFactory#isAllowBeanDefinitionOverriding()` / `DefaultListableBeanFactory#registerBeanDefinition` 这些入口切入。

<!-- CHAPTER-CARD:END -->


## 起点：BeanDefinition 覆盖（overriding）

先运行 `SpringCoreBeansBeanDefinitionOverridingLabTest` 固定「24. BeanDefinition 覆盖（overriding）：同名 bean 是“最后一个赢”还是“直接失败”？」的最小现象。后文只追三件事：入口方法、关键分支、可观察变量。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansBeanDefinitionOverridingLabTest`
    - 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanDefinitionOverridingLabTest.java`


## 机制主线

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

当读者注册两个同名 bean 时，会发生什么？

- 有的环境里：**最后一个覆盖前一个**
- 有的环境里：**直接异常**

这不是黑箱，是容器的一个开关控制的：

- `DefaultListableBeanFactory#setAllowBeanDefinitionOverriding(...)`

## allowBeanDefinitionOverriding=true：最后一个 wins

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanDefinitionOverridingLabTest.java`
  - `whenBeanDefinitionOverridingIsAllowed_lastDefinitionWins()`（证据：同名注册两次，最后一个生效）

可以观察到：

- 同名 `duplicate` 注册两次
- 最终 `getBean(Marker.class)` 得到的是第二次注册的定义

### 机制系统阐述：条件 → 分支 → 结果

**条件**：`DefaultListableBeanFactory#isAllowBeanDefinitionOverriding()` 为 `true`
**分支**：`registerBeanDefinition` 检测到同名时进入“覆盖”分支
**结果**：registry 中 **BeanDefinition 被替换**（但已创建的单例不会回滚）
**断点入口**：`DefaultListableBeanFactory#registerBeanDefinition`

## allowBeanDefinitionOverriding=false：同名注册 fail-fast

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanDefinitionOverridingLabTest.java`
  - `whenBeanDefinitionOverridingIsDisallowed_registeringSameBeanNameFailsFast()`（证据：第二次注册直接抛异常）

可以观察到：

- 第二次注册直接抛 `BeanDefinitionOverrideException`
- 甚至还不需要 refresh，注册阶段就失败

## 覆盖语义的来源：Spring vs Boot 的开关路径

在纯 Spring 容器里，覆盖与否只由 `DefaultListableBeanFactory` 的开关决定；
在 Spring Boot 中，开关通常会被 `SpringApplication` 或配置项提前设置。

需要明确“是谁设置了开关”：

- 代码路径：`SpringApplication#setAllowBeanDefinitionOverriding(...)`
- 配置路径：`spring.main.allow-bean-definition-overriding`

> 经验提示：**不要假设默认值**。不同运行方式/版本可能不一致，最稳妥的证据链是断点观察该开关被设置的时机。

## 定义层覆盖 vs 实例缓存：覆盖不会回滚已创建单例

这是最容易导致“定义已覆盖，但注入仍为旧对象”的原因：

- 覆盖只替换 **BeanDefinition**
- `singletonObjects` 里的已创建实例 **不会自动清理/替换**

因此要区分两个阶段：

1. **注册阶段**：`registerBeanDefinition` 决定“覆盖或失败”
2. **实例阶段**：`getBean` 先检查 `singletonObjects`，若已存在则直接返回

排障时需要同时看两处：

- `DefaultListableBeanFactory#getBeanDefinition(beanName)`：定义是否已被覆盖
- `DefaultSingletonBeanRegistry#singletonObjects`：实例是否仍是旧对象

## 为什么这个点重要？

因为它会影响在工程里“怎么理解装配冲突”：

- `DefaultListableBeanFactory#setAllowBeanDefinitionOverriding`：覆盖开关本身（决定同名注册是覆盖还是 fail-fast）
- `DefaultListableBeanFactory#registerBeanDefinition`：同名冲突发生的主入口（抛 `BeanDefinitionOverrideException` 的常见位置）
- `DefaultListableBeanFactory#isAllowBeanDefinitionOverriding`：注册逻辑里读取开关的位置（解释“为什么同一份代码在不同环境不一样”）
- `BeanDefinitionOverrideException#getBeanName`：定位冲突 beanName 的直接抓手（排查时先锁定名称而非类型）
- `DefaultListableBeanFactory#getBeanDefinition`：确认最终注册进容器的定义是哪一个（对照“last wins”）

可观测性补充（本仓库提供的排障小工具）：

- `BeanDefinitionOriginDumper.dump(beanFactory, beanName)`：把 beanDefinition 的 resourceDescription/source/factoryMethod 等“来源线索”打印出来
  - 对应 Lab：`SpringCoreBeansBeanDefinitionOverridingLabTest`（允许覆盖场景会输出 dump，可以直接看到最终保留下来的来源标记）

入口：

1. `DefaultListableBeanFactory#setAllowBeanDefinitionOverriding`：确认本次测试里开关的取值
2. `DefaultListableBeanFactory#registerBeanDefinition`：观察第二次注册同名 beanName 时走“覆盖”还是“抛异常”分支
3. `DefaultListableBeanFactory#getBeanDefinition`：在允许覆盖的场景下，确认最终 registry 里保存的是哪一个定义

## 排障分流：这是定义层问题还是实例层问题？
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


这章适合用“异常类型”快速分流：

- **定义层（本章）**：`BeanDefinitionOverrideException` / “注册同名 beanName 失败”
  - 关键入口：`DefaultListableBeanFactory#registerBeanDefinition`
  - 关键变量：`isAllowBeanDefinitionOverriding`（决定 fail-fast vs last-wins）
  - 关键动作：先锁定冲突的 **beanName**，再追“谁先注册、谁后注册”
- **实例层（不是 overriding 能解决）**：`NoUniqueBeanDefinitionException` / “同类型多候选注入歧义”
  - 关键入口：`DefaultListableBeanFactory#doResolveDependency`
  - 修复方向：`@Primary/@Qualifier` 或让自动配置 back-off（见 [03](ioc-dependency-injection-resolution.md)、[33](wiring-autowire-candidate-selection-primary-priority-order.md)、[10](boot-spring-boot-auto-configuration.md)）

## 可复现闭环（用本仓库实验/测试运行一次）

至少需要得到并复述三条结论：

1. **允许覆盖：后注册 wins**
   - 断点：`registerBeanDefinition`
   - 断言：最终 `getBeanDefinition(beanName)` 指向第二次注册来源
2. **禁止覆盖：注册阶段 fail-fast**
   - 断点：`registerBeanDefinition`
   - 断言：抛 `BeanDefinitionOverrideException`，无需进入 refresh
3. **覆盖不影响已创建单例**
   - 断点：`AbstractBeanFactory#doGetBean`
   - 断言：已有 `singletonObjects` 时直接返回旧对象
## 验证标准：BeanDefinition 覆盖（overriding）

- 常问：BeanDefinition overriding 解决的是什么问题？
  - 答题要点：解决“同名 BeanDefinition 冲突”的定义层问题；开关决定是 last-wins 还是 fail-fast。
- 常见追问：overriding 和“按类型注入歧义（NoUnique）”是一回事吗？
  - 答题要点：不是；overriding 是 name-based 定义冲突；注入歧义是 type-based 候选收敛问题。
- 常见追问：如何用断点证明“冲突发生在注册阶段，而不是实例化阶段”？
  - 答题要点：在 `registerBeanDefinition` 处观察分支；fail-fast 场景甚至不需要 refresh 就会抛 `BeanDefinitionOverrideException`。

## 面试常问（overriding 与注入歧义不是一回事）

- 常问：BeanDefinition overriding 是什么？它解决什么问题？
  - 答题要点：解决“同名 BeanDefinition 冲突”的定义层问题；开关控制是否允许后注册覆盖先注册。
- 常见追问：它和“按类型注入选择（多候选）”是什么关系？
  - 答题要点：几乎无关：注入歧义是“同类型多候选怎么收敛”；overriding 是“同名定义冲突怎么处理”。不要混用概念。

## 排障决策表（overriding：从异常到修复）

| 现象 | 最可能根因 | 证据（断点/观察点） | 修复思路 | 验证方式（本仓库） |
| --- | --- | --- | --- | --- |
| 启动期 `BeanDefinitionOverrideException` | 禁止 overriding（fail-fast）且同名重复注册 | 断点 `DefaultListableBeanFactory#registerBeanDefinition`；看 `isAllowBeanDefinitionOverriding` | 改名/去重；或明确开启 overriding（但要承担可观测性成本） | `SpringCoreBeansBeanDefinitionOverridingLabTest` |
| 启动正常但行为“像被悄悄改了” | 允许 overriding（last-wins），后注册覆盖前注册 | `getBeanDefinition(beanName)` 对照 source/resource/factoryMethod；看第二次注册发生点 | 优先禁止 overriding；或完善来源追踪与命名规范 | `SpringCoreBeansBeanDefinitionOriginLabTest` + overriding Lab |
| 若希望用 overriding 解决注入歧义 | 概念误用：这是 type-based 的候选收敛问题 | `doResolveDependency`→`findAutowireCandidates` | 使用 `@Qualifier/@Primary/@Priority` 收敛，或让 auto-config back-off | [03](ioc-dependency-injection-resolution.md)、[33](wiring-autowire-candidate-selection-primary-priority-order.md) |

## 边界分流：BeanDefinition 覆盖（overriding）

### 误判点：不要把外层现象当成根因

- **误区 1：把覆盖当成“解决歧义”的手段**
  - 覆盖解决的是“同名冲突”，不是“同类型多实现注入”的歧义。

- **误区 2：不同环境默认值不同**
  - Boot 环境与纯 Spring 容器的默认行为可能不同；不要靠猜。

## 收束：BeanDefinition 覆盖（overriding）


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreBeansBeanDefinitionOverridingLabTest`
- 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanDefinitionOverridingLabTest.java`

<!-- BOOKIFY:END -->
