# 020-09 Auto-Config Ordering（自动配置顺序）

## 章节学习卡片（五问闭环）
- **这章解决什么问题？** 自动配置装配的先后顺序由哪些规则决定，为什么“顺序不同”会影响最终 Bean。
- **怎么用？** 识别 `@AutoConfigureBefore/@AutoConfigureAfter/@AutoConfigureOrder` 与 `@Order` 的作用边界，定位冲突来源。
- **背后的原理？** Spring Boot 通过 `AutoConfigurationImportSelector` 收集候选自动配置类，并在“导入到容器之前”完成排序/去重/过滤；顺序决定了**谁先注册定义**、以及 `@ConditionalOnMissingBean` 等条件判断时看到的“已存在 Bean”集合。
- **源码入口？** `AutoConfigurationImportSelector`、`AutoConfigurationSorter`、`AutoConfigurationMetadata`、`ConfigurationClassParser#processImports`。
- **推荐 Lab？** `SpringCoreBeansAutoConfigurationOrderingLabTest`、`SpringCoreBeansAutoConfigurationImportOrderingLabTest`。

---

## 1. 导读：为什么“顺序”是 Bug 的根因之一？
自动配置的“顺序”并不是简单的 `@Order`，而是**多层规则叠加**：

- **显式依赖关系**（before/after）：定义局部拓扑图（谁必须在谁之前/之后）
- **元数据排序**（metadata）：用于在候选集合很大时做稳定排序（避免“看起来随机”的差异）
- **去重/排除/过滤**：排除掉不该导入的配置类，再决定最终导入列表
- **导入时机**：最终是通过 `ImportSelector` 把自动配置类导入到“配置类解析阶段”，从而进入 `BeanDefinitionRegistry`

顺序错了，最常见的“假象”是：

- **你以为条件没生效**：其实条件生效了，但它看到的“已存在 Bean”集合不同（因为导入顺序不同）
- **你以为 Bean 被覆盖**：其实是 `@ConditionalOnMissingBean` 让某个候选配置没有注册（或注册了但被后续的定义/代理改变）
- **你以为是并发问题**：实际上是排序/过滤导致的“顺序差异”，只是表现为“本地可用、CI 偶发”

---

## 1.1 快速验证（先把顺序问题跑出来）

```bash
mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansAutoConfigurationOrderingLabTest test
```

如果你想把“导入顺序 vs 条件过滤”分开看：

```bash
mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansAutoConfigurationImportOrderingLabTest test
```

## 2. 主线：自动配置排序的四个层级
1. **显式依赖关系**：`@AutoConfigureBefore/@AutoConfigureAfter` 定义局部拓扑。
2. **元数据排序**：`spring.factories` 或 `AutoConfiguration.imports` 的候选集合基于 metadata 做排序与去重。
3. **条件过滤**：`@Conditional*` 先过滤，再进入注册阶段（过滤本身不会重排）。
4. **注册时机**：`ImportSelector` 导入的配置类进入 `BeanDefinitionRegistry`，影响后续处理器的执行顺序。

> 关键点：**自动配置的排序发生在“导入到容器之前”**。  
> `@Order` 主要影响的是 `Ordered` Bean（例如某些 filter/processor/listener 的排序），而不是“自动配置类的导入顺序”。

### 2.1 更精确的主线（把“排序/过滤/导入”分清楚）

你可以把它拆成三段（建议在断点里按段确认）：

1) **收集候选**：从 imports（或历史上的 spring.factories）拿到候选自动配置类列表  
2) **排序/去重/排除**：基于 `AutoConfigurationMetadata`、before/after、order 等规则得到稳定列表  
3) **导入到容器**：把最终列表交给配置类解析器（`processImports`），进入 BeanDefinitionRegistry

这三段的任何一段出错，最终都会表现为“Bean 为什么会是这样”。

## 3. 关键分支矩阵（最易混淆的点）
- **before/after 冲突**：同时声明 before 与 after，最终以拓扑排序结果为准。
- **@Order vs AutoConfigureOrder**：前者影响 `Ordered` Bean，后者仅影响自动配置类的顺序。
- **条件过滤导致“顺序失效”**：A before B，但 A 被过滤后 B 仍会进入注册。

## 4. 断点与观察点
- `AutoConfigurationImportSelector#getAutoConfigurationEntry`
- `AutoConfigurationSorter#sort`
- `ConfigurationClassParser#processImports`

> 推荐观察点（你要在变量里看见“排序前/排序后/过滤后”的列表）：
>
> - 候选列表（original candidates）
> - 排序后列表（sorted）
> - 排除/过滤后列表（exclusions + filtered）
> - 最终导入列表（imports）

## 5. 可跑入口（证据链）
- `SpringCoreBeansAutoConfigurationOrderingLabTest`：排序规则对最终 Bean 的影响
- `SpringCoreBeansAutoConfigurationImportOrderingLabTest`：Import 级别的排序与过滤

## 6. 常见坑
- 误把 `@Order` 当作自动配置顺序控制器
- 依赖顺序未声明，导致本地可用、CI 偶发失败
- 只看日志而未下断点，无法判断“排序 vs 过滤”

## 7. 小结
自动配置顺序不是单一规则，而是“显式依赖 + 元数据排序 + 条件过滤 + 注册时机”的组合。定位问题时，先确认拓扑关系是否声明，再通过断点确定排序与过滤阶段的真实结果。

---

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：[`SpringCoreBeansAutoConfigurationOrderingLabTest`](../../src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationOrderingLabTest.java)  
  - `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansAutoConfigurationOrderingLabTest test`
- Lab：[`SpringCoreBeansAutoConfigurationImportOrderingLabTest`](../../src/test/java/com/learning/springboot/springcorebeans/part02_boot_autoconfig/SpringCoreBeansAutoConfigurationImportOrderingLabTest.java)  
  - `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansAutoConfigurationImportOrderingLabTest test`

上一章：[10. Spring Boot Auto-Configuration（主线）](021-10-spring-boot-auto-configuration.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[11. Debugging & Observability（把现象变成证据）](019-11-debugging-and-observability.md)

<!-- BOOKIFY:END -->
