# 020-09 Auto-Config Ordering（自动配置顺序）

## 章节学习卡片（五问闭环）
- **这章解决什么问题？** 自动配置装配的先后顺序由哪些规则决定，为什么“顺序不同”会影响最终 Bean。
- **怎么用？** 识别 `@AutoConfigureBefore/@AutoConfigureAfter/@AutoConfigureOrder` 与 `@Order` 的作用边界，定位冲突来源。
- **背后的原理？** Spring Boot 的 `AutoConfigurationImportSelector` 收集候选并执行排序与去重，再交给容器解析。
- **源码入口？** `AutoConfigurationImportSelector`、`AutoConfigurationSorter`、`AutoConfigurationMetadata`。
- **推荐 Lab？** `SpringCoreBeansAutoConfigurationOrderingLabTest`、`SpringCoreBeansAutoConfigurationImportOrderingLabTest`。

---

## 1. 导读：为什么“顺序”是 Bug 的根因之一？
自动配置的“顺序”并不是简单的 `@Order`，而是**多层规则叠加**：显式 before/after 关系、元数据排序、条件评估结果、以及最终注册到容器时机。顺序一旦不一致，就可能出现“Bean 被覆盖/条件失效/依赖未满足”的假象。

## 2. 主线：自动配置排序的四个层级
1. **显式依赖关系**：`@AutoConfigureBefore/@AutoConfigureAfter` 定义局部拓扑。
2. **元数据排序**：`spring.factories` 或 `AutoConfiguration.imports` 的候选集合基于 metadata 做排序与去重。
3. **条件过滤**：`@Conditional*` 先过滤，再进入注册阶段（过滤本身不会重排）。
4. **注册时机**：`ImportSelector` 导入的配置类进入 `BeanDefinitionRegistry`，影响后续处理器的执行顺序。

## 3. 关键分支矩阵（最易混淆的点）
- **before/after 冲突**：同时声明 before 与 after，最终以拓扑排序结果为准。
- **@Order vs AutoConfigureOrder**：前者影响 `Ordered` Bean，后者仅影响自动配置类的顺序。
- **条件过滤导致“顺序失效”**：A before B，但 A 被过滤后 B 仍会进入注册。

## 4. 断点与观察点
- `AutoConfigurationImportSelector#getAutoConfigurationEntry`
- `AutoConfigurationSorter#sort`
- `ConfigurationClassParser#processImports`

## 5. 可跑入口（证据链）
- `SpringCoreBeansAutoConfigurationOrderingLabTest`：排序规则对最终 Bean 的影响
- `SpringCoreBeansAutoConfigurationImportOrderingLabTest`：Import 级别的排序与过滤

## 6. 常见坑
- 误把 `@Order` 当作自动配置顺序控制器
- 依赖顺序未声明，导致本地可用、CI 偶发失败
- 只看日志而未下断点，无法判断“排序 vs 过滤”

## 7. 小结
自动配置顺序不是单一规则，而是“显式依赖 + 元数据排序 + 条件过滤 + 注册时机”的组合。定位问题时，先确认拓扑关系是否声明，再通过断点确定排序与过滤阶段的真实结果。
