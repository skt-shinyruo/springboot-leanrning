# Change Proposal: 深化 Bean 注册入口教程（02-bean-registration）

## Requirement Background

`spring-core-beans` 的 `02-bean-registration` 章节已经把“注册=BeanDefinition（定义）而不是实例”这一层说清楚了，但对于目标读者（源码进阶 / 面试 / 团队内训）仍存在几个短板：

- 缺少“入口→最短调用链→落点”的源码级对照，读者很难把「扫描/@Bean/@Import/registrar」从概念落到具体类与方法。
- 缺少“证据链脚本化”：即跑哪个 Lab、下哪些断点、看哪几个变量、如何得出结论，导致学习与内训难以复制。
- 缺少“面试可复述答案骨架”：容易停留在“我知道有这些入口”，但不能用主线+边界+证据链组织成可答题的叙事。

本变更目标：将该章节升级为“可跑、可看见、可复述、可教学”的版本。

## Change Content

1. 增补注册入口对照表：入口形式 → 注册对象（Definition/Instance）→ 最短调用链 → 关键断点/观察点 → 常见坑 → 推荐 Lab。
2. 增补“证据链”闭环：为每类入口给出可复制的 3–5 步验证流程（命令/断点/watch list/结论）。
3. 增补排障分流：明确 “BeanDefinition 是否存在” 与 “bean instance 是否存在” 的区别，并给出最短判断路径。
4. 增补面试/内训复述模板：给出高频问法与标准答题结构（主线→分支→证据链）。
5. 保持原有章节结构与导航稳定，不引入新依赖、不改变现有测试行为。

## Impact Scope

- **Modules:** `spring-core-modules/spring-core-beans`
- **Files:**
  - `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/02-bean-registration.md`
  - `helloagents/wiki/modules/spring-core-beans.md`
  - `helloagents/CHANGELOG.md`
- **APIs:** None
- **Data:** None

## Core Scenarios

### Requirement: R1-bean-registration-docs-deepen
**Module:** spring-core-beans
深化 Bean 注册入口章节，使其适用于源码进阶/面试/内训。

#### Scenario: S1-entry-to-registerBeanDefinition
读者能从“入口形式”推导出“最短调用链”，并在断点里看见 BeanDefinition 的来源与注册落点。
- 预期结果：能回答“这个 BeanDefinition 是从 scan 还是 @Bean/@Import 来的？”
- 预期结果：能在 3 分钟内用 Lab + 断点证明一次“定义层注册”。

#### Scenario: S2-definition-vs-instance-debugging
读者能区分 `containsBeanDefinition` 与 `containsSingleton` 的含义，并知道对应的排障路径。
- 预期结果：遇到“注入没生效/代理没生效”，能优先判断是否走了实例层注册。

#### Scenario: S3-interview-and-training-output
读者能用“主线→边界→证据链”的结构复述 Bean 注册入口，并能用于团队内训讲解。
- 预期结果：每类入口至少能给出 1 个“最短证据链”与 1 个典型坑解释。

## Risk Assessment

- **Risk:** 文档过度扩写导致阅读负担增加  
  **Mitigation:** 先用对照表与证据链压缩信息密度，正文保持“只讲决定性分支/入口”。
- **Risk:** 源码方法名随版本轻微变化导致断点不匹配  
  **Mitigation:** 以“落点（registerBeanDefinition）+ 核心处理器（ConfigurationClassPostProcessor）+ 扫描器（ClassPathBeanDefinitionScanner）”为稳定锚点，避免依赖脆弱内部细节。

