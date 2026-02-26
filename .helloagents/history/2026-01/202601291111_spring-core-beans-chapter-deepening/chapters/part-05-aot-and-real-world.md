# 逐章补强建议（part-05-aot-and-real-world AOT/真实世界输入）

本 Part 的补强重点：把“容器的输入/解析/AOT 限制”讲成可操作的工程知识，尤其是把 Native/AOT 的失败模式分型并落到 RuntimeHints 的可运行验证。

### 第 24 章：40. AOT / Native 总览：为什么“JVM 能跑”不等于“Native 能跑”

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/01-aot-and-native-overview.md`
- 补强策略：
  - 增加“失败分型”清单：反射缺失、动态代理、资源缺失、序列化等典型问题，并给出“如何定位到缺哪个 hint”的路径。
  - 串联到容器机制：哪些容器能力天然依赖反射/代理（FactoryBean、SpEL、Method Injection），为什么 AOT 需要显式契约。
  - 增补“适用版本与工具链”提示：避免读者把结论套用到不同版本/不同 native 工具链上。

### 41. RuntimeHints 入门：把构建期契约跑通

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/02-runtimehints-basics.md`
- 补强策略：
  - 增加“Hints 类型地图”：reflection/proxy/resource/serialization 等，分别适用于哪些 Spring 场景。
  - 增补“最小可运行验证”建议：规划一个小实验（或现有实验补强）来证明 hint 缺失会失败、补上会成功。
  - 串联 FactoryBean/SpEL/方法注入章节：让读者能把 hints 写到具体机制上，而不是停留在 API 层。

### 42. XML → BeanDefinitionReader：定义层解析与错误分型

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/03-xml-bean-definition-reader.md`
- 补强策略：
  - 强化“解析链路证据链”：从 Resource → DocumentReader → BeanDefinition 注册的关键窗口与入口方法。
  - 增补“错误分型”：schema 不匹配、属性类型转换失败、引用不存在、namespace 扩展缺失等，并给出排障入口。
  - 串联 namespace 扩展章节（46）：让读者能从“解析失败”快速跳到“扩展机制”。

### 43. 容器外对象注入：AutowireCapableBeanFactory

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/04-autowirecapablebeanfactory-external-objects.md`
- 补强策略：
  - 明确适用场景：第三方对象、手动 new 的对象、测试场景等，并说明其生命周期边界（不等于完整托管）。
  - 增补“常见误区”：以为能获得完整 AOP/生命周期/销毁管理，实际哪些能力需要显式调用或根本不会发生。
  - 串联 BeanFactory API 章节：把 external injection 放在容器能力边界的大图里理解。

### 44. SpEL 与 `@Value("#{...}")`：表达式解析链路

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/05-spel-and-value-expression.md`
- 补强策略：
  - 把表达式链路拆成“解析 → 求值 → 注入”三段，并给出每段的关键入口与可观察变量。
  - 增补“安全提示”：SpEL 作为表达式语言在某些场景存在注入风险，建议在文档中给出安全边界与工程实践。
  - 串联占位符解析（34）：清晰对比 `${...}` 与 `#{...}` 的差异，避免读者混用导致排障困难。

### 45. 自定义 Qualifier：meta-annotation 与候选收敛

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/06-custom-qualifier-meta-annotation.md`
- 补强策略：
  - 补齐“自定义 Qualifier 设计指南”：如何定义 annotation、如何选择属性、如何与 @Qualifier(value=) 互操作。
  - 加强与依赖解析章节的连接：让读者知道 Qualifier 最终由哪个 resolver 决策，并能在断点里看见匹配过程。
  - 增补边界条件：多个 Qualifier 叠加、meta-annotation 嵌套、重复候选时的行为与排障入口。

### 46. XML namespace 扩展：NamespaceHandler / Parser / spring.handlers

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/07-xml-namespace-extension.md`
- 补强策略：
  - 强化“从 XML 到 BeanDefinition”的扩展点链路：namespace resolution、handler 加载、parser 注册的关键入口。
  - 增补“排障分型”：spring.handlers 配置缺失、schemaLocation 不匹配、parser 抛异常等，并给出第一观察点。
  - 可选：规划一个最小 namespace 扩展示例（文档或测试），让读者能实际跑通一次扩展流程。

### 47. BeanDefinitionReader：除了注解与 XML，还有 Properties / Groovy

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/08-beandefinitionreader-other-inputs-properties-groovy.md`
- 补强策略：
  - 增加“输入层对比表”：注解/XML/Properties/Groovy 各自的优势、限制、适用场景以及落到 BeanDefinition 的共同点。
  - 补齐“错误分型与排障入口”：Properties 格式错误、类型转换失败、引用不存在等。
  - 串联“输入层（inputs）→ 定义层（definitions）”心智模型（链接 Part 01 的心智模型/注册入口章）。

### 48. 方法注入（Method Injection）：replaced-method / MethodReplacer

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/09-method-injection-replaced-method.md`
- 补强策略：
  - 补齐“为什么需要方法注入”的历史背景与替代方案：例如 `@Lookup`、ObjectProvider、显式工厂等。
  - 增补“它如何实现”的证据链：CGLIB 子类/方法拦截的关键入口与边界（final 方法/类限制等）。
  - 串联 AOT：方法注入对 Native 的影响与 hints/代理的要求，给出工程注意事项。

### 49. 内置 FactoryBean 图鉴：MethodInvoking / ServiceLocator / & 前缀

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/10-built-in-factorybeans-gallery.md`
- 补强策略：
  - 按“行为模型”而非列表堆叠来组织：反射调用型、服务定位型、代理生成型等，并解释它们在容器主线中的出现窗口。
  - 为每类给出“调试识别点”：如何从 beanName、类型、FactoryBean product 的差异快速判断你拿到的是什么。
  - 串联 FactoryBean 深潜与边界章：把图鉴变成可复用的排障工具，而不是知识点集合。

### 50. PropertyEditor 与 BeanDefinition 值解析：值从定义层落到对象

- 关联文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/11-property-editor-and-value-resolution.md`
- 补强策略：
  - 讲透“值解析链路”：BeanDefinitionValueResolver、TypedStringValue、占位符/SpEL、类型转换之间的关系，并能用断点串起来。
  - 增补“PropertyEditor vs ConversionService”的边界与迁移建议：在什么地方仍会遇到 editor，如何替换为 converter。
  - 串联占位符/Environment 章节：让读者能从“值不对/类型不对/解析失败”快速定位到具体链路节点。

