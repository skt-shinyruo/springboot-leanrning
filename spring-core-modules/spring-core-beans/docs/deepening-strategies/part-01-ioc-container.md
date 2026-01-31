# 逐章内容级再加深建议（part-01-ioc-container）

本 Part 的再加深重点：把注册/注入/生命周期/扩展点进一步下压到“算法级决策点”，并补齐真实工程边界（FactoryBean/泛型/循环依赖/代理叠加）。

## 执行化提示（IoC 核心章的“深度落点”）

- 优先把“算法级决策点”写进正文：入口方法（在哪里做选择）+ 关键变量（用什么信息做选择）+ 失败分型（为什么会 NoSuch/NoUnique）。
- 反例要可复现：每章至少绑定 1 个 Lab/断点闭环，用断言与 watch list 证明“边界触发条件”。

### 02. Bean 注册入口：扫描、@Bean、@Import、registrar

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/02-bean-registration.md`
- 内容级加深策略：
  - A：补“最终注册 BeanDefinition 的统一落点”与最短调用链，明确不同入口最终汇聚到哪里。
  - B：补 Import 体系反例：ImportSelector/DeferredImportSelector/Registrar 的典型误用与排障点。
  - C：补“注册失败分型”：没注册/被条件排除/名字冲突/覆盖策略冲突，第一断点入口分别是什么。
  - D：补“注册阶段断点组”：registry 写入点、配置类解析点、条件评估点。
  - E：补“面试追问”：为什么说“注册的第一性对象是 BeanDefinition”，如何证明。

### 03. 依赖注入解析：类型/名称/@Qualifier/@Primary

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/014-03-dependency-injection-resolution.md`
- 内容级加深策略：
  - A：补“候选收集→收敛→最终选择”的算法骨架（最短调用链 + 关键 if/return）。
  - B：补反例：@Order 不能选单候选、by-name fallback 的边界、泛型信息丢失导致匹配失败。
  - C：补“注入失败分型”与 SOP：NoSuch/NoUnique/UnsatisfiedDependency/类型不匹配，各自第一断点入口与观察点。
  - D：补“依赖解析断点组”：`doResolveDependency`、候选收集、candidate 决策点、value 注入分支。
  - E：补“面试追问”：@Primary/@Qualifier/@Priority 谁更强？为什么？如何证明。

### 04. Scope 与 prototype 注入陷阱

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/015-04-scope-and-prototype.md`
- 内容级加深策略：
  - A：补“prototype 注入 singleton 为什么像单例”的证据链（注入时机 vs 创建时机）。
  - B：补反例：prototype 循环依赖、prototype 销毁不自动、scoped proxy 的 equals/hashCode/序列化易错点。
  - C：补排障：资源泄漏/生命周期错觉/线程隔离不生效时如何定位。
  - D：补断点：scope get/remove、scoped proxy 触发目标创建的入口。
  - E：补面试追问：@Lookup/ObjectProvider/scoped proxy 的选择策略。

### 05. 生命周期：初始化、销毁与回调

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/016-05-lifecycle-and-callbacks.md`
- 内容级加深策略：
  - A：补“完整顺序 + 关键窗口”的证据链（Aware/BPP/init/destroy）并明确发生在 raw 还是 exposed。
  - B：补反例：init 抛异常导致 destroy 不执行、prototype 不自动销毁、代理与回调顺序误判。
  - C：补排障：初始化卡死/销毁不执行/回调顺序不符的第一断点入口。
  - D：补 watch list：回调触发前后关键变量（如 wrappedBean、exposed object 变化）。
  - E：补面试追问：哪些回调能替代哪些？为什么推荐构造器注入+PostConstruct。

### 06. 容器扩展点：BFPP vs BPP（以及它们能/不能做什么）

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/017-06-post-processors.md`
- 内容级加深策略：
  - A：补“能/不能做什么”的方法级证明：为什么 BPP 不可靠改定义，为什么 BFPP 拿不到实例态。
  - B：补反例：错误时机 getBean 导致 BPP 链不完整、手工注册破坏顺序的误区。
  - C：补排障：某注解不生效/某增强不生效/代理不出现时，先定位缺哪个处理器与顺序问题。
  - D：补断点：processor 收集/排序/注册/执行的关键入口方法。
  - E：补面试追问：BDRPP 为什么更强？与 ImportBeanDefinitionRegistrar 的边界如何说明。

### 07. `@Configuration` 增强与 `@Bean` 语义

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/018-07-configuration-enhancement.md`
- 内容级加深策略：
  - A：补 full vs lite 的判定链路与证据点（为什么会/不会增强）。
  - B：补反例：proxyBeanMethods=false 导致的“多次调用多次 new”，以及与 AOP 代理的混淆点。
  - C：补排障：明明写了 @Bean 却获取到多个实例/依赖不一致时如何定位。
  - D：补断点：配置类解析、增强生成、@Bean 方法拦截的关键入口。
  - E：补面试追问：配置类增强与 AOP 代理有何不同？如何证明。

### 01. Bean 心智模型：从 BeanDefinition 到最终暴露对象

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/020-01-bean-mental-model.md`
- 内容级加深策略：
  - A：把 pre/early/after-init 三个替换窗口做成“证据链对照表”，并给每类窗口的关键入口方法。
  - B：补反例：early reference 与最终代理不一致导致的行为差异；FactoryBean 造成的“看起来类型不对”。
  - C：补排障：看到异常先分层到定义/实例/最终对象，并给第一断点入口。
  - D：补“如何快速识别 proxy/wrapper”：调试器判别方法与代理链定位。
  - E：补面试追问：BeanFactory vs ApplicationContext 的差异如何落到 refresh 证据链。

### 08. `FactoryBean`：产品 vs 工厂（以及 `&` 前缀）

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/08-factorybean.md`
- 内容级加深策略：
  - A：补“getBean vs &getBean”的证据链与缓存语义（FactoryBean 自身 vs product）。
  - B：补反例：getObjectType 返回 null 导致 type matching 失效、isSingleton 声明不一致导致缓存错觉。
  - C：补排障：按类型发现/条件装配/注入失败时如何判断是 FactoryBean 语义导致。
  - D：补断点：FactoryBean product 获取与缓存命中的关键入口。
  - E：补面试追问：FactoryBean 在 Boot auto-config 中为何高频出现？如何解释它的价值与风险。

### 09. 循环依赖：现象、原因与规避

- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/09-circular-dependencies.md`
- 内容级加深策略：
  - A：补“构造器失败 vs setter 可能成功”的完整证据链（三级缓存写入/读出窗口）。
  - B：补反例：prototype 循环依赖、AOP 介入导致 early/final 不一致、allowCircularReferences=false 的行为差异。
  - C：补排障：如何从异常信息分型到“构造器环/属性环/depends-on 环/代理导致环”。
  - D：补断点：三级缓存、early reference 回调、包装/代理替换入口。
  - E：补面试追问：为什么说“能解不等于安全”？如何用证据链回答。
