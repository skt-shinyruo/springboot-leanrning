# 逐章内容级再加深建议（part-05-aot-and-real-world）

本 Part 的再加深重点：把“输入层解析 + AOT 契约”做成可运行、可断言、可排障的工程知识，避免只停留在 API 介绍。

## 执行化提示（Real World 的“可运行契约”）

- 输入层（XML/Properties/Groovy）章节：优先补“错误分型 → 入口方法 → 断点 → 断言”，让读者能把异常归因到解析/注册/转换阶段。
- AOT 章节：优先补“hints 作为可测试契约”的落地方式（registrar + 测试断言），避免只靠 native 失败再补。

### 40. AOT / Native 总览：为什么 JVM 运行成功 ≠ Native 运行成功

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/024-40-aot-and-native-overview.md`
- 内容级加深策略：
  - A：补“失败分型→缺口类型”的证据链：反射/代理/资源/序列化分别对应什么提示。
  - B：补反例：盲目全量放开反射的风险（安全/体积/可维护性）。
  - C：补排障 SOP：如何从 native 异常快速归类并定位到要补的 hints。
  - D：补观察点：RuntimeHints 的类别与注册入口如何观察到。
  - E：补面试追问：为什么 RuntimeHints 是“可测试的契约”？如何证明。

### 41. RuntimeHints 入门：把构建期契约完成验证

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/41-runtimehints-basics.md`
- 内容级加深策略：
  - A：补“Registrar 注册→测试断言”的证据链范式（把契约钉死）。
  - B：补反例：把 hints 当 JSON 配置到处贴导致漂移；过度开放反射导致安全面扩大。
  - C：补排障：反射/代理/资源缺失三类异常如何映射到 hints 类型。
  - D：补断点：registerHints 与 hints 写入点（reflection/resources/proxies）的观察方法。
  - E：补面试追问：为什么推荐 registrar + 单测，而不是靠 native 打包失败再补？

### 42. XML → BeanDefinitionReader：定义层解析与错误分型

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/42-xml-bean-definition-reader.md`
- 内容级加深策略：
  - A：补“Resource→Reader→registerBeanDefinition”的最短证据链与关键入口。
  - B：补反例：schema 不匹配/namespace 扩展缺失/属性类型转换失败的误判对照。
  - C：补排障 SOP：把 BeanDefinitionStoreException 分型到解析/注册/转换哪个阶段。
  - D：补断点：loadBeanDefinitions、doRegisterBeanDefinitions、注册入口。
  - E：补面试追问：XML 解析与注解解析最终为什么都落到 BeanDefinition？如何证明。

### 43. 容器外对象注入：AutowireCapableBeanFactory

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/43-autowirecapablebeanfactory-external-objects.md`
- 内容级加深策略：
  - A：补“外部对象注入的能力边界”证据链：能做什么、不能做什么（生命周期/代理/销毁）。
  - B：补反例：误以为外部对象等同容器托管导致的资源泄漏与代理不生效。
  - C：补排障：外部对象注入后行为不符合预期时如何定位到“没走哪条容器主线”。
  - D：补断点：autowireBean/initializeBean/applyBeanPostProcessors 的调用路径对照。
  - E：补面试追问：什么时候应该用它，什么时候应该重构为容器托管？

### 44. SpEL 与 `@Value("#{...}")`：表达式解析链路

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/44-spel-and-value-expression.md`
- 内容级加深策略：
  - A：补“解析→求值→注入”的证据链与关键入口方法，并与 ${} 占位符对照。
  - B：补反例：表达式注入风险、把 SpEL 与占位符混用导致误诊。
  - C：补排障 SOP：表达式失败如何定位是 parser/上下文/变量/类型转换哪一环。
  - D：补断点：SpEL parser、evaluation context、value injection 分支。
  - E：补面试追问：为什么 SpEL 在某些场景是危险的？如何给出安全建议。

### 45. 自定义 Qualifier：meta-annotation 与候选收敛

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/45-custom-qualifier-meta-annotation.md`
- 内容级加深策略：
  - A：补“Qualifier 决策发生点”的证据链：最终由哪个 resolver 判定命中。
  - B：补反例：多个 Qualifier 叠加、meta 嵌套过深导致可读性差与误命中。
  - C：补排障：Qualifier 不生效/命中错对象时如何定位到 resolver 的判定过程。
  - D：补断点：candidate resolver、qualifier match 入口与关键变量。
  - E：补面试追问：为什么推荐用 meta-annotation 而不是字符串 qualifier？优势与风险是什么。

### 46. XML namespace 扩展：NamespaceHandler / Parser / spring.handlers

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/46-xml-namespace-extension.md`
- 内容级加深策略：
  - A：补“namespace resolution→handler→parser→BeanDefinition”的证据链。
  - B：补反例：spring.handlers 缺失、schemaLocation 错误、parser 抛错的分型。
  - C：补排障 SOP：namespace 解析失败如何定位到 handler 加载/资源缺失/解析异常。
  - D：补断点：NamespaceHandlerResolver、handler mapping 加载点、parse 入口。
  - E：补面试追问：XML 扩展机制与注解扩展机制（processor）有何异同？

### 47. BeanDefinitionReader：Properties / Groovy 等其他输入

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/47-beandefinitionreader-other-inputs-properties-groovy.md`
- 内容级加深策略：
  - A：补“输入层对比”并强调共同落点：最终都落到 BeanDefinition 与注册表。
  - B：补反例：格式错误/类型转换失败/引用不存在的分型。
  - C：补排障：输入层失败如何快速定位到 reader 与 value resolver。
  - D：补断点：reader 入口、registerBeanDefinition、值解析入口。
  - E：补面试追问：为什么 Spring 能支持多输入？核心抽象是什么？

### 48. 方法注入：replaced-method / MethodReplacer

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/48-method-injection-replaced-method.md`
- 内容级加深策略：
  - A：补“它如何实现”的证据链：CGLIB 子类与方法拦截发生点。
  - B：补反例：final 限制、代理叠加、AOT 下的限制与 hint 需求。
  - C：补排障：方法注入不生效/行为偏差如何定位到代理生成与拦截器。
  - D：补断点：子类生成、方法拦截、目标解析入口。
  - E：补面试追问：@Lookup 与 replaced-method 的差异与选择策略。

### 49. 内置 FactoryBean 图鉴

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/49-built-in-factorybeans-gallery.md`
- 内容级加深策略：
  - A：按“行为模型”补证据链：反射调用型/服务定位型/代理生成型分别在哪个窗口替换最终对象。
  - B：补反例：把它们当普通 bean 导致的类型误判与调试困难。
  - C：补排障：看到某个内置 FactoryBean 时如何判断最终暴露对象、以及按类型发现的边界。
  - D：补断点：FactoryBean product 获取与缓存命中点。
  - E：补面试追问：为什么内置 FactoryBean 很常见？它们解决了什么抽象问题？

### 50. PropertyEditor 与值解析：值从定义层落到对象

- 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/50-property-editor-and-value-resolution.md`
- 内容级加深策略：
  - A：补“BeanDefinitionValueResolver→convertIfNecessary”的完整证据链，并与占位符/SpEL/转换三连对齐。
  - B：补反例：值看似已解析，但根因在于占位符未解析；editor 与 converter 混用导致行为不一致。
  - C：补排障 SOP：TypeMismatch/BeanCreationException 的分型定位（解析/求值/转换）。
  - D：补断点：value resolver、property value 应用、TypeConverterDelegate 转换路径。
  - E：补面试追问：PropertyEditor 的历史定位与为何仍会在某些路径出现。
