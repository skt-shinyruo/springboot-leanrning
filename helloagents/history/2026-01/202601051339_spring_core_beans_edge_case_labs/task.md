# Task List: spring-core-beans 边界机制补齐（编程式注册 / raw injection / prototype 销毁语义）

Directory: `helloagents/history/2026-01/202601051339_spring_core_beans_edge_case_labs/`

---

## 1. Labs：编程式注册差异
- [√] 1.1 新增 Lab：`SpringCoreBeansProgrammaticRegistrationLabTest`（registerBeanDefinition/registerSingleton/registerBean 对照）
  - 交付物：`spring-core-beans/src/test/java/com/learning/springboot/springcorebeans/part04_wiring_and_boundaries/SpringCoreBeansProgrammaticRegistrationLabTest.java`
- [√] 1.2 更新 docs：在 `docs/beans/spring-core-beans/part-04-wiring-and-boundaries/08-programmatic-bpp-registration.md` 补齐编程式注册差异与入口命令

## 2. Labs：raw injection despite wrapping
- [√] 2.1 新增 Lab：`SpringCoreBeansRawInjectionDespiteWrappingLabTest`（allowRawInjectionDespiteWrapping 的最小复现）
- [√] 2.2 更新 docs：在 `docs/beans/spring-core-beans/part-03-container-internals/05-early-reference-and-circular.md` 补齐 raw injection 风险与开关说明，并落位 Lab 入口

## 3. Labs：prototype 销毁语义
- [√] 3.1 新增 Lab：`SpringCoreBeansPrototypeDestroySemanticsLabTest`（prototype 默认不销毁 + destroyBean 手动销毁）
- [√] 3.2 更新 docs：在 `docs/beans/spring-core-beans/part-01-ioc-container/04-scope-and-prototype.md` 与 `05-lifecycle-and-callbacks.md` 补齐 prototype 销毁语义与入口

## 4. Security Check
- [√] 4.1 执行安全自检（G9）：确保无敏感信息、无高风险命令、无生产环境操作暗示

## 5. Documentation Update（Knowledge Base 同步）
- [√] 5.1 更新 `helloagents/wiki/modules/spring-core-beans.md`：补齐本次新增 Labs 与章节入口，并补充变更历史索引
- [√] 5.2 更新 `helloagents/CHANGELOG.md`：记录本次边界机制补齐

## 6. Testing
- [√] 6.1 运行 `mvn -pl spring-core-beans test`
- [√] 6.2 全量抽查 docs 相对链接：0 断链

## 7. Migrate Solution Package
- [√] 7.1 迁移方案包到 `helloagents/history/2026-01/202601051339_spring_core_beans_edge_case_labs/` 并更新 `helloagents/history/index.md`
