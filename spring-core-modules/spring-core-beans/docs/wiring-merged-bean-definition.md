# BeanDefinition 的合并（MergedBeanDefinition）：RootBeanDefinition 从哪里来？
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"
    - 使用方式：可先运行本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里优先按“定义层/实例层/最终暴露对象”分层，再用断点与 watch list 收敛原因。

    本章围绕35. BeanDefinition 的合并（MergedBeanDefinition）：RootBeanDefinition 从哪里来？展开，主线可以概括为：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansMergedBeanDefinitionLabTest`。需要下探源码时，可以从 `AbstractBeanFactory#getMergedLocalBeanDefinition` / `DefaultListableBeanFactory#getMergedBeanDefinition` / `AbstractBeanFactory#getMergedLocalBeanDefinition(beanName)` 这些入口切入。

<!-- CHAPTER-CARD:END -->


## 导读

- 阅读建议：建议先阅读“本章要点”，再沿主线展开；必要时结合源码与断点进行观察，最后通过验证实验完成闭环。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansMergedBeanDefinitionLabTest`
    - Test file：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansMergedBeanDefinitionLabTest.java`
    - 可先运行的方法（按理解收益排序）：
      - `mergedBeanDefinition_combinesParentAndChildMetadata_andTriggersMergedDefinitionPostProcessor`
      - `mergedBeanDefinition_inheritsAndOverridesMetadata_fromParentAndChild`

<!-- AE-DEEPENING:START -->
!!! tip "继续加深：把本章跑成可验证路线"

    建议 先跑 `SpringCoreBeansMergedBeanDefinitionLabTest` 把现象跑出来；跑完后回到正文，把“现象 → 调用链/分支 → 结论”对齐到源码。
    - 第一断点：`AbstractBeanFactory#getMergedLocalBeanDefinition`（以本章正文“断点建议/证据链”处为准；若本章提供固定观察点，优先按观察点收敛结论）。
    - 本章加深重点：读到“常见误区与边界”时，建议将“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，读者可以按步骤自证。
    - 下一跳：若是从现象进入，优先回到 [知识地图](appendix-knowledge-map.md) 选“章节 + 断点组 + Lab”；若是从断点进入，回到 [断点地图](guide-breakpoint-map.md) 选 C 组。
<!-- AE-DEEPENING:END -->
## 机制主线

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

很多人在深入分析 Spring 容器源码时会遇到一个“卡点”：

答案基本都落在同一个机制上：**BeanDefinition 合并（merge）**。

> 核心直觉：registry 保存的是“原始定义”；真正参与创建的是“合并后的 RootBeanDefinition（merged）”。

---

### 机制系统阐述：条件 → 分支 → 结果

**条件**：bean 定义存在 parent/子定义或需要解析默认值
**分支**：`getMergedLocalBeanDefinition` 递归合并 parent → 生成 `RootBeanDefinition`
**结果**：创建阶段只看 **merged**；registry 原始定义仅用于输入
**断点建议**：`AbstractBeanFactory#getMergedLocalBeanDefinition`

可以观察到 3 个关键现象：

1) registry 里获取到的 child definition **仍然保留 parentName**，且看不到 parent 的元数据
2) `getMergedBeanDefinition(...)` 获取到的是 **`RootBeanDefinition`**，并且已经把 parent 的元数据合并进来
3) `MergedBeanDefinitionPostProcessor` 能获取到 merged 后的 `RootBeanDefinition`（可用于“预处理/缓存元数据”）

---

## merged 到底“合并”了什么？

可以把 merged 理解为：把多个来源（parent + child + defaults + 解析结果）统一成“最终配方”。

常见会在 merged 里稳定存在的内容包括：

- **property values**：父定义提供默认值、子定义追加/覆盖
- **生命周期元数据**：init-method / destroy-method 等（子覆盖父；子不声明则继承）
- **resolved target type**：创建后可解析出更具体的 beanType（影响后续处理器）
- 其他“创建需要的元数据”与缓存字段

可以把 merge 的核心逻辑理解成下面这个“伪代码级模型”（为了理解而非逐行对齐源码）：

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

## merged 发生在时间线哪里？

两个最有价值的入口点：

1) `AbstractBeanFactory#getMergedLocalBeanDefinition`
   - 语义：为某个 beanName 计算（或读取缓存）“最终参与创建的 merged definition”
2) `DefaultListableBeanFactory#getMergedBeanDefinition`
   - 这是在业务/测试代码里更容易直接调用到的 public API（底层会走到上面那个方法）

可以把它和 [00 章](guide-deep-dive-guide.md) 的时间线对上：

- 当容器准备创建某个 bean 时，它首先会确保获取到 merged definition
- 获取到 merged 之后，才进入 `createBean → doCreateBean → populateBean → initializeBean` 这条链路

### 3.1 merged 在 `createBean` 链路中的精确位置（在调用栈里应该看到什么）

对 merged 概念的常见困惑在于：仅在 `doCreateBean` 附近观察对象，而未将 merged 过程放回更完整的时间线中理解。

一句话版的精确位置：

- **merged definition 的计算/缓存发生在 `doGetBean` 阶段**：进入 `createBean(...)` 之前就获取到了 `RootBeanDefinition`
- **`MergedBeanDefinitionPostProcessor` 的 hook 发生在 `doCreateBean` 阶段**：实例已创建，但属性还没填充（也就是 `populateBean(...)` 之前）

---

## 为什么 merged 和“注入/生命周期元数据”强相关？

因为很多基础设施处理器需要一个稳定、完整的定义来做“元数据准备/缓存”。

典型代表就是 `MergedBeanDefinitionPostProcessor`：

- 它的入口方法是：`postProcessMergedBeanDefinition(RootBeanDefinition, Class<?>, String)`
- 它在 `doCreateBean(...)` 中被调用（在实例化之后、属性填充之前）

可以把它理解为：

> “给读者一个最终配方（merged BD），可以在真正注入/初始化之前做一次准备工作（例如解析注解、建立缓存）。”

这也是为什么可以在源码里看到一些熟悉的基础设施处理器实现了它（例如与 `@Autowired`、`@PostConstruct` 相关的处理器家族）。

进一步阅读建议：

- 注解为什么能工作（基础设施处理器）：[12](internals-container-bootstrap-and-infrastructure.md)
- 注入发生在哪个阶段：`postProcessProperties`： [30](wiring-injection-phase-field-vs-constructor.md)
- 生命周期回调顺序： [17](internals-lifecycle-callback-order.md)

---

### 5.2 推荐观察点（watch / evaluate）

- `beanName`
- `mbd` / `beanDefinition`（是否是 `RootBeanDefinition`？有哪些 property values？init-method 是谁？）
- `mbd.getParentName()`（合并后通常不再需要读者手动追 parent 链了）

---

## 源码最短路径（call chain）

> 目标：给读者“最短可跟栈”，并标出 merged 与 merged-hook 在链路中的精确落点。

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

读者只要把这条链路记住，后面看到任意“元数据为什么已经准备好/为什么看到的是 RootBeanDefinition”都能对上。

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
- 若只盯着原始定义，会把“定义层对象”误当成“创建时用的最终配方”，因此结论必然错位

## 最小可运行实验（Lab）

- 本章已在正文中引用以下 LabTest（优先运行它们）：
- Lab：`SpringCoreBeansMergedBeanDefinitionLabTest`
- 建议命令：`mvn -pl :spring-core-beans test`（亦可在 IDE 中运行上述测试类）

### 验证补充（从实验现象出发）

- 明明注册的是 `GenericBeanDefinition`（或者通过注解解析得到的定义），为什么调试时经常看到的是 `RootBeanDefinition`？
- 在 registry 中获取到的 `BeanDefinition` 看起来缺了很多信息（property、init-method 等），但创建时又“似乎被自动补齐了”？

## 最小实验：用一个可断言的 Lab 将 merged 过程复现出来

对应 Lab：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansMergedBeanDefinitionLabTest.java`

> 对学习者而言，无需背“每个字段”，但要知道：**在断点里看到的 `RootBeanDefinition` 往往已经不是读者注册进去的那个对象**。

若希望把它看“更实”，请直接跳到文末的「源码最短路径（call chain）」与「固定观察点（watch list）」：它们是为断点调试准备的。

## 推荐断点与观察点（把 merged 看“实”）

### 5.1 推荐断点（优先打条件断点：只看相应的 beanName）

> 目标：读者每次停在 merged 相关断点，都只看这几项，就能快速回答“合并了什么、缓存在哪、hook 在哪”。

- `beanName`：建议加条件断点只看相应的目标 bean
- `mbd`（是否是 `RootBeanDefinition`？property values / init-method / destroy-method 是否已合并？）
- `mbd.getParentName()`：原始 child 会保留 parentName；merged 后通常不再需要读者追 parent 链
- `mergedBeanDefinitions`（或等价缓存结构）：容器缓存 merged definition 的地方（很多“为什么不变/还是旧的”都和它有关）

**反例：一直盯着 `getBeanDefinition(beanName)` 的返回值进行调试，越看越觉得“Spring 怎么不按预期生效”。**

最小对照入口如下：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansMergedBeanDefinitionLabTest.java`

在断点里应该看到什么（用于纠错）：

对应 Lab/Test：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansMergedBeanDefinitionLabTest.java`
推荐断点：`AbstractBeanFactory#getMergedLocalBeanDefinition`、`AbstractAutowireCapableBeanFactory#applyMergedBeanDefinitionPostProcessors`、`MergedBeanDefinitionPostProcessor#postProcessMergedBeanDefinition`

## 常见误区与边界

### 常见误区

- **误区 1：以为 `getBeanDefinition(beanName)` 就是“最终生效的定义”**
  其定位更接近是“registry 中的原始定义”；真正参与创建的是 merged。

- **误区 2：把 merged 当成“只对 XML 才有”**
  在注解场景也会频繁遇到 merged：容器需要一个统一的 `RootBeanDefinition` 来驱动创建与缓存。

- **误区 3：只盯着 doCreateBean，不看 merged**
  可以错过很多“为什么它这样创建/为什么元数据已准备好”的关键原因。

## 排障决策表（MergedBeanDefinition：在看的到底是不是“生效配方”）
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


| 现象 | 最可能根因 | 证据（断点/观察点） | 修复/处理思路 |
| --- | --- | --- | --- |
| 读者改了 `BeanDefinition`，但创建行为没变 | 读者改的是 registry 原始定义；创建用的是 merged 缓存 | 断点 `AbstractBeanFactory#getMergedLocalBeanDefinition` 看缓存命中；观察 `mbd` 是否仍旧 | 修改发生在 merge 之前；必要时清理/避免依赖缓存；把修改放到 BFPP/BDRPP 或 MBDPP 的正确阶段 |
| 调试时看到的是 `RootBeanDefinition`，和读者注册的类型不一致 | 这是预期：容器会把定义合并成统一的 RootBeanDefinition | 断点 `getMergedLocalBeanDefinition` / `applyMergedBeanDefinitionPostProcessors` | 用 mbd 作为事实来源，不要把 registry 返回值当“最终生效” |
| “属性/元数据好像凭空出现” | parent/child 合并、默认值补全、解析增强在 merge 阶段发生 | 观察 `mbd` 的 property values / initMethodName / destroyMethodName | 把排查口径改成：先看 mbd，再回溯原始定义来源 |
| 若希望在 MBDPP 里“改实例” | MBDPP 属于定义/元数据阶段扩展点，不是实例替换点 | 断点 `MergedBeanDefinitionPostProcessor#postProcessMergedBeanDefinition` | 改实例去 BPP（before/after init）；改定义去 BFPP/BDRPP；改 merged 元数据用 MBDPP（谨慎） |

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

## 自检要点
- 应能够解释清楚：`BeanDefinition`（registry 里的原始定义）与 `MergedBeanDefinition/RootBeanDefinition`（创建时真正使用的配方）有什么区别吗？
- 应能够指出：`MergedBeanDefinitionPostProcessor#postProcessMergedBeanDefinition` 在创建链路的哪个阶段触发吗？它“适合做什么/不适合做什么”？
- 应能够用断点证明：同一个 beanName 的 mbd 是“计算后缓存”的，而不是每次创建都重新算吗？（提示：观察 `getMergedLocalBeanDefinition` 的缓存命中）

## 小结

- `AbstractBeanFactory#getMergedLocalBeanDefinition`
- `AbstractAutowireCapableBeanFactory#applyMergedBeanDefinitionPostProcessors`
- `MergedBeanDefinitionPostProcessor#postProcessMergedBeanDefinition`

- `AbstractBeanFactory#doGetBean(beanName, ...)`
  - `getMergedLocalBeanDefinition(beanName)`
    - **这里获取到/计算/缓存 merged `RootBeanDefinition`（mbd）**
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

<!-- BOOKIFY:END -->
