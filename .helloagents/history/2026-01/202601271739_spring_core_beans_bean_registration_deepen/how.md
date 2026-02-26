# Technical Design: 深化 Bean 注册入口教程（02-bean-registration）

## Technical Solution

### Core Technologies
- Spring Framework 6.x（随 Spring Boot BOM）
- Markdown 文档（docs/）
- Maven Surefire + 既有 LabTests（用于证据链闭环）

### Implementation Key Points

- 在 `01-bean-registration.md` 中新增三类“教学友好”结构块：
  1) 入口对照表（入口→对象→调用链→断点→坑→Lab）
  2) 证据链脚本（命令→断点→watch list→结论）
  3) 面试/内训复述模板（高频问法+答题结构）
- 以稳定锚点组织源码链路：
  - 定义层落点：`DefaultListableBeanFactory#registerBeanDefinition`
  - 注解解析入口：`ConfigurationClassPostProcessor#processConfigBeanDefinitions`
  - 扫描入口：`ClassPathBeanDefinitionScanner#doScan`
  - @Import 扩展点：`ImportSelector` / `ImportBeanDefinitionRegistrar`
- 增强排障分流：强调“定义层是否存在”与“实例是否存在”的不同验证方式（containsBeanDefinition/containsSingleton）。
- 不新增依赖、不调整测试逻辑，仅复用现有 LabTests 作为可运行入口。

## Security and Performance

- **Security:** 不引入任何密钥/生产地址；文档示例保持本地可运行、无外部网络依赖。
- **Performance:** 不新增默认回归测试；文档变更不影响构建性能。

## Testing and Deployment

- **Testing:** 跑模块回归（或至少跑本章入口 LabTest），确保无误改路径/断链导致的构建失败。
- **Deployment:** None

