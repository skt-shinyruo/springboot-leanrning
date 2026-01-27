# 35. BeanDefinition 的合并（MergedBeanDefinition）：RootBeanDefinition 从哪里来？

## 导读

- 本章主题：**35. BeanDefinition 的合并（MergedBeanDefinition）：RootBeanDefinition 从哪里来？**
- 阅读方式建议：先看“本章要点”，再沿主线阅读；需要时穿插源码/断点，最后跑通实验闭环。

!!! summary "本章要点"

    - registry 里存的是 **原始 BeanDefinition**（可能是 `GenericBeanDefinition`，还带 `parentName`）；创建链路里真正使用的是 **merged 后的 `RootBeanDefinition`**。
    - merged 不只是“把 propertyValues 拼起来”，它还会把 **scope/lazy-init/init-method/解析出的类型缓存** 等元信息统一成“最终配方”（本仓库 Lab 已补齐“继承 vs 覆盖”的对照）。
    - merged 会被容器 **缓存**：你在断点里看到的 `RootBeanDefinition` 往往不是“每次现算”，而是命中缓存（这也是很多人“改了定义却没生效”的根因之一）。
    - `MergedBeanDefinitionPostProcessor` 的触发点很关键：它发生在 `doCreateBean` 中、`populateBean` 之前（实例已创建，但属性还没注入），非常适合做元数据准备/缓存。


!!! example "本章配套实验（先跑再读）"

    - Lab：`SpringCoreBeansMergedBeanDefinitionLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansMergedBeanDefinitionLabTest.java`
    - 建议先跑的方法（按理解收益排序）：
      - `mergedBeanDefinition_combinesParentAndChildMetadata_andTriggersMergedDefinitionPostProcessor`
      - `mergedBeanDefinition_inheritsAndOverridesMetadata_fromParentAndChild`

## 机制主线

很多人在深挖 Spring 容器源码时会遇到一个“卡点”：

答案基本都落在同一个机制上：**BeanDefinition 合并（merge）**。

> 核心直觉：registry 保存的是“原始定义”；真正参与创建的是“合并后的 RootBeanDefinition（merged）”。

---

你会观察到 3 个关键现象：

1) registry 里拿到的 child definition **仍然保留 parentName**，且看不到 parent 的元数据
2) `getMergedBeanDefinition(...)` 拿到的是 **`RootBeanDefinition`**，并且已经把 parent 的元数据合并进来
3) `MergedBeanDefinitionPostProcessor` 能拿到 merged 后的 `RootBeanDefinition`（可用于“预处理/缓存元数据”）

---

## 2. merged 到底“合并”了什么？

你可以把 merged 理解为：把多个来源（parent + child + defaults + 解析结果）统一成“最终配方”。

常见会在 merged 里稳定存在的内容包括：

- **property values**：父定义提供默认值、子定义追加/覆盖
- **生命周期元数据**：init-method / destroy-method 等（子覆盖父；子不声明则继承）
- **resolved target type**：创建后可解析出更具体的 beanType（影响后续处理器）
- 其他“创建需要的元数据”与缓存字段

你可以把 merge 的核心逻辑理解成下面这个“伪代码级模型”（为了理解而非逐行对齐源码）：

```text
getMergedLocalBeanDefinition(beanName):
  bd = getBeanDefinition(beanName)                 // raw definition from registry
  if bd.parentName != null:
    parentMbd = getMergedLocalBeanDefinition(bd.parentName)
    mbd = new RootBeanDefinition(parentMbd)        // inherit defaults/metadata
    mbd.overrideFrom(bd)                           // apply child overrides/additions
  else:
    mbd = copyToRootBeanDefinition(bd)             // normalize to RootBeanDefinition
  cacheMerged(beanName, mbd)
  return mbd
```

---

## 3. merged 发生在时间线哪里？

两个最有价值的入口点：

1) `AbstractBeanFactory#getMergedLocalBeanDefinition`
   - 语义：为某个 beanName 计算（或读取缓存）“最终参与创建的 merged definition”
2) `DefaultListableBeanFactory#getMergedBeanDefinition`
   - 这是你在业务/测试代码里更容易直接调用到的 public API（底层会走到上面那个方法）

你可以把它和 [00 章](../part-00-guide/011-00-deep-dive-guide.md) 的时间线对上：

- 当容器准备创建某个 bean 时，它首先会确保拿到 merged definition
- 拿到 merged 之后，才进入 `createBean → doCreateBean → populateBean → initializeBean` 这条链路

### 3.1 merged 在 `createBean` 链路中的精确位置（你在调用栈里应该看到什么）

很多人“看不懂 merged”，本质原因是：只在 `doCreateBean` 附近看对象，但没有把 merged 放回更完整的时间线。

一句话版的精确位置：

- **merged definition 的计算/缓存发生在 `doGetBean` 阶段**：进入 `createBean(...)` 之前就拿到了 `RootBeanDefinition`
- **`MergedBeanDefinitionPostProcessor` 的 hook 发生在 `doCreateBean` 阶段**：实例已创建，但属性还没填充（也就是 `populateBean(...)` 之前）

---

## 4. 为什么 merged 和“注入/生命周期元数据”强相关？

因为很多基础设施处理器需要一个稳定、完整的定义来做“元数据准备/缓存”。

典型代表就是 `MergedBeanDefinitionPostProcessor`：

- 它的入口方法是：`postProcessMergedBeanDefinition(RootBeanDefinition, Class<?>, String)`
- 它在 `doCreateBean(...)` 中被调用（在实例化之后、属性填充之前）

你可以把它理解为：

> “给你一个最终配方（merged BD），你可以在真正注入/初始化之前做一次准备工作（例如解析注解、建立缓存）。”

这也是为什么你会在源码里看到一些熟悉的基础设施处理器实现了它（例如与 `@Autowired`、`@PostConstruct` 相关的处理器家族）。

进一步阅读建议：

- 注解为什么能工作（基础设施处理器）：[12](../part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md)
- 注入发生在哪个阶段：`postProcessProperties`： [30](30-injection-phase-field-vs-constructor.md)
- 生命周期回调顺序： [17](../part-03-container-internals/17-lifecycle-callback-order.md)

---

### 5.2 推荐观察点（watch / evaluate）

- `beanName`
- `mbd` / `beanDefinition`（是否是 `RootBeanDefinition`？有哪些 property values？init-method 是谁？）
- `mbd.getParentName()`（合并后通常不再需要你手动追 parent 链了）

---

## 源码最短路径（call chain）

> 目标：给你“最短可跟栈”，并标出 merged 与 merged-hook 在链路中的精确落点。

从 `getBean(beanName)` 到创建结束的最短主干（只列关键节点）：

1) `AbstractBeanFactory#doGetBean(beanName, ...)`
2) `AbstractBeanFactory#getMergedLocalBeanDefinition(beanName)`
   - **这里计算/合并/缓存** `RootBeanDefinition (mbd)`（parent/child/默认值/解析结果统一到 mbd）
3) `AbstractAutowireCapableBeanFactory#createBean(beanName, mbd, args)`
4) `AbstractAutowireCapableBeanFactory#doCreateBean(beanName, mbd, args)`
   - `createBeanInstance(...)`（实例化）
   - `applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName)`
     - **这里触发 `MergedBeanDefinitionPostProcessor`（在注入前做元数据准备/缓存）**
   - `populateBean(...)`（属性填充/依赖注入）
   - `initializeBean(...)`（初始化回调 + after-init BPP 可能产生代理）

你只要把这条链路记住，后面看到任意“元数据为什么已经准备好/为什么看到的是 RootBeanDefinition”都能对上。

## 固定观察点（watch list）

建议在 `getMergedLocalBeanDefinition(...)` 里 watch/evaluate：

- `beanName`
- `bd`（raw definition）：是否仍带 `parentName`？scope/lazy/initMethod 是否“看起来缺失”？
- `mbd`（merged definition）：是否为 `RootBeanDefinition`？哪些字段来自 parent、哪些来自 child？
- merged 缓存（常见字段名：`mergedBeanDefinitions` 或等价结构）：是否命中缓存？是否发生“stale/需要重算”？
- `containsBeanDefinition(beanName)` / `containsLocalBean(beanName)`：确认是本地定义还是从 parent fallback

建议在 `doCreateBean(...)` 里 watch/evaluate：

- `mbdToUse`（或 `mbd`）：容器最终用于创建的 definition 引用
- `beanType` / `resolvedType`：merged 过程中/之后可能被解析并缓存（影响后续处理器分支）
- `applyMergedBeanDefinitionPostProcessors(...)` 调用点：确认它发生在 `populateBean(...)` 之前

## 反例（counterexample）

- `getBeanDefinition(beanName)` 取到的是 registry 里的“原始定义”（可能是 child，带 `parentName`，看不到 parent 的元数据）
- 真正参与创建的是 `getMergedLocalBeanDefinition(beanName)` 返回的 `RootBeanDefinition`（元数据已合并）
- 如果你只盯着原始定义，会把“定义层对象”误当成“创建时用的最终配方”，因此结论必然错位

## 源码与断点

- 建议优先从“E 中的测试用例断言”反推调用链，再定位到关键类/方法设置断点。
- 若本章包含 Spring 内部机制，请以“入口方法 → 关键分支 → 数据结构变化”三段式观察。

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（建议优先跑它们）：
- Lab：`SpringCoreBeansMergedBeanDefinitionLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（或在 IDE 直接运行上面的测试类）

### 复现/验证补充说明（来自原文迁移）

- 明明我注册的是 `GenericBeanDefinition`（或者通过注解解析得到的定义），为什么调试时经常看到的是 `RootBeanDefinition`？
- 我在 registry 里拿到的 `BeanDefinition` 看起来缺了很多信息（property、init-method 等），但创建时又“神奇地都有了”？

## 1. 最小实验：用一个可断言的 Lab 把 merged 跑出来

对应 Lab：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansMergedBeanDefinitionLabTest.java`

> 对学习者而言，你不需要背“每个字段”，但要知道：**你在断点里看到的 `RootBeanDefinition` 往往已经不是你注册进去的那个对象**。

如果你希望把它看“更实”，请直接跳到文末的「源码最短路径（call chain）」与「固定观察点（watch list）」：它们是为断点调试准备的。

## 5. 推荐断点与观察点（把 merged 看“实”）

### 5.1 推荐断点（优先打条件断点：只看你的 beanName）

> 目标：你每次停在 merged 相关断点，都只看这几项，就能快速回答“合并了什么、缓存在哪、hook 在哪”。

- `beanName`：建议加条件断点只看你的目标 bean
- `mbd`（是否是 `RootBeanDefinition`？property values / init-method / destroy-method 是否已合并？）
- `mbd.getParentName()`：原始 child 会保留 parentName；merged 后通常不再需要你追 parent 链
- `mergedBeanDefinitions`（或等价缓存结构）：容器缓存 merged definition 的地方（很多“为什么不变/还是旧的”都和它有关）

**反例：我一直盯着 `getBeanDefinition(beanName)` 的返回值调试，越看越觉得“Spring 怎么不按我写的来”。**

最小复现入口：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansMergedBeanDefinitionLabTest.java`

你在断点里应该看到什么（用于纠错）：

对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansMergedBeanDefinitionLabTest.java`
推荐断点：`AbstractBeanFactory#getMergedLocalBeanDefinition`、`AbstractAutowireCapableBeanFactory#applyMergedBeanDefinitionPostProcessors`、`MergedBeanDefinitionPostProcessor#postProcessMergedBeanDefinition`

## 常见坑与边界

### 常见误区

- **误区 1：以为 `getBeanDefinition(beanName)` 就是“最终生效的定义”**
  它更像是“registry 中的原始定义”；真正参与创建的是 merged。

- **误区 2：把 merged 当成“只对 XML 才有”**
  你在注解场景也会频繁遇到 merged：容器需要一个统一的 `RootBeanDefinition` 来驱动创建与缓存。

- **误区 3：只盯着 doCreateBean，不看 merged**
  你会错过很多“为什么它这样创建/为什么元数据已准备好”的关键原因。

## 排障决策表（MergedBeanDefinition：你在看的到底是不是“生效配方”）

| 现象 | 最可能根因 | 证据（断点/观察点） | 修复/处理思路 |
| --- | --- | --- | --- |
| 你改了 `BeanDefinition`，但创建行为没变 | 你改的是 registry 原始定义；创建用的是 merged 缓存 | 断点 `AbstractBeanFactory#getMergedLocalBeanDefinition` 看缓存命中；观察 `mbd` 是否仍旧 | 修改发生在 merge 之前；必要时清理/避免依赖缓存；把修改放到 BFPP/BDRPP 或 MBDPP 的正确阶段 |
| 调试时看到的是 `RootBeanDefinition`，和你注册的类型不一致 | 这是预期：容器会把定义合并成统一的 RootBeanDefinition | 断点 `getMergedLocalBeanDefinition` / `applyMergedBeanDefinitionPostProcessors` | 用 mbd 作为事实来源，不要把 registry 返回值当“最终生效” |
| “属性/元数据好像凭空出现” | parent/child 合并、默认值补全、解析增强在 merge 阶段发生 | 观察 `mbd` 的 property values / initMethodName / destroyMethodName | 把排查口径改成：先看 mbd，再回溯原始定义来源 |
| 你想在 MBDPP 里“改实例” | MBDPP 属于定义/元数据阶段扩展点，不是实例替换点 | 断点 `MergedBeanDefinitionPostProcessor#postProcessMergedBeanDefinition` | 改实例去 BPP（before/after init）；改定义去 BFPP/BDRPP；改 merged 元数据用 MBDPP（谨慎） |

## 面试常问（MergedBeanDefinition / MBDPP）

### Q1：`BeanDefinition` 和 `MergedBeanDefinition` 的关系是什么？为什么要有 merge？

- 标准答案（可复述）：
  - registry 里保存的是原始定义；创建时容器需要一个统一且可缓存的 `RootBeanDefinition`（merged），把 parent/child、默认值、解析结果等合并起来，避免每次创建都重复计算。
- 证据链（方法级）：
  - `AbstractBeanFactory#getMergedLocalBeanDefinition`（计算/缓存入口）
  - `AbstractAutowireCapableBeanFactory#applyMergedBeanDefinitionPostProcessors`（MBDPP 介入点）
- 最小复现：
  - `SpringCoreBeansMergedBeanDefinitionLabTest`

### Q2：`MergedBeanDefinitionPostProcessor` 适合做什么？不适合做什么？

- 标准答案（可复述）：
  - 适合在“定义已合并但实例未创建”窗口补齐元数据/做解析缓存；不适合替换实例或做重副作用（那属于 BPP/实例阶段）。
- 证据链（方法级）：
  - `applyMergedBeanDefinitionPostProcessors` 的调用位置（发生在 `createBeanInstance` 之后、`populateBean` 之前）

## 一句话自检

- 你能解释清楚：`BeanDefinition`（registry 里的原始定义）与 `MergedBeanDefinition/RootBeanDefinition`（创建时真正使用的配方）有什么区别吗？
- 你能指出：`MergedBeanDefinitionPostProcessor#postProcessMergedBeanDefinition` 在创建链路的哪个阶段触发吗？它“适合做什么/不适合做什么”？
- 你能用断点证明：同一个 beanName 的 mbd 是“计算后缓存”的，而不是每次创建都重新算吗？（提示：观察 `getMergedLocalBeanDefinition` 的缓存命中）

## 小结与下一章

- `AbstractBeanFactory#getMergedLocalBeanDefinition`
- `AbstractAutowireCapableBeanFactory#applyMergedBeanDefinitionPostProcessors`
- `MergedBeanDefinitionPostProcessor#postProcessMergedBeanDefinition`

- `AbstractBeanFactory#doGetBean(beanName, ...)`
  - `getMergedLocalBeanDefinition(beanName)`
    - **这里拿到/计算/缓存 merged `RootBeanDefinition`（mbd）**
  - `AbstractAutowireCapableBeanFactory#createBean(beanName, mbd, args)`
    - `doCreateBean(beanName, mbd, args)`
      - `createBeanInstance(...)`（实例化）
      - `applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName)`
        - **这里触发 `MergedBeanDefinitionPostProcessor#postProcessMergedBeanDefinition`**
      - `populateBean(...)`（属性填充 / 注入）
      - `initializeBean(...)`（初始化回调：`@PostConstruct`/init-method/...）

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`SpringCoreBeansMergedBeanDefinitionLabTest`
- Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansMergedBeanDefinitionLabTest.java`

上一章：[34. @Value 占位符解析：strict vs non-strict](34-value-placeholder-resolution-strict-vs-non-strict.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[36. 类型转换：BeanWrapper / ConversionService / PropertyEditor 的边界](36-type-conversion-and-beanwrapper.md)

<!-- BOOKIFY:END -->
