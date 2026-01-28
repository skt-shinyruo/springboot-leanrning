# 94. 生产排障清单（Troubleshooting Checklist）：从症状到证据链

## 导读

- 本章主题：**生产排障清单：从症状到证据链**
- 阅读方式建议：把本章当成“排障 SOP”。你遇到问题时不要凭感觉改配置/改注入，而是按本章固定流程：先定位阶段 → 再找最短断点入口 → 再用最小复现验证。

!!! summary "本章要点"

    - 排障优先级：**先确定发生阶段**（definition vs bean creation vs after-init）→ 再确定分支点（if/then）→ 最后才谈修复方案。
    - 生产排障不要直接“猜改”：优先把现象缩小到最小容器/最小配置（本仓库的 Lab 就是为这一步准备的）。
    - 证据链要闭环：Symptoms → Repro → Evidence → Decision → Fix → Verify（少一步就容易复发）。

!!! example "本章配套实验（先跑再读）"

    - Lab（排障入口总集合）：
      - `SpringCoreBeansBreakpointPackLabTest`
      - `SpringCoreBeansIocBranchMatrixLabTest`
      - `SpringCoreBeansInternalsBranchMatrixLabTest`

## 机制主线：先把问题放回 refresh 的哪一段

几乎所有 IoC 相关事故，都能归到 refresh 的某一段（见 [10. 主线时间线](../part-00-guide/010-03-mainline-timeline.md)）：

- **定义层（Definition Phase）**：解析/注册 BeanDefinition（XML/Reader/扫描/导入）、BFPP/BDRPP 改写定义
- **实例层（Creation Phase）**：`doCreateBean` 实例化/注入/初始化、BPP 链、单例缓存
- **完成后（Post Refresh）**：容器就绪回调、运行期 getBean、懒加载触发

排障第一步永远是：你现在处在哪一段。

---

## 0. 总分流表（先选最短入口）

> 目标：不要上来就“改注入/改配置”。先用一张表把问题压缩到“阶段 + 最短断点 + 最短复现”。

| 现象（Symptoms） | 首要阶段 | 第一断点（最短证据链） | 第一章（最短阅读） | 推荐 Lab |
| --- | --- | --- | --- | --- |
| 扫不到/导不进（NoSuchBeanDefinition） | 定义层 | `DefaultListableBeanFactory#registerBeanDefinition` | `part-01-ioc-container/02-bean-registration.md` | `SpringCoreBeansComponentScanLabTest` / `SpringCoreBeansImportLabTest` |
| 多候选歧义（NoUniqueBeanDefinition） | 注入解析 | `DefaultListableBeanFactory#doResolveDependency` | `part-01-ioc-container/014-03-dependency-injection-resolution.md` | `SpringCoreBeansAutowireCandidateSelectionLabTest` |
| 代理不生效（像绕过 AOP） | 创建/after-init | `applyBeanPostProcessorsAfterInitialization` | `part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md` | `SpringCoreBeansProxyingPhaseLabTest`（或对应分支矩阵） |
| 循环依赖异常/行为诡异 | 创建层（窗口期） | `DefaultSingletonBeanRegistry#getSingleton` | `part-01-ioc-container/09-circular-dependencies.md` | `SpringCoreBeansCircularDependencyBoundaryLabTest` |
| `@Value` 值不对/缺失不失败 | 定义层 + 注入阶段 | `AbstractBeanFactory#resolveEmbeddedValue` | `part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md` | `SpringCoreBeansValuePlaceholderResolutionLabTest` |
| `FactoryBean` 拿到的不是你以为的对象 | getBean 分流 | `AbstractBeanFactory#doGetBean` | `part-01-ioc-container/08-factorybean.md` | `SpringCoreBeansFactoryBeanDeepDiveLabTest` |
| Boot 自动装配“偶发失效” | 定义层顺序 | `AutoConfigurationImportSelector#selectImports` | `part-02-boot-autoconfig/020-09-auto-config-ordering.md` | `SpringCoreBeansAutoConfigurationOrderingLabTest` |
| XML/namespace 解析失败 | 定义层输入 | `XmlBeanDefinitionReader#loadBeanDefinitions` | `part-05-aot-and-real-world/42-xml-bean-definition-reader.md` | `SpringCoreBeansXmlBeanDefinitionReaderLabTest` |
| AOT/Native 行为缺失（反射/资源） | 构建期契约 | `RuntimeHintsRegistrar#registerHints` | `part-05-aot-and-real-world/41-runtimehints-basics.md` | `SpringCoreBeansAotRuntimeHintsLabTest` |

---

## 1. 排障 SOP（建议固定为团队模板）

### 1.1 Symptoms（现象）

- 异常类型是什么（root cause 关键词）？
- 是启动时失败，还是运行一段时间后失败？
- 影响面：所有请求都挂，还是某条路径触发？

### 1.2 Repro（最小复现）

- 能否用 **最小容器** 复现（`AnnotationConfigApplicationContext`/`GenericApplicationContext`）？
- 能否用本仓库 **对应 Lab** 复现同类机制边界？

### 1.3 Evidence（证据链）

- 选 1 个关键方法下断点 + 3 个观察点（watch list）
- 把“猜”变成“看见”：候选集合是什么？BPP 链顺序是什么？三层缓存状态是什么？

### 1.4 Decision（分流决策）

- 定义层问题？实例层问题？时机问题（过早实例化）？
- 顺序问题（谁包谁）？还是形态问题（early vs final）？

### 1.5 Fix（修复）

- 修复优先级：消除根因（设计/边界） > 改注入策略（Qualifier/Provider/Lazy） > 开关兜底（最后才用）

### 1.6 Verify（验证）

- 写一个可回归的最小测试（或在现有 Lab 中补断言）
- 再跑一遍主线回归（本模块 `mvn -pl :spring-core-beans test`）

---

## 1.7 误归因对照（生产最常见三错）

- **错因**：把“代理不生效”归因到配置没开  
  **纠正**：先看 BPP 链是否完整、bean 是否过早创建  

- **错因**：把 `NoUniqueBeanDefinition` 当成“自动装配坏了”  
  **纠正**：这是候选收敛规则没表达清楚（Qualifier/Primary/Priority）  

- **错因**：把 `@Value` 原样字符串当成“配置没加载”  
  **纠正**：优先确认 strict/non-strict resolver  

## 2. 常见事故分类（现象 → 证据链入口）

### 2.1 启动失败：`BeanDefinitionStoreException` / XML/Reader 相关

**Symptoms：**

- `BeanDefinitionStoreException`、`BeanDefinitionParsingException`、XML 解析失败等

**Evidence：**

- `AbstractApplicationContext#refresh`（定位阶段）
- `XmlBeanDefinitionReader#loadBeanDefinitions`（如果走 XML）
- `DefaultListableBeanFactory#registerBeanDefinition`（注册入口）

**Decision：**

- 定义层输入有误（XML/资源路径/占位符）还是 processor 改写造成冲突？

**Docs：**

- `part-01-ioc-container/02-bean-registration.md`
- `part-05-aot-and-real-world/42-xml-bean-definition-reader.md`

### 2.2 启动失败：`NoSuchBeanDefinitionException` / `NoUniqueBeanDefinitionException`

**Symptoms：**

- 缺 bean / 多候选歧义

**Evidence：**

- `DefaultListableBeanFactory#doResolveDependency`
- `findAutowireCandidates`（候选集合）
- `determineAutowireCandidate`（收敛规则）

**Docs：**

- `part-01-ioc-container/014-03-dependency-injection-resolution.md`
- `part-04-wiring-and-boundaries/33-autowire-candidate-selection-primary-priority-order.md`

### 2.3 代理不生效（事务/安全/缓存像没开）

**Symptoms：**

- 你“确定加了 AOP”，但调用路径像绕过代理

**Evidence：**

- `beanFactory.getBeanPostProcessors()`（BPP 链是否完整 & 顺序）
- `applyBeanPostProcessorsAfterInitialization`（代理/替换发生点）
- 是否存在“过早实例化”（bean 在 BPP 注册前就被创建）

**Decision：**

- 顺序问题（谁包谁） vs 时机问题（错过 BPP）

**Docs：**

- `part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md`
- `part-04-wiring-and-boundaries/25-programmatic-bpp-registration.md`

### 2.4 循环依赖 / 提前引用相关（启动失败或行为诡异）

**Symptoms：**

- `BeanCurrentlyInCreationException`
- 代理/增强后开始出现循环依赖相关异常

**Evidence：**

- `DefaultSingletonBeanRegistry#getSingleton`（三层缓存命中分支）
- `DefaultSingletonBeanRegistry#addSingletonFactory`（early exposure 起点）
- `AbstractAutowireCapableBeanFactory#getEarlyBeanReference`（early 形态决定）

**Decision：**

- constructor cycle（通常 fail-fast）还是 setter cycle（窗口期可救）？
- early/raw 与 final/proxy 是否一致？

**Docs：**

- `part-01-ioc-container/09-circular-dependencies.md`
- `part-03-container-internals/16-early-reference-and-circular.md`

### 2.5 `@Value` 值不对 / 缺失不失败 / 运行期才暴露

**Symptoms：**

- 值是 `"${missing}"` 原样字符串
- strict/non-strict 行为在不同环境不一致

**Evidence：**

- `AbstractBeanFactory#resolveEmbeddedValue`（解析输入/输出）
- `PropertySourcesPlaceholderConfigurer#postProcessBeanFactory`（strict 策略来源）

**Docs：**

- `part-04-wiring-and-boundaries/34-value-placeholder-resolution-strict-vs-non-strict.md`
- `part-05-aot-and-real-world/44-spel-and-value-expression.md`
- `part-04-wiring-and-boundaries/36-type-conversion-and-beanwrapper.md`

---

## 3. Debugger Pack：排障时的“第一入口”

如果你只记一个入口，记这个：

- `appendix/98-debugger-pack.md`

它把常见问题都压缩成“断点入口 + watch list + 对应 Lab”，适合作为生产排障的第一跳转页。

## 最短调用链（方法级）：把“Evidence”写成可执行路线

本页每个条目都给了 Evidence（方法名），但你真正落地排障时，需要把它组装成 3 步的最短调用链：

1) **定位阶段**：先在 `AbstractApplicationContext#refresh`（或启动异常栈顶）确认自己处在 refresh 的哪一段。
2) **锁定入口**：选择该现象的第一入口方法（例如 `doResolveDependency` / `getSingleton` / `resolveEmbeddedValue`）。
3) **看见关键数据结构**：候选 Map / 三层缓存 / embedded value 解析前后值 / BPP 链顺序。

你不需要追完整条链，只要能用 2–3 个方法把“阶段→分支→结论”连起来即可。

---

## 一句话自检

你应该能做到：

1) 任意一个 IoC 相关异常，先定位它属于 definition 还是 bean creation，再决定下哪个断点。
2) 解释“为什么这个断点能证明我的结论”（而不是碰巧）。
3) 用本仓库的 Lab 复现同类机制边界，并把修复方案固化成可回归验证。

<!-- BOOKIFY:START -->

上一章：[93. 面试复述模板（Interview Playbook）](93-interview-playbook.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[95. spring-beans Public API Index（索引）](95-spring-beans-public-api-index.md)

<!-- BOOKIFY:END -->
