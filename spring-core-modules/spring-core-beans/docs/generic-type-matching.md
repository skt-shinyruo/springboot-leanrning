# 泛型匹配与注入误区：ResolvableType 与代理导致的类型信息丢失
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 使用方式：先运行章首 Lab，把现象固化为断言；排查真实问题时，按“定义层/实例层/最终暴露对象”分层，再用断点与观察清单收敛原因。

    观察对象：37. 泛型匹配与注入误区：ResolvableType 与代理导致的类型信息丢失。
    主线位置：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansGenericTypeMatchingPitfallsLabTest`。需要下探源码时，可以从 `BeanDefinition#getResolvableType` / `FactoryBean#getObjectType` / `GenericTypeAwareAutowireCandidateResolver#checkGenericTypeMatch` 这些入口切入。

<!-- CHAPTER-CARD:END -->


## 问题：泛型匹配与注入误区：ResolvableType 与代理导致的类型信息丢失

先运行 `SpringCoreBeansGenericTypeMatchingPitfallsLabTest`，观察泛型匹配如何受代理与运行时类型信息影响；再围绕入口方法、关键分支和可观察变量阅读正文。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! example "本章配套实验（先运行再读）"

    - Lab：`SpringCoreBeansGenericTypeMatchingPitfallsLabTest`
    - 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansGenericTypeMatchingPitfallsLabTest.java`


## 机制主线

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

这一章要把一件“表面上是黑箱”的事变成可解释、可复现、可排障的结论：

> **Spring 的“泛型匹配”不是隐式行为，它依赖 `ResolvableType`，而 `ResolvableType` 的准确性依赖候选 bean 是否能提供稳定的类型元信息。**

所以遇到“`Handler<String>` 注入失败”的时候，真正要问的不是“泛型是不是坏了”，而是：

1. Spring 在注入点看到的目标类型（`descriptor.getResolvableType()`）到底是什么？
2. Spring 在候选 bean 上看到的候选类型（`beanFactory.getType(beanName)` / `BeanDefinition#getResolvableType` / `FactoryBean#getObjectType`）到底是什么？
3. 两者在泛型参数层面能不能对上？

把这三件事打通，这章读者就掌握了。

---

### 机制边界：条件、分支与结果

**条件**：注入点是带泛型的 `ResolvableType`，候选类型信息不完整
**分支**：`GenericTypeAwareAutowireCandidateResolver#checkGenericTypeMatch` 发现泛型参数无法匹配
**结果**：按原始类型能匹配，按泛型类型失配
**断点入口**：`GenericTypeAwareAutowireCandidateResolver#checkGenericTypeMatch`

## 先建立一个“排障口径”：候选类型信息的三大来源
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


当 Spring 需要做“按类型（含泛型）匹配”时，候选 bean 的类型信息主要来自三条路径（按可靠性排序）：

1. **BeanDefinition（class metadata）**：读者提供了 beanClass（或 targetType）
   - 优点：容器在“定义层”就能做匹配，不必实例化对象
   - 典型：`RootBeanDefinition(StringHandler.class)`
2. **FactoryBean 的 product type**：通过 `FactoryBean#getObjectType()`（以及一些预测机制）
   - 优点：能把“复杂构建逻辑”隐藏在工厂里
   - 风险：`getObjectType()` 不可靠/返回 null，会直接让 type discovery 变得不稳定（见 [23](wiring-factorybean-deep-dive.md)、[29](wiring-factorybean-edge-cases.md)）
3. **已存在的 singleton instance（运行时对象）**：容器只能“看实例类型”
   - 优点：简单
   - 风险：如果实例是 **JDK 动态代理** 或“擦掉泛型信息的包装对象”，泛型参数可能完全不可见

这一章的核心就是：**当候选从 1) 退化到 3) 时，泛型匹配的可靠性会大幅下降。**

## 实验：把现象固定成断言

本章可复核的实验入口：
- Lab：`SpringCoreBeansGenericTypeMatchingPitfallsLabTest`
- 命令：`mvn -pl :spring-core-beans test`（亦可在 IDE 中运行上述测试类）

## 边界：泛型匹配与注入误区：ResolvableType 与代理导致的类型信息丢失

这一章解决一个“表面上很合理，但在真实项目里经常易错点”的问题：

> 明明容器里有一个 `Handler<String>`，
> 为什么按 `Handler<String>`（带泛型）去找/注入时，Spring 却说“没有候选”？
> 但按原始类型 `Handler`（不带泛型）又能找到？

核心结论：

- Spring 在“按泛型匹配”时依赖 `ResolvableType`
- **如果候选 bean 在运行时丢失了泛型信息（常见原因：JDK dynamic proxy、手工注册 singleton 实例等）**，那么：
  - 按原始类型能匹配（`Handler`）
  - 按带泛型的 ResolvableType 可能匹配不到（`Handler<String>`）

---

## 为什么 Spring 要关心泛型？

在大型系统中，“同一个原始类型”的实现可能很多；泛型经常被用作“更精确的语义”：

- `Handler<String>` vs `Handler<Long>`
- `Repository<User>` vs `Repository<Order>`

因此 Spring 在一部分场景中会尝试用 `ResolvableType` 做更精细的候选筛选。

---

## 最小可复现：泛型信息一旦丢失，按 ResolvableType 就会失配

### 2.1 运行入口

- 入口测试（先运行通过，再设置断点）：
  - `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansGenericTypeMatchingPitfallsLabTest.java`
  - 类级：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansGenericTypeMatchingPitfallsLabTest test`
  - 方法级（更快）：`mvn -pl :spring-core-beans -Dtest=SpringCoreBeansGenericTypeMatchingPitfallsLabTest#genericTypeMatching_canFailWhenTypeInfoIsLost test`
- 运行后应能观察到：
  - 容器“表面上”注册了一个 `T` 的实现，但在按 `ResolvableType` 匹配时可能失配（尤其是代理/手工注册实例时）
  - 结论不是“泛型不能用”，而是“泛型匹配依赖可被保留的类型信息”，要知道何时会丢

对应实验（可运行 + 可断言）：

- `spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansGenericTypeMatchingPitfallsLabTest.java`

直接运行：

```bash
mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansGenericTypeMatchingPitfallsLabTest test
```

这个 Lab 做了两件事（对照强）：

1. **用 class metadata 注册**（`RootBeanDefinition(StringHandler.class)`）
   - BeanFactory 能通过 `StringHandler implements Handler<String>` 推断出泛型信息
   - 因此 `getBeanNamesForType(Handler<String>)` 能找到它
2. **用运行时 JDK proxy 实例注册 singleton**
   - 代理实现的是原始接口 `Handler`，运行时很难保留 `Handler<String>` 的泛型参数
   - 因此按 `Handler` 能找到，但按 `Handler<String>` 会失配

应当把这个现象记成一句话：

> **“泛型匹配依赖类型信息；一旦候选从‘类定义’退化为‘运行时代理实例’，泛型信息可能就不再可靠。”**

---

## 再加两步（本仓库已补齐）：如何证明“能修复”

仅仅知道“会失败”还不够。读者还应该知道两类“能修复/能规避”的路线，并能用 Lab 证明它们：

### 3.1 规避路线：不要让候选退化成“只有运行时代理实例”

对应 Lab（本仓库已补齐）：

- `SpringCoreBeansGenericTypeMatchingPitfallsLabTest#genericTypeMatching_canWorkWhenCandidateKeepsGenericSignature_likeConcreteClassInstance`

它用一个对照证明：

- 同样是 singleton instance：如果实例的运行时 class 仍是“真实类”（不是 JDK proxy），Spring 仍可能从 class metadata 推断泛型参数

### 3.2 修复路线：显式提供 target type 元信息（让匹配回到定义层）

对应 Lab（本仓库已补齐）：

- `SpringCoreBeansGenericTypeMatchingPitfallsLabTest#genericTypeMatching_canBeRestoredByProvidingTargetTypeMetadata_evenIfRuntimeInstanceIsAProxy`

它演示了一个工程化的技巧：

- 当相应的候选必须是“运行时对象/代理对象”，但读者又想让它参与泛型匹配
- 可以在 BeanDefinition 上显式设置 target type（`ResolvableType`），让容器在定义层做正确匹配

这不是“黑箱机制”，而是把“不可靠的运行时类型信息”替换成“可靠的定义层元信息”。

---

## 真实项目里可以在哪些地方遇到它？

常见触发点：

1. **JDK 动态代理（接口代理）**
   - 代理类只实现接口，泛型参数在运行时往往不可见/不可推断
2. **手工注册 singleton 实例**（`registerSingleton`）
   - 容器获取到的是一个对象，不一定能反推出“它原本的泛型语义”
3. **FactoryBean 的类型预测**
   - `FactoryBean#getObjectType()` 返回不准确/为 `null` 时，按类型发现会出现边界问题（见 [FactoryBean 深潜：product vs factory、类型匹配、以及 isSingleton 缓存语义](wiring-factorybean-deep-dive.md) 与 [FactoryBean 边界：getObjectType 返回 null 会让“按类型发现”失效](wiring-factorybean-edge-cases.md)）

---

## 怎么避免/修复（工程取舍）

1. 若依赖“按泛型精确匹配”，尽量让候选以 **BeanDefinition + class metadata** 的形式进入容器
   - 让容器在定义层就能看到目标类，从而更可靠地计算 ResolvableType
2. 对运行时代理场景，不要过度依赖“按泛型查找”
   - 更稳妥的方式通常是：原始类型 + `@Qualifier`（或显式命名）来表达意图
3. 如果必须让“运行时对象”参与泛型匹配
  - 处理：在 BeanDefinition 上显式声明 target type（`ResolvableType`），让容器在定义层做匹配（见本章第 3.2 节 Lab）
   - 退而求其次：用 name/Qualifier 把意图表达清楚（把“泛型参数”从“匹配条件”降级为“业务语义”）

---

## Debug / 断点入口（把“泛型匹配失效”定位到具体分支）

### 5.1 断点入口（候选收敛与泛型匹配的关键点）

- 依赖解析主线（需要看到候选如何收敛）：
  - `DefaultListableBeanFactory#doResolveDependency`
  - `DefaultListableBeanFactory#findAutowireCandidates`
- 泛型匹配关键点（需要看到 ResolvableType 如何参与匹配）：
  - `GenericTypeAwareAutowireCandidateResolver#checkGenericTypeMatch`
  - `ResolvableType#forClass`

### 5.2 条件断点模板（降低噪声）

在 `DefaultListableBeanFactory#doResolveDependency` 上使用条件断点：

- 按依赖类型过滤（示例：相应的泛型接口/父类）：
  - `descriptor.getDependencyType().getName().contains("YourService")`
- 按 beanName 过滤（当读者已知目标候选名称时）：
  - `"yourBeanName".equals(beanName)`

### 5.3 观察点（观察清单）

- `descriptor`：依赖描述（包含注入点、required/optional 等信息）
- `descriptor.resolvableType`：Spring 看到的“带泛型的目标类型”
- `candidateName` / `candidate`：候选集合与当前候选
- `beanFactory.getType(candidateName)`：容器推断的候选类型（可能因为代理/FactoryBean 变得不可靠）

## 源码锚点：从这里设置断点

- `ResolvableType`：泛型类型描述的核心抽象
- `DefaultListableBeanFactory#getBeanNamesForType(ResolvableType ...)`：按 ResolvableType 查找候选的入口之一

---

## 面试常问（泛型匹配：ResolvableType 为什么会“看不见”相应的泛型）

### Q1：为什么按 `Handler<String>` 注入可能失败，但按原始类型 `Handler` 又能成功？

- 标准答案（可复述）：
  - 因为泛型匹配依赖 `ResolvableType` 与候选的“可被推断的类型元信息”。当候选退化为运行时代理实例（尤其是 JDK proxy）时，泛型参数往往不可见，导致按 `ResolvableType` 失配；原始类型匹配则更宽松。
- 证据链（方法级）：
  - 依赖解析：`DefaultListableBeanFactory#doResolveDependency` / `#findAutowireCandidates`
  - 泛型校验：`GenericTypeAwareAutowireCandidateResolver#checkGenericTypeMatch`
- 最小复现：
  - `SpringCoreBeansGenericTypeMatchingPitfallsLabTest`

### Q2：如何修复/规避“泛型信息丢失”导致的失配？

- 标准答案（可复述）：
  - 规避：尽量让候选以 class metadata（BeanDefinition）形式进入容器，不要只靠运行时代理实例；必要时按接口注入并配合 `@Qualifier` 表达意图。
  - 修复：显式提供 target type 元信息（让匹配回到定义层），避免容器只能从运行时对象猜类型。
- 最小复现：
  - `SpringCoreBeansGenericTypeMatchingPitfallsLabTest#genericTypeMatching_canBeRestoredByProvidingTargetTypeMetadata_evenIfRuntimeInstanceIsAProxy`

## 验收口径：泛型匹配与注入误区：ResolvableType 与代理导致的类型信息丢失
- 需要解释清楚：为什么“泛型注入”在遇到代理/桥接方法时容易失去类型信息吗？（提示：运行时类型 vs 声明时类型）
- 读者知道 Spring 用什么抽象来表达泛型类型信息吗？（提示：`ResolvableType`）
- 遇到“按泛型注入失败”时，第一步会去哪设置断点/看哪个变量来确认类型信息有没有丢？（提示：依赖解析与 type matching 路径）

## 小结：泛型匹配与注入误区：ResolvableType 与代理导致的类型信息丢失


<!-- BOOKIFY:START -->

### 对应实验/测试

- Lab：`SpringCoreBeansGenericTypeMatchingPitfallsLabTest`
- 测试文件：`spring-core-modules/spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/appendix/SpringCoreBeansGenericTypeMatchingPitfallsLabTest.java`

<!-- BOOKIFY:END -->
