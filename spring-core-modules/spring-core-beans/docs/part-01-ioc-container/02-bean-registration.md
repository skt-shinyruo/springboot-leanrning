# 02. Bean 注册入口：扫描、`@Bean`、`@Import`、Registrar

本章解决一个“看似简单但会影响一切”的问题：

> **BeanDefinition 从哪里来？**  
> 你能解释“为什么这个 Bean 在容器里 / 为什么不在 / 为什么名字是这样 / 为什么顺序是这样”吗？

如果你把 Spring 的 IoC 容器当成一个“黑盒自动注入器”，你在真实项目里一定会在这些场景翻车：

- 明明写了类/注解，但容器里就是找不到（注册根本没发生）。
- 同一个接口出现多个候选者，不知道“是谁先注册/谁覆盖/谁被排除”（定义层的问题，而不是注入层）。
- 以为 `@Import` “只是引入一个配置类”，却不知道它还能动态选路、批量注册、甚至按环境决定导入什么。

---

## 你将观察到什么（What you will observe）

读完并跑通本章，你应该能用“可验证证据”回答：

1. **扫描（Component Scan）**：哪些类会变成 BeanDefinition？BeanName 是谁生成的？
2. **`@Configuration + @Bean`**：`@Bean` 方法对应的 BeanDefinition 是什么时候注册的？是否会被增强（CGLIB）？
3. **`@Import` 家族**：`@Import` 导入的类，走的是“直接导入”还是 “ImportSelector/Registrar” 的动态路径？
4. **编程式注册**：`registerBeanDefinition/registerBean` 注册进去的定义是否会参与完整创建流水线（BPP/生命周期/代理）？

---

## 5 分钟验证（Lab：先把注册跑起来）

> 目标：先把“定义从哪里来”跑出来，再回头读源码主线。

### 1) Component Scan（扫描）

```bash
mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansComponentScanLabTest test
```

你应该观察到：
- 扫描会把候选组件转成 `BeanDefinition` 并注册到 `BeanDefinitionRegistry`。
- BeanName 可能来自：显式 name / `BeanNameGenerator` / 默认类名规则。

### 2) `@Import`（导入/动态选路/批量注册）

```bash
mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansImportLabTest test
```

你应该观察到：
- `@Import(普通类)`：把该类当成配置/组件候选进行处理（不是“直接 new”）。
- `ImportSelector`：按条件返回要导入的类名列表（是“选路器”）。
- `ImportBeanDefinitionRegistrar`：可以直接往 registry 里塞定义（是“批量注册器”）。

### 3) 进阶：编程式注册（定义层 vs 外部对象）

（本章只需要知道“它存在且很常用”；细节见 Part 04 相关章节。）

```bash
mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansProgrammaticRegistrationLabTest test
```

---

## 最小心智模型：注册发生在“定义层”

你可以把容器分成两层：

1) **定义层（Definition Layer）**：把“将来怎么创建 Bean”的元数据放进 registry  
2) **实例层（Instance Layer）**：在 `getBean()` 或容器启动阶段，把定义变成实例，并经历注入/回调/代理

本章只聚焦第一层：**BeanDefinitionRegistry**。

关键对象：

- `BeanDefinitionRegistry`：存放与管理 BeanDefinition（本质是一个“定义表”）
- `BeanDefinition`：描述“怎么创建 Bean”的元数据（class、scope、依赖、属性、factoryMethod…）
- `BeanNameGenerator`：决定 beanName（这个名字会影响注入、覆盖、alias、调试）

---

## 四类注册入口（你需要能分辨“我现在在哪条路上”）

### 1) 扫描（`@ComponentScan` / `scan()`）

适用：你希望按 package 批量发现并注册组件（`@Component/@Service/...`）。

主线（概念图）：

```
scan(basePackages)
 -> ClassPathBeanDefinitionScanner#doScan
 -> findCandidateComponents
 -> BeanDefinitionReaderUtils#registerBeanDefinition
 -> registry.beanDefinitionMap.put(beanName, beanDefinition)
```

关键分支：
- 是否命中组件过滤器（include/exclude）
- BeanName 生成策略（默认 vs 自定义）
- 条件注解（`@Conditional`）不会在“扫描阶段”就决定最终创建，它会在后续解析/注册/匹配阶段继续参与

### 2) `@Configuration + @Bean`（配置类解析）

适用：你希望用显式配置来声明 Bean，并希望这些 Bean 能被容器的扩展点完整处理。

主线（概念图）：

```
refresh()
 -> invokeBeanFactoryPostProcessors
 -> ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry
 -> parse @Configuration classes
 -> register @Bean method definitions
```

关键分支：
- `@Configuration(proxyBeanMethods=true/false)`：是否需要 CGLIB 增强（直接影响“同一 @Bean 方法多次调用是否返回同一实例”的语义）
- Lite mode（只有 `@Bean`，但不是 full `@Configuration`）的边界：很多“我以为会被增强”的误解都来自这里

### 3) `@Import` 家族（导入/选路/批量注册）

适用：模块化配置、条件化导入、框架扩展（Spring Boot 自动配置、各种 starter 的核心机制都依赖它）。

你至少要分清三种形态：

1) `@Import(普通类/配置类)`：相当于把“另一个配置类”加入解析集合  
2) `@Import(ImportSelector)`：运行时计算“要导入哪些类”（动态选路）  
3) `@Import(ImportBeanDefinitionRegistrar)`：直接向 registry 注册多个 BeanDefinition（批量注入定义）

主线（概念图）：

```
ConfigurationClassParser#processImports
 -> (direct) add config class
 -> (selector) ImportSelector#selectImports
 -> (registrar) ImportBeanDefinitionRegistrar#registerBeanDefinitions
```

关键分支：
- `DeferredImportSelector`：延迟到“所有配置类解析完”再选路（典型：Boot 的 AutoConfigurationImportSelector）
- Selector/Registrar 是否实现 *Aware 接口（EnvironmentAware/ResourceLoaderAware…）——这决定它拿到什么上下文信息

### 4) 编程式注册（Registry API）

适用：需要“更可控”的动态注册；或者你在写框架/中间件，必须直接操作 registry。

你需要分清两个常被混用的入口：

- `registerBeanDefinition(name, beanDefinition)`：注册“定义”（后续由容器创建/注入/回调）
- `registerBean(name, Supplier)`：仍然是“定义层”注册，只是把创建逻辑交给 Supplier（依然参与 BPP/生命周期）

> 反例：把外部对象直接塞进容器（例如把某个对象当作 singleton instance 注册）会绕开部分创建流水线——这类边界在 Part 04 会单独讲。

---

## 断点与观察点（Debugger Entry & Watchpoints）

> 建议你用 `SpringCoreBeansImportLabTest` 作为载体来打断点（最容易看见“解析 → 选路 → 注册”全过程）。

推荐断点（至少打 2 个）：

- `ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry`（配置类解析入口）
- `ConfigurationClassParser#processImports`（`@Import` 主入口）
- `ImportSelector#selectImports`（动态选路点）
- `ImportBeanDefinitionRegistrar#registerBeanDefinitions`（批量注册点）
- `BeanDefinitionReaderUtils#registerBeanDefinition`（真正落库的统一入口）

推荐观察点（至少盯 3 个变量）：

- `BeanDefinitionRegistry#getBeanDefinitionNames()`（注册了哪些定义）
- `BeanDefinition#getBeanClassName()` / `getFactoryMethodName()`（定义来自 class 还是 factory method）
- `BeanDefinition#getRole()` / `isInfrastructure`（是否是基础设施 bean：processor 等）

---

## 常见坑（Pitfalls：现象 → 根因 → 怎么验证）

1) **现象：写了 `@Component`，但容器里找不到**
- 根因：扫描基包没覆盖 / include/exclude filter 把它排了 / 根本没触发 scan
- 验证：在 `ClassPathBeanDefinitionScanner#doScan` 看候选集合是否包含你的类

2) **现象：`@Import` 导入不生效**
- 根因：导入发生在配置类解析阶段，你的类不是配置类/没被纳入解析集合
- 验证：在 `ConfigurationClassParser#processImports` 看当前 config class 的 imports 列表

3) **现象：`@Bean` 方法看起来被调用多次，像是产生了多个实例**
- 根因：`proxyBeanMethods=false` 或 Lite mode 导致“没有 CGLIB 增强”，`@Bean` 方法调用变成普通方法调用
- 验证：在 `ConfigurationClassEnhancer#enhance` 看是否增强了配置类；或在调用栈确认是否走了代理子类

---

## 练习（Exercises：做完你就真的会了）

对应练习：`SpringCoreBeansImportExerciseTest`（默认 `@Disabled`）。

建议你完成两件事：

1) 扩展 `@Import`：同时导入两个配置类，并写断言证明每个 Bean 的来源  
2) 让 `ImportSelector` 同时依赖两个属性（例如 mode + enabled），并写断言覆盖两个分支

---

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab（扫描）：[`SpringCoreBeansComponentScanLabTest`](../../src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansComponentScanLabTest.java)
  - `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansComponentScanLabTest test`
- Lab（导入）：[`SpringCoreBeansImportLabTest`](../../src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansImportLabTest.java)
  - `mvn -q -pl :spring-core-beans -Dtest=SpringCoreBeansImportLabTest test`
- Exercise：[`SpringCoreBeansImportExerciseTest`](../../src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansImportExerciseTest.java)
- Solution：[`SpringCoreBeansImportExerciseSolutionTest`](../../src/test/java/com/learning/springboot/springcorebeans/part01_ioc_container/SpringCoreBeansImportExerciseSolutionTest.java)

上一章：[01. Bean 心智模型：Bean 是“定义 + 实例 + 生命周期 +（可能）代理/包装”](020-01-bean-mental-model.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[03. 依赖注入解析：`resolveDependency` 的关键分支](014-03-dependency-injection-resolution.md)

<!-- BOOKIFY:END -->
