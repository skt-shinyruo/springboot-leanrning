# Task List: spring-core-beans 文档深化 + Labs/Exercises（源码级）

Directory: `helloagents/plan/202601010649_spring-core-beans-deep-dive/`

---

## 1. spring-core-beans（文档 + Labs）

- [√] 1.1 审计现有 docs/Labs 覆盖：确认本次优先主题的“结论→断点→观察点→测试方法”链路
- [√] 1.2 深化 DI 解析（docs/03）：补齐源码级决策树、断点闭环与排障速查
- [√] 1.3 深化 生命周期（docs/05、docs/17）：补齐源码级时间线、回调顺序与 prototype 销毁边界
- [√] 1.4 深化 PostProcessor/代理（docs/06、docs/31）：补齐三类后处理器、排序机制与“3 个替换点”
- [√] 1.5 深化 循环依赖与 early reference（docs/09、docs/16）：补齐三层缓存、Boot 默认差异与代理介入边界
- [√] 1.6 深化 `@Configuration` 增强（docs/07）：补齐 proxyBeanMethods 语义与源码锚点
- [√] 1.7 深化 FactoryBean（docs/08、docs/23、docs/29）：补齐 type matching / allowEagerInit / 缓存语义与边界
- [√] 1.8 扩写 [90. 常见坑](../../../docs/beans/spring-core-beans/90-common-pitfalls.md) 与 [99. 自测题](../../../docs/beans/spring-core-beans/99-self-check.md)，让每条坑都能映射到对应章节与 Lab
- [√] 1.9 README 一致性检查：确保 docs 链接与测试路径仍一致（必要时小幅修订）

## 2. Security Check

- [√] 2.1 安全检查：不引入敏感信息、不做生产环境操作、不引入危险命令/配置

## 3. Knowledge Base Update

- [√] 3.1 同步更新 `helloagents/wiki/modules/spring-core-beans.md` 的规格与变更记录
- [√] 3.2 更新 `helloagents/CHANGELOG.md`（记录本次变更）

## 4. Testing

- [√] 4.1 运行并通过：`mvn -pl spring-core-beans test`
