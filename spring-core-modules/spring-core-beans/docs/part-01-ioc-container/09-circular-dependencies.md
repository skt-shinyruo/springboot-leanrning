# 09. 循环依赖：现象、原因与规避（constructor vs setter）

## 导读

- 本章主题：**循环依赖：现象、原因与规避（constructor vs setter）**
- 阅读方式建议：先跑“constructor fail-fast vs setter 可能成功”的最小实验，再带着断点把“为什么能救/为什么救不了”的证据链走通。

!!! summary "本章要点"

    - 循环依赖不是“Spring 的黑魔法题”，它首先是一个**依赖图/职责边界**问题：能启动不代表设计健康。
    - **constructor cycle 基本 fail-fast**：因为构造器依赖发生在实例化之前，容器还没机会产生“可注入的引用”。
    - **setter cycle 有时能成功**：因为 singleton 创建过程中存在一个“提前暴露（early exposure）”窗口，容器可以先让依赖方拿到一个 early reference 把环跑起来。
    - “三级缓存”不是背字段名：它表达的是三种语义（final / early / factory），并把 early reference 的产生时机钉死在 `doCreateBean` 的窗口期。
    - 工程上优先级：**重构消环 > 延迟依赖（@Lazy/ObjectProvider）> 临时开关**；把所有注入改成 setter 只是在制造更隐蔽的故障。

!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansEarlyReferenceLabTest`
    - Test file：
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansCircularDependencyBoundaryLabTest.java`
      - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java`

## 机制主线：为什么 constructor 死、setter 有时能活？

把循环依赖先分成两类（这是排障时最省脑的第一步）：

1) **构造器循环依赖（constructor cycle）**：依赖发生在“实例化之前” → 容器没有 early exposure 的窗口 → 典型 fail-fast
2) **属性/Setter 循环依赖（setter/field cycle）**：依赖发生在“实例已创建、但尚未完成初始化”的窗口期 → 容器可能提前暴露一个引用 → 有机会把环跑起来

这也是为什么同样是“相互依赖”，表现会完全不同。

---

## 1. 先把现象固定成断言（不要靠脑补）

建议你先跑完下面两类现象，保证你能“用测试复现”，再进入源码断点：

- constructor cycle（fail-fast）：`SpringCoreBeansContainerLabTest#circularDependencyWithConstructorsFailsFast`
- setter cycle（可能成功）：`SpringCoreBeansContainerLabTest#circularDependencyWithSettersMaySucceedViaEarlySingletonExposure`

如果你希望把“打断 constructor 环”的工程手段也一起验证（而不是只背概念），再跑：

- `SpringCoreBeansCircularDependencyBoundaryLabTest#constructorCycleCanBeBrokenViaLazyInjectionPointProxy`
- `SpringCoreBeansCircularDependencyBoundaryLabTest#constructorCycleCanBeBrokenViaObjectProvider`

> 这里的重点是：constructor 环“不是靠三级缓存救”，而是靠“延迟获取依赖”改变时机；这属于工程层面的折中，代价要你自己承担。

---

## 2. 三层缓存的真实语义：final / early / factory

循环依赖相关的缓存都在 `DefaultSingletonBeanRegistry` 里。你不需要背字段名，但你必须能把三类语义对上：

- `singletonObjects`：一级缓存，**final**（完全初始化完成后对外暴露的单例）
- `earlySingletonObjects`：二级缓存，**early**（循环依赖窗口期暴露的引用，可能是 raw，也可能是 proxy）
- `singletonFactories`：三级缓存，**factory**（一个 `ObjectFactory`，用于“按需生成 early reference”）

这套设计解决的是一个非常具体的问题：

> 当 A 在创建中（还没初始化完）但 B 需要注入 A 时，容器能不能给 B 一个“暂时可用”的 A 引用？

它的答案是：**可以，但只能在 singleton 的特定窗口期，并且通过 factory 延迟决定 early reference 的形态**。

---

## 3. 关键窗口期：early exposure 发生在 `doCreateBean` 的哪一步？

把 `doCreateBean` 只看成 4 句话（足够你对照断点理解）：

1) **实例化**：先把 bean new 出来（此时还没有属性注入，也没有初始化回调）
2) **（可选）提前暴露**：如果允许循环依赖，注册一个 `singletonFactory` 到三级缓存（还没产生 early object）
3) **属性填充**：开始解析依赖并注入（setter/field 注入在这里发生）
4) **初始化**：执行 Aware/@PostConstruct/afterPropertiesSet/initMethod，以及 after-init BPP 可能返回 proxy，然后把 final 对象放入一级缓存

setter cycle 之所以“可能成功”，就在于第 2 步留下的窗口：**B 在创建时请求 A，`getSingleton(..., allowEarlyReference=true)` 可以从 factory 里拿到一个 early reference**。

constructor cycle 之所以“基本无解”，就在于构造器依赖发生在第 1 步之前：**连实例都还没 new 出来，谈不上提前暴露**。

---

## 4. 断点闭环：从 `getSingleton` 看清“到底救没救”

建议至少跑一次“看缓存变化”的断点闭环（跑一次，你以后就很难再被“三级缓存神话”误导）。

### 4.1 推荐断点（按收益排序）

1) `DefaultSingletonBeanRegistry#getSingleton`：看三层命中分支（final/early/factory）
2) `DefaultSingletonBeanRegistry#addSingletonFactory`：看“何时把 factory 放进三级缓存”（early exposure 的起点）
3) `AbstractAutowireCapableBeanFactory#doCreateBean`：看 earlySingletonExposure 的判定与创建窗口
4) `AbstractAutowireCapableBeanFactory#populateBean`：看 setter/field 注入发生的时机

### 4.2 固定观察点（watch list）

在 `getSingleton(beanName, allowEarlyReference)` 里只盯这些就够了：

- `isSingletonCurrentlyInCreation(beanName)`：是否处于创建中（决定 early 分支是否可能发生）
- `singletonObjects.containsKey(beanName)`：final 是否已产生
- `earlySingletonObjects.containsKey(beanName)`：是否已有 early 引用
- `singletonFactories.containsKey(beanName)`：是否存在 early 工厂（允许“按需创建”）

在 `doCreateBean(beanName, ...)` 里建议盯：

- `earlySingletonExposure`：是否允许 early exposure（关键开关）
- `exposedObject`：最终对外暴露对象（可能被 after-init BPP 替换成 proxy）
- `earlySingletonReference`：early 引用（出现循环依赖时常能看到它）

### 4.3 你应该能复述的“证据链”

当你跑 setter cycle 并在断点里看到下面这条链，就算真正掌握了：

1) 创建 A：`doCreateBean("a")` → `addSingletonFactory("a", ...)`（三级缓存出现工厂）
2) 创建 B：注入时需要 A → `getSingleton("a", allowEarlyReference=true)`
3) `getSingleton` 发现 A “in creation” 且存在 factory → 调用 factory 生成 early reference
4) B 拿到 early reference 完成创建 → 回到 A 的 populate/initialize → 最终对象进入 `singletonObjects`

> 提醒：这一章到这里为止就够了。若你想进一步搞清“early reference 应该是 raw 还是 proxy”“raw vs wrapped 不一致为何会 fail-fast”，请去看 [16. early reference 与循环依赖](../part-03-container-internals/16-early-reference-and-circular.md)（那一章专门讲这个坑）。

---

## 5. Framework vs Boot：策略差异（不要用“能启动”骗自己）

在纯 Spring Framework 容器里，循环依赖策略通常更“宽松”；在 Spring Boot 里，启动过程往往会更倾向 fail-fast，并提供配置开关（例如 `spring.main.allow-circular-references`）。

学习阶段你可以在不同容器之间切换来观察差异，但工程上请牢记：

- “容器能救活”只是机制的副作用，不是你设计循环依赖的理由
- 依赖图是系统复杂度的真实来源：越早消环越省成本

---

## 6. 工程处理策略（按优先级）

### 6.1 首选：重构消环（把依赖图改对）

常见做法：

- 把双向依赖拆成单向：抽出第三方组件/接口（例如把“协调逻辑”下沉到 `Orchestrator`）
- 引入事件/回调解耦（见 `spring-core-events` 模块）
- 把“需要对方”变成“需要对方的能力”（接口化 + 依赖倒置）

### 6.2 次选：延迟依赖打断环（了解代价后再用）

- `ObjectProvider<T>`：把“构造时必须拿到”改成“用到时再拿”（依赖关系变成运行时分支）
- `@Lazy`（注入点代理）：把依赖解析延迟到第一次使用（多一层代理/调试复杂度上升）

### 6.3 不推荐：为了启动而把所有依赖改 setter

setter 注入能“让环跑起来”的前提是：你愿意接受半初始化窗口 + 更隐蔽的运行时问题。学习阶段可以用它理解机制，工程里通常是更糟的选择。

---

## 常见坑与边界

1) **误区：Spring 解决了循环依赖**
   - 更准确的说法：Spring 只在非常特定条件下“救活某些环”（singleton + early exposure + 合适的增强/代理策略）。
2) **误区：constructor 环就一定无解**
   - “能启动”并不等于“无代价”：`@Lazy/ObjectProvider` 的本质是改变时机，并引入代理/分支复杂度。
3) **误区：只看启动成功，不看对象形态一致性**
   - 一旦 AOP/代理介入，early 与 final 形态不一致会让问题更隐蔽；这部分请看 [16](../part-03-container-internals/16-early-reference-and-circular.md)。

---

## 面试常问（循环依赖）

1) **constructor cycle 为什么基本 fail-fast？setter cycle 为什么有时能救？**
   - 要点：constructor 依赖发生在实例化之前，没有 early exposure 窗口；setter/field 依赖发生在实例已创建但未初始化完的窗口期，singleton 可以提前暴露引用把环跑起来。
   - 证据链：`doCreateBean` 的 early exposure（`addSingletonFactory`）+ `getSingleton(beanName, allowEarlyReference)` 三层命中分支。

2) **三级缓存到底解决了什么问题？它没解决什么？**
   - 要点：它解决的是“singleton 在创建窗口期的提前引用”，不是“任意依赖图都能救”；prototype、constructor cycle 等场景仍然是边界。
   - 证据链：`DefaultSingletonBeanRegistry#getSingleton`（final/early/factory 三层）+ `earlySingletonObjects` 命中情况。

3) **`getEarlyBeanReference` 的意义是什么？为什么会牵扯 raw vs wrapped 一致性？**
   - 要点：early 引用是否等于最终暴露形态（proxy/wrapper）很关键；不一致会导致 raw 注入绕过代理，或触发一致性保护 fail-fast。
   - 证据链：`AbstractAutowireCapableBeanFactory#getEarlyBeanReference` + `SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference` + `doCreateBean` 尾部一致性检查。

推荐复习入口：`appendix/93-interview-playbook.md`（Q6/Q7）。

## 源码调用链（方法级）：三层缓存 + early reference 在哪发生

当你在面试/排障里讲循环依赖，最关键的是把“结论”落到方法级调用链：

1) `AbstractAutowireCapableBeanFactory#doCreateBean`（单 bean 创建主线）
2) `DefaultSingletonBeanRegistry#addSingletonFactory`（early exposure：注册 early factory）
3) `DefaultSingletonBeanRegistry#getSingleton`（三层缓存命中分支：final/early/factory）
4) `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`（决定 early 形态：raw vs proxy）

你不需要背实现细节，但必须能解释“为什么在这个窗口期能救 setter 循环、救不了 constructor 循环”。

## 一句话自检

你应该能用 3 句完整回答：

1) constructor cycle 为什么 fail-fast？（依赖发生在实例化之前，没有 early exposure 窗口）
2) setter cycle 为什么可能成功？（singleton 创建窗口期 + early exposure + `getSingleton(..., allowEarlyReference=true)`）
3) 工程上你怎么处理？（重构消环优先；延迟依赖是折中；setter 不是默认解法）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansContainerLabTest` / `SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansEarlyReferenceLabTest`
- Test file：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansContainerLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansCircularDependencyBoundaryLabTest.java`
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part03_container_internals/SpringCoreBeansEarlyReferenceLabTest.java`

上一章：[08. `FactoryBean`：产品 vs 工厂（以及 `&` 前缀）](08-factorybean.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[10. Spring Boot 自动装配如何影响 Bean（Auto-configuration）](../part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md)

<!-- BOOKIFY:END -->
