# 技术方案：spring-core-beans 覆盖 spring-beans Public API + 关键内部算法（B 目标）

## 技术方案概述

本次采用“**机制优先主线** + **全量 Public 类型索引**”的组合方案：

1. **机制优先主线（Curated Mechanism Domains）**  
   把 `spring-beans` 的能力拆成若干“机制域”，每个机制域都要落成：
   - docs（主线/边界/误区）
   - Lab（可复现/可断言）
   - 断点入口/观察点（可证明）

2. **全量 Public 类型索引（可生成、可审计）**  
   从 `spring-beans-6.2.15-sources.jar` 提取 public 类型清单，生成一个索引文档：
   - 按包分组列出 public 顶层类型（必要时扩展到 public nested types）
   - 为每个类型提供映射：所属机制域 → 章节 → Lab/Exercise/Solution（或 N/A 理由）
   - 提供“索引再生脚本”和“漂移检查（可选）”

## 核心技术点

### 1) Public API 清单提取（来源与规则）

- **输入来源：**本地 Maven 仓库 `spring-beans-6.2.15-sources.jar`
- **提取方式：**脚本化扫描 `.java` 源码，识别 public 顶层类型：
  - `public class|interface|enum|record`
  - 默认仅顶层；对“常见会被直接使用/调试”的 nested public 类型做二期扩展
- **输出：**Markdown（按包分组 + 类型列表），并为每个类型留出映射字段（chapter/lab）。

> 说明：索引的“可维护性”优先于“完美解析 Java 语法”。第一版以高置信规则覆盖顶层 public 类型即可；后续再把误差压到可接受范围。

### 2) 机制域（Mechanism Domains）划分（草案）

> 最终以 gap 审计结果为准，本草案用于任务拆分与批次规划。

1. BeanDefinition 模型与注册：`BeanDefinition/*Definition*`、`BeanDefinitionRegistry`
2. BeanFactory API 与层级：`BeanFactory/Listable/Hierarchical/Configurable*`
3. 单例注册表与缓存：`SingletonBeanRegistry/DefaultSingletonBeanRegistry`
4. 依赖解析与候选收敛：`DependencyDescriptor/AutowireCandidateResolver`、`doResolveDependency` 主线
5. 实例化与生命周期：`InstantiationAwareBPP/Smart*`、`doCreateBean/populate/initialize` 主线
6. 值解析与类型转换：`BeanWrapper/TypeConverter/ConversionService/PropertyEditor`、embedded value
7. FactoryBean 体系与 & 前缀：`FactoryBean`、对象类型推断与边界
8. XML/Reader/元数据输入：`BeanDefinitionReader`、XML/Properties/Groovy、Namespace 扩展
9. 注解基础设施与处理器：annotation processors（与 `spring-context` 的边界保持清晰）

### 3) “关键内部算法主线”的交付标准

每个算法主线都要给出：
- **调用链草图（call-chain sketch）**
- **关键分支/判定条件**
- **断点入口（推荐断点点位）**
- **观察点（变量/集合/缓存对象）**
- **对应 Lab：**能触发关键分支并断言结果（尽量避免只靠日志）

### 4) Tests 策略（你要求 1+2 都要）

- **Core Labs：**默认参与回归（JUnit 正常执行），覆盖主线与高频边界。
- **Explore/Debug：**提供深水断点用例（如缓存/性能/竞态类观察），默认不影响 CI：
  - 采用显式开关策略（例如 JUnit Tag + Surefire exclude 或 `@Disabled("manual")`）
  - docs 明确“何时开启/如何运行/观察什么”

## 安全与性能

- **安全：**不接触生产环境，不引入明文密钥，不做破坏性数据操作；所有脚本仅用于本地生成 docs 索引。
- **性能：**索引生成脚本需可在秒级/可接受时间完成；Explore 用例默认不进回归，避免拉长 CI。

每批次必须通过：
- `mvn -pl spring-core-beans test`
- `python3 scripts/check-md-relative-links.py docs/beans/spring-core-beans`

（可选增强）
- Public API 索引再生并与仓库内索引一致（漂移检查）

