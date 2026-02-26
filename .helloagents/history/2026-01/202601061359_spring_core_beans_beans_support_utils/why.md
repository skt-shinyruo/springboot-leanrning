# Change Proposal: spring-core-beans 补齐 `org.springframework.beans.support`（support 工具类）闭环

## Requirement Background

`spring-core-beans` 已经覆盖了 IoC/Bean 的主线机制（注册入口、注入解析、生命周期、BPP/BFPP、FactoryBean、循环依赖、代理、值解析与类型转换等），并通过 Appendix 95/96 引入了 `spring-beans` Public API 索引与 Gap 清单，确保“能审计还缺什么”。

当前缺口集中在 `org.springframework.beans.support` 这一组偏低层的 support 工具类（例如 `ArgumentConvertingMethodInvoker`、`ResourceEditorRegistrar`、`PagedListHolder`、`PropertyComparator` 等）。它们并不是容器主线的入口，但代表了 Spring 如何把 `BeanWrapper/TypeConverter/PropertyEditor` 这些能力抽象成可复用组件，理解后能让排障与源码阅读更连贯。

本变更的目标是把这组类型补成“可读 + 可跑 + 可断言”的最小闭环，并让 Appendix 96（Gap）归零，形成可审计的完成状态。

## Change Content

1. 扩写 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/19-type-conversion-and-beanwrapper.md`：
   - 增加 `org.springframework.beans.support` 小节，解释 support 工具类与 BeanWrapper/TypeConverter 的关系
   - 给出可运行 Lab 入口与推荐命令
2. 新增专门 Lab：覆盖 `org.springframework.beans.support` 关键类型的“可断言现象”
3. 更新索引生成器规则并重新生成 Appendix 95/96：将 beans.support 从 partial 提升为 core 覆盖
4. 同步 helloagents 知识库与变更记录（模块 wiki + changelog + history）

## Impact Scope

- **Modules:** `spring-core-beans`、`scripts`、`helloagents`
- **Docs:** `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/19-type-conversion-and-beanwrapper.md`
- **Tests:** 新增 `spring-core-beans/src/test/java/.../*LabTest.java`（默认参与回归）
- **Generated Docs:** `docs/beans/spring-core-beans/appendix/95`、`96` 将由脚本重新生成（不手工编辑）

## Core Scenarios

1. 能解释并断言：`ArgumentConvertingMethodInvoker` 如何在反射调用时执行参数转换（`convertIfNecessary`）
2. 能解释并断言：为什么 `Resource/File/URL/Path` 这类属性可以从字符串（`classpath:` 等）在属性填充阶段转换出来（`ResourceEditorRegistrar`）
3. 能解释并断言：按“嵌套属性路径”排序/分页的行为边界（`PropertyComparator`/`MutableSortDefinition`/`PagedListHolder`）

## Risk Assessment

- **Risk:** 低（仅新增/扩写文档与测试，不影响生产逻辑）
- **Mitigation:** 所有行为通过最小 Lab 可断言固定；Appendix 95/96 由脚本生成并校验路径存在性

