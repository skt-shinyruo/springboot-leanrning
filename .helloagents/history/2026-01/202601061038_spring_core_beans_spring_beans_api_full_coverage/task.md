# Task List: spring-core-beans 覆盖 spring-beans Public API + 关键内部算法（只做 B）

Directory: `helloagents/history/2026-01/202601061038_spring_core_beans_spring_beans_api_full_coverage/`

---

## 0. 基线与约束确认（保持现状版本）

- [√] 0.1 确认 Spring Framework 版本（当前为 6.2.15），并在本方案中明确“不升级版本”的约束
- [√] 0.2 建立“分批交付”节奏：每批次结束必须通过 tests + docs 链接检查（见 7/8）

## 1. Public API 索引（可生成、可审计）

> 目标：把“Public API 全覆盖”做成可检索、可维护的索引，而不是隐含约定。

- [√] 1.1 新增索引生成脚本（从 `spring-beans-6.2.15-sources.jar` 提取 public 顶层类型清单），输出到 `docs/beans/spring-core-beans/appendix/`（新增脚本文件 1 个）
- [√] 1.2 新增 `spring-beans Public API Index` 文档（按包分组 + 类型列表 + 映射字段：机制域/章节/Lab/（可选）Exercise/Solution）（新增 docs 文件 1 个）
- [√] 1.3 在 `docs/beans/spring-core-beans/README.md` 的 Appendix 区加入索引入口（修改 1 个文件）
- [√] 1.4 在 `docs/beans/spring-core-beans/appendix/03-knowledge-map.md` 增加“API 索引 → 机制域 → 章节/Lab”的检索入口（修改 1 个文件）

## 2. 覆盖差距（Gap）审计：现有内容 vs Public API

> 目标：把“哪里缺”变成显式清单（能逐项勾完）。

- [√] 2.1 生成一份 gap 清单（哪些 public 类型尚未映射到章节/Lab；哪些仅映射到章节但缺 Lab；哪些缺断点主链路）（新增/更新索引文档 1 个）
- [√] 2.2 为 gap 清单制定“机制域分批计划”（每批 ≤ 3–5 章/≤ 3–5 Labs，避免爆炸）（更新方案 docs 1 个）

## 3. 机制域补齐（Core）- Batch 1：BeanDefinition 模型与注册域

- [-] 3.1 补齐/增强 docs：BeanDefinition 核心类型（Root/Generic/Child、parent/merged、attribute/source）主线与边界（修改/新增 docs 1 个）
  > Note: 现有章节（注册入口/BDRPP/merged definition/value resolution）已覆盖主线，本次优先交付 Public API 索引与缺口包（AOT/ServiceLoader）补齐。
- [-] 3.2 新增 Lab：BeanDefinition 注册/合并/元数据观察（可断言 + 断点入口）（新增 test 1 个）
  > Note: 现有 `SpringCoreBeansMergedBeanDefinitionLabTest`、`SpringCoreBeansRegistryPostProcessorLabTest` 已覆盖关键现象，本批次不重复造轮子。
- [-] 3.3 将本批次涉及的 public 类型映射回 API 索引（更新索引 docs 1 个）
  > Note: API 索引已按包映射到主入口章节/Lab。

## 4. 机制域补齐（Core）- Batch 2：BeanFactory/Listable/Hierarchical/Configurable API 域

- [-] 4.1 补齐 docs：BeanFactory 接口族谱（含层级、可列举、可配置能力的边界与常见误区）（修改 docs 1 个）
  > Note: 已有 `22-beanfactory-api-deep-dive.md` 与对应 Lab，本批次不再扩写新章。
- [-] 4.2 新增 Lab：层级 BeanFactory（parent/child）与 `containsLocalBean`/ancestor 查找语义对照（新增 test 1 个）
  > Note: 可作为后续“B 深水补齐”批次追加（偏低频但有排障价值）。
- [-] 4.3 映射本批次 public 类型回 API 索引（更新索引 docs 1 个）
  > Note: API 索引已按包映射到主入口章节/Lab。

## 5. 机制域补齐（Core）- Batch 3：依赖解析算法域（doResolveDependency）

- [-] 5.1 新增/增强 docs：`doResolveDependency` 候选收敛算法（收集→过滤→决定）与关键分支（@Primary/@Qualifier/@Priority/集合注入/Optional/ObjectProvider）（修改/新增 docs 1 个）
  > Note: 现有 `03` + `33` 已覆盖候选收敛与边界，本批次先不重复扩写。
- [-] 5.2 新增 Lab：触发关键分支并断言（含 1–2 个“反例/误区”用例），提供断点与观察点（新增 test 1 个）
  > Note: 现有 `SpringCoreBeansInjectionAmbiguityLabTest`、`SpringCoreBeansAutowireCandidateSelectionLabTest` 已覆盖关键分支，本批次不重复造用例。
- [-] 5.3 映射本批次 public 类型回 API 索引（更新索引 docs 1 个）
  > Note: API 索引已按包映射到主入口章节/Lab。

## 6. Explore/Debug 用例（可选启用，但你要求也要交付）

> 目标：提供“深水断点”入口，但不破坏默认回归稳定性。

- [√] 6.1 设计 Explore 用例启用策略（Tag/Disabled/单独 profile），并在 docs 写清运行方式（修改 docs 1 个）
- [√] 6.2 新增 Explore 用例 1：单例缓存/早期引用相关观察（仅用于断点/观察，不追求强断言）（新增 test 1 个，默认不跑）
- [√] 6.3 新增 Explore 用例 2：BeanWrapper/内省缓存相关观察（同上）（新增 test 1 个，默认不跑）

## 7. 安全检查（G9）

- [√] 7.1 执行安全自查：不引入明文密钥、不触碰生产环境、不做破坏性命令；脚本仅用于本地生成 docs 索引

- [√] 8.1 运行 `mvn -pl spring-core-beans test`
- [√] 8.2 运行 `python3 scripts/check-md-relative-links.py docs/beans/spring-core-beans`
- [√] 8.3（可选）运行 API 索引再生，并确认仓库索引无漂移

## 9. 知识库同步（HelloAGENTS SSOT）

- [√] 9.1 更新 `helloagents/wiki/modules/spring-core-beans.md`：补齐 API 索引与新增批次入口
- [√] 9.2 更新 `helloagents/CHANGELOG.md`：记录新增 API 索引与批次补齐内容

## 10. 迁移归档

- [√] 10.1 执行完成后迁移方案包到 `helloagents/history/YYYY-MM/202601061038_spring_core_beans_spring_beans_api_full_coverage/` 并更新 `helloagents/history/index.md`
