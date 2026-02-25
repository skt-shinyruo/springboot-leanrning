# 章节逐章补强建议（part-05-aot-and-real-world AOT 与真实世界）

说明：以下建议是按每个章节的主题与现有素材（入口方法/关键类型/对应实验）来给出，重点是让内容更“可复现、可讲述、可排障、可落地”。

### 第 24 章：40. AOT / Native 总览：为什么“JVM 能跑”不等于“Native 能跑”
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/01-aot-and-native-overview.md`
- 当前侧重点提示：入口: `RuntimeHintsRegistrar#registerHints`, `ApplicationContext#refresh`；类型: `AotServices`, `NoSuchMethod`；实验: `SpringCoreBeansAotRuntimeHintsLabTest`
- 补充与深化策略：
  - 补一张“AOT 从构建期到运行期的契约流转图”：`RuntimeHints` 的注册位置、生成产物、运行期消费点各自是什么，给一张总览图把链路闭环。
  - 把 `RuntimeHintsRegistrar#registerHints` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `AotServices`, `NoSuchMethod` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansAotRuntimeHintsLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 41. RuntimeHints 入门：把构建期契约跑通
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/02-runtimehints-basics.md`
- 当前侧重点提示：入口: `RuntimeHintsRegistrar#registerHints`, `Class#getDeclaredMethods`；类型: `RuntimeHints`, `RuntimeHintsRegistrar`；实验: `SpringCoreBeansAotRuntimeHintsLabTest`
- 补充与深化策略：
  - 补一张“AOT 从构建期到运行期的契约流转图”：`RuntimeHints` 的注册位置、生成产物、运行期消费点各自是什么，给一张总览图把链路闭环。
  - 把 `RuntimeHintsRegistrar#registerHints` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `RuntimeHints`, `RuntimeHintsRegistrar` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansAotRuntimeHintsLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 42. XML → BeanDefinitionReader：定义层解析与错误分型
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/03-xml-bean-definition-reader.md`
- 当前侧重点提示：入口: `XmlBeanDefinitionReader#loadBeanDefinitions`, `DefaultListableBeanFactory#registerBeanDefinition`；类型: `BeanDefinitionStoreException`, `BeanDefinition`；实验: `SpringCoreBeansXmlBeanDefinitionReaderLabTest`
- 补充与深化策略：
  - 补一张“AOT 从构建期到运行期的契约流转图”：`RuntimeHints` 的注册位置、生成产物、运行期消费点各自是什么，给一张总览图把链路闭环。
  - 把 `XmlBeanDefinitionReader#loadBeanDefinitions` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `BeanDefinitionStoreException`, `BeanDefinition` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansXmlBeanDefinitionReaderLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 43. 容器外对象注入：AutowireCapableBeanFactory
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/04-autowirecapablebeanfactory-external-objects.md`
- 当前侧重点提示：入口: `AutowireCapableBeanFactory#initializeBean`, `AutowireCapableBeanFactory#autowireBean`；实验: `SpringCoreBeansAutowireCapableBeanFactoryLabTest`
- 补充与深化策略：
  - 补一张“依赖解析候选筛选漏斗图”：从“按类型找到候选”→“@Primary/@Qualifier/@Priority/@Order 过滤/排序”→“泛型匹配与 @Resource name-first”逐层标注。
  - 把 `AutowireCapableBeanFactory#initializeBean` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 补“关键类型/接口地图”：把本章涉及的接口族（如 *Aware/*PostProcessor/*FactoryBean/*Scope 等）按“参与时机”分组，并给出每组最常见的误用方式。
  - 把本章与 `SpringCoreBeansAutowireCapableBeanFactoryLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“生产排障映射”：把 `NoSuchBeanDefinitionException` / `NoUniqueBeanDefinitionException` / `UnsatisfiedDependencyException` 的典型日志片段与本章规则逐条对照，给出 3 步排查路径。
### 44. SpEL 与 `@Value("#{...}")`：表达式解析链路
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/05-spel-and-value-expression.md`
- 当前侧重点提示：入口: `AbstractBeanFactory#resolveEmbeddedValue`, `StandardBeanExpressionResolver#evaluate`；类型: `PropertySourcesPlaceholderConfigurer`, `NumberFormatException`；实验: `SpringCoreBeansSpelValueLabTest`
- 补充与深化策略：
  - 补一张“AOT 从构建期到运行期的契约流转图”：`RuntimeHints` 的注册位置、生成产物、运行期消费点各自是什么，给一张总览图把链路闭环。
  - 把 `AbstractBeanFactory#resolveEmbeddedValue` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `PropertySourcesPlaceholderConfigurer`, `NumberFormatException` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansSpelValueLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 45. 自定义 Qualifier：meta-annotation 与候选收敛
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/06-custom-qualifier-meta-annotation.md`
- 当前侧重点提示：入口: `QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate`, `DefaultListableBeanFactory#determineAutowireCandidate`；类型: `DependencyDescriptor`, `NoUniqueBeanDefinitionException`；实验: `SpringCoreBeansCustomQualifierLabTest`
- 补充与深化策略：
  - 补一张“依赖解析候选筛选漏斗图”：从“按类型找到候选”→“@Primary/@Qualifier/@Priority/@Order 过滤/排序”→“泛型匹配与 @Resource name-first”逐层标注。
  - 把 `QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `DependencyDescriptor`, `NoUniqueBeanDefinitionException` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansCustomQualifierLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 46. XML namespace 扩展：NamespaceHandler / Parser / spring.handlers
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/07-xml-namespace-extension.md`
- 当前侧重点提示：入口: `BeanDefinitionParser#parse`, `BeanDefinitionParserDelegate#parseCustomElement`；类型: `BeanDefinitionParser`, `NamespaceHandler`；实验: `SpringCoreBeansXmlNamespaceExtensionLabTest`
- 补充与深化策略：
  - 补一张“AOT 从构建期到运行期的契约流转图”：`RuntimeHints` 的注册位置、生成产物、运行期消费点各自是什么，给一张总览图把链路闭环。
  - 把 `BeanDefinitionParser#parse` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `BeanDefinitionParser`, `NamespaceHandler` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansXmlNamespaceExtensionLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 47. BeanDefinitionReader：除了注解与 XML，还有 Properties / Groovy
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/08-beandefinitionreader-other-inputs-properties-groovy.md`
- 当前侧重点提示：入口: `AbstractBeanDefinitionReader#loadBeanDefinitions`, `DefaultListableBeanFactory#registerBeanDefinition`；类型: `GroovyBeanDefinitionReader`, `BeanDefinition`；实验: `SpringCoreBeansGroovyBeanDefinitionReaderLabTest`
- 补充与深化策略：
  - 补一张“AOT 从构建期到运行期的契约流转图”：`RuntimeHints` 的注册位置、生成产物、运行期消费点各自是什么，给一张总览图把链路闭环。
  - 把 `AbstractBeanDefinitionReader#loadBeanDefinitions` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `GroovyBeanDefinitionReader`, `BeanDefinition` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansGroovyBeanDefinitionReaderLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 48. 方法注入（Method Injection）：replaced-method / MethodReplacer
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/09-method-injection-replaced-method.md`
- 当前侧重点提示：入口: `AbstractAutowireCapableBeanFactory#instantiateWithMethodInjection`, `MethodReplacer#reimplement`；类型: `MethodReplacer`, `MethodOverrides`；实验: `SpringCoreBeansReplacedMethodLabTest`
- 补充与深化策略：
  - 补一张“依赖解析候选筛选漏斗图”：从“按类型找到候选”→“@Primary/@Qualifier/@Priority/@Order 过滤/排序”→“泛型匹配与 @Resource name-first”逐层标注。
  - 把 `AbstractAutowireCapableBeanFactory#instantiateWithMethodInjection` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `MethodReplacer`, `MethodOverrides` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansReplacedMethodLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“生产排障映射”：把 `NoSuchBeanDefinitionException` / `NoUniqueBeanDefinitionException` / `UnsatisfiedDependencyException` 的典型日志片段与本章规则逐条对照，给出 3 步排查路径。
### 49. 内置 FactoryBean 图鉴：MethodInvoking / ServiceLocator / & 前缀
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/10-built-in-factorybeans-gallery.md`
- 当前侧重点提示：入口: `AbstractBeanFactory#getObjectForBeanInstance`, `FactoryBeanRegistrySupport#getObjectFromFactoryBean`；类型: `MethodInvokingFactoryBean`, `ServiceLocatorFactoryBean`；实验: `SpringCoreBeansBuiltInFactoryBeansLabTest`
- 补充与深化策略：
  - 补一张“AOT 从构建期到运行期的契约流转图”：`RuntimeHints` 的注册位置、生成产物、运行期消费点各自是什么，给一张总览图把链路闭环。
  - 把 `AbstractBeanFactory#getObjectForBeanInstance` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `MethodInvokingFactoryBean`, `ServiceLocatorFactoryBean` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansBuiltInFactoryBeansLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
### 50. PropertyEditor 与 BeanDefinition 值解析：值从定义层落到对象
- 📍 文件：`spring-core-modules/spring-core-beans/docs/part-05-aot-and-real-world/11-property-editor-and-value-resolution.md`
- 当前侧重点提示：入口: `BeanDefinitionValueResolver#resolveValueIfNecessary`, `AbstractAutowireCapableBeanFactory#applyPropertyValues`；类型: `RuntimeBeanReference`, `TypedStringValue`；实验: `SpringCoreBeansBeanDefinitionValueResolutionLabTest`
- 补充与深化策略：
  - 补一张“AOT 从构建期到运行期的契约流转图”：`RuntimeHints` 的注册位置、生成产物、运行期消费点各自是什么，给一张总览图把链路闭环。
  - 把 `BeanDefinitionValueResolver#resolveValueIfNecessary` 的关键分支补成“最短调用链 + 分支条件清单”：只追到发生决策的 if/return，明确每个分支对应的现象（例如“为什么会提前返回/为什么会创建新实例/为什么会走 parent factory”）。
  - 围绕 `RuntimeBeanReference`, `TypedStringValue` 补一个“职责边界对照”：分别负责什么、不负责什么；以及它们在本章主问题里的交互点在哪一行/哪一个回调阶段。
  - 把本章与 `SpringCoreBeansBeanDefinitionValueResolutionLabTest` 的对应关系写得更“可复现”：明确“跑这个测试会看到什么日志/断点停在哪里/变量应该是什么值”，并补一个反例用来验证边界。
  - 补“现象 → 章节 → 断点”快速定位：列 3~5 个最常见现象（报错或怪异行为），说明如何判断它属于本章范围，以及第一断点建议下在哪里。
