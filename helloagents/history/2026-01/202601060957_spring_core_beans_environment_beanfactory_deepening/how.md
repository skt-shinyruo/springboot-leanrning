# Technical Design: Environment Abstraction & BeanFactory API deepening

## Scope & Principles

- 保持现状版本：不升级 Spring/Boot。
- 优先使用最小可复现实验：`AnnotationConfigApplicationContext` / `GenericApplicationContext` + 内存 PropertySource。
- 每章输出：复现入口（可运行）+ 断点入口 + 观察点（watch list）+ 常见误区。

## Docs placement

- `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/21-environment-and-propertysource.md`
  - 覆盖：Environment 接口族谱、PropertySource 抽象、PropertySource 优先级、@PropertySource 的进入链路、占位符解析与 BFPP 的关系
  - 关联已有章节：34（placeholder）、36（type conversion）、10/11（Boot 排障中的 Environment）

- `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/22-beanfactory-api-deep-dive.md`
  - 覆盖：BeanFactory 接口族谱（BeanFactory/Listable/Hierarchical/AutowireCapable/Configurable*）、为什么 plain BeanFactory 没有“自动处理注解”的能力、如何手动 bootstrap（addBeanPostProcessor）以及排障观察点
  - 关联已有内容：01（容器心智模型）、12（注解基础设施 bootstrap）、06/14（post-processors 时机/排序）、43（AutowireCapableBeanFactory）

## Labs

### Lab 1: Environment / PropertySource precedence

新增：`SpringCoreBeansEnvironmentPropertySourceLabTest`

- Case A：`@PropertySource` 提供默认值
- Case B：在 refresh 前插入 `MapPropertySource` 并覆盖同名 key，验证优先级
- Case C：展示 `Environment#getProperty` 与 `@Value("${...}")` 解析一致性（同时给出断点路径）

### Lab 2: BeanFactory API + manual bootstrap boundary

新增：`SpringCoreBeansBeanFactoryApiLabTest`

- Case A：纯 `DefaultListableBeanFactory` 创建 bean，不自动处理 `@Autowired` field / `@PostConstruct`
- Case B：手动 `addBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor(..))` 与 `CommonAnnotationBeanPostProcessor` 后，再创建同样 bean，断言注入与回调发生
- 同时展示 ListableBeanFactory 的枚举能力（getBeanNamesForType / getBeansOfType）

## Debugger entrypoints & watchpoints（写入 docs）

### Environment

- Entrypoint:
  - `ConfigurableEnvironment#getPropertySources`
  - `PropertySourcesPropertyResolver#getProperty`
- Placeholder path:
  - `AbstractBeanFactory#resolveEmbeddedValue`
  - `PropertySourcesPlaceholderConfigurer#postProcessBeanFactory`（若需要 strict/自定义行为）
- Watchpoints:
  - `propertySources` 顺序（谁在前谁覆盖）
  - `key` 与 `resolved value`

### BeanFactory

- Entrypoint:
  - `AbstractBeanFactory#doGetBean`
  - `DefaultListableBeanFactory#doResolveDependency`
- Post-processor boundary:
  - `AbstractAutowireCapableBeanFactory#populateBean`
  - `AutowiredAnnotationBeanPostProcessor#postProcessProperties`
  - `CommonAnnotationBeanPostProcessor#postProcessProperties`
- Watchpoints:
  - `beanPostProcessors` 列表（plain vs 手动添加）
  - `beanName` / `mbd` / `resolvedDependency`

