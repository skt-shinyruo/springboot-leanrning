# Technical Design: Spring Core Beans 学习闭环计划（First Pass）

## Technical Solution

本方案包原始目标是“学习任务清单”，根据反馈，本次不再生成额外 docs 闭环文件；保留本方案包用于后续复盘与追溯。

### Core Technologies
- Java 17 / Maven（单模块与单测试运行）
- Spring Framework IoC Container（`ApplicationContext` / `BeanFactory`）
- JUnit 5 + AssertJ（以断言固化“可复现结论”）
- IDE Debugger（断点/条件断点/表达式求值/Step Into）

### Implementation Key Points
1. **以测试驱动阅读**：先跑对应 Lab 观察现象，再读 docs 建立解释，最后用断点映射到容器主线（避免只背结论）。
2. **每个结论必须可验证**：至少包含 1 条断言（测试结果）+ 1 个观察点（断点/关键方法）。
3. **优先跑单测/单方法**：降低噪声，便于反复 Step Into/Step Over。
4. **建立“容器主线断点地图”**：统一从 `AbstractApplicationContext#refresh` 进入，再分叉到注册定义/后处理器/创建实例/early reference。

## Debugging Map（建议断点入口）

### 1) refresh 主线（从上往下建立阶段感）
- `org.springframework.context.support.AbstractApplicationContext#refresh`
- `org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors`
- `org.springframework.context.support.PostProcessorRegistrationDelegate#registerBeanPostProcessors`
- `org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons`

### 2) 创建/注入/初始化主线（把“生命周期”落到源码）
- `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`
- `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#populateBean`
- `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean`

### 3) 单例缓存与 early reference（循环依赖关键）
- `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton`
- `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#getEarlyBeanReference`

### 4) 代理/替换相关（最终暴露对象可能不是原始实例）
- `org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation`
- `org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization`

## Testing and Deployment

### Commands（只跑一个模块）
- 只跑模块：`mvn -pl spring-core-beans test`
- 只跑一个测试类：`mvn -pl spring-core-beans -Dtest=SpringCoreBeansContainerLabTest test`
- 只跑一个测试方法：`mvn -pl spring-core-beans -Dtest=SpringCoreBeansContainerLabTest#beanDefinitionIsNotTheBeanInstance test`
- 需要 IDE attach 时：`mvn -pl spring-core-beans -Dmaven.surefire.debug -Dtest=SpringCoreBeansContainerLabTest test`

## Security and Performance
- **Security:** 所有操作仅限本地代码与单元测试，不涉及生产环境/外部密钥/敏感数据。
- **Performance:** 优先使用 `-Dtest=...` 精准运行单测，减少上下文启动次数与调试等待时间。
