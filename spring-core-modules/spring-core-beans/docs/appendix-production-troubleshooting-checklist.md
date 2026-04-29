# 生产排障清单（Troubleshooting Checklist）：从症状到证据链
<!-- CHAPTER-CARD:START -->
!!! summary "章节入口"
    - 使用方式：先用本章的“清单/索引/分流”把问题分型，再回到对应章节用断点与 Lab 把结论证明出来；团队内训/复盘时可直接按本章结构复用。

    观察对象：生产排障清单：从症状到证据链。
    主线位置：`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。

    对照入口：`SpringCoreBeansBreakpointPackLabTest`。需要下探源码时，可以从 `DefaultListableBeanFactory#registerBeanDefinition` / `DefaultListableBeanFactory#doResolveDependency` / `DefaultSingletonBeanRegistry#getSingleton` 这些入口切入。

<!-- CHAPTER-CARD:END -->

## 读法：把生产问题先压到阶段

本页不是新的主线章节，而是把已读过的机制拿回来验证、排障和自检。读法如下：

1. 先运行 Book Matrix、Branch Matrix 或本页列出的最小 Lab，把现象固定成可重复结果。
2. 再按现象、题目或坑点定位对应章节、断点和关键变量。
3. 最后用对应实验/测试收敛答案；如果答案仍然只停留在概念层面，再回到正文补齐机制。

## 生产排障入口：先分层，再下断点

- 这页可以直接当成“排障 SOP”：遇到问题时先定位阶段，再找最短断点入口，最后用最小复现验证；尽量避免在业务项目里凭感觉改配置/改注入。

- 官方文档对照（适用版本：Spring Framework 6.2.x；本仓库基线：6.2.15）：https://docs.spring.io/spring-framework/reference/core/beans.html


!!! example "本章配套实验（先运行再读）"

    - Lab（排障入口总集合）：
      - `SpringCoreBeansBreakpointPackLabTest`
      - `SpringCoreBeansIocBranchMatrixLabTest`
      - `SpringCoreBeansInternalsBranchMatrixLabTest`

## 机制主线：先把问题放回 refresh 的哪一段

> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html

几乎所有 IoC 相关事故，都能归到 refresh 的某一段（见 [主线时间线](guide-mainline-timeline.md)）：

- **定义层（Definition Phase）**：解析/注册 BeanDefinition（XML/Reader/扫描/导入）、BFPP/BDRPP 改写定义
- **实例层（Creation Phase）**：`doCreateBean` 实例化/注入/初始化、BPP 链、单例缓存
- **完成后（Post Refresh）**：容器就绪回调、运行期 getBean、懒加载触发

排障第一步永远是：读者现在处在哪一段。

---

## 总分流表（先选最短入口）

> 落点：不要上来就“改注入/改配置”。先用一张表把问题压缩到“阶段 + 最短断点 + 最短复现”。

若需要更系统的“现象 → 章节 → 断点组 → Lab”（学习/复盘视角）索引，跳到：[知识地图](appendix-knowledge-map.md)。

| 现象（Symptoms） | 首要阶段 | 第一断点（最短证据链） | 第一章（最短阅读） | 对应 Lab |
| --- | --- | --- | --- | --- |
| 扫不到/导不进（NoSuchBeanDefinition） | 定义层 | `DefaultListableBeanFactory#registerBeanDefinition` | `ioc-bean-registration.md` | `SpringCoreBeansComponentScanLabTest` / `SpringCoreBeansImportLabTest` |
| 多候选歧义（NoUniqueBeanDefinition） | 注入解析 | `DefaultListableBeanFactory#doResolveDependency` | `ioc-dependency-injection-resolution.md` | `SpringCoreBeansAutowireCandidateSelectionLabTest` |
| 代理不生效（像绕过 AOP） | 创建/after-init | `applyBeanPostProcessorsAfterInitialization` | `wiring-proxying-phase-bpp-wraps-bean.md` | `SpringCoreBeansProxyingPhaseLabTest`（或对应分支矩阵） |
| 循环依赖异常/行为诡异 | 创建层（窗口期） | `DefaultSingletonBeanRegistry#getSingleton` | `ioc-circular-dependencies.md` | `SpringCoreBeansCircularDependencyBoundaryLabTest` |
| `@Value` 值不对/缺失不失败 | 定义层 + 注入阶段 | `AbstractBeanFactory#resolveEmbeddedValue` | `wiring-value-placeholder-resolution-strict-vs-non-strict.md` | `SpringCoreBeansValuePlaceholderResolutionLabTest` |
| `FactoryBean` 获取到的不是容易误以为的对象 | getBean 分流 | `AbstractBeanFactory#doGetBean` | `ioc-factorybean.md` | `SpringCoreBeansFactoryBeanDeepDiveLabTest` |
| Boot 自动装配“偶发失效” | 定义层顺序 | `AutoConfigurationImportSelector#selectImports` | `boot-auto-config-ordering.md` | `SpringCoreBeansAutoConfigurationOrderingLabTest` |
| XML/namespace 解析失败 | 定义层输入 | `XmlBeanDefinitionReader#loadBeanDefinitions` | `aot-xml-bean-definition-reader.md` | `SpringCoreBeansXmlBeanDefinitionReaderLabTest` |
| AOT/Native 行为缺失（反射/资源） | 构建期契约 | `RuntimeHintsRegistrar#registerHints` | `aot-runtimehints-basics.md` | `SpringCoreBeansAotRuntimeHintsLabTest` |

---

## 三类高频事故：最短诊断路径（3–5 步）

> 落点：把“感觉上像是……”压成可验证步骤。每一步都能落到断点与可复现实验（实验/测试），避免在业务项目里盲调。

### 注入失败（NoSuch/NoUnique）：先把候选集合与收敛规则看清

1. 先分型：是“找不到候选（NoSuch）”，还是“候选太多（NoUnique）”？
2. 第一断点：`DefaultListableBeanFactory#doResolveDependency`（断点地图：[C6](guide-breakpoint-map.md#c6)）
3. 三个观察点（最小够用）：
   - 候选集合：`findAutowireCandidates` 的结果（候选数量/beanName 列表）
   - 收敛规则：`determineAutowireCandidate` 的选择过程（Qualifier/Primary/Priority 是否参与）
   - 注入点语义：`DependencyDescriptor`（是否 required / 是否 @Lazy / 是否带 Qualifier）
4. 最短下一跳：
   - 章节：`ioc-dependency-injection-resolution.md`、`wiring-autowire-candidate-selection-primary-priority-order.md`
   - 对照入口：`SpringCoreBeansAutowireCandidateSelectionLabTest`

### 代理不生效（像绕过 AOP）：先证“BPP 链是否完整”再证“替换是否发生”

1. 先分型：问题更接近“未注册/顺序不对”（BPP 链问题），还是“已注册但错过时机”（过早实例化）？
2. 第一断点：
   - `PostProcessorRegistrationDelegate#registerBeanPostProcessors`（断点地图：[C4](guide-breakpoint-map.md#c4)）
   - `AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`（断点地图：[C7](guide-breakpoint-map.md#c7)）
3. 三个观察点（最小够用）：
   - `beanFactory.getBeanPostProcessorCount()` 与关键 BPP 是否在列表中
   - 目标 bean 是否在 BPP 链完整前就被创建（过早实例化信号）
   - after-init 返回的对象是否发生替换（raw vs proxy）
4. 最短下一跳：
   - 章节：`wiring-proxying-phase-bpp-wraps-bean.md`、`wiring-programmatic-bpp-registration.md`
   - 对照入口：`SpringCoreBeansProxyingPhaseLabTest` / `SpringCoreBeansProgrammaticBeanPostProcessorLabTest`

### 循环依赖/early reference：先区分 constructor vs setter，再看 early 与 final 是否一致

1. 先分型：constructor cycle（通常 fail-fast）还是 setter/field cycle（窗口期可能被 early reference 缓解）？
2. 第一断点：`DefaultSingletonBeanRegistry#getSingleton`（断点地图：[C5](guide-breakpoint-map.md#c5)）
3. 三个观察点（最小够用）：
   - `singletonObjects` / `earlySingletonObjects` / `singletonFactories` 的命中分支
   - `singletonsCurrentlyInCreation`（是否处于创建窗口期）
   - early reference 的形态（raw vs proxy）与最终暴露对象是否一致
4. 最短下一跳：
   - 章节：`ioc-circular-dependencies.md`、`internals-early-reference-and-circular.md`
   - 对照入口：`SpringCoreBeansCircularDependencyBoundaryLabTest` / `SpringCoreBeansEarlyReferenceLabTest`

## 排障的最短闭环（把“感觉”压成可验证步骤）
> 官方参考（Spring Framework 6.2.x，BeanFactory/Bean 语义总览）：https://docs.spring.io/spring-framework/reference/core/beans.html


### 1.1 先把现象写成事实

- 异常类型是什么（root cause 关键词）？
- 是启动时失败，还是运行一段时间后失败？
- 影响面：所有请求都挂，还是某条路径触发？

### 1.2 把复现入口缩小到“可重复”

- 能否用 **最小容器** 复现（`AnnotationConfigApplicationContext`/`GenericApplicationContext`）？
- 能否用本仓库 **对应 Lab** 复现同类机制边界？

### 1.3 证据链：断点 + 观察点

- 选 1 个关键方法设置断点 + 3 个观察点（观察清单）
- 把“猜”变成“观察到”：候选集合是什么？BPP 链顺序是什么？三层缓存状态是什么？

### 1.4 分流：这是定义层、实例层还是时机问题？

- 定义层问题？实例层问题？时机问题（过早实例化）？
- 顺序问题（谁包谁）？还是形态问题（early vs final）？

### 1.5 修复：先消根因，再谈策略

- 修复优先级：消除根因（设计/边界） > 改注入策略（Qualifier/Provider/Lazy） > 开关回退（最后才用）

### 1.6 回归：把修复写成可重复验证

- 写一个可回归的最小测试（或在现有 Lab 中补断言）
- 再运行一次主线回归（本模块 `mvn -pl :spring-core-beans test`）

---

## 1.7 误归因对照（生产最常见三错）

- **错因**：把“代理不生效”归因到配置没开
  **纠正**：优先确认 BPP 链是否完整，以及 bean 是否过早创建

- **错因**：把 `NoUniqueBeanDefinition` 当成“自动装配坏了”
  **纠正**：这是候选收敛规则没表达清楚（Qualifier/Primary/Priority）

- **错因**：把 `@Value` 原样字符串当成“配置没加载”
  **纠正**：优先确认 strict/non-strict resolver

## 常见事故分类（现象 → 证据链入口）

### 2.1 启动失败：`BeanDefinitionStoreException` / XML/Reader 相关

现象通常表现为：

- `BeanDefinitionStoreException`、`BeanDefinitionParsingException`、XML 解析失败等

可以从这些断点/观察点收敛：

- `AbstractApplicationContext#refresh`（定位阶段）
- `XmlBeanDefinitionReader#loadBeanDefinitions`（如果走 XML）
- `DefaultListableBeanFactory#registerBeanDefinition`（注册入口）

分流时重点问：

- 定义层输入有误（XML/资源路径/占位符）还是 processor 改写造成冲突？

对应章节：

- `ioc-bean-registration.md`
- `aot-xml-bean-definition-reader.md`

### 2.2 启动失败：`NoSuchBeanDefinitionException` / `NoUniqueBeanDefinitionException`

现象通常表现为：

- 缺 bean / 多候选歧义

可以从这些断点/观察点收敛：

- `DefaultListableBeanFactory#doResolveDependency`
- `findAutowireCandidates`（候选集合）
- `determineAutowireCandidate`（收敛规则）

对应章节：

- `ioc-dependency-injection-resolution.md`
- `wiring-autowire-candidate-selection-primary-priority-order.md`

### 2.3 代理不生效（事务/安全/缓存像没开）

现象通常表现为：

- 读者“确定加了 AOP”，但调用路径像绕过代理

可以从这些断点/观察点收敛：

- `beanFactory.getBeanPostProcessors()`（BPP 链是否完整 & 顺序）
- `applyBeanPostProcessorsAfterInitialization`（代理/替换发生点）
- 是否存在“过早实例化”（bean 在 BPP 注册前就被创建）

分流时重点问：

- 顺序问题（谁包谁） vs 时机问题（错过 BPP）

对应章节：

- `wiring-proxying-phase-bpp-wraps-bean.md`
- `wiring-programmatic-bpp-registration.md`

### 2.4 循环依赖 / 提前引用相关（启动失败或行为诡异）

现象通常表现为：

- `BeanCurrentlyInCreationException`
- 代理/增强后开始出现循环依赖相关异常

可以从这些断点/观察点收敛：

- `DefaultSingletonBeanRegistry#getSingleton`（三层缓存命中分支）
- `DefaultSingletonBeanRegistry#addSingletonFactory`（early exposure 起点）
- `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`（early 形态决定）

分流时重点问：

- constructor cycle（通常 fail-fast）还是 setter cycle（窗口期可救）？
- early/raw 与 final/proxy 是否一致？

对应章节：

- `ioc-circular-dependencies.md`
- `internals-early-reference-and-circular.md`

### 2.5 `@Value` 值不对 / 缺失不失败 / 运行期才暴露

现象通常表现为：

- 值是 `"${missing}"` 原样字符串
- strict/non-strict 行为在不同环境不一致

可以从这些断点/观察点收敛：

- `AbstractBeanFactory#resolveEmbeddedValue`（解析输入/输出）
- `PropertySourcesPlaceholderConfigurer#postProcessBeanFactory`（strict 策略来源）

对应章节：

- `wiring-value-placeholder-resolution-strict-vs-non-strict.md`
- `aot-spel-and-value-expression.md`
- `wiring-type-conversion-and-beanwrapper.md`

---

## 断点包：排障时的“第一入口”

若只记一个入口，记这个：

- `appendix-debugger-pack.md`

它把常见问题都压缩成“断点入口 + 观察清单 + 对应 Lab”，适合作为生产排障的第一跳转页。

## 最短调用链（方法级）：把“证据链”写成可执行路线

本页每个条目都给了“证据链入口”（方法名），但真正落地排障时，需要把它组装成 3 步的最短调用链：

1. **定位阶段**：先在 `AbstractApplicationContext#refresh`（或启动异常栈顶）确认自己处在 refresh 的哪一段。
2. **锁定入口**：选择该现象的第一入口方法（例如 `doResolveDependency` / `getSingleton` / `resolveEmbeddedValue`）。
3. **观察到关键数据结构**：候选 Map / 三层缓存 / embedded value 解析前后值 / BPP 链顺序。

无需追完整条链，只要能用 2–3 个方法把“阶段→分支→结论”连起来即可。

---

## 验收口径：能把事故压成阶段和第一断点
读完后应能做到：

1. 任意一个 IoC 相关异常，先定位它属于 definition 还是 bean creation，再决定下哪个断点。
2. 解释“为什么这个断点能证明结论”（而不是碰巧）。
3. 用本仓库的 Lab 复现同类机制边界，并把修复方案固化成可回归验证。


## 小结：生产排障不从猜测开始

`ApplicationContext#refresh` 主线：注册 BeanDefinition → BFPP 加工定义 → 实例化/注入 → BPP 增强（代理/回调）→ 生命周期与销毁。
