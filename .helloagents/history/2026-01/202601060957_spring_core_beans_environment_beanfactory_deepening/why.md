# Change Proposal: spring-core-beans 补齐 Environment Abstraction 与 BeanFactory API（保持现状版本）

## Requirement Background

当前 `spring-core-beans` 已覆盖 IoC/DI/生命周期/扩展点/FactoryBean/值解析等主线，并且能通过 Labs 进行可断言复现。

但对照 Spring Framework Reference（core/beans）中的两个知识点，仍存在“被用到/被提到，但缺少系统化闭环”的情况：

1) `beans/environment.html`（Environment Abstraction）
   - 已覆盖：profiles/占位符/值解析等局部排障点
   - 缺少：Environment/PropertySource 的全链路解释（属性从哪里来、优先级如何决定、@PropertySource 如何进入 Environment、如何自定义/插入 PropertySource）

2) `beans/beanfactory.html`（The BeanFactory API）
   - 已覆盖：BeanFactory vs ApplicationContext 的基础对比（可运行 Lab）
   - 缺少：BeanFactory API 的“接口族谱 + 典型使用方式 + 为什么在 plain BeanFactory 下很多注解能力不生效（需要手动 bootstrap post-processors）”的可复现闭环

本变更保持当前版本（Spring 6.2 / Boot 3.5.x）不升级，目标是把上述两点补齐到统一教学闭环标准：

> docs（主线/边界/误区）+ Lab（可复现/可断言）+ 断点入口/观察点 +（可选）Exercise/Solution

## Change Content

- 新增 docs 章节（Part 04）：
  - `38`：Environment/PropertySource（含 @PropertySource 与优先级）
  - `39`：BeanFactory API 深挖（接口族谱 + 手动 bootstrap 的边界）
- 新增对应 Labs（默认参与回归）：
  - Environment：PropertySource 优先级 + @PropertySource 进入链路
  - BeanFactory：plain BeanFactory 下 post-processors 不自动生效（对比手动添加 BPP 的效果）
- 更新 docs 目录与知识点地图，确保可检索
- 同步 HelloAGENTS 知识库与变更记录，并将方案包归档到 history

## Impact Scope

- Modules: spring-core-beans, helloagents
- APIs: None
- Data: None

## Risk Assessment

- 风险：新增章节编号与导航可能导致 docs 链接断链
  - 缓解：运行 `scripts/check-md-relative-links.py` 全量校验
- 风险：新 Lab 依赖容器启动时机/PropertySource 顺序，若写得不严谨可能造成不稳定
  - 缓解：使用纯内存 PropertySource（MapPropertySource）+ 固定断言，避免随机与外部依赖

