# Task List: Spring Core Beans 学习闭环计划（First Pass）

Directory: `helloagents/history/2026-01/202601030641_spring-core-beans-first-pass/`

---

说明：
- 本方案包是“学习任务清单（First Pass）”，用于个人/团队按 Lab 逐步完成；根据反馈，本次不再生成额外 docs 闭环文件。

## 1. spring-core-beans（第一轮：把“概念”落到“主线 + 观察点”）

- [-] 1.1 跑 `SpringCoreBeansContainerLabTest#beanDefinitionIsNotTheBeanInstance`，写下你对“BeanDefinition vs instance”的 2 句解释（必须包含：定义层/实例层），验证 why.md#scenario-prove-definition--instance-lab--debugger
- [-] 1.2 从 `AbstractApplicationContext#refresh` 下断点，逐步进入 BFPP/BPP 注册与执行位置，画出你自己的 6 步 refresh 粗粒度流程图（1 行/步即可），验证 why.md#scenario-observe-definitioninstance-mutation-points
- [-] 1.3 阅读 `docs/beans/spring-core-beans/01-bean-registration.md`，跑 `SpringCoreBeansBootstrapInternalsLabTest`，列出 3 个“BeanDefinition 来源入口”并各写一句定位方法，验证 why.md#scenario-trace-where-beandefinition-comes-from
- [-] 1.4 跑 `SpringCoreBeansInjectionAmbiguityLabTest`，用 `@Primary` 与 `@Qualifier` 各修复一次歧义并解释差异，验证 why.md#scenario-resolve-ambiguity-deterministically
- [-] 1.5 跑 `SpringCoreBeansAutowireCandidateSelectionLabTest`，回答：`@Order/@Priority/@Primary` 哪些影响“候选选择”，哪些只影响“排序/链路”，验证 why.md#scenario-resolve-ambiguity-deterministically
- [-] 1.6 跑 `SpringCoreBeansLabTest` 中 prototype 相关用例，解释“prototype 注入 singleton 看起来像单例”的根因，并给出 1 个修复方案，验证 why.md#scenario-explain-prototype-injected-into-singleton
- [-] 1.7 跑 `SpringCoreBeansLifecycleCallbackOrderLabTest`，写下 `@PostConstruct` 触发时机与 prototype 销毁语义（各 1 句），验证 why.md#scenario-verify-callback-timing-and-order
- [-] 1.8 跑 `SpringCoreBeansPostProcessorOrderingLabTest` 与 `SpringCoreBeansRegistryPostProcessorLabTest`，总结 BDRPP/BFPP/BPP 各自“改什么/什么时候改”，验证 why.md#scenario-observe-definitioninstance-mutation-points
- [-] 1.9 跑 `SpringCoreBeansEarlyReferenceLabTest`，回答：哪类循环依赖可能被 early reference 缓解？构造器循环依赖为何通常无解？验证 why.md#scenario-reproduce-and-explain-early-exposure
- [-] 1.10 跑 `SpringCoreBeansExceptionNavigationLabTest` / `SpringCoreBeansBeanGraphDebugLabTest`，按异常信息找到 1 个你认为“最有效”的断点入口，并解释为什么，验证 why.md#scenario-locate-root-cause-via-breakpoint-map

## 2. Security Check
- [-] 2.1 确认所有操作仅限本地测试与阅读，不连接生产环境、不写入任何密钥/Token/个人敏感信息（per G9）

## 3. Testing
- [-] 3.1 只跑关键用例验证（推荐）：`mvn -pl spring-core-beans -Dtest=SpringCoreBeansContainerLabTest test`
- [-] 3.2 完整回归（可选）：`mvn -pl spring-core-beans test`
