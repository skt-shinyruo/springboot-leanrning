# Boot 自动配置 Bean：出现、排序与 backoff

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文解释 Boot 自动配置如何把默认 Bean 注册进容器，以及它什么时候会因为 backoff 退让。
    - 重点观察自动配置导入、条件判断、注册时点、用户 Bean 覆盖矩阵和 ConditionEvaluationReport。
    - 读完后应能判断一个默认 Bean 是“没导入”“条件没过”，还是“已经退让了”。

    观察对象：`@AutoConfiguration` 导入、条件判定、backoff 时点、覆盖矩阵和条件报告。
    主线位置：Boot 叠加在标准 BeanDefinition 注册之后的那一层。
    对照入口：`SpringCoreBeansAutoConfigurationLabTest`、`SpringCoreBeansAutoConfigurationBackoffTimingLabTest`、`SpringCoreBeansAutoConfigurationOverrideMatrixLabTest`、`SpringCoreBeansConditionEvaluationReportLabTest`。
<!-- CHAPTER-CARD:END -->

Boot 自动配置不是“启动时偷偷 new 一个对象”。它先把自动配置类导入到普通配置解析链里，再用条件决定哪些默认 BeanDefinition 留在 registry。真正影响用户的是注册结果，而不是自动配置类本身是否存在于 classpath。

## 自动配置先进入注册链

`SpringCoreBeansAutoConfigurationLabTest` 先给出最基础的事实：`@ConditionalOnProperty` 不满足时，对应 Bean 根本不会进入容器；满足时，默认 BeanDefinition 才被注册。这里的关键不是创建对象，而是条件是否允许定义进入 registry。

换句话说，Boot 自动配置的第一层问题不是“对象为什么没构造”，而是“这条定义有没有被放进去”。

## backoff 看的是时点，不是口号

`SpringCoreBeansAutoConfigurationBackoffTimingLabTest` 把 backoff 的时间窗讲得很清楚：

| 场景 | 结果 | 你该怎么理解 |
| --- | --- | --- |
| 用户 Bean 在条件评估之前就已经注册 | 自动配置退让，默认 Bean 不再出现 | `@ConditionalOnMissingBean` 看到的是“已经存在的候选” |
| 用户 Bean 在条件评估之后才注册 | 自动配置已经落下默认定义，后面会出现两个候选 | 这不是 backoff 失效，而是时点太晚 |

这说明 backoff 不是事后删除 Bean，而是在导入和注册阶段提前让位。晚到的注册动作不会把已经通过条件的自动配置定义自动撤回。

## 自动配置顺序会改变条件能看见什么

自动配置导入后，Boot 不只是按 imports 文件或 `AutoConfigurations.of(...)` 的书写顺序逐个应用。导入列表会先经过排序，再进入配置类处理流程。排序的目的不是决定 Bean 的初始化先后，而是决定某个自动配置被处理时，前面哪些自动配置已经把 BeanDefinition 放进 registry。

这个时点会直接影响条件结果。`SpringCoreBeansAutoConfigurationOrderingLabTest` 展示了一个典型现象：运行结束时 `Marker` 明明存在，但依赖它的 `DependentFeature` 没有注册，因为 `@ConditionalOnBean(Marker.class)` 判断时，`Marker` 对应的定义还没有对这个条件可见。

排序规则常见有三类：

| 排序手段 | 控制什么 | 对条件和 backoff 的影响 |
| --- | --- | --- |
| auto-configuration imports 顺序 | 候选自动配置的初始输入顺序 | 只能作为起点，后续 before/after/order 元数据仍会重新排序 |
| `@AutoConfigureBefore` / `@AutoConfiguration(before = ...)` | 当前自动配置应排在目标自动配置之前 | 让当前配置先注册定义，目标配置的条件就能看见这些定义 |
| `@AutoConfigureAfter` / `@AutoConfiguration(after = ...)` | 当前自动配置应排在目标自动配置之后 | 让当前配置依赖目标配置先完成定义注册，`@ConditionalOnBean` 更稳定 |
| `@AutoConfigureOrder` | 在没有明确 before/after 关系时给自动配置一个相对优先级 | 影响同组自动配置的处理先后，从而影响条件可见集合 |

`SpringCoreBeansAutoConfigurationImportOrderingLabTest` 进一步说明：即使输入顺序是 `Dependent -> Marker -> First`，Boot 也会根据 before/after 元数据排序成更适合条件判断的顺序。也就是说，imports 文件顺序不是唯一事实；真正要看的是排序后的自动配置处理顺序。

这和 `@ConditionalOnMissingBean` / backoff 的关系尤其直接：某个默认 Bean 的条件判断只会根据当时已经可见的 BeanDefinition 做决定。如果用户定义或另一个自动配置的定义排在它之前，默认配置可能 backoff；如果排在它之后，默认定义可能已经注册，后面就变成多个候选并存。`@AutoConfigureBefore`、`@AutoConfigureAfter` 和 `@AutoConfigureOrder` 的价值就在于把这种“条件能看见谁”固定下来。

## 用户 Bean 覆盖矩阵

`SpringCoreBeansAutoConfigurationOverrideMatrixLabTest` 把最常见的四种结论放在一起：

| 用户侧动作 | 容器里的结果 | 单值注入结果 |
| --- | --- | --- |
| 没有用户 Bean | 只剩自动配置默认值 | 正常注入默认值 |
| 用户 Bean 太晚才注册 | 两个候选并存 | 单值注入可能报不唯一 |
| 用户 Bean 先注册，且条件能看到它 | 自动配置退让，只留用户 Bean | 单值注入稳定 |
| 两个候选都保留，但其中一个带 `@Primary` 或显式 `@Qualifier` | 候选仍然都在 | 单值注入可收敛到指定对象 |

这个矩阵最重要的结论是：覆盖和退让不是同一件事。`@Primary`、`@Qualifier` 解决的是候选收敛，`@ConditionalOnMissingBean` 解决的是默认定义是否还要注册。

## 条件报告告诉你为什么

`SpringCoreBeansConditionEvaluationReportLabTest` 负责把“为什么匹配/为什么没匹配”从猜测变成证据。`ConditionEvaluationReport` 会记录来源和每条条件的 outcomes，你可以直接看到某个自动配置是 full match 还是被某个条件挡住了。

排障时最好按这个顺序看：

1. 先确认自动配置类是否真的被导入。
2. 再看条件是否满足。
3. 再看用户 Bean 是否在条件评估前已经可见。
4. 最后再看候选冲突和单值注入异常。

## 读这类问题的最短路径

如果现场表现是“默认 Bean 没了”，先别盯构造器，先看导入和条件。
如果现场表现是“出现了两个同类型 Bean”，先别怀疑 backoff 机制，先看用户 Bean 注册时点和 `@Primary` / `@Qualifier`。
如果现场表现是“本地能跑、测试失败”，优先怀疑条件输入不同，而不是自动配置逻辑本身变了。
