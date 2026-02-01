# spring-core-beans docs Round 2：逐章继续深化策略（按章差异化，不套统一骨架）

说明：

- Round 2 的目标不是“统一格式”，而是让每一章都更接近“读完就能动手验证”的状态。
- 本文件刻意不使用“主题/结构/入口/源码锚点/验收口径”的固定小标题；每章只写它最该补的那几刀。
- 章节中如果已存在卡片/导读/断点建议/推荐 Lab，本轮优先复用并把它们收敛成更短的证据链与更短的排错路径。
- 需要对照上一轮的“逐章基线提取（含入口用例/源码锚点/已发现缺口）”时，参考：
  - `helloagents/history/2026-02/202602011343_beans_docs_deepen_all/audit/chapter-strategies.md`

---

## docs root

### spring-core-modules/spring-core-beans/docs/README.md

- 把目录页从“资源列表”进一步推向“可选路径”：每条路径都给一句“你现在为什么该走它”，并落到可执行下一步（去哪个工具页/跑哪个入口用例/看哪组断点）。
- 症状表不扩写成大表：更重要的是在表附近补“定位到章节后下一步怎么证”的统一提示（把读者送到知识地图/断点地图/排障清单，而不是让读者自己拼）。
- 章节跳转不堆链接：只保留 1–2 条高频跨模块跳转（例如 Beans → AOP），并写清“跳转目的 + 验证入口”。

---

## deepening-strategies

### spring-core-modules/spring-core-beans/docs/deepening-strategies/README.md

- 这页的价值是“怎么继续加深”，不是“列维度”：把两条最常用入口路径写得更像 SOP（从现象进入 / 从断点进入），让读者不需要先理解一整套方法论。
- 把“策略如何落地到章节正文”说得更短：明确提醒读者优先改“本章最缺的一块”，而不是把每章写成同样结构。

### spring-core-modules/spring-core-beans/docs/deepening-strategies/appendix.md

- appendix 的加深不追求覆盖全：更像“复盘工具箱”。建议挑 3 类高频用途写清楚（面试复盘 / 生产排障 / 快速自检），并分别指向最短入口页。
- 将“工具页之间的互链”写成原因式跳转：从术语表/知识地图/排障清单跳过去时，读者应该带着什么问题去看。

### spring-core-modules/spring-core-beans/docs/deepening-strategies/docs-root.md

- 把 docs/README 的“下一步动作”与各章节的“可验证入口”对齐：强调目录页负责分流，正文负责证明；不要让目录页重复正文主线。
- 对“最短开始路径”给一个可复用示例（1 条即可）：例如从一个高频现象出发，怎么 3 步走到证据链。

### spring-core-modules/spring-core-beans/docs/deepening-strategies/module-readme.md

- 模块 README 的再加深重点是“全局误判纠偏”：建议列出 3 个最常见误判并给出最短纠偏入口（章节 + 断点组 + 推荐 Lab）。
- 用 1 个对照把“定义层/实例层/最终暴露对象”打通（不必统一成固定小节，只要读者能复述并能验证）。

### spring-core-modules/spring-core-beans/docs/deepening-strategies/part-00-guide.md

- Guide 的加深更像“导航质量”：建议标注哪些页是“只看一次就够”（例如总览/矩阵），哪些页是“反复回看工具页”（例如断点地图/排障清单）。
- 若存在历史遗留的占位语气，必须替换成可验证说明（跳到哪一页、跑哪条用例、看哪个观察点）。

### spring-core-modules/spring-core-beans/docs/deepening-strategies/part-01-ioc-container.md

- 这一 Part 的继续加深建议围绕“容器的最小主线”：推荐明确读者先把依赖解析、生命周期、BPP 三个误判点补齐，再进 Internals。
- 对每个“误判点”给一个最短对照（1 句 + 1 个入口）：例如“能注入 ≠ 一定是 Bean”（ResolvableDependency）这种层级误判。

### spring-core-modules/spring-core-beans/docs/deepening-strategies/part-02-boot-autoconfig.md

- Boot 相关章节容易变成“看不见的魔法”：继续加深时要强调“怎么证明条件成立/顺序生效”，把“猜测”变成“可观察事实”。
- 建议把读者从 Boot 视角送回 Beans 视角：明确“最终变成了哪些 BeanDefinition / 哪些 BPP”，并给出最短回链。

### spring-core-modules/spring-core-beans/docs/deepening-strategies/part-03-container-internals.md

- Internals 的继续加深不追求更长流程：更值钱的是“关键窗口期与不可逆点”（例如 BPP 注册完成前就创建的 bean 无法 retroactively 生效）。
- 把“窗口期”变成可复现：每个窗口期至少给一个入口用例/断点组（复用断点地图）。

### spring-core-modules/spring-core-beans/docs/deepening-strategies/part-04-wiring-and-boundaries.md

- 这一 Part 的再加深应该偏“排错与边界”：建议明确哪些章必须带对照实验（例如候选选择/占位符/类型转换/泛型匹配），避免只停留在概念解释。
- 把“误归因对照”写成可复用的排错路径：让读者能用同一套路把问题归因到“定义/解析/转换/代理/生命周期”之一。

### spring-core-modules/spring-core-beans/docs/deepening-strategies/part-05-aot-and-real-world.md

- AOT/扩展章节的加深不要抢主线篇幅：建议强调“什么时候需要它/如何验证它生效”，并把读者导回到主线证据链。
- 对每类扩展点给一个最短入口（例如 runtime hints、XML reader、namespace extension），避免变成概览堆砌。

---

## part-00-guide

### spring-core-modules/spring-core-beans/docs/part-00-guide/009-00-why-index.md

- Index 页继续加深的重点是“把跳转写成目的句”：读者点进去前就知道要验证什么，而不是只看到章节名。
- 对高频条目补“最短证据链入口”提示：例如“从断点地图挑 Cx 断点组，再回到章节验证分支”。

### spring-core-modules/spring-core-beans/docs/part-00-guide/010-03-mainline-timeline.md

- 时间线页继续加深时，优先补“关键窗口期”的标记：哪些阶段做错了就不可逆（比如 BPP 注册完成前的早创建）。
- 把时间线与断点地图互链：时间线负责告诉读者“现在在哪一段”，断点地图负责告诉读者“在这一段看什么对象/分支”。

### spring-core-modules/spring-core-beans/docs/part-00-guide/011-00-deep-dive-guide.md

- Deep Dive 页要更像“学习路线编排”：建议给两条路线即可（顺读主线 / 现象驱动复盘），每条路线都落到具体入口用例与工具页。
- 避免把指南写成“概念全覆盖”：重点在“这一步做完，你应该能证明什么”。

### spring-core-modules/spring-core-beans/docs/part-00-guide/011-04-branch-decision-matrix.md

- 决策矩阵继续加深时，优先把“主观判断”改成“可验证条件”：每个分支至少写清一个观察点（变量/对象/日志/异常类型）。
- 矩阵不要膨胀：挑高频 10–15 个分支做到可直接跳转与可验证，剩余保持简洁。

### spring-core-modules/spring-core-beans/docs/part-00-guide/012-01-quickstart-30min.md

- Quickstart 的加深方向是“更短闭环”：减少读者在 30min 内需要理解的概念数量，强调先跑通入口用例、先看到关键对象变化。
- 对“容易误判的分支”（FactoryBean/占位符/代理）补一个最短对照提示：告诉读者它们为什么会让你以为“Spring 没生效”。

### spring-core-modules/spring-core-beans/docs/part-00-guide/013-01-applicationcontext-refresh-call-chain.md

- 调用链页继续加深时，最值钱的是“只留关键节点 + 节点意义”：每个节点告诉读者它影响的是定义层还是实例层、会不会改变最终暴露对象。
- 把“调用链节点”与“章节/断点组”挂钩：让读者能从节点直接跳到解释与验证（而不是只看一串方法名）。

### spring-core-modules/spring-core-beans/docs/part-00-guide/013-02-breakpoint-map.md

- 断点地图继续加深时，优先做“从症状选断点组”的入口，让工具页本身更像中枢，而不是一篇很长的说明书。
- 每个断点组给 2–3 个“决定性观察点”（变量/对象/返回值），并写清“看到什么就能断言什么分支/结论”。
- 如果需要稳定互链，倾向于补锚点而不改结构；锚点的目的只能是让知识地图/排障清单稳定跳转。

---

## part-01-ioc-container

### spring-core-modules/spring-core-beans/docs/part-01-ioc-container/02-bean-registration.md

- 本章继续加深建议聚焦“输入 → BeanDefinition”：把读者最常忽略的“谁在何时注册定义”写成可验证链路（组件扫描/@Bean/@Import/XML/编程注册的对照）。
- 补一个“注册成功但你以为没注册”的反例：把误判点落到可观察证据（beanDefinitionNames/registry 视角），并给出最短下一跳到 Internals。

### spring-core-modules/spring-core-beans/docs/part-01-ioc-container/08-factorybean.md

- 把 FactoryBean 的最大误判压成一句可验证规则：`getBean(name)` 可能返回“产品”，`&name` 才是“工厂”；并给出在调试器里如何识别两者。
- 继续加深可以补一个“与代理混淆”的对照：FactoryBean 返回的产品对象 vs after-init BPP 替换的 proxy，告诉读者怎么分辨发生在什么阶段。

### spring-core-modules/spring-core-beans/docs/part-01-ioc-container/09-circular-dependencies.md

- 继续加深时把“循环依赖”拆成两种可复现形态：constructor vs setter/field，并明确哪一种能被 early reference 缓解、哪一种会 fail-fast。
- 补一个“看起来像循环依赖、其实是代理替换导致”的对照（引导读者去 early reference 与 proxying phase 章节验证）。

### spring-core-modules/spring-core-beans/docs/part-01-ioc-container/014-03-dependency-injection-resolution.md

- 这章继续加深建议把“候选选择链路”压成最短路径：先把 `@Value`（值注入）与按类型候选选择拆开，再把 Primary/Priority/Order 的优先级用一个对照用例钉住。
- 对 NoSuch/NoUnique 给一个最短排错路线：先确认候选集合来自哪（定义层 vs resolvable dependency），再回链到对应工具页与章节。

### spring-core-modules/spring-core-beans/docs/part-01-ioc-container/015-04-scope-and-prototype.md

- 继续加深时突出“prototype 的生命周期误判”：创建/注入是容器做的，但销毁回调不是自动；用一个可复现对照让读者看到差异。
- 把 scope 与 scoped proxy 的关系写清：什么时候是“同名代理 + scopedTarget”，如何在定义层与实例层分别验证。

### spring-core-modules/spring-core-beans/docs/part-01-ioc-container/016-05-lifecycle-and-callbacks.md

- 继续加深建议把回调顺序落到“能断言的事件序列”：挑 1 个典型 bean，把 init/destroy/aware/postConstruct/BPP 的顺序写成可调试证据链。
- 给一个“为什么某个回调没触发”的最短排错：把原因分型为（未注册 / scope 不同 / proxy 包装 / 过早创建错过 BPP）。

### spring-core-modules/spring-core-beans/docs/part-01-ioc-container/017-06-post-processors.md

- 本章继续加深的关键是“时机与排序”：用一个反例证明“在 BPP 链完整前创建的 bean 不能被 retroactively 修复”，并告诉读者怎么识别这种风险。
- 把 BFPP/BDRPP/BPP 的区别落到“谁改定义、谁改实例、谁改最终暴露对象”，并给出最短跳转到 Internals 的窗口期章节。

### spring-core-modules/spring-core-beans/docs/part-01-ioc-container/018-07-configuration-enhancement.md

- 继续加深建议补一个对照：`@Configuration`（proxyBeanMethods=true/false）与普通 `@Component + @Bean` 的差异，要求读者能在调试器里看到拦截发生的位置。
- 把“看起来像调用了同一个 @Bean 方法但对象不同/相同”的误判写成最短排错路径（从最终暴露对象视角去断言）。

### spring-core-modules/spring-core-beans/docs/part-01-ioc-container/020-01-bean-mental-model.md

- 本章已经偏“纠偏章”，Round 2 更值得做的是把“四类对象”（定义/合并定义/raw instance/exposed object）与后续章节的误判点建立更短互链（FactoryBean/scoped proxy/early reference）。
- 继续加深可以补一个“用同一条证据链解释三种现象”的小结：同一条主线分别如何解释“注入歧义/循环依赖/代理替换”。

---

## part-02-boot-autoconfig

### spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/019-11-debugging-and-observability.md

- 继续加深建议把“观察点”再压短：挑 3 个最常用观察手段（断点组/异常导航/日志），把读者从现象送到证据链的路径写成 3–5 步。
- 把 Boot 的“自动装配影响了什么”落到 Beans 的可观察事实：最终注册了哪些定义/哪些 BPP，在哪个阶段生效。

### spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/020-09-auto-config-ordering.md

- 本章继续加深应强调“顺序与条件的关系”：读者要能证明“先后顺序改变了哪些 BeanDefinition/BPP 是否存在”，而不是只背注解。
- 建议补一个最短对照：同一套配置，改变 order 后的最终暴露对象是否变化（引导读者去断点地图与知识地图验证）。

### spring-core-modules/spring-core-beans/docs/part-02-boot-autoconfig/021-10-spring-boot-auto-configuration.md

- 继续加深建议把“auto-configuration”落到容器主线：它最终是“注册定义 + 注册基础设施 + 参与后续创建”，并指出读者可以在哪一段看到它的痕迹。
- 章节末尾给一个最短承接：从 Boot 视角回到 Beans 视角（定义层/实例层/最终对象）应该怎么复盘。

---

## part-03-container-internals

### spring-core-modules/spring-core-beans/docs/part-03-container-internals/022-12-container-bootstrap-and-infrastructure.md

- 继续加深建议把“基础设施”做成可验证清单：哪些是定义层基础设施、哪些是实例层链路必需品，读者能在调试器里枚举/定位到它们。
- 避免把本章写成“名词图鉴”：更值钱的是把这些基础设施放回 refresh 主线，解释它们为什么出现在那个时机点。

### spring-core-modules/spring-core-beans/docs/part-03-container-internals/13-bdrpp-definition-registration.md

- 继续加深建议把 BDRPP 的“定义注册”压成证据链：从 registry 视角看到新增定义，且能解释“为什么这一步发生在实例创建前”。
- 补一个“注册了但你没看到”的误判对照：例如定义来源差异导致你在错误的 registry/阶段去找。

### spring-core-modules/spring-core-beans/docs/part-03-container-internals/14-post-processor-ordering.md

- 继续加深建议把排序规则落到一个可复现排序结果：PriorityOrdered/Ordered/无序三类在同一用例里如何决定最终链路。
- 给一个最短排错路径：当你怀疑“BPP 顺序不对”时，先看哪个集合/哪个排序点能直接证明。

### spring-core-modules/spring-core-beans/docs/part-03-container-internals/15-pre-instantiation-short-circuit.md

- 这章继续加深的关键是“短路点”：明确哪些扩展点会导致绕过常规 `doCreateBean` 路径，并告诉读者如何在断点组中捕捉到短路发生。
- 补一个“短路导致的误判”对照：例如你以为 init/bpp 没执行，实际上是被替换/提前返回了别的对象。

### spring-core-modules/spring-core-beans/docs/part-03-container-internals/16-early-reference-and-circular.md

- 继续加深建议把 early reference 的三层缓存讲到能调试：singletonObjects/earlySingletonObjects/singletonFactories 的状态变化如何对应“你拿到的对象是什么”。
- 把 early reference 与最终 proxy 替换的关系写清：强调“early 与 final 不一致”会导致的真实故障形态。

### spring-core-modules/spring-core-beans/docs/part-03-container-internals/17-lifecycle-callback-order.md

- 继续加深建议补一个“单个 bean 的完整回调时间线”：让读者能把回调顺序与断点组/refresh 节点对应起来。
- 把 SmartInitializingSingleton/SmartLifecycle 等延迟回调点与普通 init 回调做对照，避免误判“init 没执行”。

### spring-core-modules/spring-core-beans/docs/part-03-container-internals/18-refresh-to-bean-creation-mainline.md

- 这章继续加深更像“总线”：建议把 refresh→创建主线再压缩成读者能复述的 5–7 个节点，并且每个节点都能跳到对应章节/断点组。
- 补一个“同一现象在不同节点产生”的对照：例如注入失败可能发生在定义注册期、依赖解析期或类型转换期，如何最快定位到阶段。

---

## part-04-wiring-and-boundaries

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/023-18-lazy-semantics.md

- 继续加深建议把 lazy 讲到能证：lazy 影响的是“何时创建/是否预实例化”，但不等于“永远不创建”；用一个对照用例展示差异。
- 把 lazy 与代理/循环依赖的交互写清：读者最容易误判 lazy 导致的时机变化。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/19-depends-on.md

- 继续加深建议补一个“dependsOn 不等于依赖注入”的对照：强调它影响创建顺序而不是候选选择，并给出在调试器里如何证明顺序生效。
- 把典型误用写成最短排错：当你以为 dependsOn 生效但顺序没变，先看哪里能直接断言。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/20-resolvable-dependency.md

- 继续加深重点是“能注入 ≠ 一定是 Bean”：建议用一个反例让读者看到“注入成功但 getBeansOfType 找不到”的现象，并解释它属于哪条链路。
- 给一个与外部对象自动装配的最短承接（AutowireCapableBeanFactory），避免读者把它当作“隐藏 Bean”。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/21-context-hierarchy.md

- 继续加深建议把层级查找落到可复现的查找顺序：parent/child 的 bean 查找与覆盖规则如何验证。
- 对“同名 bean 在不同 context”给最短排错：先确定你拿到的是哪一个 ApplicationContext，再去定位定义与最终对象。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/22-bean-names-and-aliases.md

- 继续加深可以把“命名/别名”与候选选择串起来：当注入失败时，哪些失败是“名字没对上”而不是“类型没对上”。
- 补一个“别名导致误判”对照：读者在调试器里如何确认最终解析到的 beanName。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/23-factorybean-deep-dive.md

- 本章继续加深建议聚焦“缓存与生命周期”：FactoryBean 的产品对象何时缓存、何时重新创建，如何在调试器里观察。
- 补一个“产品是单例、工厂也是单例，但行为仍不直观”的对照（解释这不是 proxy，而是 FactoryBean 语义）。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/24-bean-definition-overriding.md

- 继续加深建议把 override 讲成“定义层最后写入者获胜”：并补一个“你以为覆盖了但实际没覆盖”的反例（把 fail-fast 与 silent override 的差异写清）。
- 给一个最短排错：同名 bean 行为异常时，先在哪一步确认最终生效的 BeanDefinition。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md

- 本章继续加深的关键是“注册时机”：programmatic 注册同步到容器后，是否赶得上影响实例创建；建议补一个错过时机的反例。
- 给一个最短承接：与 post processor ordering / pre-instantiation short circuit 章节互链，避免读者孤立理解。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/26-smart-initializing-singleton.md

- 继续加深建议把 SmartInitializingSingleton 的触发点写清：它发生在单例预实例化完成之后，读者能在断点组看到它与普通 init 的差别。
- 补一个使用场景对照：为什么它适合做“所有单例就绪后”的工作，而不是用 @PostConstruct。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/27-smart-lifecycle-phase.md

- 继续加深建议把 phase 讲到能验证：哪些 bean 在哪个 phase start/stop，如何在调试器或日志中确认。
- 把 SmartLifecycle 与普通 lifecycle callback 做边界对照：避免读者误把“没 start”当成“没创建”。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/28-custom-scope-and-scoped-proxy.md

- 继续加深建议把 custom scope 的两个层面拆开：定义层的 scoped proxy 生成 vs 运行期的目标对象解析；并给一个可复现对照。
- 指出最容易踩坑的点：scope 不同导致的回调/销毁语义差异，读者如何验证。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/29-factorybean-edge-cases.md

- 继续加深应强调“边界组合拳”：FactoryBean + 循环依赖 + proxy 替换的交互是如何导致 early/final 不一致的。
- 给一个最短排错：当你怀疑是 FactoryBean 相关边界问题时，先确认你拿到的是工厂还是产品，再判断是否被 proxy 替换。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/30-injection-phase-field-vs-constructor.md

- 继续加深建议用同一 bean 的两种注入方式做对照，明确“注入发生在什么阶段、影响什么可观察结果”，避免停在风格争论。
- 补一个排错建议：当循环依赖/代理相关问题出现时，如何用注入方式快速判断是否会 fail-fast。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md

- 继续加深建议把 proxy 替换的“发生点”写成可验证断言：after-init BPP 替换 vs early reference 替换，并说明这两者对最终对象的影响。
- 给一个最短排错：当你怀疑“代理不生效/自调用失效”时，先确认最终暴露对象是不是 proxy，再回链到对应章节。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/32-resource-injection-name-first.md

- 继续加深建议把 @Resource 的 name-first 与 @Autowired 的 type-first 用一个对照用例写清：读者能通过观察候选集合确认差异。
- 给最短排错：当你遇到“明明有类型却注入失败”，先检查是不是 name-first 在找别名/beanName。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md

- 继续加深建议把 Primary/Priority/Order 的优先级钉死在一个对照实验：同类型多候选时，如何稳定选中目标，读者能复盘出“为什么是它”。
- 补一个“误把顺序当成 Primary”的反例，让读者知道什么时候看排序、什么时候看 primary。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md

- 继续加深建议把“占位符解析 / SpEL / 类型转换”三件事继续保持拆分，并把最短排错路线写得更像决策表（先确认哪一步失败）。
- 对 strict/non-strict 的差异，强调“不是 @Value 决定的，而是 resolver/configurer 决定的”，并给一个可验证入口。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/35-merged-bean-definition.md

- 继续加深建议把 merged 的意义落到“你看到的定义不等于最终生效定义”：读者能在调试器里拿到 merged 版本并解释差异来自哪里。
- 给一个“为什么 parent/child 合并后行为变了”的反例，并指出最短定位方式。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md

- 继续加深建议把“转换失败”排错写成最短路径：先确认是占位符/SpEL 还是 conversion service/property editor，再看注入点类型。
- 给一个对照：同一个字符串值在不同注入点类型下如何触发不同的转换链路。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/37-generic-type-matching-pitfalls.md

- 继续加深建议把泛型匹配问题收敛成“你以为按泛型选，其实没选上”的可复现反例，并告诉读者如何观察候选被过滤的原因。
- 给一个最短承接：与 candidate selection 章节互链，避免读者把泛型匹配当成另一套完全不同的系统。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/38-environment-and-propertysource.md

- 继续加深建议把 property source precedence 讲到能验证：同名 key 来自多个 source 时，最终取值如何在调试器里确认。
- 补一个“配置没生效”的最短排错：区分“配置没加载/被覆盖/被 placeholder non-strict 放行”三类。

### spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/39-beanfactory-api-deep-dive.md

- 继续加深建议把 API 深潜页做成“排错入口索引”：当你怀疑注册/创建/候选选择问题时，最短该用哪一个 API 取证。
- 把 API 与四类对象（定义/merged/raw/exposed）挂钩，避免读者只记方法名不知该拿哪个对象验证。

---

## part-05-aot-and-real-world

### spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/024-40-aot-and-native-overview.md

- 继续加深建议强调“什么时候需要 AOT/Native 视角”：把读者从“概览”送到“如何验证是否准备充分”。
- 补一个与 runtime hints 的最短承接：读者看完本章后知道下一步该去哪里补 hints、如何确认生效。

### spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/41-runtimehints-basics.md

- 继续加深建议把 runtime hints 的落地写成最短闭环：写 hints → 运行/构建验证 → 失败时如何定位缺的是什么。
- 避免泛讲：挑 1–2 个最常见场景（反射/资源/代理）讲到可验证即可。

### spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/42-xml-bean-definition-reader.md

- 继续加深建议把 XML reader 的“定义层证据”写清：读者能确认 XML 是在何时、以何种 BeanDefinition 形态进入 registry。
- 补一个“XML 解析失败”的最短排错：异常类型 → 入口方法 → 最短定位点（与异常导航/排障清单互链）。

### spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/43-autowirecapablebeanfactory-external-objects.md

- 继续加深建议把外部对象装配的边界写清：它能完成注入，但不一定让对象成为可枚举 Bean；并给出如何验证。
- 与 ResolvableDependency 章节互链：让读者理解这类能力属于“依赖解析链路”，而不是“注册定义”。

### spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/44-spel-and-value-expression.md

- 继续加深建议把 SpEL 与占位符、类型转换三者关系保持“可排错”：给读者一个最短决策表，先判定是哪一步在出错。
- 补一个“组合写法”最短解释：为什么 `${...}` 往往先解析、再做 `#{...}` 求值，最后才类型转换。

### spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/45-custom-qualifier-meta-annotation.md

- 继续加深建议用一个对照把“自定义 qualifier 是否参与候选过滤”讲清：读者能验证过滤发生在候选选择的哪一步。
- 与 injection resolution/candidate selection 章节互链：避免读者把它当作“注解魔法”。

### spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/46-xml-namespace-extension.md

- 继续加深建议把 namespace 扩展讲到能落地：从 schema/handler/parser 到 BeanDefinition 注册的证据链，读者能在调试器里走通一次。
- 补一个“解析到哪里失败”的排错入口（异常类型 + 最短定位点）。

### spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/47-beandefinitionreader-other-inputs-properties-groovy.md

- 继续加深建议强调“不同输入最终同归 BeanDefinition”：properties/groovy 的差异在输入层，定义层如何统一表示。
- 给一个对照：同一 bean 用不同输入定义时，registry 中最终定义是否一致、如何验证。

### spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/48-method-injection-replaced-method.md

- 继续加深建议把 method injection 的限制讲清：它依赖子类代理/方法拦截，哪些写法会导致它无法生效，并给出可验证入口。
- 与 proxying phase/config enhancement 章节互链：让读者知道这是“代理的一种用途”，不是独立机制。

### spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/49-built-in-factorybeans-gallery.md

- 继续加深建议把“内置 FactoryBean”讲成“你在调试器里会遇到的真实对象”：挑 3–5 个高频的，告诉读者它们为什么出现、如何识别产品/工厂。
- 与 FactoryBean 章节互链：让读者能把“图鉴”变成排错能力。

### spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/50-property-editor-and-value-resolution.md

- 继续加深建议把 property editor 与 conversion service 的边界写清：读者在遇到转换异常时知道该往哪个方向排。
- 补一个与 @Value/SpEL/占位符的承接：强调这些链路最终都会落到“把值转成注入点类型”。

---

## appendix

### spring-core-modules/spring-core-beans/docs/appendix/025-90-common-pitfalls.md

- 继续加深建议把“误区清单”收敛成“最短诊断路线”：每类误区给一个第一断点/第一排除项，并回链到对应章节与入口用例。
- 对特别高频的误判补对照：例如把“代理不生效”误判成“注入失败”，要告诉读者怎么最快分型。

### spring-core-modules/spring-core-beans/docs/appendix/026-99-self-check.md

- 继续加深建议把自测题的“答案”改成“验证路线”：每题给最短回链（章节/入口用例/断点组），让读者能真正自证。
- 对综合题补“分步取证”：先判定层级（定义/实例/最终对象），再进入细分章节。

### spring-core-modules/spring-core-beans/docs/appendix/91-glossary.md

- 继续加深建议减少抽象解释，补“落到代码里是什么”：每个术语至少关联一个关键类/方法/数据结构，并回链到首次出现章节。
- 对易混术语给对照：例如 BeanDefinition vs Bean、BPP vs BFPP 等。

### spring-core-modules/spring-core-beans/docs/appendix/92-knowledge-map.md

- 继续加深建议把“从现象到证据链”的路径再压短：高频现象优先补齐（章节 + 断点组 + 推荐 Lab + 第一观察点）。
- 把误归因对照做得更像决策表：读者能用 2–3 个观察点就把问题归因到正确层级。

### spring-core-modules/spring-core-beans/docs/appendix/93-interview-playbook.md

- 继续加深建议把“能说”变成“能证”：每个高频问法给一个最短证据链入口（章节/断点/用例），避免只背结论。
- 对容易背错的题，补一个反例或边界，帮助读者在面试中避免过度绝对化。

### spring-core-modules/spring-core-beans/docs/appendix/94-production-troubleshooting-checklist.md

- 继续加深建议把最常见的 3 类事故写成 3–5 步最短 SOP，并让每一步都能跳到对应章节/断点组/入口用例取证。
- 强化“阶段分型”：让读者先判断问题在定义层/解析层/代理替换/值解析/转换，再进入对应章节，避免盲目重启与试错。

### spring-core-modules/spring-core-beans/docs/appendix/95-spring-beans-public-api-index.md

- 继续加深建议把 API 索引做成“取证入口表”：每个 API 对应你能回答什么问题（注册/查定义/拿 merged/看单例缓存）。
- 只挑高频 API 写深：避免把索引写成百科；其余保持简洁并指向对应章节。

### spring-core-modules/spring-core-beans/docs/appendix/96-spring-beans-public-api-gap.md

- 继续加深建议把 “gap” 写成“为什么这里会让人误判”：每个 gap 给一个典型误用/误读，并给最短纠偏入口（章节/工具页）。
- 避免泛泛列缺口：挑最常见的 5–8 个写到能落地即可。

### spring-core-modules/spring-core-beans/docs/appendix/97-explore-debug-tests.md

- 继续加深建议把“如何跑测试/怎么看输出”写得更像复盘教程：每个推荐测试都说清楚“要观察的对象/断言点”。
- 与断点地图互链：告诉读者跑到哪条测试时最适合打哪一组断点。

### spring-core-modules/spring-core-beans/docs/appendix/98-debugger-pack.md

- 继续加深建议把 debugger pack 做成“可复用断点包”：按症状/阶段分组，不追求覆盖全部，只追求读者能快速选中一包开始调。
- 每个断点包给一个最短入口用例，避免读者不知道如何触发断点。

### spring-core-modules/spring-core-beans/docs/appendix/99-team-training-kit.md

- 继续加深建议把训练营做成“可执行课程表”：按 1–2 周节奏给出每天/每次训练的最短任务（跑用例/看断点/复盘问题）。
- 给一个“对照题/边界题”清单：用于检查团队是否真的理解（避免只会照抄结论）。

