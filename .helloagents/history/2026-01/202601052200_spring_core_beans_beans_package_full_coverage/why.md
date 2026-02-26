# Change Proposal: spring-core-beans 覆盖 Spring Framework spring-beans 全机制（5 组补齐）

## Requirement Background

当前 `spring-core-beans` 已能覆盖 IoC/DI/生命周期/扩展点/代理/循环依赖/AOT/排障等主线，并形成“docs → Lab → 断点 → 观察点”的教学闭环。

但如果把范围扩大到 **Spring Framework `spring-beans` 包的全部机制**，仍有 5 组典型机制缺少系统化闭环（原因通常是：这些机制在现代 Boot 项目里不常“手写”，但在排障/源码理解时会频繁出现）：

1) XML 生态的扩展机制（自定义 XML namespace：NamespaceHandler/Parser + spring.handlers/schemas）
2) BeanDefinitionReader 的其它输入源（Properties/Groovy 等）
3) 方法注入的另一半：replaced-method / MethodReplacer（以及 MethodOverrides/InstantiationStrategy）
4) 内置 FactoryBean 生态（MethodInvokingFactoryBean/ServiceLocatorFactoryBean 等）
5) 属性填充与类型转换的可插拔细节（PropertyEditor 体系 + BeanDefinitionValueResolver/值解析主线）

本变更目标：把上述 5 组机制按统一标准补齐到仓库，使读者能达到“知道是什么 → 会用 → 能解释主线与边界 → 能下断点验证”的源码级理解。

## Change Content

1. 新增/补强 docs 章节：按统一结构输出“主线/边界/误区/断点入口/观察点/复现入口”
2. 新增可运行 Labs：每组机制至少 1 个可断言 Lab（默认参与回归）
3. （可选）新增 Exercises/Solutions：对适合练习的主题提供 Exercise stub 与 Solution（保持回归稳定）
4. 更新 docs/README：收敛“章节 ↔ Lab ↔ Exercise ↔ Solution”入口，确保可检索与可跳读
5. 同步 HelloAGENTS 知识库与变更记录：wiki/modules + CHANGELOG + history 归档

## Impact Scope

- **Modules:** spring-core-beans, helloagents
- **Files:** 新增/修改 spring-core-beans 的 docs、tests、resources；更新 helloagents/wiki 与 CHANGELOG/history
- **APIs:** None
- **Data:** None

## Core Scenarios

### Requirement: XML namespace 扩展闭环（beans xml extension）
**Module:** spring-core-beans
读者能理解并复现：自定义 XML namespace 如何从 XML 元素解析到 BeanDefinition 注册。

#### Scenario: 能定位自定义 namespace 的解析入口与产物
读者能通过 Lab 复现并在断点中观察：
- `META-INF/spring.handlers` 如何映射 namespace URI → NamespaceHandler
- `META-INF/spring.schemas` 如何映射 XSD URL → classpath resource
- `BeanDefinitionParserDelegate#parseCustomElement` 如何把自定义 XML 元素解析成 BeanDefinition 并注册

### Requirement: BeanDefinitionReader 多输入源（Properties/Groovy）
**Module:** spring-core-beans
读者能理解并复现：不同 reader 如何把“输入源”统一落到 BeanDefinitionRegistry。

#### Scenario: properties 定义如何变成 BeanDefinition
- 能用 `PropertiesBeanDefinitionReader` 加载 `.properties` 定义
- 能断言：BeanDefinition 元信息/属性注入结果

#### Scenario: groovy DSL 如何变成 BeanDefinition（可选依赖）
- 能用 `GroovyBeanDefinitionReader` 加载 `.groovy` 定义
- 能断言：BeanDefinition/bean 实例存在与行为

### Requirement: replaced-method / MethodReplacer 方法注入
**Module:** spring-core-beans
读者能理解并复现：为什么/何时 Spring 会走“CGLIB 子类化 + 方法拦截”的实例化分支。

#### Scenario: replaced-method 的行为与源码主线
能复现并解释：
- `MethodOverrides` 如何存在于 BeanDefinition（定义层）
- `InstantiationStrategy` 何时切到 method injection 分支
- `MethodReplacer#reimplement` 如何接管方法调用

### Requirement: 内置 FactoryBean 生态（可运行闭环）
**Module:** spring-core-beans
读者能理解并复现：内置 FactoryBean 如何“把配置变成对象”，以及 `&beanName` 的意义与排障价值。

#### Scenario: MethodInvokingFactoryBean / ServiceLocatorFactoryBean 的典型用法
能断言：
- `getBean("x")` 返回 product，`getBean("&x")` 返回 factory
- service locator 模式如何做到“每次调用时再去容器查找”

### Requirement: PropertyEditor 与 BeanDefinition 值解析主线
**Module:** spring-core-beans
读者能理解并复现：值如何从“字符串/引用/集合”落到真实对象，并能解释 PropertyEditor 在哪些位置介入。

#### Scenario: 自定义 PropertyEditor 影响属性填充/转换
能复现：
- 未注册 editor 时注入失败/行为不符合预期
- 注册 editor 后注入成功，且能断点证明触发点

#### Scenario: BeanDefinitionValueResolver 如何解析 RuntimeBeanReference/TypedStringValue
能复现并断言：
- 引用解析、集合/Map/Properties 解析
- 解析发生在 `applyPropertyValues` 主线内

## Risk Assessment

- **Risk:** 新增 XML namespace/reader 相关资源与测试，可能带来 classpath 资源冲突或解析失败。
  - **Mitigation:** namespace URI 采用项目内唯一值；resources 放在 `src/test/resources`，避免影响生产包；加入断言校验并跑全量测试。
- **Risk:** 若引入 Groovy 作为 test scope 依赖，可能带来体积与安全风险。
  - **Mitigation:** 优先不引入或采用“可选 Lab（@Disabled）+ 明确依赖说明”策略；若引入则固定版本并记录到 CHANGELOG/知识库。

