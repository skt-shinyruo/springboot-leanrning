# 逐章补强建议（part-03-container-internals 容器内部机制）

本 Part 的补强目标是把“容器为什么这样工作”讲成可断点证明的算法：基础设施处理器如何让注解工作、后处理器如何排序、early reference 如何出现、refresh→doCreateBean 主线如何串起来。

### 第 22 章：12. 容器启动与基础设施处理器：为什么注解能工作？

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md`
- 补强策略：
  - 增加“注解能力 → 对应基础设施处理器”的映射表：例如 `@Autowired/@Value/@Resource/@PostConstruct` 分别依赖哪些 processor。
  - 把“为什么能工作”落到 `registerBeanPostProcessors` 与 `invokeBeanFactoryPostProcessors` 的具体链路与观察变量。
  - 增补“缺失处理器”的最小复现与排障：例如手工创建 BeanFactory 时哪些能力默认不存在，如何补齐。

### 13. BeanDefinitionRegistryPostProcessor：在“注册阶段”动态加定义

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/13-bdrpp-definition-registration.md`
- 补强策略：
  - 补充与 `ImportBeanDefinitionRegistrar` 的边界对比：两者都能“加定义”，但触发时机与适用场景不同。
  - 增补“动态加定义”的副作用：对排序、条件评估、后续 BFPP/BPP 的影响（以及如何在调试器中证明）。
  - 增加“典型工程用法”与“误用风险”：例如滥用注册导致 bean graph 难以推断。

### 14. 顺序（Ordering）：PriorityOrdered / Ordered / 无序

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/14-post-processor-ordering.md`
- 补强策略：
  - 给出“排序算法的骨架”：按分类收集/排序/执行的步骤与关键入口方法，让读者能在断点里看见列表如何变化。
  - 增补“顺序影响结果”的可复现案例：例如同一个属性占位符/注入解析在不同顺序下的差异（可规划 Lab）。
  - 串联“手工添加 BPP”的章节：把 programmatic 注册如何破坏默认排序讲透（链接 Part 04）。

### 15. 实例化前短路：postProcessBeforeInstantiation 能让构造器根本不执行

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/15-pre-instantiation-short-circuit.md`
- 补强策略：
  - 强化“短路到底跳过了什么”：构造器、populate、initialize 哪些会被跳过/仍会执行，给出证据链入口。
  - 补充“短路常见来源”：AOP auto-proxy、懒代理、特殊 InstantiationAwareBPP，帮助读者在真实项目中识别。
  - 增补“短路带来的边界问题”：例如字段注入是否发生、生命周期回调是否执行、debug 时如何判断是 short-circuit。

### 16. early reference 与循环依赖：getEarlyBeanReference 到底解决什么？

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/16-early-reference-and-circular.md`
- 补强策略：
  - 用“三级缓存快照”讲清每一层缓存存的是什么对象（raw/early/final），以及对象何时写入/何时读出。
  - 补齐“early reference 与最终对象一致性”的关键点：哪些 BPP 参与 early reference，哪些只在 after-init 生效。
  - 增加“常见失败模式”：构造器循环依赖、prototype 循环依赖、AOP 介入导致的循环依赖升级。

### 17. 生命周期回调顺序：Aware / BPP / init / destroy（以及 prototype 为什么不销毁）

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/17-lifecycle-callback-order.md`
- 补强策略：
  - 增加“完整回调顺序的证据链”：用关键入口方法串起 Aware、BPP before/after、initMethod、destroyMethod 的触发点。
  - 补齐“prototype 不销毁”的工程解释：谁负责调用 destroy、什么时候需要手工销毁、常见资源泄漏案例。
  - 串联 `SmartInitializingSingleton` / `SmartLifecycle`：把“容器完成单例创建后的钩子”与生命周期回调区别讲清（链接 Part 04）。

### 18. 从 `refresh()` 到 `doCreateBean()`：把 Spring Bean “变成对象”的主线走通（源码级）

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/18-refresh-to-bean-creation-mainline.md`
- 补强策略：
  - 对超长章节增加“分层索引”：定义层处理、实例化策略、依赖解析、populate、initialize、代理替换各自形成小节入口。
  - 增加“锚点断点 + 关键对象快照”：例如在 `doGetBean`、`doCreateBean` 的关键窗口列出必须观察的变量集合。
  - 增补“分支归因表”：当读者看到某种现象（代理、循环依赖、FactoryBean、@Lazy）时，主线在哪个窗口分叉，并链接到对应专题章。

