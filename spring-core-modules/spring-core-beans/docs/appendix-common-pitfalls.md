# Appendix：常见误区与失败症状

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文只做支持：把常见误区、失败症状和最短观察入口放在一起。
    - 这里不解释完整机制，机制正文回到对应 owner 文档。
    - 最短验证入口是 `SpringCoreBeansTroubleshootingPlaybookLabTest` 和 `SpringCoreBeansExceptionNavigationLabTest`。

    观察对象：常见误区、失败症状和最短观察入口。
    主线位置：支持文档，不新增机制正文。
    对照入口：`SpringCoreBeansTroubleshootingPlaybookLabTest`、`SpringCoreBeansExceptionNavigationLabTest`。
<!-- CHAPTER-CARD:END -->

这页的作用是让你先判断“问题像哪一类”，再决定去看哪篇正文或哪个 Lab。它不是完整机制说明书，只是一个故障分类表。

## 误区对照

| 误区 | 常见症状 | 最短观察入口 |
| --- | --- | --- |
| 有注解就一定会有 Bean | `NoSuchBeanDefinitionException`，或者 `getBean()` 根本找不到目标 | `SpringCoreBeansTroubleshootingPlaybookLabTest` |
| 单值注入会自动挑一个“差不多”的候选 | `NoUniqueBeanDefinitionException` | `SpringCoreBeansTroubleshootingPlaybookLabTest`，必要时再看 `SpringCoreBeansExceptionNavigationLabTest` |
| 构造器里看到的对象和最终拿到的对象一定相同 | raw 对象、early reference 和代理对象不一致 | `SpringCoreBeansTroubleshootingPlaybookLabTest` |
| 循环依赖只要开着就都会成功 | 有的场景能过，有的场景会炸，或者注入到的是 raw 引用 | `SpringCoreBeansTroubleshootingPlaybookLabTest` |
| FactoryBean 返回的就是工厂本身 | `getBean("name")` 拿到产品，`&name` 才是工厂本身 | `SpringCoreBeansTroubleshootingPlaybookLabTest` |
| `@Lazy` 会让一切都不提前创建 | 仍然可能因为预实例化、依赖链或代理窗口被触发 | `SpringCoreBeansTroubleshootingPlaybookLabTest` |
| XML / 定义输入出错会像普通注入失败一样表现 | `BeanDefinitionStoreException`，而不是注入异常 | `SpringCoreBeansExceptionNavigationLabTest` |
| 生产问题都该先怀疑 Boot 或 AOT | 很多问题其实只是注册、候选或创建阶段的基础错误 | 先跑 `SpringCoreBeansTroubleshootingPlaybookLabTest` |

## 最短判断法

先分两类：

1. 如果异常来自定义读取或解析，先看 `SpringCoreBeansExceptionNavigationLabTest`。
2. 如果异常来自依赖、候选、代理或循环依赖，先看 `SpringCoreBeansTroubleshootingPlaybookLabTest`。

这一步的目标不是直接修复，而是把“哪里坏了”先分出来。只要分对层，后面的正文就会短很多。
