# Technical Design: spring-core-beans Part 01（IoC Container）逐章内容深度完善（Solution）

## Technical Solution

### Core Technologies
- Spring Framework（BeanFactory / BeanDefinition / Post-Processor / 生命周期）
- Spring Boot（用于对照“容器叠加后为何变复杂”的理解口径）
- JUnit（本仓库 `spring-core-beans` 的 Labs/Test）
- Markdown（文档结构与可跳转导航）

### Implementation Key Points

1. **逐章策略优先**：不做统一模板硬套，而是基于每章主题补齐“最容易断层”的点（边界、反例、证据链抓手、章节桥接）。
2. **证据链写法统一但不标准化**：同一种结论，必须能落到“入口方法 + 关键变量 + 最短调用链 + 可跑实验/断点”。
3. **章节间桥接可点击**：每章补齐“上一章/下一章怎么衔接”，让读者能从现象直接跳到正确章节与断点入口。
4. **术语一致性**：Definition / Instance / Exposed（最终暴露对象）、early reference、proxy/wrapper、candidate collection/narrow down 等用词保持稳定。

## Security and Performance

- **Security:** 文档变更不涉及密钥/PII；避免在文档示例中引入真实凭证或生产地址。
- **Performance:** 仅文档与学习资产变更，不影响运行时性能；但会强调性能相关的语义边界（例如 `proxyBeanMethods=false` 的取舍）。

## Testing and Deployment

- **Testing:** 执行阶段建议跑 `mvn -pl spring-core-modules/spring-core-beans test`，确保所有 Lab/Test 仍可运行。
- **Deployment:** 无部署动作；若文档站点/索引依赖目录结构，执行阶段需要同步更新导航页（若存在）。

---

## 逐章：补充 / 完善 / 深入（Part 01｜IoC Container）

> 说明：下面每章给出“补充/完善/深入”的具体策略，强调的是“怎么把这一章变成可迁移能力”，而不是套固定检查清单。

### 01-bean-registration.md（注册入口：扫描 / @Bean / @Import / registrar / programmatic）

**补充（缺口补齐）**
- 补齐 `BeanDefinition` 的关键字段如何被不同入口写入：例如 `beanClassName` / `factoryMethodName` / `role` / `scope` / qualifiers / `autowireCandidate` 等，并说明“哪些字段能直接决定后续注入/代理/生命周期”。
- 补齐“同一个目标对象用不同入口注册”的对照策略：让读者能用 `BeanDefinition` 类型与字段差异解释行为差异（而不是只记“入口名字”）。

**完善（表达与结构）**
- 强化“注册时机决定能力”的桥接：把注册窗口与 BFPP/BDRPP、BPP 注册阶段的关系写成可点击的路径（避免读者误把注册当成实例化）。
- 增加“命名与别名”在注册阶段的入口提示：把 beanName 生成、冲突、alias 的影响提前放进排障路线（否则读者会在注入歧义时走弯路）。

**深入（进阶与真实世界）**
- 加深 `@Import` 的分叉：`ImportSelector`（返回类名清单） vs `ImportBeanDefinitionRegistrar`（直接操作 registry）在工程化扩展中的适用边界与风险。
- 补齐“定义层注册 vs 实例层注册”的工程后果：`registerSingleton` 对 BPP/回调/代理的影响（为何慎用），给出最短证据链策略。

### 02-dependency-injection-resolution.md（注入解析：候选收集 → 收敛 → 最终注入）

**补充（缺口补齐）**
- 把“注入点元数据”拉到台前：字段注入 vs 构造器/方法参数注入点的差异（Field vs `MethodParameter`），并明确它会影响泛型保真度、注解集合、参数名可见性等。
- 补齐“异常信息其实是注入点摘要”的解析策略：从异常还原 `DependencyDescriptor` 的关键字段（required、dependencyType、dependencyName、annotations）。

**完善（表达与结构）**
- 用“决策树 + 关键变量”的方式，把 `findAutowireCandidates`/`determineAutowireCandidate` 的候选收敛过程写成读者可复述的路径（强调哪里会 early return，哪里会 by-name fallback）。
- 明确区分“单依赖注入”与“集合/流注入”的决策规则：`@Order`/`Ordered` 只影响集合顺序，不解决单依赖歧义；把这一边界放到更靠前的位置，减少误判。

**深入（进阶与真实世界）**
- 补齐“泛型匹配为什么不稳定”的迁移策略：如何从 `ResolvableType` 与 class metadata 解释“为什么有时能缩窄、有时不能”，并给出稳定的修复路径（显式限定/拆分类型/避免擦除陷阱）。
- 补齐“FactoryBean product 参与按类型查找”的交叉提醒：候选集合里出现的类型有时来自 `getObjectType()`，错误实现会直接污染注入解析。

### 03-scope-and-prototype.md（Scope / prototype 注入陷阱）

**补充（缺口补齐）**
- 把 scoped proxy 的双 Bean 名语义讲透：`beanName`（proxy）与 `scopedTarget.beanName`（target）并存；补齐 `ScopedProxyMode.INTERFACES/TARGET_CLASS/NO` 的语义差异与排障证据链。
- 补齐“prototype 的销毁语义为什么默认不托管”的解释口径：明确容器托管边界与手动销毁入口（避免把“没触发 @PreDestroy”误判为生命周期 bug）。

**完善（表达与结构）**
- 强化三种方案的取舍依据：`ObjectProvider`（延迟获取）/ `@Lookup`（方法注入）/ scoped proxy（运行期取 target）分别适合解决什么问题、会引入什么新的边界（代理类型、序列化、equals/hashCode、调试难度）。
- 把“prototype 循环依赖通常救不了”的原因放回创建 guard 与缓存语义解释（避免读者把它当成“三级缓存失效”）。

**深入（进阶与真实世界）**
- 补齐“自定义 Scope”在工程中的关键点：注册位置、存储策略、回收策略与线程/请求边界，明确哪些问题属于 scope 设计而不是容器 bug。

### 04-lifecycle-and-callbacks.md（生命周期：初始化/销毁/回调）

**补充（缺口补齐）**
- 明确 `@PostConstruct/@PreDestroy` 的触发者与窗口：它们是 BPP 触发的，并分别落在初始化链与销毁链；补齐“为什么不是语法魔法”的解释抓手。
- 补齐“回调发生在 raw 还是 proxy”这一高频困惑：用代理替换发生点（before/after initialization、early reference 等）解释观察差异。

**完善（表达与结构）**
- 把生命周期放回 `doCreateBean` 的五段式：实例化 →（可能 early exposure）→ populate → initialize → expose；让读者能把“回调顺序”对齐到方法级入口。
- 强化 scope 交叉：prototype / 自定义 scope 与销毁链的差异，不再把“没回调”当作异常。

**深入（进阶与真实世界）**
- 细化“容器级生命周期”与“Bean 级生命周期”的选型：`SmartInitializingSingleton` / `SmartLifecycle` / 事件（例如 `ContextRefreshedEvent`）各自的适用边界与常见误用。

### 05-post-processors.md（扩展点：BFPP / BPP / BDRPP）

**补充（缺口补齐）**
- 用“介入点地图”补齐 BPP 的真实能力：实例化前短路、early reference、merged definition、销毁前回调四类能力与各自风险。
- 补齐“基础设施处理器”视角：哪些 processors 让注解真正生效、它们的排序与时机为什么决定结果（避免把问题误判为“依赖没引入”）。

**完善（表达与结构）**
- 强化“什么时候用哪一种扩展点”的工程化口径：注册新定义/改定义/改实例/换代理分别应落在哪一类扩展点，并给出典型反例（例如在 BFPP 里过早 `getBean()`）。
- 把 `PostProcessorRegistrationDelegate` 的“两段式/循环发现”讲成可复述算法：为什么会反复扫描、为什么顺序影响最终行为。

**深入（进阶与真实世界）**
- 补齐“为什么某个 bean 会错过某些 BPP”的排障策略：创建时机与 BPP 链装载时机不一致时，如何用断点与关键变量证明。

### 06-configuration-enhancement.md（`@Configuration` 增强与 `@Bean` 语义）

**补充（缺口补齐）**
- 补齐“方法参数注入不依赖增强”的证明路径：`@Bean` 工厂方法参数解析会构造注入点元数据（`MethodParameter`），即使 `proxyBeanMethods=false` 也走标准依赖解析。
- 补齐“lite vs full 配置类”的差异提示：哪些场景下 `@Bean` 方法只是普通工厂方法，哪些场景会触发配置类解析与增强（避免读者把所有 `@Bean` 都当成同一语义）。

**完善（表达与结构）**
- 把“互相调用 @Bean 方法”的风险写成强对照：在 `proxyBeanMethods=false` 下会退化为普通方法调用，绕开容器，从而绕开代理/生命周期/注入。
- 把增强入口与关键类写清楚：`ConfigurationClassPostProcessor` / `ConfigurationClassEnhancer` / 拦截器的作用点与证据链抓手。

**深入（进阶与真实世界）**
- 补齐与循环依赖/代理的交叉：配置类增强与 early reference、AOP 代理替换发生点之间如何相互影响（给出“如何证明”的路径）。

### 07-factorybean.md（FactoryBean：product vs factory）

**补充（缺口补齐）**
- 补齐“类型匹配来自哪”的关键点：product 的类型往往来自 `getObjectType()`；错误实现会导致按类型注入/查找异常与候选污染。
- 补齐“缓存语义”的解释抓手：`isSingleton()` 影响的是 product 是否被缓存，不等价于 FactoryBean 本身的 scope。

**完善（表达与结构）**
- 增加与 DI 的桥接提示：为什么 FactoryBean 会让“按类型找 bean”出现反直觉结果（尤其在候选收集阶段），以及如何用 `&` 前缀把 factory 本体取出来定位问题。

**深入（进阶与真实世界）**
- 补齐与代理/循环依赖交叉的排障策略：FactoryBean 既可能制造 proxy，也可能参与循环依赖救援窗口；需要明确它在 `doGetBean` 的分支位置与证据链。

### 08-circular-dependencies.md（循环依赖：三级缓存、early reference、工程规避）

**补充（缺口补齐）**
- 补齐“救援窗口”的方法级定位：early exposure 发生在 `doCreateBean` 的哪一步；把“能救/不能救”落到构造器 vs setter、prototype vs singleton 的边界解释。
- 补齐“early vs final 不一致”的风险解释：`getEarlyBeanReference` 可能返回 proxy，最终暴露对象可能不同；用证据链避免“能启动就当没问题”。

**完善（表达与结构）**
- 强化“从异常到断点入口”的路线：`Requested bean is currently in creation` 的最短跳转路径、要看哪些缓存/标志位、如何定位环路边。
- 把工程规避策略写成可落地路径：重构解耦、延迟获取（`ObjectProvider`）、注入点 `@Lazy`、事件/回调等，每条策略指出适用边界与副作用。

**深入（进阶与真实世界）**
- 补齐配置项与默认策略对照：`allowCircularReferences` / `allowRawInjectionDespiteWrapping` 的工程后果（强调“默认能跑”不等于“可维护”）。

### 09-bean-mental-model.md（Bean 心智模型：Definition/Instance/Exposed）

**补充（缺口补齐）**
- 补齐“能注入但不是 Bean”的边界：ResolvableDependency 与外部对象（`AutowireCapableBeanFactory`）如何进入注入链路，避免误判为“没注册”。
- 补齐与 scoped proxy / FactoryBean 的桥接：proxy/target、product/factory 都会让“最终对象”与“定义/实例”不一致，把这些都映射回三层模型。

**完善（表达与结构）**
- 用“一张表 + 一条主线”串起：refresh → doCreateBean → doGetBean，强调读者应先判断问题属于定义层/实例层/暴露层。

**深入（进阶与真实世界）**
- 增加“排障迁移策略”：给出从现象到层级的快速归因路线（例如：类型不匹配→先判定代理/FactoryBean；注入歧义→先看候选收敛；回调不执行→先看创建/销毁链路）。

