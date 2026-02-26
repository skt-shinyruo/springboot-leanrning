# Technical Design: spring-core-beans 覆盖 Spring Framework spring-beans 全机制（5 组补齐）

## Technical Solution

### Core Technologies
- Java 17 + Spring Boot 3.5.9（间接带 Spring Framework 6.2.x）
- spring-core-beans 模块的 docs（Markdown）+ Labs（JUnit5）
- 文档校验：`scripts/check-md-relative-links.py`

### Implementation Key Points

- **统一闭环标准（每组机制至少一个可运行闭环）：**
  - docs：主线（发生在哪个阶段/产物是什么）+ 边界/误区 + 断点入口 + 观察点 + 复现入口
  - Lab：可运行、可断言、可下断点（避免依赖长日志）
  - Exercise/Solution：仅对“适合练习”的主题提供（Exercise 默认 @Disabled，Solution 默认参与回归）

- **文件布局策略：**
  - docs：补充放入 `docs/beans/spring-core-beans/part-05-aot-and-real-world/`，保持“真实世界补齐/遗留机制”语义
  - tests：新增 `spring-core-beans/src/test/java/.../part05_aot_and_real_world/*LabTest.java`
  - resources：XML、XSD、spring.handlers/schemas 放入 `spring-core-beans/src/test/resources/`，避免污染主 artifact

- **每组机制对应的最小闭环实现：**
  1) XML namespace 扩展：自定义 NamespaceHandler + Parser + XSD + spring.handlers/schemas + 最小 XML
  2) PropertiesBeanDefinitionReader：`.properties` 定义 + reader load + getBean 断言
     - GroovyBeanDefinitionReader：优先不引入依赖；若用户确认需要 full out-of-box，则再引入 test scope groovy 并补齐 Lab
  3) replaced-method：用 XML `<replaced-method>` 或 programmatic `ReplaceOverride` 复现 method injection
  4) 内置 FactoryBeans：MethodInvokingFactoryBean（method->value）+ ServiceLocatorFactoryBean（service locator）
  5) PropertyEditor & 值解析：CustomEditorConfigurer/PropertyEditorRegistrar + BeanDefinitionValueResolver 断点链路

## Security and Performance

- **Security:**
  - 不引入任何生产环境外部连接；所有实验均为 in-memory context。
  - 若新增第三方依赖（例如 Groovy test scope），必须固定版本并记录变更；避免动态版本漂移。
- **Performance:** Labs 必须保持轻量（启动最小化的 `GenericApplicationContext`/`AnnotationConfigApplicationContext`），避免全量 SpringBootTest。

## Testing and Deployment

- **Testing:**
  - `mvn -pl spring-core-beans test`
  - `python3 scripts/check-md-relative-links.py docs/beans/spring-core-beans`
- **Deployment:** 无部署变更（教学仓库内容与 tests 增量）。

