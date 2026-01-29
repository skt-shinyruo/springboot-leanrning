# 逐章补强建议（part-01-ioc-container 注册/注入/生命周期/扩展点）

本 Part 是“把 IoC 容器学到能解释”的核心：补强重点是把概念落到 `DefaultListableBeanFactory` 的方法级证据链，并补齐高频边界条件（FactoryBean、泛型、循环依赖、proxy）。

### 02. Bean 注册入口：扫描、@Bean、@Import、registrar（已合并）

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/02-bean-registration.md`
- 补强策略：
  - 增加“注册入口统一主线”：把扫描/@Bean/@Import/Registrar 汇总到“最终注册 BeanDefinition 的入口方法”这一层（例如 registry 操作点）。
  - 把 Import 体系细化到可断点的链路：区分 ImportSelector / DeferredImportSelector / ImportBeanDefinitionRegistrar 的触发窗口与差异。
  - 增补“条件与 back-off”预告：为后续 Boot auto-config 铺垫（条件满足/不满足时定义层发生什么），并链接到 Part 02。
  - 加入“注册失败的分型”：类路径缺失、条件不满足、beanName 冲突、重复扫描等，并给出每类的第一断点入口。

### 第 14 章：03. 依赖注入解析：类型/名称/@Qualifier/@Primary

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/014-03-dependency-injection-resolution.md`
- 补强策略：
  - 将注入解析拆为“候选收集 → 候选收敛 → 最终选择”三步，并把每一步对读者最关键的入口方法列出来（方法级证据链）。
  - 深化 `@Qualifier` 的匹配语义：value/name、meta-annotation、自定义 Qualifier 与 resolver 的关系（并串联 Part 05 的自定义 Qualifier 章节）。
  - 补齐“集合注入/泛型注入”的真实匹配逻辑与坑位预告（并链接到 Part 04 的泛型匹配坑）。

### 第 15 章：04. Scope 与 prototype 注入陷阱（ObjectProvider / @Lookup / scoped proxy）

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/015-04-scope-and-prototype.md`
- 补强策略：
  - 把“prototype 注入 singleton 为什么看起来像单例”拆成：注入发生时机 vs 创建发生时机，并给出可断点证明的入口。
  - 增加“scoped proxy 的代价与边界”：例如序列化、equals/hashCode、AOP 叠加、调试时如何识别代理链。
  - 明确 prototype 生命周期的“谁负责销毁”与实际工程风险（资源泄漏/线程池/连接等），并串联到生命周期回调章节。

### 第 16 章：05. 生命周期：初始化、销毁与回调（@PostConstruct/@PreDestroy 等）

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/016-05-lifecycle-and-callbacks.md`
- 补强策略：
  - 用“完整顺序 + 关键窗口”把回调顺序讲透：Aware、BPP before/after、init、destroy 的触发点与边界（与 Part 03 的回调顺序章互相对齐）。
  - 补充“代理与生命周期”的交互：哪些回调发生在 raw instance 上，哪些发生在 exposed object 上，如何用断点证明。
  - 增补“失败分型”：init 抛异常、destroy 不执行、prototype 不销毁等真实问题的排障入口。

### 第 17 章：06. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/017-06-post-processors.md`
- 补强策略：
  - 增加“能/不能做什么”的反例证明：例如为什么 BPP 不能可靠修改 BeanDefinition，为什么 BFPP 不能拿到完整实例态信息。
  - 串联 BDRPP：把“注册阶段动态加定义”与 BFPP/BPP 的窗口放在同一时间轴上，形成闭环（链接 Part 03）。
  - 补齐“顺序影响结果”的具体案例：用一个小例子解释 PriorityOrdered/Ordered 的差异如何影响最终行为（链接 Part 03 顺序章）。

### 第 18 章：07. `@Configuration` 增强与 `@Bean` 语义（proxyBeanMethods）

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/018-07-configuration-enhancement.md`
- 补强策略：
  - 强化“full vs lite”差异：哪些配置类会被增强，哪些不会；为什么 `proxyBeanMethods=false` 会改变语义。
  - 增加“从现象到证据链”：同一个 `@Bean` 方法多次调用到底返回谁（raw/new vs 容器单例），给出可断点证明入口。
  - 串联“代理产生阶段”章节：把 configuration 增强产生的代理与 AOP 代理区别讲清楚，避免“代理一锅粥”。

### 第 20 章：01. Bean 心智模型：从 BeanDefinition 到最终暴露对象

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/020-01-bean-mental-model.md`
- 补强策略：
  - 增加“最终暴露对象三大替换入口”的对照表：pre/early/after-init 三个窗口分别能改变什么、不能改变什么。
  - 补充“从错误定位到分层”的速记：读者遇到异常时如何先定位到定义层还是实例层，并给出第一断点入口。
  - 把“raw instance vs exposed object”的判别手段讲得更工程化：如何在调试器中快速确认是否 proxy、代理链是什么。

### 08. `FactoryBean`：产品 vs 工厂（以及 `&` 前缀）

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/08-factorybean.md`
- 补强策略：
  - 深挖 “& 前缀”背后的缓存语义：FactoryBean 自身与 product 的缓存分别在哪层（并串联 Part 04 的 FactoryBean 深潜与边界章）。
  - 增补“按类型发现/注入”的关键点：`getObjectType`、`isSingleton`、eager init 等对 type matching 的影响，避免读者只记概念。
  - 增加“与内置 FactoryBean 的联系”：给读者一个“看到某个内置 FactoryBean 时如何判断最终暴露对象”的方法（链接 Part 05 图鉴）。

### 09. 循环依赖：现象、原因与规避（constructor vs setter）

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/09-circular-dependencies.md`
- 补强策略：
  - 用“为什么构造器失败、setter 有时能成功”的机制解释贯通三级缓存与 early reference，并明确关键窗口的入口方法。
  - 补齐“代理参与时的坑”：early reference 与最终代理不一致、AOP 导致的循环依赖加剧、self-invocation 等。
  - 增加“规避策略的决策树”：`@Lazy`、拆分依赖、引入接口、重构依赖方向分别适用什么场景。

