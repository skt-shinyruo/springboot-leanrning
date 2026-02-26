# Task List: spring-core-beans 覆盖 Spring Framework spring-beans 全机制（5 组补齐）

Directory: `helloagents/plan/202601052200_spring_core_beans_beans_package_full_coverage/`

---

## 1. 机制补齐（5 组闭环）

### 1.1 XML namespace 扩展闭环（NamespaceHandler/Parser）
- [√] 1.1.1 新增 XML namespace extension Lab（可断言）：`spring-core-beans/src/test/java/.../part05_aot_and_real_world/SpringCoreBeansXmlNamespaceExtensionLabTest.java`
- [√] 1.1.2 新增 test resources：`META-INF/spring.handlers` / `META-INF/spring.schemas` / XSD / demo XML（确保 classpath 可解析）
- [√] 1.1.3 新增 docs 章节：`docs/beans/spring-core-beans/part-05-aot-and-real-world/07-xml-namespace-extension.md`（主线/边界/误区/断点/观察点/复现入口）

### 1.2 BeanDefinitionReader 多输入源（Properties/Groovy）
- [√] 1.2.1 新增 PropertiesBeanDefinitionReader Lab：`SpringCoreBeansPropertiesBeanDefinitionReaderLabTest`
- [√] 1.2.2 新增 properties 定义资源并断言属性注入/引用解析
- [√] 1.2.3 新增 docs 章节：`08-beandefinitionreader-other-inputs-properties-groovy.md`
- [√] 1.2.4 （可选）GroovyBeanDefinitionReader：评估是否引入 test scope groovy 依赖；若引入则新增 Lab 并记录版本
  > Note: 已引入 `org.apache.groovy:groovy:4.0.21`（test scope）并新增 `SpringCoreBeansGroovyBeanDefinitionReaderLabTest`，确保 out-of-box 可运行。

### 1.3 replaced-method / MethodReplacer（方法注入）
- [√] 1.3.1 新增 replaced-method Lab（XML 或 programmatic BeanDefinition）：`SpringCoreBeansReplacedMethodLabTest`
- [√] 1.3.2 新增 docs 章节：`09-method-injection-replaced-method.md`（MethodOverrides/InstantiationStrategy/CGLIB 主线）

### 1.4 内置 FactoryBean 生态（代表性闭环）
- [√] 1.4.1 新增内置 FactoryBean Lab：`SpringCoreBeansBuiltInFactoryBeansLabTest`
  - 覆盖：`MethodInvokingFactoryBean`、`ServiceLocatorFactoryBean`、`&beanName` 语义与排障价值
- [√] 1.4.2 新增 docs 章节：`10-built-in-factorybeans-gallery.md`（常见内置 FactoryBean 清单 + 何时用/误区/断点）

### 1.5 PropertyEditor 与 BeanDefinition 值解析（值如何落到对象）
- [√] 1.5.1 新增 PropertyEditor Lab：`SpringCoreBeansPropertyEditorLabTest`（CustomEditorConfigurer/registrar）
- [√] 1.5.2 新增 BeanDefinitionValueResolver Lab：`SpringCoreBeansBeanDefinitionValueResolutionLabTest`（TypedStringValue/RuntimeBeanReference/Managed*）
- [√] 1.5.3 新增 docs 章节：`11-property-editor-and-value-resolution.md`（定义层 value → 实例层注入的主线）

## 2. 目录与检索收敛
- [√] 2.1 更新 `docs/beans/spring-core-beans/README.md`：把 46–50 加入 Part05 TOC，并在“快速定位/对照表”补齐入口
- [√] 2.2 更新 `docs/beans/spring-core-beans/appendix/03-knowledge-map.md`：补齐 5 组机制的检索路径（Concept → Chapter → Lab）

## 3. Security Check
- [√] 3.1 执行安全自检（G9）：确认无外联/无硬编码敏感信息/无破坏性命令；若新增依赖记录版本与风险

## 4. Knowledge Base Update
- [√] 4.1 更新 `helloagents/wiki/modules/spring-core-beans.md`：补齐“beans 全机制覆盖”与新增章节/Lab 入口
- [√] 4.2 更新 `helloagents/CHANGELOG.md`：记录新增的 5 组机制闭环

## 5. Verification
- [√] 5.1 运行 `mvn -pl spring-core-beans test`
- [√] 5.2 运行 `python3 scripts/check-md-relative-links.py docs/beans/spring-core-beans`

## 6. Migration
- [√] 6.1 将方案包迁移至 `helloagents/history/2026-01/202601052200_spring_core_beans_beans_package_full_coverage/` 并更新 `helloagents/history/index.md`
