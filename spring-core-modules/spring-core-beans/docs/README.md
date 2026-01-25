# Spring Core Beans（IoC 容器）：可验证教程（兼顾 A/B/C，重点 B/C）

> 这不是“API 速记”，而是把 IoC 容器讲成**能复现、能断点、能排障**的教程。  
> 你会反复做同一件事：**跑一个 Lab → 下断点 → 看变量 → 回到文档的关键分支表**。

## 你在这里要拿到的能力

- **A（新手）**：能用测试把容器跑起来，知道“Bean = 定义 + 创建 + 注入 + 生命周期 +（可能）代理/包装”。
- **B（工程师）**：能在遇到注入歧义、生命周期顺序、BPP 不生效、循环依赖等问题时，快速定位到“容器处在主线的哪一段”并证明根因。
- **C（源码党）**：能从 `ApplicationContext#refresh` 跟到 `AbstractAutowireCapableBeanFactory#doCreateBean`，并能解释关键分支背后的设计取舍。

## 最小闭环（先做一次，10 分钟够）

1) 跑一个稳定入口（建议从“主线调用链”开始）：

```bash
mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansMainlineCallChainLabTest test
```

2) 下 2 个断点（入口 + 核心机制）：

- `org.springframework.context.support.AbstractApplicationContext#refresh`
- `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`

3) 盯 3 个观察点（变量就是证据）：

- `DefaultListableBeanFactory#beanDefinitionMap`：注册了哪些“定义”
- `DefaultSingletonBeanRegistry#singletonObjects`：单例何时出现
- `AbstractAutowireCapableBeanFactory#populateBean`：注入发生在哪一步（字段/构造器/方法参数）

> 断点地图与“看什么变量”：见 [Debugger Pack（断点包总入口）](appendix/98-debugger-pack.md)。

## 阅读路线（按目标走，不要乱翻）

### 1) A：30 分钟跑通容器（推荐新手）

1. [012-01 30 分钟快速闭环（跑起来 + 看见主线）](part-00-guide/012-01-quickstart-30min.md)
2. [013-02 断点图（排障优先）](part-00-guide/013-02-breakpoint-map.md)
3. [020-01 Bean 心智模型（先建立“容器视角”）](part-01-ioc-container/020-01-bean-mental-model.md)

### 2) B：把容器机制跑通（工程排障必备）

1. [010-03 主线时间线（现象 → 证据链 → 源码段落）](part-00-guide/010-03-mainline-timeline.md)
2. [013-01 refresh() 调用链（阶段感）](part-00-guide/013-01-applicationcontext-refresh-call-chain.md)
3. [02 Bean 注册入口（扫描/@Bean/@Import/registrar）](part-01-ioc-container/02-bean-registration.md)
4. [014-03 依赖解析与注入（`resolveDependency` 关键分支）](part-01-ioc-container/014-03-dependency-injection-resolution.md)
5. [016-05 生命周期与回调（顺序 + 扩展点）](part-01-ioc-container/016-05-lifecycle-and-callbacks.md)
6. [017-06 Post-Processor（BPP/BFPP/BDRPP 生效时机）](part-01-ioc-container/017-06-post-processors.md)
7. [09 循环依赖（三级缓存 + early reference 边界）](part-01-ioc-container/09-circular-dependencies.md)

### 3) C：源码主线深挖（refresh → doCreateBean）

1. [18 主线叙事：从 refresh() 走到 doCreateBean()（源码级）](part-03-container-internals/18-refresh-to-bean-creation-mainline.md)
2. [14 Post-Processor Ordering（算法级解析）](part-03-container-internals/14-post-processor-ordering.md)
3. [16 Early Reference & Circular（三级缓存）](part-03-container-internals/16-early-reference-and-circular.md)
4. [33 候选者选择（Primary/Priority/Order）](part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md)
5. [36 类型转换与 BeanWrapper（类型系统与转换链路）](part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md)

## 进阶入口（把问题快速缩小到 1–2 个关键分支）

- 断点地图（排障优先）：[013-02-breakpoint-map.md](part-00-guide/013-02-breakpoint-map.md)
- Debugger Pack（断点包总入口）：[98-debugger-pack.md](appendix/98-debugger-pack.md)
- 关键分支矩阵（If/Then 收敛）：[011-04-branch-decision-matrix.md](part-00-guide/011-04-branch-decision-matrix.md)
- 排障 playbook：[025-90-common-pitfalls.md](appendix/025-90-common-pitfalls.md)
- 生产排障清单（Checklist）：[94-production-troubleshooting-checklist.md](appendix/94-production-troubleshooting-checklist.md)
- 自检清单：[026-99-self-check.md](appendix/026-99-self-check.md)

## 推荐可跑入口（用来做“证据链”）

- Book Matrix（本模块最小可运行覆盖面）：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansBookMatrixLabTest test`
- Branch Matrix - IoC 分支：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansIocBranchMatrixLabTest test`
- Branch Matrix - 内部机制分支：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansInternalsBranchMatrixLabTest test`
- Solutions（本模块答案回归）：`mvn -q -pl :spring-core-beans -Dtest=*ExerciseSolutionTest test`
- 并发/性能：同一 BeanFactory 并发 getBean：`mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansConcurrentGetBeanLabTest test`

## 排坑与自检（读完一定要做）

- [常见坑](appendix/025-90-common-pitfalls.md)
- [自检](appendix/026-99-self-check.md)
