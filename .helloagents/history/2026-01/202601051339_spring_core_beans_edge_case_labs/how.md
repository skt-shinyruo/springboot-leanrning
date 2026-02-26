# Technical Design: spring-core-beans 边界机制补齐（编程式注册 / raw injection / prototype 销毁语义）

## Technical Solution

### Core Technologies
- Java 17
- Spring Framework（通过 Spring Boot parent 管理）
- JUnit 5

### Implementation Key Points

1. **Programmatic Registration Lab**
   - 使用 `GenericApplicationContext`/`AnnotationConfigApplicationContext`
   - 对比三种入口：
     - `BeanDefinitionRegistry#registerBeanDefinition`（定义层）
     - `SingletonBeanRegistry#registerSingleton`（实例层）
     - `GenericApplicationContext#registerBean`（定义层 + Supplier）
   - 用一个稳定的 `BeanPostProcessor` 标记 bean 是否被处理，验证差异

2. **allowRawInjectionDespiteWrapping Lab**
   - 构造 setter 注入循环依赖（确保循环依赖机制可介入）
   - 提供一个“只在 afterInitialization 包裹、不提供 early reference”的 BPP
   - 对比两种行为：
     - 默认（false）：预期启动失败（保护不一致注入）
     - 允许（true）：启动成功但 dependent 持有 raw（引用不一致）

3. **Prototype Destroy Semantics Lab**
   - 定义 prototype bean 实现 `DisposableBean` 计数 destroy 次数
   - 断言 context close 不触发 destroy（默认行为）
   - 断言 `destroyBean` 可以手动触发 destroy（正确姿势）

## Security and Performance

- **Security:** 不涉及生产环境操作、不引入敏感信息；仅测试与文档增强
- **Performance:** 新增测试用例均为轻量级纯容器实验

## Testing and Deployment

- `mvn -pl spring-core-beans test`
- 方法级抽查：
  - `SpringCoreBeansProgrammaticRegistrationLabTest`
  - `SpringCoreBeansRawInjectionDespiteWrappingLabTest`
  - `SpringCoreBeansPrototypeDestroySemanticsLabTest`

