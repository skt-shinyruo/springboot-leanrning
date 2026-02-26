# Technical Design: spring-core-beans 深化（PostProcessors + 容器启动基础设施）源码解析补强

## Technical Solution

### Core Technologies
- Spring Framework 容器主线：`AbstractApplicationContext#refresh`
- Post-processor 主线：`PostProcessorRegistrationDelegate`
  - `invokeBeanFactoryPostProcessors`
  - `registerBeanPostProcessors`
- Bean 创建主线：`AbstractAutowireCapableBeanFactory#doCreateBean` / `initializeBean`
- Annotation processors：`AnnotationConfigUtils#registerAnnotationConfigProcessors`
  - `ConfigurationClassPostProcessor`
  - `AutowiredAnnotationBeanPostProcessor`
  - `CommonAnnotationBeanPostProcessor`
- JUnit 5 + AssertJ：以“事件顺序/是否被处理”作为稳定断言

### Documentation Writing Rules（写法约束）
1. **不粘贴大段 Spring Framework 原始源码**：采用“方法名 + 调用链 + 分叉逻辑 + 精简伪代码 + 关键状态”的表达。
2. **必要时新增仓库 `src/` 代码**：当一个关键结论缺少“最小可运行复现”时，通过新增 `src/test/java` 的 Lab 来补齐。
3. **代码片段最小化**：文档只展示 5~30 行级别的关键片段；其余通过文件路径与测试名定位。
4. **断言优先于日志**：示例尽量用事件列表/状态位断言“是否发生”，避免依赖输出文本。

## Implementation Key Points

### 1) docs/06（post-processors）补强点
- 增加 `invokeBeanFactoryPostProcessors` 的算法级解释：
  - 先跑手工注册 processors
  - BDRPP 先 registry 后 factory，且需要“反复扫描”
  - BFPP 再执行，按 PriorityOrdered/Ordered/无序分组
- 增加 `registerBeanPostProcessors` 的算法级解释：
  - BPP 本身也是 bean（注册阶段会实例化 BPP）
  - 说明 “BeanPostProcessorChecker” 类问题的根因：某些 bean 被提前创建，无法再被后续 BPP 处理
- 增加 “static @Bean for post-processors” 的可复现解释，并落地为新增 Lab

### 2) docs/12（container bootstrap）补强点
- 增加 `AnnotationConfigApplicationContext` 的默认装配链路：
  - 构造器/reader 如何触发 `registerAnnotationConfigProcessors`
  - internal processors 先作为 BeanDefinition 进入 registry
  - 随 refresh 的 `registerBeanPostProcessors` 进入 beanFactory 的 BPP 列表并参与实例链路
- 补齐“定义层 vs 实例层”的排障结论：为什么“进 registry”不等于“已经生效”

## Testing
- 运行：`mvn -pl spring-core-beans test`
- 只跑新增 Lab（若需要）：`mvn -pl spring-core-beans -Dtest=<NewLabTest> test`

## Security and Performance
- **Security:** 不涉及外部服务、密钥、生产环境
- **Performance:** 新增实验使用小上下文（`GenericApplicationContext` / 最小 config），避免启动完整 Boot 应用

