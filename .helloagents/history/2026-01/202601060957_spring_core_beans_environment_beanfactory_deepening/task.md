# Task List: spring-core-beans 补齐 Environment Abstraction 与 BeanFactory API（保持现状版本）

Directory: `helloagents/history/2026-01/202601060957_spring_core_beans_environment_beanfactory_deepening/`

---

## 1. Docs（章节补齐）

- [√] 1.1 新增 Environment 章节：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/21-environment-and-propertysource.md`
  - 覆盖：PropertySource 抽象、@PropertySource 进入链路、PropertySources 顺序/优先级、与 placeholder 解析的连接点
- [√] 1.2 新增 BeanFactory API 章节：`docs/beans/spring-core-beans/part-04-wiring-and-boundaries/22-beanfactory-api-deep-dive.md`
  - 覆盖：BeanFactory 接口族谱、plain BeanFactory 的边界、手动 bootstrap post-processors 的可复现路径
- [√] 1.3 更新章节导航链路：37 → 38 → 39 → 40，并保持“上一章｜目录｜下一章”可顺读

## 2. Labs（可复现/可断言）

- [√] 2.1 新增 Environment Lab：`SpringCoreBeansEnvironmentPropertySourceLabTest`
  - 覆盖：@PropertySource 默认值 + programmatic PropertySource 覆盖 + precedence 断言
- [√] 2.2 新增 BeanFactory API Lab：`SpringCoreBeansBeanFactoryApiLabTest`
  - 覆盖：plain DefaultListableBeanFactory 不自动处理注解；手动 addBeanPostProcessor 后行为变化

## 3. Docs Index / Knowledge Map

- [√] 3.1 更新 `docs/beans/spring-core-beans/README.md`：在 Part04 TOC 加入 38/39，并在“快速定位/对照表”补齐入口
- [√] 3.2 更新 `docs/beans/spring-core-beans/appendix/03-knowledge-map.md`：补齐 Environment/BeanFactory 的 Concept → Chapter → Lab 映射

## 4. Knowledge Base Update

- [√] 4.1 更新 `helloagents/wiki/modules/spring-core-beans.md`：补齐新章节与 Labs 入口
- [√] 4.2 更新 `helloagents/CHANGELOG.md`：记录新增机制闭环

## 5. Verification

- [√] 5.1 运行 `mvn -pl spring-core-beans test`
- [√] 5.2 运行 `python3 scripts/check-md-relative-links.py docs/beans/spring-core-beans`

## 6. Migration

- [√] 6.1 将方案包迁移至 `helloagents/history/2026-01/202601060957_spring_core_beans_environment_beanfactory_deepening/` 并更新 `helloagents/history/index.md`
