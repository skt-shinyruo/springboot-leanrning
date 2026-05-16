# 主线时间线：refresh、创建、注入、初始化、暴露

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文把 refresh 和单 Bean 创建放进同一条时间线里。
    - 重点是每一步改变了什么容器状态，而不是把源码方法逐个背下来。
    - 验证入口是 `SpringCoreBeansMainlineCallChainLabTest`。

    观察对象：refresh、创建、注入、初始化和最终暴露对象的时序。
    主线位置：容器准备完成之后到 Bean 对外可见之间。
    对照入口：`SpringCoreBeansMainlineCallChainLabTest`。
<!-- CHAPTER-CARD:END -->

把 Spring 容器的主线拆成两个视角会更清楚：一个视角看 `refresh()` 如何准备容器并触发非懒 singleton 预实例化，另一个视角看某个具体 Bean 进入创建链后如何完成实例化、注入、初始化和暴露。很多排障问题之所以绕，是因为把“容器阶段”和“单 Bean 创建细节”混成了一段。

## 时间线一：refresh 先把容器状态准备好

`refresh()` 关注的是容器状态变化，不是某一个 Bean 的对象细节。大致顺序可以这样看：

| 顺序 | 发生了什么 | 容器状态变化 |
| --- | --- | --- |
| 1 | 准备环境和上下文 | 旧状态清理完毕，新的 refresh 开始 |
| 2 | 读取并注册 BeanDefinition | registry 里开始有定义，而不是对象 |
| 3 | 执行 BeanDefinitionRegistryPostProcessor / BeanFactoryPostProcessor | 定义会被补充、修正或重新排序 |
| 4 | 注册 BeanPostProcessor | 后续创建链路开始受后处理器影响 |
| 5 | 初始化消息源、事件广播器和监听器 | ApplicationContext 的基础设施就位 |
| 6 | 预实例化非懒 singleton | 真正的 Bean 创建开始被触发 |
| 7 | 发布刷新完成信号 | 容器对外宣布“可以用了” |

`SpringCoreBeansMainlineCallChainLabTest` 把这段和单 Bean 创建主线放在一起，适合用来理解“refresh 不是某一个 Bean 的创建细节，但它会在预实例化阶段触发非懒 singleton 创建”。

## 时间线二：单个 Bean 从请求到暴露

当某个 Bean 被真正触发创建时，链路会更具体。触发点可能是 `refresh()` 内部的 `preInstantiateSingletons()`，也可能是后续显式 `getBean()`、lazy Bean 首次访问、prototype 请求或运行期依赖解析：

| 顺序 | 关键阶段 | 结果 |
| --- | --- | --- |
| 1 | `getBean()` 入口 | 先看缓存，再决定是否创建 |
| 2 | `doGetBean()` | 确认 scope、父工厂委托和依赖顺序 |
| 3 | `createBean()` / `doCreateBean()` | 进入实例化窗口 |
| 4 | `populateBean()` | 属性填充和依赖注入发生 |
| 5 | `initializeBean()` | Aware、init callback 和 BPP 介入 |
| 6 | exposed object | 最终对外返回的对象确定下来 |

这条链里最容易误判的是两个地方：构造器之前和初始化之后。前者决定原始实例怎么来，后者决定最后暴露的是不是同一个对象。

## 为什么顺序不能反过来

定义处理、后处理器注册和基础设施准备必须先于普通 Bean 创建，因为容器必须先知道有哪些定义、哪些后处理器、哪些基础设施 Bean，才能正确处理单个对象。创建又必须先于暴露，因为注入、初始化和代理包装都要先完成，调用方才能拿到稳定的对象。

如果你把 `refresh()` 当成“所有创建都结束”，会看不懂后面的 `getBean()` 为什么还会触发对象创建。正确的理解是：refresh 内部会集中创建非懒 singleton；而 lazy、prototype 或其他按需场景，会在 refresh 之后继续使用同一条单 Bean 创建链。
