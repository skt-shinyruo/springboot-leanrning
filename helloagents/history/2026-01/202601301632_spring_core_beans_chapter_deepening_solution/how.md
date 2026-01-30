# Technical Design: spring-core-beans 逐章内容深度完善（Solution）

## Technical Solution

### Core Technologies
- **Markdown 文档**：`spring-core-modules/spring-core-beans/docs/**/*.md`
- **可验证用例（Labs/Explore）**：`spring-core-modules/spring-core-beans/src/test/**`
- **断点与观测资产**：`docs/part-00-guide/013-02-breakpoint-map.md` + `docs/appendix/98-debugger-pack.md`

### Implementation Key Points

- **逐章定制，不强制模板**：每章只补强“最缺的一环”（证据链/反例/排障/复述/观察点），避免所有章节同质化堆叠。
- **以“证据链”做粘合剂**：每章至少落到一个“入口方法/关键变量/可跑用例”，让读者能在 IDE 里证明结论。
- **反例与边界优先**：每章至少补一个可复现反例，用来分辨常见误诊（尤其是名称相近、现象相似的机制）。
- **工具页反哺正文**：Appendix（地图/排障/断点包/题库）必须能反向链接到章节正文与 Lab，形成闭环。

## Security and Performance

- 文档与示例不引入真实密钥/生产地址/个人信息；涉及外部服务时使用占位符。
- 新增测试保持可重复、无外部依赖；Explore 用例默认不影响回归。

## Testing and Deployment

- 以模块维度跑回归：优先跑 `spring-core-modules/spring-core-beans` 的测试。
- 变更批次落地后做 3 类自检：链接可达性（含锚点）、术语一致性、章节与 Lab/断点资产互链。

---

## 逐章：补充 / 完善 / 深入（具体策略）

> 说明：以下为“按章节主题定制”的加深策略清单；每章可先选 2–4 个点落地，再迭代补齐。

### 模块入口与目录页

#### spring-core-beans（模块 README）
- 文件：`spring-core-modules/spring-core-beans/README.md`
- 补充：
  - 增加“证据链最短路径”入口：遇到注入/代理/循环依赖/占位符等症状，优先跳转到哪一章、对应关键入口方法是什么。
  - 增加 3–5 个最常见误用反例（如把 `dependsOn` 当注入、把 `@Order` 当单候选选择、把 `FactoryBean` 当普通 bean）。
- 完善：
  - 增加“症状入口索引”，与 `docs/README.md` 的症状导航表互链且术语一致。
  - 增加 Debugger Pack/断点地图的使用分流：什么时候看断点地图（主线），什么时候看 Debugger Pack（专题）。
- 深入：
  - 增加“面试复述入口”：从 `appendix/93-interview-playbook.md` 反向回到章节与 Lab 的证明路径。

#### Docs TOC（导航中枢）
- 文件：`spring-core-modules/spring-core-beans/docs/README.md`
- 补充：
  - 在症状导航表中补充“证据链入口方法提示”（如依赖解析从 `doResolveDependency` 进、代理替换从 `postProcessAfterInitialization` 进）。
  - 为每个症状补 1 个“最常见误诊反例”（如把 `Circular depends-on relationship` 当成三级缓存循环依赖）。
- 完善：
  - 把异常分为“定义层失败/实例层失败/运行期行为异常”，并给出第一断点入口建议。
  - 与 Debugger Pack/断点地图互链：目录页明确如何组合两套断点资产。
- 深入：
  - 增加“面试题 → 章节 → Lab”的导航入口，形成训练闭环。

---

### Part 00｜Guide（学习路径与证据链方法论）

#### 第 09 章：00. 基础问题索引（Why Index）
- 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/009-00-why-index.md`
- 补充：
  - 为每个“为什么”问题补“最短证据链入口”（关键方法 + 观察点），避免只给结论。
  - 补“易混问题对照”：同一句问题在不同机制域的不同答案（例如“为什么没代理”可能是 BPP 缺失/短路/自调用）。
- 完善：
  - 让 Why Index 与 `92-knowledge-map.md`、`94-production-troubleshooting-checklist.md`、`98-debugger-pack.md` 形成互链：从问题到章节/断点/Lab 的跳转路径固定化。
  - 为高频问题增加“反例触发条件”（什么时候这条解释不成立、需要转向另一章）。
- 深入：
  - 增加“面试追问版本”：把 Why Index 的问题转成可复述答案结构（结论→证据链→反例→追问）。

#### 第 10 章：主线时间线（refresh → create bean）
- 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/010-03-mainline-timeline.md`
- 补充：
  - 为每个阶段补“主线关键窗口最短调用链”：入口方法 + 必看对象快照（definitions / processors / singleton caches）。
  - 补“时间线误判反例”：把“创建顺序”与“注入选择”混为一谈；把 `lazy-init` 与注入点 `@Lazy` 混为一谈。
- 完善：
  - 增加“从症状回放到时间线窗口”：注入失败/代理/循环依赖/占位符/FactoryBean 各自落在哪个窗口分叉点。
  - 给出“断点组 + watch list”，并明确每个断点停下后看什么值能判断走了哪条分支。
- 深入：
  - 补“主线复述模板”：用“主线→分支→证据链”三句复述，作为面试与排障统一表达。

#### 第 11 章：深挖指南（Bean 三层模型）
- 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/011-00-deep-dive-guide.md`
- 补充：
  - 将“三层模型”进一步下沉为“第一断点入口选择器”（定义层/实例层/最终对象），并给最短调用链。
  - 补“新手调试反例”：断点不命中/代理层过深/过早 `getBean` 导致现象偏移。
- 完善：
  - 把症状导航升级为“3 步 SOP”：症状→分层→第一断点（附 watch list）。
  - 增加“断点稳定性注记”：哪些断点跨版本稳定，哪些可能漂移以及替代入口。
- 深入：
  - 把每章“自检要点”改写为“面试追问回答框架”，并绑定可证明路径（断点/Lab）。

#### 第 11 章：关键分支矩阵（Branch Decision Matrix）
- 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/011-04-branch-decision-matrix.md`
- 补充：
  - 将每个分支明确到“真实 if/return 发生点”（入口方法 + 分支条件 + 关键变量）。
  - 为每个分支补 1 个反例：何时会被更强信号覆盖（例如 `@Primary` 覆盖 `@Priority`）。
- 完善：
  - 增加“分支误诊排障”：读者常把哪两个分支混淆？用什么断点/变量一眼区分。
  - 给出“分支→断点套件”建议：每类分支对应最小断点组（主线/专题）。
- 深入：
  - 补“追问题”：让读者能解释“为什么是这个顺序”，并给证据链。

#### 第 12 章：30 分钟快速闭环（最小实验）
- 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/012-01-quickstart-30min.md`
- 补充：
  - 为每个实验补“机制证据链入口”：跑完后下一步去哪下断点证明结论。
  - 补“常见偏差反例”：为什么你可能看不到预期现象（版本差异/代理/初始化顺序）。
- 完善：
  - 增加“从实验到排障”的映射：实验结论如何迁移到生产排障第一断点入口。
  - 每个实验给“断点闭环路径”：推荐 3–5 个断点与 watch list。
- 深入：
  - 增加“3 分钟复述训练”：每个实验给 1 个面试式回答模板（可证明）。

#### 第 13 章：`refresh()` 调用链（主线）
- 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/013-01-applicationcontext-refresh-call-chain.md`
- 补充：
  - 对关键节点补“为什么必须在这里做”（例如为何先 BFPP/再 BPP、为何 `preInstantiateSingletons` 在后半段）。
  - 补“过早 getBean 反例”：如何导致 BPP 未注册/注解不生效/占位符没解析等偏差。
- 完善：
  - 增加“按异常分型定位到 refresh 窗口”：解析失败 vs 创建失败 vs 运行期行为异常。
  - 给读者一组稳定的“主线断点组”，并标注对应 watch list 与判定标准。
- 深入：
  - 增加“refresh 主线复述题”：复述 6 个关键节点及其作用（附证据链入口）。

#### 第 13 章：断点地图（主线断点套件）
- 文件：`spring-core-modules/spring-core-beans/docs/part-00-guide/013-02-breakpoint-map.md`
- 补充：
  - 为每个断点补“它在证明什么分支”，避免只背方法名。
  - 补“断点误用反例”：哪些断点可能因版本/环境不稳定，给替代入口。
- 完善：
  - 增加“从症状选择断点组”的决策表（注入/代理/循环依赖/占位符/FactoryBean）。
  - 把 watch list 升级为“判定标准”：变量值如何判断当前分支/阶段。
- 深入：
  - 增加“面试追问的断点证明”：给 3 个高频题的断点证明路径。

---

### Part 01｜IoC Container（注册/注入/生命周期/扩展点）

#### 02. Bean 注册入口：扫描、@Bean、@Import、registrar
- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/02-bean-registration.md`
- 补充：
  - 明确“最终注册 BeanDefinition 的统一落点”与最短调用链：不同入口最终汇聚到哪里。
  - 补 Import 体系反例：ImportSelector/DeferredImportSelector/Registrar 的典型误用与排障点。
- 完善：
  - 增加“注册失败分型”：没注册/被条件排除/名字冲突/覆盖策略冲突，各自第一断点入口。
  - 增加“注册阶段断点组”：registry 写入点、配置类解析点、条件评估点。
- 深入：
  - 增加“面试追问”：为什么说“注册的第一性对象是 BeanDefinition”，如何证明。

#### 第 14 章：依赖注入解析（类型/名称/@Qualifier/@Primary）
- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/014-03-dependency-injection-resolution.md`
- 补充：
  - 补“候选收集→收敛→最终选择”的算法骨架（最短调用链 + 关键决策点）。
  - 补反例：`@Order` 不能选单候选、by-name fallback 边界、泛型信息丢失导致匹配失败。
- 完善：
  - 增加“注入失败分型”与 SOP：NoSuch/NoUnique/UnsatisfiedDependency/类型不匹配，各自第一断点入口与观察点。
  - 增加“依赖解析断点组”：`doResolveDependency`、候选收集、candidate 决策点、value 注入分支。
- 深入：
  - 补“面试追问”：`@Primary/@Qualifier/@Priority` 谁更强？为什么？如何证明。

#### 第 15 章：Scope 与 prototype 注入陷阱
- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/015-04-scope-and-prototype.md`
- 补充：
  - 补“prototype 注入 singleton 为什么像单例”的证据链（注入时机 vs 创建时机）。
  - 补反例：prototype 循环依赖、prototype 销毁不自动、scoped proxy 的 equals/hashCode/序列化坑。
- 完善：
  - 增加排障：资源泄漏/生命周期错觉/线程隔离不生效时如何定位。
  - 增加断点：scope get/remove、scoped proxy 触发目标创建入口。
- 深入：
  - 增加选择策略：`@Lookup`/ObjectProvider/scoped proxy 何时用谁（给可证明理由）。

#### 第 16 章：生命周期（初始化/销毁/回调）
- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/016-05-lifecycle-and-callbacks.md`
- 补充：
  - 补“完整顺序 + 关键窗口”的证据链（Aware/BPP/init/destroy），并明确发生在 raw 还是 exposed。
  - 补反例：init 抛异常导致 destroy 不执行、prototype 不自动销毁、代理与回调顺序误判。
- 完善：
  - 增加排障：初始化卡死/销毁不执行/回调顺序不符的第一断点入口。
  - 增加 watch list：回调触发前后关键变量（如 wrappedBean、exposed object 变化）。
- 深入：
  - 增加面试追问：哪些回调能替代哪些？为什么推荐构造器注入 + PostConstruct。

#### 第 17 章：容器扩展点（BFPP vs BPP）
- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/017-06-post-processors.md`
- 补充：
  - 用方法级证明“能/不能做什么”：为什么 BPP 不可靠改定义，为什么 BFPP 拿不到实例态。
  - 补反例：错误时机 `getBean` 导致 BPP 链不完整、手工注册破坏顺序的坑。
- 完善：
  - 增加排障：某注解不生效/某增强不生效/代理不出现时，先定位缺哪个处理器与顺序问题。
  - 增加断点：processor 收集/排序/注册/执行关键入口方法。
- 深入：
  - 增加面试追问：BDRPP 为什么更强？与 ImportBeanDefinitionRegistrar 的边界如何说明。

#### 第 18 章：`@Configuration` 增强与 `@Bean` 语义
- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/018-07-configuration-enhancement.md`
- 补充：
  - 补 full vs lite 判定链路与证据点（为什么会/不会增强）。
  - 补反例：`proxyBeanMethods=false` 导致“多次调用多次 new”，以及与 AOP 代理混淆点。
- 完善：
  - 增加排障：写了 `@Bean` 却拿到多个实例/依赖不一致时如何定位。
  - 增加断点：配置类解析、增强生成、`@Bean` 方法拦截关键入口。
- 深入：
  - 增加面试追问：配置类增强与 AOP 代理有何不同？如何证明。

#### 第 20 章：Bean 心智模型（三层：Definition/Instance/Exposed）
- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/020-01-bean-mental-model.md`
- 补充：
  - 将 pre/early/after-init 三个替换窗口做成“证据链对照表”，并给每类窗口的关键入口方法。
  - 补反例：early reference 与最终代理不一致导致行为差异；FactoryBean 造成的“类型错觉”。
- 完善：
  - 增加排障：异常先分层到定义/实例/最终对象，并给第一断点入口。
  - 增加“如何快速识别 proxy/wrapper”：调试器判别方法与代理链定位。
- 深入：
  - 增加面试追问：BeanFactory vs ApplicationContext 差异如何落到 refresh 证据链。

#### 08. `FactoryBean`：产品 vs 工厂（以及 `&` 前缀）
- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/08-factorybean.md`
- 补充：
  - 补 `getBean` vs `&getBean` 的证据链与缓存语义（FactoryBean 自身 vs product）。
  - 补反例：`getObjectType` 返回 null 导致 type matching 失效、`isSingleton` 声明不一致导致缓存错觉。
- 完善：
  - 增加排障：按类型发现/条件装配/注入失败时如何判断是否 FactoryBean 语义导致。
  - 增加断点：product 获取与缓存命中关键入口。
- 深入：
  - 增加面试追问：FactoryBean 在 Boot auto-config 中为何高频？价值与风险如何解释。

#### 09. 循环依赖：现象、原因与规避（constructor vs setter）
- 文件：`spring-core-modules/spring-core-beans/docs/part-01-ioc-container/09-circular-dependencies.md`
- 补充：
  - 补“构造器失败 vs setter 可能成功”的证据链（三级缓存写入/读出窗口）。
  - 补反例：prototype 循环依赖、AOP 介入导致 early/final 不一致、`allowCircularReferences=false` 的行为差异。
- 完善：
  - 增加排障分型：从异常分到“构造器环/属性环/depends-on 环/代理导致环”。
  - 增加断点：三级缓存、early reference 回调、包装/代理替换入口。
- 深入：
  - 增加面试追问：为什么说“能解不等于安全”？如何用证据链回答。

---

### Part 02｜Boot Auto-Config（条件/导入/顺序/可观测）

#### 第 19 章：调试与自检（如何“看见”容器在做什么）
- 文件：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/019-11-debugging-and-observability.md`
- 补充：
  - 补“定义层可观测证据链”：如何证明一个 BeanDefinition 是谁注册的/何时注册的/是否被改写。
  - 补反例：debug 日志误读、条件评估报告与真实注册行为不一致时的定位方法。
- 完善：
  - 增加排障 SOP：容器里没有/有但不是我想要的/被 proxy 了/值不对四类症状如何收敛。
  - 增加观察点：ConditionEvaluationReport、BeanDefinition 来源、auto-config import 列表（查看方式）。
- 深入：
  - 增加面试追问：Boot 为什么会影响 Bean 图？如何用证据链解释 back-off。

#### 09. Auto-Configuration 顺序（偶发失效的根因）
- 文件：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/020-09-auto-config-ordering.md`
- 补充：
  - 补“顺序影响条件命中”的证据链：导入顺序/条件评估时机/定义是否已存在的交互。
  - 补反例：`@AutoConfigureBefore/After/Order` 的边界；同一条件在不同阶段评估导致“看似偶发”。
- 完善：
  - 增加排障：把“偶发失效”归因到顺序、条件、还是定义覆盖/替换。
  - 增加断点：auto-config 导入、条件评估、BeanDefinition 注册关键入口。
- 深入：
  - 增加面试追问：为什么建议把条件写成“可确定性强”的形式？如何解释 matchIfMissing 三态。

#### 第 21 章：Boot 自动装配如何影响 Bean（导入与 back-off）
- 文件：`spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md`
- 补充：
  - 补“导入链路证据链”：候选收集→导入→注册 BeanDefinition 的最短调用链。
  - 补反例：用户 bean 顶掉 auto-config / `@ConditionalOnMissingBean` 误判 / FactoryBean+type matching 导致条件误命中。
- 完善：
  - 增加排障：从“没注册/注册了但不对”到“第一断点入口”的 SOP。
  - 增加 watch list：导入列表、条件上下文、BeanDefinition 注册表关键对象快照。
- 深入：
  - 增加面试追问：auto-config 的 back-off 与覆盖策略如何解释且可证明。

---

### Part 03｜Container Internals（处理器/排序/短路/early/final）

#### 第 22 章：容器启动与基础设施处理器（为什么注解能工作）
- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md`
- 补充：
  - 补“注解能力→处理器→生效窗口”的证据链对照表，并落到入口方法。
  - 补反例：缺失基础设施时哪些注解不生效；过早 `getBean` 导致处理器未注册。
- 完善：
  - 增加排障：`@Autowired/@Value/@PostConstruct` 不生效时的第一断点入口。
  - 增加断点：处理器注册与执行顺序关键锚点 + watch list。
- 深入：
  - 增加面试追问：注解不是魔法，是处理器装进 refresh 主线（证据链）。

#### 13. BDRPP：注册阶段动态加定义
- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/13-bdrpp-definition-registration.md`
- 补充：
  - 补“动态加定义”进入主线的证据链：何时被调用、对 registry 造成何种影响。
  - 补反例：滥用导致 bean graph 难推断；与 ImportBeanDefinitionRegistrar 混用导致时机误判。
- 完善：
  - 增加排障：为什么某个 bean “凭空出现/出现顺序异常/条件判断异常”。
  - 增加断点：`invokeBeanFactoryPostProcessors` 内部对 BDRPP 的分组与执行顺序。
- 深入：
  - 增加面试追问：BDRPP 与 BFPP 边界与适用场景（可证明）。

#### 14. 顺序（Ordering）：PriorityOrdered / Ordered / 无序
- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/14-post-processor-ordering.md`
- 补充：
  - 补“排序算法骨架”与关键列表快照（收集→排序→执行）。
  - 补反例：programmatic 注册绕过默认排序；`@Order` 与 `@Priority` 的边界误判。
- 完善：
  - 增加排障：增强偶发不生效/顺序错乱时的第一入口与观察点。
  - 增加断点：排序发生点、processor 列表构建点、注册点。
- 深入：
  - 增加面试追问：为什么 PriorityOrdered 必须先于 Ordered？给可证明后果。

#### 15. 实例化前短路：postProcessBeforeInstantiation
- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/15-pre-instantiation-short-circuit.md`
- 补充：
  - 补“短路跳过了什么/仍会做什么”的证据链（构造器/populate/initialize/销毁）。
  - 补反例：短路导致字段注入没发生、生命周期回调不符合预期、代理链难追踪。
- 完善：
  - 增加排障：构造器没执行但 bean 存在？第一断点如何定位到短路发生点。
  - 增加断点：`resolveBeforeInstantiation`/`postProcessBeforeInstantiation` 入口与 watch list。
- 深入：
  - 增加面试追问：短路与 after-init proxy 有何区别？如何证明。

#### 16. early reference 与循环依赖：getEarlyBeanReference
- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/16-early-reference-and-circular.md`
- 补充：
  - 补三级缓存“写入/读出时间点”与对象类型（raw/early/final）证据链对照。
  - 补反例：early 与 final 不一致的后果（事务/AOP/懒代理叠加），以及 `allowRawInjectionDespiteWrapping` 边界。
- 完善：
  - 增加排障：如何从异常/行为差异定位到“early window 参与者是谁”。
  - 增加断点：`addSingletonFactory`/`getEarlyBeanReference`/after-init proxy 对照断点组。
- 深入：
  - 增加面试追问：为什么“能解不等于安全”？给证据链。

#### 17. 生命周期回调顺序：Aware / BPP / init / destroy
- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/17-lifecycle-callback-order.md`
- 补充：
  - 补“顺序与触发点”证据链：入口方法串起每类回调，并明确 raw vs exposed。
  - 补反例：prototype 不销毁误判、destroy 顺序与 dependent 图的交互。
- 完善：
  - 增加排障：初始化/销毁顺序异常、回调没触发的第一断点入口。
  - 增加 watch list：disposableBeans、dependentBeanMap、回调注册点。
- 深入：
  - 增加面试追问：SmartInitializingSingleton/SmartLifecycle 与普通回调的边界。

#### 18. refresh → doCreateBean 主线（源码级）
- 文件：`spring-core-modules/spring-core-beans/docs/part-03-container-internals/18-refresh-to-bean-creation-mainline.md`
- 补充：
  - 增加“主线分层索引 + 最短调用链”：快速定位 instantiate/populate/initialize/代理替换/缓存窗口。
  - 为关键分支补 1 个失败/偏差案例（FactoryBean/@Lazy/dependsOn/parent/prototype guard）。
- 完善：
  - 增加“现象→阶段→关键方法→必看变量→Lab”映射覆盖率，确保症状可定位。
  - 增加“断点组 + 判定标准”：不只给断点，还给“变量意味着哪条分支”。
- 深入：
  - 增加“主线复述题”：用证据链回答“Bean 如何变成对象”。

---

### Part 04｜Wiring & Boundaries（工程边界与真实坑位）

#### 第 23 章：18. Lazy（lazy-init vs `@Lazy` 注入点）
- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/023-18-lazy-semantics.md`
- 补充：
  - 补“两类 Lazy 的证据链对照”：lazy-init 创建时机 vs 注入点 `@Lazy` 的代理时机。
  - 补反例：懒代理叠加 AOP/循环依赖时的偏差；final 类/方法限制。
- 完善：
  - 增加排障：lazy bean 被提前创建？判断是 dependsOn 拉起还是 proxy 触发。
  - 增加断点：代理创建点、首次触发目标创建点、注入解析分支。
- 深入：
  - 增加选择策略：`@Lazy` vs ObjectProvider 的边界与可证明理由。

#### 19. dependsOn：强制初始化顺序
- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/19-depends-on.md`
- 补充：
  - 补 `doGetBean` 内 dependsOn 分支证据链（含依赖图写入点）。
  - 补反例：Circular depends-on 与三级缓存循环依赖的误判对照。
- 完善：
  - 增加排障 SOP：lazy-init 被拉起、关闭顺序反直觉、写错 beanName 三类场景。
  - 增加 watch list：dependentBeanMap/dependenciesForBeanMap 判定标准。
- 深入：
  - 增加面试追问：dependsOn 为什么不等于注入依赖？如何证明。

#### 20. registerResolvableDependency：能注入但不是 Bean
- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/20-resolvable-dependency.md`
- 补充：
  - 补“命中在 doResolveDependency 之前”的证据链，并对比 bean candidates 分支。
  - 补反例：滥用导致候选收敛被绕过、与 `@Qualifier` 语义冲突。
- 完善：
  - 增加排障：能注入但 `getBeansOfType` 查不到？如何证明不是 BeanDefinition。
  - 增加断点：resolvableDependencies 命中、AutowireUtils 解包、Aware 回调对照。
- 深入：
  - 增加面试追问：它与 *Aware 的边界与适用场景。

#### 21. 父子 ApplicationContext：可见性与覆盖边界
- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/21-context-hierarchy.md`
- 补充：
  - 补“可见性规则与搜索顺序”的方法级证据链（child→parent）。
  - 补反例：同名 bean 覆盖、同 type 不可见、event/环境继承误判。
- 完善：
  - 增加排障 SOP：多 context 场景 bean 不可见/注入到错误上下文。
  - 增加断点：父子容器查找、注册覆盖点。
- 深入：
  - 增加面试追问：ApplicationContext 的“加法能力”与 hierarchy 如何关联。

#### 22. Bean 名称与 alias
- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/22-bean-names-and-aliases.md`
- 补充：
  - 补 alias 注册结构与 lookup 路径证据链，并对比按类型发现。
  - 补反例：`@Resource` 注入错对象、FactoryBean `&` 前缀误判、覆盖策略冲突。
- 完善：
  - 增加排障：按名注入/按名获取异常时如何定位 alias 与 canonicalName。
  - 增加 watch list：aliasMap、canonicalName、beanName 解析入口。
- 深入：
  - 增加面试追问：为什么说 `@Resource` 更像按名称找？alias 如何影响它。

#### 23. FactoryBean 深潜：类型匹配与缓存语义
- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/23-factorybean-deep-dive.md`
- 补充：
  - 补 type matching 算法与 `getObjectType/isSingleton` 证据链，并给关键分支快照。
  - 补反例：`getObjectType=null` 导致条件误判/按类型发现失败；SmartFactoryBean 与 eager init 边界。
- 完善：
  - 增加排障：按类型注入/条件装配“偶发失效”时先判断 FactoryBean 语义。
  - 增加断点：FactoryBeanRegistrySupport 缓存、`getObjectFromFactoryBean` 调用链。
- 深入：
  - 增加面试追问：FactoryBean 的价值与高频坑点（两个缓存）如何证明。

#### 24. BeanDefinition 覆盖（overriding）
- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/24-bean-definition-overriding.md`
- 补充：
  - 补覆盖发生在注册阶段的证据链与配置入口（Framework/Boot 差异明确）。
  - 补反例：覆盖导致注入命中改变但不易察觉；与 auto-config back-off 的交互误判。
- 完善：
  - 增加排障 SOP：同名冲突/覆盖导致行为偏差（先看谁注册、再看覆盖策略）。
  - 增加观察点：注册冲突位置、BeanDefinition 源信息（resourceDescription/source）。
- 深入：
  - 增加面试追问：为什么团队通常不建议默认允许覆盖？给工程化理由与证据。

#### 25. 手工添加 BeanPostProcessor：顺序与陷阱
- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md`
- 补充：
  - 补“绕过默认注册流程导致顺序变化”的证据链与关键列表快照。
  - 补反例：增强偶发不生效、代理链丢失、`@Autowired/@Value` 行为偏移。
- 完善：
  - 增加排障：怀疑 BPP 顺序问题时的第一断点入口与关键观察变量。
  - 增加断点：`addBeanPostProcessor`、`registerBeanPostProcessors`、排序位置对照。
- 深入：
  - 增加面试追问：为什么不建议业务侧手工注册 BPP？可证明副作用有哪些。

#### 26. SmartInitializingSingleton：所有单例创建完再做事
- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/26-smart-initializing-singleton.md`
- 补充：
  - 补它在主线中的触发窗口证据链（与 `preInstantiateSingletons`/refresh 事件对照）。
  - 补反例：lazy 单例不在其中；过早初始化导致副作用。
- 完善：
  - 增加排障：为什么 hook 没触发/触发顺序不符期望？
  - 增加断点：`afterSingletonsInstantiated` 触发点与执行顺序观察。
- 深入：
  - 增加面试追问：它与 ContextRefreshedEvent 的差异与选择策略。

#### 27. SmartLifecycle：start/stop 时机与 phase 顺序
- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/27-smart-lifecycle-phase.md`
- 补充：
  - 补 phase 排序算法证据链与关键列表快照。
  - 补反例：autoStartup 与 isRunning 误判、stop 未执行导致资源泄漏。
- 完善：
  - 增加排障：组件没启动/没停止？如何从 phase/依赖/状态收敛。
  - 增加断点：LifecycleProcessor、start/stop 调度点与观察变量。
- 深入：
  - 增加面试追问：SmartLifecycle 与普通 init/destroy 的边界与适用场景。

#### 28. 自定义 Scope + scoped proxy
- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/28-custom-scope-and-scoped-proxy.md`
- 补充：
  - 补 scope 契约（get/remove/registerDestructionCallback）的证据链与典型实现骨架。
  - 补反例：thread-local 泄漏、销毁回调不执行、代理导致类型信息丢失。
- 完善：
  - 增加排障：scope 失效/对象串线程/销毁不执行如何定位。
  - 增加断点：Scope#get、scoped proxy 创建与目标解析入口。
- 深入：
  - 增加面试追问：何时不该自定义 scope？替代方案怎么选。

#### 29. FactoryBean 边界：getObjectType 返回 null
- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/29-factorybean-edge-cases.md`
- 补充：
  - 补“type matching 失效”的算法证据链（条件判断/候选收集如何受影响）。
  - 补反例：按类型注入失效但 `getBean(name)` 仍可用、条件装配误判。
- 完善：
  - 增加排障 SOP：先确认是否 FactoryBean，再看 `getObjectType/isSingleton` 与缓存路径。
  - 增加断点：type match 分支、FactoryBean objectType 读取点。
- 深入：
  - 增加面试追问：为什么 getObjectType 这么关键？如何用证据链解释。

#### 30. 注入阶段：field vs constructor（`postProcessProperties`）
- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/30-injection-phase-field-vs-constructor.md`
- 补充：
  - 补“注入发生在哪一步”的证据链：构造器注入 vs 属性填充 vs `@PostConstruct`。
  - 补反例：field injection 构造器不可用导致 NPE；循环依赖更难排；测试隔离更差。
- 完善：
  - 增加排障：注入时机误判导致 bug 如何定位（第一断点与变量）。
  - 增加断点：ConstructorResolver、populateBean、`postProcessProperties`。
- 深入：
  - 增加面试追问：为什么更推荐构造器注入？给证据链与工程理由。

#### 31. 代理产生阶段：BPP 如何换成 Proxy（self-invocation）
- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md`
- 补充：
  - 补“proxy 替换发生点”的证据链，并对比 pre/early/after-init 三类替换。
  - 补反例：self-invocation 绕过代理、多个代理叠加导致行为偏移。
- 完善：
  - 增加排障：代理没生效？如何用调用栈证明绕过代理。
  - 增加断点：`postProcessAfterInitialization`、`getEarlyBeanReference`、AOP auto-proxy 入口。
- 深入：
  - 增加面试追问：self-invocation 根因与修复策略如何解释并可证明。

#### 32. `@Resource` 注入：name-first
- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/32-resource-injection-name-first.md`
- 补充：
  - 补 name-first 完整决策链（name 指定/默认字段名/fallback type）证据链。
  - 补反例：alias/同名覆盖导致注入错对象；与 `@Primary/@Qualifier` 的误对比。
- 完善：
  - 增加排障 SOP：先查 beanName/alias，再查 type。
  - 增加断点：CommonAnnotationBeanPostProcessor 与依赖解析入口对照。
- 深入：
  - 增加面试追问：`@Resource` vs `@Autowired` 的选择策略（证据链）。

#### 33. 候选选择 vs 顺序：`@Primary/@Priority/@Order/@Qualifier`
- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md`
- 补充：
  - 补“选择 vs 排序”证据链：单注入 vs 集合注入两条路径的决策点。
  - 补反例：`@Order` 不能解决单注入歧义；by-name fallback 边界。
- 完善：
  - 增加排障：NoUnique 发生时的收敛路径（先 `@Primary/@Qualifier`，再 `@Priority`，再 by-name）。
  - 增加断点：candidate 决策点、collection injection 排序点。
- 深入：
  - 增加面试追问：`@Primary` 与 `@Priority` 谁更强？给可证明解释。

#### 34. `@Value(\"${...}\")`：strict vs non-strict（占位符）
- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md`
- 补充：
  - 补“resolveEmbeddedValue → placeholder resolver”的最短证据链，并区分 `${}` 与 `#{}`。
  - 补反例：把占位符解析/SpEL 求值/类型转换混为一谈导致误诊。
- 完善：
  - 增加排障 SOP：三连分层（占位符→SpEL→转换），每层第一断点入口与判断标准。
  - 增加 watch list：embeddedValueResolver、propertySources、missing key 处理策略。
- 深入：
  - 增加面试追问：strict 策略是谁决定的？为什么不建议默认 non-strict？

#### 35. MergedBeanDefinition：RootBeanDefinition 从哪里来
- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/35-merged-bean-definition.md`
- 补充：
  - 补“合并触发点与缓存语义”证据链（merged 何时生成/复用）。
  - 补反例：看到的 BeanDefinition 与最终行为不一致（根因在 merged）。
- 完善：
  - 增加排障：注解元信息处理异常/属性不生效时如何先确认 merged BD。
  - 增加断点：`getMergedLocalBeanDefinition`、`applyMergedBeanDefinitionPostProcessors`。
- 深入：
  - 增加面试追问：为什么 MBPP（MergedBeanDefinitionPostProcessor）重要？窗口期如何证明。

#### 36. 类型转换：BeanWrapper / ConversionService / PropertyEditor
- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md`
- 补充：
  - 补“属性访问 vs 类型转换”证据链：populateBean → BeanWrapper → TypeConverterDelegate。
  - 补反例：占位符没解析导致转换失败、集合/枚举/日期转换链路误判。
- 完善：
  - 增加排障 SOP：TypeMismatch/ConversionFailed（先定位 propertyPath，再定位 requiredType 与分支）。
  - 增加断点：`setPropertyValues`、`convertIfNecessary`、converter/editor 命中路径。
- 深入：
  - 增加面试追问：PropertyEditor 为什么还存在？与 ConversionService 的边界与迁移建议。

#### 37. 泛型匹配坑：ResolvableType 与代理导致类型信息丢失
- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/37-generic-type-matching-pitfalls.md`
- 补充：
  - 补 `checkGenericTypeMatch` 决策链与关键变量（ResolvableType 推断结果）。
  - 补反例：代理/桥接方法/父类擦除导致泛型信息丢失与匹配失败。
- 完善：
  - 增加排障：List<Foo> 注入失败？如何在依赖解析入口证明是泛型不匹配。
  - 增加断点：generic match、candidate resolver、type descriptor 对照。
- 深入：
  - 增加面试追问：Spring 的泛型匹配如何实现？为什么代理会影响它？

#### 38. Environment/PropertySource：优先级与排障主线
- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/38-environment-and-propertysource.md`
- 补充：
  - 补“最终取值→来源”的证据链：PropertySources 顺序如何影响 getProperty。
  - 补反例：值被覆盖但不自知、profile/条件导致 property source 不同。
- 完善：
  - 增加排障 SOP：值不对/找不到/被覆盖三类症状回推 propertySources 与 resolver。
  - 增加 watch list：MutablePropertySources 顺序、property resolver 命中路径。
- 深入：
  - 增加面试追问：Environment abstraction 与 Boot config data 的关系如何解释。

#### 39. BeanFactory API 深挖：接口族谱与手动 bootstrap 边界
- 文件：`spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/39-beanfactory-api-deep-dive.md`
- 补充：
  - 补“接口能力→可观察行为”证据链：Listable/Configurable 等接口意味着哪些行为窗口。
  - 补反例：手工 new BeanFactory 时注解不工作/占位符不解析/代理不出现的误判。
- 完善：
  - 增加排障：怀疑容器能力缺失时如何快速确认（processor 是否安装、主线缺环）。
  - 增加断点：手工 bootstrap 装配点、processor 注册点、注入解析入口。
- 深入：
  - 增加面试追问：BeanFactory vs ApplicationContext 的差异如何落到“能力清单 + 证据链”。

---

### Part 05｜AOT & Real World（输入层解析 + 构建期契约）

#### 40. AOT / Native 总览：JVM 能跑 ≠ Native 能跑
- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/024-40-aot-and-native-overview.md`
- 补充：
  - 补“失败分型→缺口类型”的证据链：反射/代理/资源/序列化分别对应什么提示。
  - 补反例：盲目全量放开反射的风险（安全/体积/可维护性）。
- 完善：
  - 增加排障 SOP：从 native 报错快速归类并定位到要补的 hints。
  - 增加观察点：RuntimeHints 类别与注册入口如何看见。
- 深入：
  - 增加面试追问：为什么 RuntimeHints 是“可测试契约”？如何证明。

#### 41. RuntimeHints 入门：把构建期契约跑通
- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/41-runtimehints-basics.md`
- 补充：
  - 补“Registrar 注册→测试断言”的证据链范式（把契约钉死）。
  - 补反例：把 hints 当 JSON 到处贴导致漂移；过度开放反射扩大安全面。
- 完善：
  - 增加排障：反射/代理/资源缺失报错如何映射到 hints 类型。
  - 增加断点：registerHints 与 hints 写入点（reflection/resources/proxies）。
- 深入：
  - 增加面试追问：为什么推荐 registrar + 单测，而不是靠 native 失败再补？

#### 42. XML → BeanDefinitionReader：定义层解析与错误分型
- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/42-xml-bean-definition-reader.md`
- 补充：
  - 补“Resource→Reader→registerBeanDefinition”的最短证据链与入口。
  - 补反例：schema 不匹配/namespace 扩展缺失/属性转换失败的误判对照。
- 完善：
  - 增加排障 SOP：把 BeanDefinitionStoreException 分型到解析/注册/转换阶段。
  - 增加断点：`loadBeanDefinitions`、`doRegisterBeanDefinitions`、注册入口。
- 深入：
  - 增加面试追问：XML 与注解解析最终为何都落到 BeanDefinition？如何证明。

#### 43. 容器外对象注入：AutowireCapableBeanFactory
- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/43-autowirecapablebeanfactory-external-objects.md`
- 补充：
  - 补“外部对象注入能力边界”证据链：能做什么/不能做什么（生命周期/代理/销毁）。
  - 补反例：误以为外部对象等同托管导致资源泄漏与代理不生效。
- 完善：
  - 增加排障：外部对象注入后行为不符预期时如何定位到“没走哪条主线”。
  - 增加断点：autowireBean/initializeBean/applyBPP 的路径对照。
- 深入：
  - 增加面试追问：什么时候用它，什么时候应重构为容器托管？

#### 44. SpEL 与 `@Value(\"#{...}\")`：表达式解析链路
- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/44-spel-and-value-expression.md`
- 补充：
  - 补“解析→求值→注入”证据链与入口方法，并与 `${}` 占位符对照。
  - 补反例：表达式注入风险、SpEL 与占位符混用导致误诊。
- 完善：
  - 增加排障 SOP：表达式失败定位 parser/上下文/变量/类型转换哪一环。
  - 增加断点：SpEL parser、evaluation context、value injection 分支。
- 深入：
  - 增加面试追问：为什么 SpEL 某些场景危险？如何给出安全建议。

#### 45. 自定义 Qualifier：meta-annotation 与候选收敛
- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/45-custom-qualifier-meta-annotation.md`
- 补充：
  - 补“Qualifier 决策发生点”证据链：最终由哪个 resolver 判定命中。
  - 补反例：多个 Qualifier 叠加、meta 嵌套过深导致可读性差与误命中。
- 完善：
  - 增加排障：Qualifier 不生效/命中错对象如何定位到 resolver 判定过程。
  - 增加断点：candidate resolver、qualifier match 入口与关键变量。
- 深入：
  - 增加面试追问：为什么推荐 meta-annotation 而不是字符串 qualifier？优势与风险。

#### 46. XML namespace 扩展：NamespaceHandler / Parser / spring.handlers
- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/46-xml-namespace-extension.md`
- 补充：
  - 补“namespace resolution→handler→parser→BeanDefinition”证据链。
  - 补反例：spring.handlers 缺失、schemaLocation 错误、parser 抛错分型。
- 完善：
  - 增加排障 SOP：namespace 解析失败如何定位到 handler 加载/资源缺失/解析异常。
  - 增加断点：NamespaceHandlerResolver、handler mapping 加载点、parse 入口。
- 深入：
  - 增加面试追问：XML 扩展机制与注解扩展机制（processor）异同。

#### 47. BeanDefinitionReader：Properties / Groovy 等其他输入
- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/47-beandefinitionreader-other-inputs-properties-groovy.md`
- 补充：
  - 补“输入层对比”并强调共同落点：最终都落到 BeanDefinition 与 registry。
  - 补反例：格式错误/类型转换失败/引用不存在的分型。
- 完善：
  - 增加排障：输入层失败如何快速定位到 reader 与 value resolver。
  - 增加断点：reader 入口、registerBeanDefinition、值解析入口。
- 深入：
  - 增加面试追问：为什么 Spring 能支持多输入？核心抽象是什么？

#### 48. 方法注入：replaced-method / MethodReplacer
- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/48-method-injection-replaced-method.md`
- 补充：
  - 补“如何实现”证据链：CGLIB 子类与方法拦截发生点。
  - 补反例：final 限制、代理叠加、AOT 下限制与 hints 需求。
- 完善：
  - 增加排障：方法注入不生效/行为偏差如何定位到代理生成与拦截器。
  - 增加断点：子类生成、方法拦截、目标解析入口。
- 深入：
  - 增加面试追问：`@Lookup` 与 replaced-method 差异与选择策略。

#### 49. 内置 FactoryBean 图鉴
- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/49-built-in-factorybeans-gallery.md`
- 补充：
  - 按“行为模型”补证据链：反射调用型/服务定位型/代理生成型在哪个窗口替换最终对象。
  - 补反例：把它们当普通 bean 导致的类型误判与调试困难。
- 完善：
  - 增加排障：看到内置 FactoryBean 时如何判断最终暴露对象、以及按类型发现边界。
  - 增加断点：FactoryBean product 获取与缓存命中点。
- 深入：
  - 增加面试追问：为什么内置 FactoryBean 很常见？它们解决了什么抽象问题？

#### 50. PropertyEditor 与值解析：值从定义层落到对象
- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/50-property-editor-and-value-resolution.md`
- 补充：
  - 补“BeanDefinitionValueResolver→convertIfNecessary”完整证据链，并与占位符/SpEL/转换三连对齐。
  - 补反例：值看似解析但其实占位符没解析；editor 与 converter 混用导致行为不一致。
- 完善：
  - 增加排障 SOP：TypeMismatch/BeanCreationException 分型定位（解析/求值/转换）。
  - 增加断点：value resolver、property value 应用、TypeConverterDelegate 转换路径。
- 深入：
  - 增加面试追问：PropertyEditor 的历史定位与为何仍在某些路径出现。

---

### Appendix｜工具型章节（地图/排障/断点/题库/训练）

#### 90. 常见误区清单
- 文件：`spring-core-modules/spring-core-beans/docs/appendix/025-90-common-pitfalls.md`
- 补充：
  - 为每类误区补“最短证据链入口方法”链接。
  - 为高频误区补“反例对照”（如 depends-on 环 vs 循环依赖）。
- 完善：
  - 把误区映射到排障 SOP：典型症状与第一断点入口。
  - 每类误区给最小断点组与 watch list。
- 深入：
  - 把误区转成面试追问（为什么/如何证明/反例是什么）。

#### 99. 自测题
- 文件：`spring-core-modules/spring-core-beans/docs/appendix/026-99-self-check.md`
- 补充：
  - 每题绑定“证据链入口方法 + 推荐 Lab”，让自测可证明。
  - 加入“反例题/边界题”，避免只背概念。
- 完善：
  - 按“定义层/实例层/代理/值解析/Boot/AOT”分型题库，形成训练路径。
  - 为高频题给断点闭环建议（断点+watch list+判定标准）。
- 深入：
  - 与 interview playbook 互链：自测题可直接转面试复述练习。

#### 91. 术语表（Glossary）
- 文件：`spring-core-modules/spring-core-beans/docs/appendix/91-glossary.md`
- 补充：
  - 为关键术语补“对应证据链入口方法”。
  - 补易混词反例：BeanDefinition vs instance vs exposed；BFPP vs BPP vs BDRPP。
- 完善：
  - 增加“术语误诊”提示：遇到某词如何避免错误联想。
  - 为核心术语补“看见它”的断点/观察点。
- 深入：
  - 将术语映射到面试题：术语解释必须给证据链与反例。

#### 92. 知识地图（症状→章节→断点→Lab）
- 文件：`spring-core-modules/spring-core-beans/docs/appendix/92-knowledge-map.md`
- 补充：
  - 把每条主线补“证据链入口方法”，与章节内部一致。
  - 为每条症状补“最常见误诊点”，提高分流精度。
- 完善：
  - 强化“症状→章节→Lab→断点”闭环，作为排障导航主入口之一。
  - 与 Debugger Pack/断点地图互链，形成可复用断点套件。
- 深入：
  - 将地图与面试题库映射：某题对应哪条地图路径与证明方式。

#### 93. 面试复述模板（Interview Playbook）
- 文件：`spring-core-modules/spring-core-beans/docs/appendix/93-interview-playbook.md`
- 补充：
  - 为每道题补“方法级证据链”（最短调用链 + 决策点）。
  - 为高频题补“反例/边界追问”，避免背诵式答案。
- 完善：
  - 增加“真实排障对应场景”，让面试题反哺工程能力。
  - 为题目给“断点证明路径”，帮助 IDE 实证。
- 深入：
  - 统一答案结构：结论→证据链→反例→追问（保持一致可训练）。

#### 94. 生产排障清单（Troubleshooting Checklist）
- 文件：`spring-core-modules/spring-core-beans/docs/appendix/94-production-troubleshooting-checklist.md`
- 补充：
  - 为每类症状补“第一断点入口 + 关键变量”，把清单变成可执行 SOP。
  - 补“误判对照”：相似症状可能属于不同机制域，如何分流。
- 完善：
  - 与 docs/README 症状导航打通（双向链接）。
  - 与 Debugger Pack 互链：每类症状给推荐断点组与判定标准。
- 深入：
  - 把排障题转成面试追问（如何定位/如何证明/如何修复）。

#### 95. spring-beans Public API 索引
- 文件：`spring-core-modules/spring-core-beans/docs/appendix/95-spring-beans-public-api-index.md`
- 补充：
  - 为每个 API 域补“对应章节与证据链入口”，让索引能反向定位机制。
  - 增加“常见排障场景入口”，让索引服务于排障而不是目录堆叠。
- 完善：
  - 为 API 域补建议断点：哪个 API 域对应哪个关键断点入口。
  - 增加面试题映射：某 API 域典型面试题与证明路径。
- 深入：
  - 与 Gap 清单联动：以 Gap 驱动后续章节/实验深化。

#### 96. spring-beans Public API Gap 清单
- 文件：`spring-core-modules/spring-core-beans/docs/appendix/96-spring-beans-public-api-gap.md`
- 补充：
  - 增加“可执行 Gap 项”实例：按包/机制域列出真实 gap（而不仅是方法论说明）。
  - 每个 gap 项补“反例/边界触发条件”，说明为何它是 gap。
- 完善：
  - 为 gap 项绑定“章节 + 证据链入口方法 + 推荐 Lab”，让 gap 可被验证与关闭。
  - 增加“关闭方式”：补文档/补 Lab/补排障 SOP 三类路径任选其一。
- 深入：
  - 把 gap 变成可追踪的 backlog（与 solution package/task list 对齐）。

#### 97. Explore/Debug 用例（可选启用）
- 文件：`spring-core-modules/spring-core-beans/docs/appendix/97-explore-debug-tests.md`
- 补充：
  - 为每个用例补“它在证明什么机制分支”，并指向正文对应章节。
  - 补“反例与版本差异注记”，避免用例被误读。
- 完善：
  - 与排障清单/知识地图/目录页打通（统一导航）。
  - 补 watch list 与判定标准：断点停下后看什么值才算证据成立。
- 深入：
  - 把用例组织成训练脚本：面试复述/团队内训可直接引用其证据链与复现入口。

#### 98. Debugger Pack（断点包总入口）
- 文件：`spring-core-modules/spring-core-beans/docs/appendix/98-debugger-pack.md`
- 补充：
  - 为每个断点包补“它在证明什么机制分支”，避免成为纯目录。
  - 补“断点包选择建议”：不同症状优先用哪套断点包。
- 完善：
  - 与断点地图/知识地图/排障清单互链，形成统一入口。
  - 补 watch list 与判定标准（停下后看什么值得出结论）。
- 深入：
  - 把断点包升级为“可复述证明路径”：给出示例题与断点证明链。

#### 99. 团队内训讲义（Training Kit）
- 文件：`spring-core-modules/spring-core-beans/docs/appendix/99-team-training-kit.md`
- 补充：
  - 为每节课补“可运行证明入口”（对应 Lab/断点包），避免只讲概念。
  - 补“反例题与追问题”，提升训练强度。
- 完善：
  - 与面试题库/自测题/知识地图互链，形成训练闭环。
  - 增加“课后作业与验收方式”（以证据链为主）。
- 深入：
  - 给出“讲师操作脚本”：每个关键点怎么在 IDE 里当场证明。

---

### deepening-strategies（策略资产本身）

> 本目录已经是逐章策略清单，但仍可进一步“执行化”（从建议 → 可落地任务/用例/断点包）。

#### deepening-strategies 总入口
- 文件：`spring-core-modules/spring-core-beans/docs/deepening-strategies/README.md`
- 补充：
  - 增加“从策略到落地”的最小流程：如何把 A/B/C/D/E 转成具体文档段落与 Lab 改动。
- 完善：
  - 给每个维度补 1–2 个“示例落地片段”（指向某章的实际段落/断点/用例），降低理解成本。
- 深入：
  - 增加“策略效果验证”说明：落地后如何用断点/Lab 证明改动带来的学习收益。

#### 策略页：module-readme/docs-root/part-00..05/appendix
- 文件：
  - `spring-core-modules/spring-core-beans/docs/deepening-strategies/module-readme.md`
  - `spring-core-modules/spring-core-beans/docs/deepening-strategies/docs-root.md`
  - `spring-core-modules/spring-core-beans/docs/deepening-strategies/part-00-guide.md`
  - `spring-core-modules/spring-core-beans/docs/deepening-strategies/part-01-ioc-container.md`
  - `spring-core-modules/spring-core-beans/docs/deepening-strategies/part-02-boot-autoconfig.md`
  - `spring-core-modules/spring-core-beans/docs/deepening-strategies/part-03-container-internals.md`
  - `spring-core-modules/spring-core-beans/docs/deepening-strategies/part-04-wiring-and-boundaries.md`
  - `spring-core-modules/spring-core-beans/docs/deepening-strategies/part-05-aot-and-real-world.md`
  - `spring-core-modules/spring-core-beans/docs/deepening-strategies/appendix.md`
- 补充：
  - 为每章策略补“推荐落地优先级（2–4 个点）”与“预计改动位置”（增加哪个段落/新增哪个小节）。
- 完善：
  - 为每章策略补“对应 tests/Lab 名称”，并建议是否新增 Explore 用例或扩展现有 Lab。
- 深入：
  - 把“策略→任务”同步到 solution package（本目录作为“策略 SSOT”，task.md 作为“执行清单”）。
