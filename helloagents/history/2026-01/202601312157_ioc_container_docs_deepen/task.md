# Task List: part-01-ioc-container 文档深度完善

Directory: `helloagents/plan/202601312157_ioc_container_docs_deepen/`

> **Status:** 未执行（遗留方案归档；已由后续执行包覆盖）

---

## 1. spring-core-beans docs / part-01-ioc-container

验收口径（本章节任务通用）：
- 每章按 `helloagents/plan/202601312157_ioc_container_docs_deepen/how.md` 的「段落级深化清单（可直接写入章节）」逐段落地（可根据章节结构调整落位，但不遗漏关键段落意图）
- 每章补充内容形成“概念 → 源码主线 → 可验证实验 → 排错闭环”的最短链路（不追求统一模板，也不追求固定模块覆盖）
- 本次范围内不保留“未完/TODO/FIXME”占位

- [-] 1.1.1 深化 Bean 心智模型章节内容：新增“世界观三件套/两条主线/扩展点插槽/异常定位”相关段落（why.md#requirement-020-01-bean-心智模型读者建立容器的世界观-scenario-读者能把问题定位到主线阶段），更新 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/020-01-bean-mental-model.md`
- [-] 1.1.2 增补 Bean 心智模型章节可验证抓手：补充断点地图与 Lab 引用（`SpringCoreBeansBeanFactoryVsApplicationContextLabTest` / `SpringCoreBeansBeanGraphDebugLabTest` 等），并加入必要跨章跳转，更新 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/020-01-bean-mental-model.md`
- [-] 1.2.1 深化 Bean 注册章节内容：补强“入口全景图/注册时点/覆盖与冲突/反查来源路径”（why.md#requirement-02-bean-注册读者理解beandefinition-从哪里来-scenario-读者能选择合适的注册方式并解释代价），更新 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/02-bean-registration.md`
- [-] 1.2.2 增补 Bean 注册章节可验证抓手：补充断点（`registerBeanDefinition` / `ConfigurationClassPostProcessor` / scanner 等）与 Lab 引用（`SpringCoreBeansBeanDefinitionRegistrationDiffLabTest` 等），更新 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/02-bean-registration.md`
- [-] 1.3.1 深化依赖注入解析章节内容：补强“注入点画像/候选收集/歧义收敛/集合与 Provider 语义差异”（why.md#requirement-014-03-依赖注入解析读者掌握候选人怎么选出来-scenario-读者能把注入歧义变成可调试的决策树），更新 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/014-03-dependency-injection-resolution.md`
- [-] 1.3.2 增补依赖注入解析章节可验证抓手：补充断点（`resolveDependency` / `findAutowireCandidates` / `determineAutowireCandidate`）与 Lab 引用（`SpringCoreBeansDependencyDescriptorMetadataLabTest`），更新 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/014-03-dependency-injection-resolution.md`
- [-] 1.4.1 深化 Scope 与 Prototype 章节内容：补强“prototype 的创建/注入/销毁边界、Provider/@Lookup 对比、scopedTarget 可见证据”（why.md#requirement-015-04-scope-与-prototype读者理解生命周期边界-scenario-读者能解释-prototype-注入-singleton-的真实语义），更新 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/015-04-scope-and-prototype.md`
- [-] 1.4.2 增补 Scope 与 Prototype 章节可验证抓手：补充断点（`doGetBean` / `getSingleton` / scoped proxy 生成点）与跨章跳转（part-04 的 custom scope 等），更新 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/015-04-scope-and-prototype.md`
- [-] 1.5.1 深化生命周期与回调章节内容：补强“顺序主线/多入口对照/代理介入导致差异”（why.md#requirement-016-05-生命周期与回调读者掌握生命周期顺序与插槽-scenario-读者能定位并修复生命周期顺序问题），更新 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/016-05-lifecycle-and-callbacks.md`
- [-] 1.5.2 增补生命周期与回调章节可验证抓手：补充断点（`initializeBean` / `CommonAnnotationBeanPostProcessor` / `DisposableBeanAdapter`）与 Lab 引用（`SpringCoreBeansLifecycleRawVsProxyLabTest`），更新 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/016-05-lifecycle-and-callbacks.md`
- [-] 1.6.1 深化后置处理器章节内容：补强“分类边界/时序主线/排序误区/为什么没生效”（why.md#requirement-017-06-后置处理器读者理解容器可编程能力的核心-scenario-读者能解释为什么某个后置处理器没生效生效太晚），更新 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/017-06-post-processors.md`
- [-] 1.6.2 清理后置处理器章节未完占位：移除“未完”标记并补齐对应内容，同时补充验证入口（`SpringCoreBeansEarlyGetBeanMissesBppLabTest`），更新 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/017-06-post-processors.md`
- [-] 1.7.1 深化配置类增强章节内容：补强“full/lite 语义、proxyBeanMethods 取舍、与循环依赖关联”（why.md#requirement-018-07-配置类增强读者理解configuration-的代理语义-scenario-读者能正确选择-proxybeanmethods-并避免语义坑），更新 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/018-07-configuration-enhancement.md`
- [-] 1.7.2 增补配置类增强章节可验证抓手：补充增强触发点与增强后类名可见证据、建议断点（`ConfigurationClassPostProcessor` / `ConfigurationClassEnhancer`），更新 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/018-07-configuration-enhancement.md`
- [-] 1.8.1 深化 FactoryBean 章节内容：补强“两套身份/& 前缀/产物缓存/getObjectType-isSingleton 语义/边界案例”（why.md#requirement-08-factorybean读者理解工厂产物-vs-工厂本身-scenario-读者能解释拿到的是-factorybean-还是它生产的对象），更新 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/08-factorybean.md`
- [-] 1.8.2 增补 FactoryBean 章节可验证抓手：补充断点（`doGetBean` 与 `FactoryBeanRegistrySupport`）与 Lab 引用（`SpringCoreBeansFactoryBeanEdgeCasesLabTest`），更新 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/08-factorybean.md`
- [-] 1.9.1 深化循环依赖章节内容：补强“边界矩阵/三级缓存主线/开关风险/可落地解环方案”（why.md#requirement-09-循环依赖读者掌握三级缓存与早期引用边界-scenario-读者能给出可落地的解环方案），更新 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/09-circular-dependencies.md`
- [-] 1.9.2 清理循环依赖章节未完占位：移除“未完”标记并补齐对应内容，同时补充断点（`doCreateBean` / `getSingleton` / `getEarlyBeanReference`）与 Lab 引用（`SpringCoreBeansCircularDependencyBoundaryLabTest`），更新 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/09-circular-dependencies.md`

## 2. Quality Verification（文档一致性与可用性）

- [-] 2.1 进行相对链接/锚点自检：重点关注 part-01 目录内跳转与跨目录跳转（建议使用脚本扫描 Markdown `[]()` 链接目标是否存在）
- [-] 2.2 自检“未完/TODO/FIXME”残留：确保本次范围内不再出现占位标记

## 3. Security Check

- [-] 3.1 执行安全自检（G9）：确认新增文档不包含密钥/token/内网地址/个人信息；外链优先官方与源码仓库

## 4. Knowledge Base Sync

- [-] 4.1 同步更新模块知识库：更新 `helloagents/wiki/modules/spring-core-beans.md`（记录本次 part-01 的增强范围与关键变化）
- [-] 4.2 更新变更记录：更新 `helloagents/CHANGELOG.md`

## 5. Verification（可运行性）

- [-] 5.1 运行并确保 Lab 可用（可选但推荐）：执行 `mvn -pl spring-core-modules/spring-core-beans test`，确认相关 `*LabTest` 仍可运行
