# Appendix：生产排障检查清单

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文给的是生产排障顺序：先注册，再候选，再创建，再代理，最后再看 Boot/AOT。
    - 这里的目标是快速缩小故障层，不是把所有机制重新讲一遍。
    - 最短验证入口是 `SpringCoreBeansTroubleshootingPlaybookLabTest`、`SpringCoreBeansExceptionNavigationLabTest` 和 `SpringCoreBeansModuleContractLabTest`。

    观察对象：注册、候选、创建、代理、Boot/AOT 的排障顺序。
    主线位置：生产问题的排查顺序。
    对照入口：`SpringCoreBeansTroubleshootingPlaybookLabTest`、`SpringCoreBeansExceptionNavigationLabTest`、`SpringCoreBeansModuleContractLabTest`。
<!-- CHAPTER-CARD:END -->

生产排障最忌讳跳层。看到最终异常后，先别直接怀疑 AOP、Boot 或 Native。先按下面的顺序把层次压缩掉，通常会更快。

## 1. 先查注册

先确认目标 BeanDefinition 是否真的进入容器。

- 如果是 XML、`@Bean`、扫描或 `@Import` 来源，先看它有没有被注册。
- 如果是条件注册，先看条件是否满足。
- 如果是定义读取失败，优先用 `SpringCoreBeansExceptionNavigationLabTest` 区分它是不是 `BeanDefinitionStoreException` 一类的问题。

这一步的结论只有两个：要么有定义，要么根本没进容器。不要在这里讨论代理。

## 2. 再查候选

如果定义在，但单值注入失败，下一步看候选收敛。

- 多个同类型 Bean 是否同时存在。
- 有没有 `@Primary`。
- 有没有显式 `@Qualifier`。
- 这是集合注入还是单值注入。

`SpringCoreBeansTroubleshootingPlaybookLabTest` 适合在这一步定位“为什么我拿到的是多个候选”。

## 3. 再查创建

如果候选没问题，但对象还是出错，就看创建链。

- 构造器依赖是否能解析。
- 属性填充是否发生在你期待的时点。
- 初始化回调是否执行。
- 失败是出在实例化前，还是在 populate / initialize 阶段。

如果这里抛的是 `UnsatisfiedDependencyException`，先回到 `SpringCoreBeansExceptionNavigationLabTest`，把根因和外层包装分开。

## 4. 再查代理

如果对象看起来“有了”，但调用结果不对，通常要检查代理和 early reference。

- 你拿到的是 raw instance 还是最终 exposed object。
- 代理是在初始化前还是初始化后出现。
- 循环依赖是否让某个引用提前暴露成 raw 对象。

这类问题先用 `SpringCoreBeansTroubleshootingPlaybookLabTest` 缩小范围，再回到创建和代理相关正文。

## 5. 最后才看 Boot / AOT

如果基础容器层都没问题，再看 Boot 自动配置和 AOT / Native。

- Boot 问题先看条件、导入和 backoff。
- Native 问题先看 RuntimeHints、反射、代理和资源访问。
- JVM 上能跑，不代表 Native 上还会跑。

这一步不要再回头猜“是不是 BeanDefinition 少了”。如果前面几层都正常，问题才真正落到 Boot / AOT 边界。

## 一个实用顺序

遇到线上故障时，按下面顺序执行通常最快：

1. 先用 `SpringCoreBeansExceptionNavigationLabTest` 判断是定义错误还是依赖错误。
2. 再用 `SpringCoreBeansTroubleshootingPlaybookLabTest` 找到症状对应的故障族。
3. 如果问题已经定位到文档层，最后用 `SpringCoreBeansModuleContractLabTest` 确认对应文档和支撑测试没有漂移。
