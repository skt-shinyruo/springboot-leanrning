# 断点地图：问题出现时先停哪里

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文给的是断点入口，不是机制正文。
    - 目标是把常见问题快速停在最有信息量的方法上，再回到对应 Lab。
    - 验证入口是 `SpringCoreBeansBreakpointPackLabTest`。

    观察对象：故障症状、源码断点和 Lab 入口。
    主线位置：注册、候选、创建、代理和特殊解析分支。
    对照入口：`SpringCoreBeansBreakpointPackLabTest`。
<!-- CHAPTER-CARD:END -->

断点地图的作用不是“把源码全看完”，而是先站在分叉口。你要看的不是每一行代码，而是最能解释现象的那一个方法。

## 先按症状找入口

| 现象 | 先停哪里 | 断点包观察口径 |
| --- | --- | --- |
| 类明明有注解却没进容器 | `ConfigurationClassPostProcessor` / `BeanDefinitionRegistryPostProcessor` 注册链 | 用 `SpringCoreBeansBreakpointPackLabTest` 进入注册和覆盖分支 |
| 单值注入报不唯一 | `DefaultListableBeanFactory#doResolveDependency` | 先用断点包确认是否属于候选冲突，再回到注入正文 |
| 字段和 setter 注入比构造器晚 | `AbstractAutowireCapableBeanFactory#populateBean` / `AutowiredAnnotationBeanPostProcessor#postProcessProperties` | 用断点包观察后处理器顺序 |
| 循环依赖只是在一部分场景下成功 | `AbstractAutowireCapableBeanFactory#doCreateBean` 和 early reference 相关分支 | 用断点包进入循环依赖、early reference 和 raw injection 分支 |
| 拿到的是 FactoryBean 产品还是工厂本身 | `AbstractBeanFactory#transformedBeanName` / `FactoryBeanRegistrySupport` | 用断点包观察 FactoryBean 边界 |
| merged definition 和注册定义看起来不一样 | `AbstractBeanFactory#getMergedBeanDefinition` | 用断点包观察 merged definition 分支 |
| 占位符没有被解析 | `ConfigurableBeanFactory#resolveEmbeddedValue` 相关链路 | 用断点包观察 value placeholder 解析 |
| `@Lazy` 但还是提前创建 | `DefaultListableBeanFactory#preInstantiateSingletons` / lazy 判断 | 用断点包观察 lazy 与预实例化边界 |
| 自动配置退让或覆盖没按预期发生 | 自动配置导入和条件判断链 | 用断点包先确认覆盖问题是否发生在定义层 |

## 断点怎么打才有用

先打在“分叉点”，不要一开始就打在具体 setter 或构造器里。比如：

- 注册问题先停在 registry 写入之前。
- 候选问题先停在依赖解析入口。
- 生命周期问题先停在 `doCreateBean()` 和 `initializeBean()` 的边界。
- 代理问题先停在后处理器包装前后。

这样你能先判断问题属于“没有定义”“有定义但没被选中”“已经创建但最后暴露的不是原对象”，再决定要不要继续往下钻。

## 跟 Lab 的配合方式

`SpringCoreBeansBreakpointPackLabTest` 不是单个问题的答案，而是一组高频分叉的回归入口。先在这里确认症状，再回到对应专题文档，就能把“看源码”变成“看一个具体分支”。
