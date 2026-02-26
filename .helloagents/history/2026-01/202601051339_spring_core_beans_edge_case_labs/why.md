# Change Proposal: spring-core-beans 边界机制补齐（编程式注册 / raw injection / prototype 销毁语义）

## Requirement Background

`spring-core-beans` 的主线已经能覆盖容器 refresh、依赖解析、BPP/循环依赖/代理等核心机制，但读者在真实项目里最容易“翻车”的往往是一些边界条件：

1. **编程式注册**：`registerBeanDefinition` / `registerSingleton` / `registerBean` 到底有什么差异？为什么“注册了但是没走 BPP/没注入/没回调”？  
2. **raw injection despite wrapping**：循环依赖 + 代理包裹下，为什么会出现“容器里拿到的是 proxy，但某个依赖里拿到的是 raw bean”的不一致？`allowRawInjectionDespiteWrapping` 到底在保护什么？  
3. **prototype 销毁语义**：prototype 默认不执行销毁回调，读者需要一个最小可断言的实验来建立“资源释放责任在谁”的正确直觉。

这些点如果缺失，读者会在排障时缺少“可复现 + 可断点”的抓手，理解会停留在概念层。

## Change Content

1. 新增 Lab：编程式注册差异（BeanDefinition vs singleton instance vs registerBean supplier）
2. 新增 Lab：`allowRawInjectionDespiteWrapping` 的最小复现（循环依赖 + wrapping BPP）
3. 新增 Lab：prototype 销毁语义（不自动销毁 + 手动 destroyBean 的正确姿势）
4. 将 Lab 入口落位到对应章节（docs/16、docs/04、docs/05、docs/25），补齐“复现入口/推荐断点/观察点”承接

## Impact Scope

- **Modules:** `spring-core-beans`、`helloagents`
- **Files:**
  - `spring-core-beans/src/test/java/**`
  - `docs/beans/spring-core-beans/**`
  - `helloagents/wiki/**`
  - `helloagents/CHANGELOG.md`

## Core Scenarios

### Requirement: 编程式注册差异可复现
**Module:** spring-core-beans
把 `registerBeanDefinition/registerSingleton/registerBean` 的差异落到“是否走创建管线/是否被 BPP 处理/是否能注入”的可断言实验。

#### Scenario: 一眼看出 registerSingleton 不会 retroactive 触发 BPP
- 通过同一个 BPP + 三种注册方式对照，证明“定义层注册”与“实例层注册”的语义差异

### Requirement: raw injection 不一致可复现
**Module:** spring-core-beans
复现循环依赖下 raw bean 注入导致的“依赖里拿到 raw、容器里拿到 proxy”的引用不一致，并解释 `allowRawInjectionDespiteWrapping` 的意义。

#### Scenario: 开关为 true 时允许启动但埋下隐患
- 证明 dependent 持有 raw reference，而容器暴露 proxy
- 证明调用路径会出现“绕过代理”的行为差异

### Requirement: prototype 销毁语义可复现
**Module:** spring-core-beans
给出一个最小实验，证明 prototype 默认不执行 destroy 回调；并给出手动销毁的正确用法。

#### Scenario: context close 不会触发 prototype destroy
- 断言 destroy 回调计数为 0（默认行为）
- 断言手动 `destroyBean` 才会触发 destroy 回调（正确姿势）

## Risk Assessment

- **Risk:** 循环依赖相关实验如果写得不够“最小”，容易引入不稳定性
- **Mitigation:** 使用 `AnnotationConfigApplicationContext` 做纯 Spring 容器实验；断言以“引用一致性/是否 proxy/是否抛异常”这种稳定信号为主

