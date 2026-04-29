# 章节深化路线（AOT & Real World）

## 定位：AOT & Real World 章节的深化方式

AOT & Real World 章节把定义层输入和构建期契约放到真实工程语境中：XML、Properties、Groovy、SpEL、RuntimeHints、外部对象注入和自定义 namespace。深化时要把“能在 JVM 跑”与“能在 AOT/Native 下成立”分开，并把输入解析错误归因到明确阶段。


## 官方文档对照（版本语境）

- Spring Framework：`6.2.x`（本仓库基线：`6.2.15`）
- Spring Boot：`3.5.9`

- Spring Framework Reference（AOT）：https://docs.spring.io/spring-framework/reference/core/aot.html
- Spring Framework Reference（Beans）：https://docs.spring.io/spring-framework/reference/core/beans.html
- Spring Framework Reference（SpEL）：https://docs.spring.io/spring-framework/reference/core/expressions.html
- Spring Framework Reference（Resources）：https://docs.spring.io/spring-framework/reference/core/resources.html


本部分的再加深重点，是把输入层解析和 AOT 契约做成可运行、可断言、可排障的工程知识，避免停留在 API 介绍。

## 执行化提示（Real World 的“可运行契约”）

- 输入层（XML/Properties/Groovy）章节优先补“错误分型 → 入口方法 → 断点 → 断言”，让读者能把异常归因到解析、注册或转换阶段。
- AOT 章节优先补“hints 作为可测试契约”的落地方式（registrar + 测试断言），避免只靠 native 失败再补。

### AOT / Native 总览：为什么 JVM 运行成功 ≠ Native 运行成功

- 文件：`spring-core-modules/spring-core-beans/docs/aot-aot-and-native-overview.md`
- 深化落点：
    - `SpringCoreBeansAotFactoriesLabTest`（再对照 `SpringCoreBeansAotRuntimeHintsLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `ApplicationContext#refresh` 进，到 `org.springframework.context.support.AbstractApplicationContext#refresh` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“常见误区与边界”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### RuntimeHints 入门：把构建期契约完成验证

- 文件：`spring-core-modules/spring-core-beans/docs/aot-runtimehints-basics.md`
- 深化落点：
    - `SpringCoreBeansAotRuntimeHintsLabTest`，把本章要解释的现象跑出来（能稳定复现）。
    - 从 `Class#getDeclaredMethods` 进，到 `Constructor#newInstance` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“5. 排障决策表（Native 异常 → 该补哪类 hints）”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### XML → BeanDefinitionReader：定义层解析与错误分型

- 文件：`spring-core-modules/spring-core-beans/docs/aot-xml-bean-definition-reader.md`
- 深化落点：
    - `SpringCoreBeansXmlBeanDefinitionReaderLabTest`，把本章要解释的现象跑出来（能稳定复现）。
    - 从 `DefaultListableBeanFactory#registerBeanDefinition` 进，到 `AbstractApplicationContext#refresh` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“常见误区与边界”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### 容器外对象注入：AutowireCapableBeanFactory

- 文件：`spring-core-modules/spring-core-beans/docs/aot-autowirecapablebeanfactory-external-objects.md`
- 深化落点：
    - `SpringCoreBeansAutowireCapableBeanFactoryLabTest`，把本章要解释的现象跑出来（能稳定复现）。
    - 从 `AbstractAutowireCapableBeanFactory#populateBean` 进，到 `AbstractAutowireCapableBeanFactory#initializeBean` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“常见误区与边界”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### SpEL 与 `@Value("#{...}")`：表达式解析链路

- 文件：`spring-core-modules/spring-core-beans/docs/aot-spel-and-value-expression.md`
- 深化落点：
    - `SpringCoreBeansSpelValueLabTest`（再对照 `SpringCoreBeansValuePlaceholderResolutionLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `AbstractBeanFactory#resolveEmbeddedValue` 进，到 `BeanFactory#resolveEmbeddedValue` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“3. 三连排障（按这张表完成分流）”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### 自定义 Qualifier：meta-annotation 与候选收敛

- 文件：`spring-core-modules/spring-core-beans/docs/aot-custom-qualifier-meta-annotation.md`
- 深化落点：
    - `SpringCoreBeansCustomQualifierLabTest`，把本章要解释的现象跑出来（能稳定复现）。
    - 从 `DefaultListableBeanFactory#findAutowireCandidates` 进，到 `DefaultListableBeanFactory#determineAutowireCandidate` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“常见误区与边界”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### XML namespace 扩展：NamespaceHandler / Parser / spring.handlers

- 文件：`spring-core-modules/spring-core-beans/docs/aot-xml-namespace-extension.md`
- 深化落点：
    - `SpringCoreBeansXmlNamespaceExtensionLabTest`，把本章要解释的现象跑出来（能稳定复现）。
    - 从 `DefaultListableBeanFactory#registerBeanDefinition` 进，到 `BeanDefinitionParserDelegate#parseCustomElement` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“常见误区与边界”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### BeanDefinitionReader：Properties / Groovy 等其他输入

- 文件：`spring-core-modules/spring-core-beans/docs/aot-beandefinitionreader-other-inputs-properties-groovy.md`
- 深化落点：
    - `SpringCoreBeansPropertiesBeanDefinitionReaderLabTest#propertiesBeanDefinitionReader_registersBeanDefinitions_fromPropertiesFile`（再对照 `SpringCoreBeansGroovyBeanDefinitionReaderLabTest#groovyBeanDefinitionReader_registersBeanDefinitions_fromGroovyScript`），把“现象差异”固定成可重复的断言/输出。
    - 从 `DefaultListableBeanFactory#registerBeanDefinition` 进，到 `SpringCoreBeansPropertiesBeanDefinitionReaderLabTest#propertiesBeanDefinitionReader_registersBeanDefinitions_fromPropertiesFile` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“常见误区与边界”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### 方法注入：replaced-method / MethodReplacer

- 文件：`spring-core-modules/spring-core-beans/docs/aot-method-injection-replaced-method.md`
- 深化落点：
    - `SpringCoreBeansReplacedMethodLabTest#replacedMethod_overridesTargetMethodViaCglibSubclassing_andIsVisibleInBeanDefinitionMethodOverrides`（再对照 `SpringCoreBeansReplacedMethodLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `AbstractAutowireCapableBeanFactory#instantiateWithMethodInjection` 进，到 `SpringCoreBeansReplacedMethodLabTest#replacedMethod_overridesTargetMethodViaCglibSubclassing_andIsVisibleInBeanDefinitionMethodOverrides` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“常见误区与边界”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### 内置 FactoryBean 图鉴

- 文件：`spring-core-modules/spring-core-beans/docs/aot-built-in-factorybeans-gallery.md`
- 深化落点：
    - `SpringCoreBeansBuiltInFactoryBeansLabTest`（再对照 `SpringCoreBeansServiceLoaderFactoryBeansLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `AbstractBeanFactory#getObjectForBeanInstance` 进，到 `AbstractAutowireCapableBeanFactory#getEarlyBeanReference` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“常见误区与边界”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。

### PropertyEditor 与值解析：值从定义层落到对象

- 文件：`spring-core-modules/spring-core-beans/docs/aot-property-editor-and-value-resolution.md`
- 深化落点：
    - `SpringCoreBeansBeanDefinitionValueResolutionLabTest`（再对照 `SpringCoreBeansPropertyEditorLabTest`），把“现象差异”固定成可重复的断言/输出。
    - 从 `BeanDefinitionValueResolver#resolveValueIfNecessary` 进，到 `CustomEditorConfigurer#postProcessBeanFactory` 看关键分支；用正文里给出的观察点（变量/对象/集合）判断当前命中的路径是否与结论一致。
  - 针对“0. `${...}` vs `#{...}` 的职责边界（先分清再排障）”时，把“误判点”收敛成更短的分流：现象 → 第一入口 → 关键分支 → 结论，按步骤验证。
