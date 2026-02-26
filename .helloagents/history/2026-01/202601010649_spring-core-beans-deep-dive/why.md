# 变更提案：spring-core-beans 文档深化 + Labs/Exercises（源码级）

## 需求背景

当前 `spring-core-beans` 的目标是“讲透 Bean/容器”，但存在两个核心问题：

1. **深度与闭环不足：** 用户看完文档后，仍难以把概念映射到 `refresh()/createBean()/resolveDependency()` 等关键路径，更难在真实报错里定位原因。
2. **“文档 ↔ Labs ↔ 断点入口”的映射不够系统：** 模块已经具备大量 Labs/Exercises，但部分章节缺少“从结论 → 断点 → 可断言观察点 → 对应测试方法”的闭环指引，读者很难把知识沉淀成稳定的排障能力。

本次变更要把模块升级为：**源码级心智模型 + 可运行验证闭环**。

## 变更内容

1. 扩写并重构 `docs/beans/spring-core-beans/` 中的核心章节，使其覆盖“启动时间线、注入解析、生命周期、后处理器/代理、循环依赖、@Configuration 增强、FactoryBean”这些关键机制，并给出断点入口与观察点。
2. 补齐 `spring-core-beans` 的可运行最小示例与 Labs/Exercises（`src/main/java` + `src/test/java`），让每个关键机制都能被稳定复现与断言。
3. 清理/修正文档中对不存在代码的引用，使“README → docs → Labs”形成一致的学习路径。

## 影响范围

- **Modules:** `spring-core-beans`
- **Files:** `docs/beans/spring-core-beans/*.md`、`spring-core-beans/README.md`、`spring-core-beans/src/main/java/**`、`spring-core-beans/src/test/java/**`
- **APIs:** 无对外 API 变更
- **Data:** 无数据模型变更

## 核心场景

### Requirement: 源码级理解 Bean（面向读者 C）
**Module:** spring-core-beans
学习目标是“能解释 + 能断点 + 能定位问题”，并且每一条结论可通过 Labs/Exercises 验证。

#### Scenario: 能复述容器启动主线（refresh 时间线）
- 用户能按阶段解释 `refresh()` 的关键步骤与“每一步在干什么”
- 用户能通过 Lab 在断点里观察 BFPP/BPP/单例实例化与顺序

#### Scenario: 能从注入报错反推候选选择过程（@Primary/@Qualifier/集合注入）
- 用户能从 `NoUniqueBeanDefinitionException` 等报错反推出候选筛选逻辑
- 用户能用 Lab 验证：@Primary、@Qualifier、名称匹配、集合注入排序与 ObjectProvider

#### Scenario: 能讲清循环依赖边界（含代理介入）
- 用户能解释三层缓存与 early reference 的语义
- 用户能区分：构造器循环为何失败、setter 循环为何可能成功、代理为什么会改变 early reference 行为

#### Scenario: 能把 Bean 三层模型映射到关键类与扩展点
- 用户能把 BeanDefinition/实例/生命周期三层，映射到关键参与者与扩展点（BFPP/BPP/BDRPP）
- 用户能在 Lab 中“看见”这些对象何时出现、何时被合并/替换/增强

## 风险评估

- **风险:** 主要是学习型代码与文档规模增加，可能引入构建失败或示例误导
- **Mitigation:** 每个机制用可断言测试兜底；文档以代码为事实来源；变更后运行单模块测试作为最低验证门槛
