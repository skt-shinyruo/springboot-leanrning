# 逐章内容级再加深建议（part-03-container-internals）

本 Part 的再加深重点：从“能讲流程”升级到“能讲算法/能讲关键分支/能用断点证明”，并强化与真实排障的连接。

## 执行化提示（Internals 章的最低交付）

- 每章至少给出 1 条“最短调用链 + 决策点 + 关键变量”的证据链（避免只讲概念）。
- 每章至少给出 1 个“现象→阶段→入口方法→判定标准”的排障分流，确保可迁移到生产问题。

### 第 22 章：12. 容器启动与基础设施处理器：为什么注解能工作？

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md`
- 内容级加深策略：
  - A：补“注解能力→处理器→生效窗口”的证据链对照表，并落到具体入口方法。
  - B：补反例：缺失基础设施时哪些注解不生效；过早 getBean 导致处理器未注册。
  - C：补排障：@Autowired/@Value/@PostConstruct 不生效时的第一断点入口。
  - D：补断点：处理器注册与执行顺序的关键锚点 + watch list。
  - E：补面试追问：为什么说“注解能工作不是魔法，是处理器装进了 refresh 主线”。

### 13. BDRPP：注册阶段动态加定义

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/13-bdrpp-definition-registration.md`
- 内容级加深策略：
  - A：补“动态加定义”进入主线的证据链：何时被调用、对 registry 造成什么影响。
  - B：补反例：滥用导致 bean graph 难以推断；与 ImportBeanDefinitionRegistrar 混用导致时机误判。
  - C：补排障：为什么某个 bean “凭空出现/出现顺序异常/条件判断异常”。
  - D：补断点：invokeBeanFactoryPostProcessors 内部对 BDRPP 的分组与执行顺序。
  - E：补面试追问：BDRPP 与 BFPP 的边界与适用场景如何解释并证明。

### 14. 顺序（Ordering）：PriorityOrdered / Ordered / 无序

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/14-post-processor-ordering.md`
- 内容级加深策略：
  - A：补“排序算法骨架”与关键列表快照（收集→排序→执行），让读者能在断点里看见顺序如何被决定。
  - B：补反例：programmatic 注册绕过默认排序；@Order 与 @Priority 的边界误判。
  - C：补排障：增强偶发不生效/顺序错乱时的第一入口与观察点。
  - D：补断点：排序发生点、processor 列表构建点、注册点。
  - E：补面试追问：为什么 PriorityOrdered 必须先于 Ordered？否则会出现什么可证明的问题。

### 15. 实例化前短路：postProcessBeforeInstantiation

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/15-pre-instantiation-short-circuit.md`
- 内容级加深策略：
  - A：补“短路跳过了什么/仍会做什么”的证据链（构造器/populate/initialize/销毁等）。
  - B：补反例：短路导致字段注入没发生、生命周期回调不符合预期、代理链难以追踪。
  - C：补排障：为什么构造器没执行但 bean 仍存在？第一断点如何定位到短路发生点。
  - D：补断点：resolveBeforeInstantiation、postProcessBeforeInstantiation 关键入口与 watch list。
  - E：补面试追问：短路与 after-init proxy 有何区别？如何用证据链证明。

### 16. early reference 与循环依赖：getEarlyBeanReference

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/16-early-reference-and-circular.md`
- 内容级加深策略：
  - A：补三级缓存的“写入/读出时间点”与对象类型（raw/early/final）的证据链对照。
  - B：补反例：early 与 final 不一致的真实后果（事务/AOP/懒代理叠加），以及 allowRawInjectionDespiteWrapping 的边界。
  - C：补排障：如何从异常/行为差异定位到“early window 参与者是谁”。
  - D：补断点：addSingletonFactory/getEarlyBeanReference/after-init proxy 的对照断点组。
  - E：补面试追问：为什么说“能解不等于安全”？用哪条证据链回答。

### 17. 生命周期回调顺序：Aware / BPP / init / destroy

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/17-lifecycle-callback-order.md`
- 内容级加深策略：
  - A：补“顺序与触发点”证据链：关键入口方法串起每类回调，并明确 raw vs exposed。
  - B：补反例：prototype 不销毁的误判、destroy 顺序与 dependent 图的交互。
  - C：补排障：初始化/销毁顺序异常、回调没触发的第一断点入口。
  - D：补 watch list：disposableBeans、dependentBeanMap、回调注册点。
  - E：补面试追问：SmartInitializingSingleton/SmartLifecycle 与普通生命周期回调的边界。

### 18. refresh → doCreateBean 主线（源码级）

- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/18-refresh-to-bean-creation-mainline.md`
- 内容级加深策略：
  - A：补“主线分层索引 + 最短调用链”：让读者能快速定位到 instantiate/populate/initialize/代理替换/缓存窗口。
  - B：补反例：每个关键分支给 1 个失败/偏差案例（FactoryBean、@Lazy 注入点、dependsOn、parent、prototype guard）。
  - C：补“现象→阶段→关键方法→必看变量→Lab”映射的覆盖率，确保常见症状都能被快速定位。
  - D：补“断点组”与判定标准：不只给断点，还给“看到什么变量意味着走了哪条分支”。
  - E：补“主线复述题”：让读者能用证据链回答“Bean 是怎么变成对象的”。
