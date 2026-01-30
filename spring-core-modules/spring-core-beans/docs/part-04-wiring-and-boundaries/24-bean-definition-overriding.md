# 24. BeanDefinition 覆盖（overriding）：同名 bean 是“最后一个赢”还是“直接失败”？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：24. BeanDefinition 覆盖（overriding）：同名 bean 是“最后一个赢”还是“直接失败”？
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里优先按“定义层/实例层/最终暴露对象”分层，再用断点与 watch list 收敛原因。
    - 原理：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
    - 源码入口：`DefaultListableBeanFactory#setAllowBeanDefinitionOverriding(...)` / `DefaultListableBeanFactory#isAllowBeanDefinitionOverriding()` / `DefaultListableBeanFactory#registerBeanDefinition`
    - 推荐 Lab：`SpringCoreBeansBeanDefinitionOverridingLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[23. FactoryBean 深挖：getObjectType/isSingleton 与缓存](23-factorybean-deep-dive.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[25. 手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱](25-programmatic-bpp-registration.md)
<!-- GLOBAL-BOOK-NAV:END -->



## 导读

- 本章主题：**24. BeanDefinition 覆盖（overriding）：同名 bean 是“最后一个赢”还是“直接失败”？**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - BeanDefinition overriding 解决的是 **name-based 的定义冲突**：同一个 beanName 被注册多次时，是 last-wins 还是 fail-fast。
    - `allowBeanDefinitionOverriding=true`：后注册覆盖先注册（更“灵活”，但更难排障）；`false`：注册阶段直接失败（更安全、更可控）。
    - overriding ≠ 按类型多候选（`NoUniqueBeanDefinitionException`）：不要用“允许覆盖”去解决注入歧义。
    - 排障关键不是“类型”，而是 **beanName + 来源**：谁先注册、谁后注册、最终 registry 里保存的是哪一个（本仓库 Lab 已补齐 BeanDefinition 来源 dump 的证据链）。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansBeanDefinitionOverridingLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanDefinitionOverridingLabTest.java`

<!-- AE-DEEPENING:START -->
!!! tip "内容级再加深（A–E 维度）"

    - A（证据链）：“覆盖发生在注册阶段”的证据链与配置入口（Framework/Boot 差异需明确）。
    - B（边界反例）：反例：覆盖导致注入命中改变但不易察觉；与 auto-config back-off 的交互误判。
    - C（排障 SOP）：排障：同名 bean 冲突/覆盖导致行为偏差的 SOP（先看谁注册、后看覆盖策略）。
    - D（断点观察）：观察点：注册冲突位置、BeanDefinition 源信息（如 resourceDescription）。
    - E（面试复述）：面试追问：为什么团队通常不建议默认允许覆盖？如何给出工程化理由与证据。
<!-- AE-DEEPENING:END -->
## 机制主线

当读者注册两个同名 bean 时，会发生什么？

- 有的环境里：**最后一个覆盖前一个**
- 有的环境里：**直接报错**

这不是黑箱，是容器的一个开关控制的：

- `DefaultListableBeanFactory#setAllowBeanDefinitionOverriding(...)`

## 1. allowBeanDefinitionOverriding=true：最后一个 wins

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanDefinitionOverridingLabTest.java`
  - `whenBeanDefinitionOverridingIsAllowed_lastDefinitionWins()`（证据：同名注册两次，最后一个生效）

可以观察到：

- 同名 `duplicate` 注册两次
- 最终 `getBean(Marker.class)` 得到的是第二次注册的定义

### 机制讲透：条件 → 分支 → 结果

**条件**：`DefaultListableBeanFactory#isAllowBeanDefinitionOverriding()` 为 `true`  
**分支**：`registerBeanDefinition` 检测到同名时进入“覆盖”分支  
**结果**：registry 中 **BeanDefinition 被替换**（但已创建的单例不会回滚）  
**断点建议**：`DefaultListableBeanFactory#registerBeanDefinition`

## 2. allowBeanDefinitionOverriding=false：同名注册 fail-fast

对应测试：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanDefinitionOverridingLabTest.java`
  - `whenBeanDefinitionOverridingIsDisallowed_registeringSameBeanNameFailsFast()`（证据：第二次注册直接抛异常）

可以观察到：

- 第二次注册直接抛 `BeanDefinitionOverrideException`
- 甚至还不需要 refresh，注册阶段就失败

## 3. 覆盖语义的来源：Spring vs Boot 的开关路径

在纯 Spring 容器里，覆盖与否只由 `DefaultListableBeanFactory` 的开关决定；  
在 Spring Boot 中，开关通常会被 `SpringApplication` 或配置项提前设置。

需要明确“是谁设置了开关”：

- 代码路径：`SpringApplication#setAllowBeanDefinitionOverriding(...)`
- 配置路径：`spring.main.allow-bean-definition-overriding`

> 经验提示：**不要假设默认值**。不同运行方式/版本可能不一致，最稳妥的证据链是断点观察该开关被设置的时机。

## 4. 定义层覆盖 vs 实例缓存：覆盖不会回滚已创建单例

这是最容易造成“我明明覆盖了，但注入还是旧的”的原因：

- 覆盖只替换 **BeanDefinition**
- `singletonObjects` 里的已创建实例 **不会自动清理/替换**

因此要区分两个阶段：

1) **注册阶段**：`registerBeanDefinition` 决定“覆盖或失败”
2) **实例阶段**：`getBean` 先检查 `singletonObjects`，若已存在则直接返回

排障时需要同时看两处：

- `DefaultListableBeanFactory#getBeanDefinition(beanName)`：定义是否已被覆盖
- `DefaultSingletonBeanRegistry#singletonObjects`：实例是否仍是旧对象

## 5. 为什么这个点重要？

因为它会影响在工程里“怎么理解装配冲突”：

- `DefaultListableBeanFactory#setAllowBeanDefinitionOverriding`：覆盖开关本身（决定同名注册是覆盖还是 fail-fast）
- `DefaultListableBeanFactory#registerBeanDefinition`：同名冲突发生的主入口（抛 `BeanDefinitionOverrideException` 的常见位置）
- `DefaultListableBeanFactory#isAllowBeanDefinitionOverriding`：注册逻辑里读取开关的位置（解释“为什么同一份代码在不同环境不一样”）
- `BeanDefinitionOverrideException#getBeanName`：定位冲突 beanName 的直接抓手（排查时先锁定名称而非类型）
- `DefaultListableBeanFactory#getBeanDefinition`：确认最终注册进容器的定义是哪一个（对照“last wins”）

可观测性补充（本仓库提供的排障小工具）：

- `BeanDefinitionOriginDumper.dump(beanFactory, beanName)`：把 beanDefinition 的 resourceDescription/source/factoryMethod 等“来源线索”打印出来
  - 对应 Lab：`SpringCoreBeansBeanDefinitionOverridingLabTest`（允许覆盖场景会输出 dump，应能够直接看到最终保留下来的来源标记）

入口：

1) `DefaultListableBeanFactory#setAllowBeanDefinitionOverriding`：确认本次测试里开关的取值
2) `DefaultListableBeanFactory#registerBeanDefinition`：观察第二次注册同名 beanName 时走“覆盖”还是“抛异常”分支
3) `DefaultListableBeanFactory#getBeanDefinition`：在允许覆盖的场景下，确认最终 registry 里保存的是哪一个定义

## 6. 排障分流：这是定义层问题还是实例层问题？

这章非常适合用“异常类型”快速分流：

- **定义层（本章）**：`BeanDefinitionOverrideException` / “注册同名 beanName 失败”
  - 关键入口：`DefaultListableBeanFactory#registerBeanDefinition`
  - 关键变量：`isAllowBeanDefinitionOverriding`（决定 fail-fast vs last-wins）
  - 关键动作：先锁定冲突的 **beanName**，再追“谁先注册、谁后注册”
- **实例层（不是 overriding 能解决）**：`NoUniqueBeanDefinitionException` / “同类型多候选注入歧义”
  - 关键入口：`DefaultListableBeanFactory#doResolveDependency`
  - 修复方向：`@Primary/@Qualifier` 或让自动配置 back-off（见 [03](../part-01-ioc-container/014-03-dependency-injection-resolution.md)、[33](33-autowire-candidate-selection-primary-priority-order.md)、[10](../part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md)）

## 可复现闭环（用本仓库 Lab/Test 跑一遍）

至少应能够跑出并复述三条结论：

1) **允许覆盖：后注册 wins**  
   - 断点：`registerBeanDefinition`  
   - 断言：最终 `getBeanDefinition(beanName)` 指向第二次注册来源
2) **禁止覆盖：注册阶段 fail-fast**  
   - 断点：`registerBeanDefinition`  
   - 断言：抛 `BeanDefinitionOverrideException`，无需进入 refresh
3) **覆盖不影响已创建单例**  
   - 断点：`AbstractBeanFactory#doGetBean`  
   - 断言：已有 `singletonObjects` 时直接返回旧对象
## 7. 自检要点

- 常问：BeanDefinition overriding 解决的是什么问题？
  - 答题要点：解决“同名 BeanDefinition 冲突”的定义层问题；开关决定是 last-wins 还是 fail-fast。
- 常见追问：overriding 和“按类型注入歧义（NoUnique）”是一回事吗？
  - 答题要点：不是；overriding 是 name-based 定义冲突；注入歧义是 type-based 候选收敛问题。
- 常见追问：如何用断点证明“冲突发生在注册阶段，而不是实例化阶段”？
  - 答题要点：在 `registerBeanDefinition` 处观察分支；fail-fast 场景甚至不需要 refresh 就会抛 `BeanDefinitionOverrideException`。

## 8. 面试常问（overriding 与注入歧义不是一回事）

- 常问：BeanDefinition overriding 是什么？它解决什么问题？
  - 答题要点：解决“同名 BeanDefinition 冲突”的定义层问题；开关控制是否允许后注册覆盖先注册。
- 常见追问：它和“按类型注入选择（多候选）”是什么关系？
  - 答题要点：几乎无关：注入歧义是“同类型多候选怎么收敛”；overriding 是“同名定义冲突怎么处理”。不要混用概念。

## 9. 排障决策表（overriding：从异常到修复）

| 现象 | 最可能根因 | 证据（断点/观察点） | 修复思路 | 验证方式（本仓库） |
| --- | --- | --- | --- | --- |
| 启动期 `BeanDefinitionOverrideException` | 禁止 overriding（fail-fast）且同名重复注册 | 断点 `DefaultListableBeanFactory#registerBeanDefinition`；看 `isAllowBeanDefinitionOverriding` | 改名/去重；或明确开启 overriding（但要承担可观测性成本） | `SpringCoreBeansBeanDefinitionOverridingLabTest` |
| 启动正常但行为“像被悄悄改了” | 允许 overriding（last-wins），后注册覆盖前注册 | `getBeanDefinition(beanName)` 对照 source/resource/factoryMethod；看第二次注册发生点 | 优先禁止 overriding；或完善来源追踪与命名规范 | `SpringCoreBeansBeanDefinitionOriginLabTest` + overriding Lab |
| 若希望用 overriding 解决注入歧义 | 概念误用：这是 type-based 的候选收敛问题 | `doResolveDependency`→`findAutowireCandidates` | 使用 `@Qualifier/@Primary/@Priority` 收敛，或让 auto-config back-off | [03](../part-01-ioc-container/014-03-dependency-injection-resolution.md)、[33](33-autowire-candidate-selection-primary-priority-order.md) |

## 10. 常见误区与边界

### 常见误区

- **误区 1：把覆盖当成“解决歧义”的手段**
  - 覆盖解决的是“同名冲突”，不是“同类型多实现注入”的歧义。

- **误区 2：不同环境默认值不同**
  - Boot 环境与纯 Spring 容器的默认行为可能不同；不要靠猜。

## 小结与下一章

- 本章完成后：请对照上一章/下一章导航继续阅读，形成模块内连续主线。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansBeanDefinitionOverridingLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansBeanDefinitionOverridingLabTest.java`

上一章：[23. FactoryBean 深挖：getObjectType/isSingleton 与缓存](23-factorybean-deep-dive.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[25. 手工添加 BeanPostProcessor：顺序与 Ordered 的陷阱](25-programmatic-bpp-registration.md)

<!-- BOOKIFY:END -->
