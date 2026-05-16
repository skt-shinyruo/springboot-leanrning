# BeanPostProcessor：初始化窗口、包装和代理

<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 本文只讲 `BeanPostProcessor` 如何参与 bean 实例创建、属性注入、初始化、包装和销毁窗口。
    - 不展开 BFPP，也不讨论定义注册阶段。
    - 读完后应该知道 BPP 什么时候能看到原始对象、什么时候能注入属性、什么时候可以把它换成另一个对象。

    观察对象：实例化前后、属性填充、初始化前后、包装结果、销毁回调和程序化注册顺序。
    主线位置：单个 bean 创建期间、最终暴露之前，以及容器销毁阶段。
    对照入口：`SpringCoreBeansLifecycleRawVsProxyLabTest`、`SpringCoreBeansProgrammaticBeanPostProcessorLabTest`。
<!-- CHAPTER-CARD:END -->

`BeanPostProcessor` 的作用对象是实例，不是定义。它不负责注册或改写 `BeanDefinition`，而是在普通 bean 创建和销毁过程中参与实例处理。最常见的两个窗口在初始化前后：

1. `postProcessBeforeInitialization`，在初始化回调之前。
2. `postProcessAfterInitialization`，在初始化回调之后、最终暴露之前。

但 BPP 家族不只这两个方法。`InstantiationAwareBeanPostProcessor` 可以更早进入实例化和属性填充窗口，`DestructionAwareBeanPostProcessor` 可以在销毁前进入收尾窗口。它们仍然属于实例级处理：处理的是对象创建、注入、初始化、包装、销毁，不处理定义注册。

## 实例化和属性填充窗口

`InstantiationAwareBeanPostProcessor` 是 BPP 家族里最靠近对象创建前半段的扩展点。它的几个窗口要分开看：

| 窗口 | 典型方法 | 能做什么 | 边界 |
| --- | --- | --- | --- |
| 实例化前 | `postProcessBeforeInstantiation` | 在构造器执行前返回代理或替代对象 | 返回非空对象会短路普通实例化 |
| 实例化后 | `postProcessAfterInstantiation` | 决定是否继续给这个实例填充属性 | 已有 raw instance，但属性注入还没完成 |
| 属性注入 | `postProcessProperties` | 发现注入点、解析依赖、写入字段或方法参数 | 发生在初始化回调之前 |

这三个窗口解释了为什么有些能力看起来像“注解自己生效”，实际是后处理器在处理实例。`@Autowired` 字段和方法注入不是 Java 对象天然会做的事，而是自动装配处理器在属性填充阶段扫描注入点、构造依赖描述、调用 BeanFactory 解析候选，再把结果写回实例。

实例化前短路和普通 after-init 包装也不是一回事。前者可以让构造器根本不执行，直接返回替代对象；后者是在 raw instance 已经完成初始化之后，把最终暴露对象换成代理。本文只在这里说明它们的窗口边界，完整短路机制由 `pre-instantiation-short-circuit.md` 负责。

## 初始化窗口

当一个 bean 进入初始化阶段时，容器通常已经完成了构造器调用和属性注入。此时 BPP 能看到的是“即将被初始化的对象”，而不是空壳。

`postProcessBeforeInitialization` 发生在 `@PostConstruct`、`InitializingBean`、自定义 init-method 之前之后的衔接位置。它不负责创建对象，只负责在初始化前把对象调到正确状态。

`postProcessAfterInitialization` 发生在初始化回调之后。这里返回什么，容器最终就可能暴露什么。对于 AOP、事务、缓存这类能力，最常见的就是在这个窗口返回一个代理对象。

生命周期注解同样依赖 BPP。`@PostConstruct` 和 `@PreDestroy` 不是 `InitializingBean` 或 `DisposableBean` 这种接口回调；容器需要相应的后处理器识别这些注解，并在初始化或销毁窗口主动调用方法。缺少这些处理器时，注解留在类上也不会自动执行。

`SpringCoreBeansLifecycleRawVsProxyLabTest` 把这个窗口关系看得很清楚：

- `@PostConstruct` 记录到的是 raw bean 的 identity。
- `BeanPostProcessor` 在 after-init 阶段返回了 JDK proxy。
- `context.getBean(WorkService.class)` 拿到的是最终代理，而不是原始实现。

这说明 BPP 不只是“插点日志”，它真的能改变外部调用方接触到的对象。

## 包装和代理

只要 `postProcessAfterInitialization` 返回了新对象，后面的容器缓存、注入和 `getBean()` 都会围绕这个新对象展开。对调用方来说，原始对象可能只存在于创建过程内部，真正暴露的是代理或包装器。

这也是为什么 BPP 经常和 AOP、`@Transactional`、异步执行等能力绑在一起。它们需要在 bean 完成基本初始化后，把“原始实例”包成“可拦截实例”。

一个实用判断是：

- 如果你想改 bean 的内部状态，通常看 before-init。
- 如果你想改变调用方最终拿到的对象，通常看 after-init。

## 销毁窗口

`DestructionAwareBeanPostProcessor` 参与的是实例生命末尾。容器准备销毁 singleton 或某个 scope 管理的实例时，会给这类处理器机会，让它在对象真正释放前做收尾。

这个窗口适合做：

- 调用注解声明的销毁方法，例如 `@PreDestroy`。
- 清理后处理器自己附加到实例上的资源。
- 在销毁前记录或断开包装对象和目标对象之间的关系。

它的边界也很清楚：销毁窗口不创建 bean，不负责依赖解析，也不改变已暴露对象的身份。它处理的是“这个实例即将退出容器管理”时的最后一次回调。

## 程序化注册的特殊性

`SpringCoreBeansProgrammaticBeanPostProcessorLabTest` 说明了一条容易误判的规则：通过 `beanFactory.addBeanPostProcessor(...)` 程序化加入的 BPP，执行顺序先于容器自动检测到的 BPP，而且它们的顺序主要按注册顺序走，不会像自动发现的 BPP 那样完整参与 `Ordered` 体系。

这个实验里：

- 程序化 BPP 先于 bean 定义出来的 BPP 执行。
- 两个程序化 BPP 的顺序就是加入顺序，而不是 `Ordered` 值的大小。

所以，如果一个扩展点是通过代码直接塞进 `BeanFactory` 的，不能默认它会被容器当作普通候选 bean 那样重新排序。

## 什么时候它会改变对象身份

不是每个 BPP 都会改对象身份，但它确实允许这么做。判断依据很简单：

- before-init 通常不改身份。
- after-init 可以改身份。
- 返回原对象时，外部看到的还是原实例。
- 返回代理或包装器时，外部看到的是新身份。

`SpringCoreBeansLifecycleRawVsProxyLabTest` 的 identity hash 差异就是这个事实的直接证据。

## 本模块的观察入口

```bash
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansLifecycleRawVsProxyLabTest test
mvn -pl :spring-core-beans -Dtest=SpringCoreBeansProgrammaticBeanPostProcessorLabTest test
```

- `SpringCoreBeansLifecycleRawVsProxyLabTest`：`@PostConstruct` 作用在 raw bean 上，而 after-init BPP 可以把最终对象换成代理。
- `SpringCoreBeansProgrammaticBeanPostProcessorLabTest`：程序化注册的 BPP 先执行，且顺序主要取决于注册顺序。
