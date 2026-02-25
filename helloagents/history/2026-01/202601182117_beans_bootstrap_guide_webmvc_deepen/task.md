# Task List: 继续深化文档（Beans：主线/Bootstrap/Guide；Web MVC：主链路）

Directory: `helloagents/plan/202601182117_beans_bootstrap_guide_webmvc_deepen/`

---

## 1. spring-core-beans：主线章（18）继续下压到关键分支
- [√] 1.1 在 `docs/beans/spring-core-modules/spring-core-beans/part-03-container-internals/07-refresh-to-bean-creation-mainline.md` 补齐 `finishBeanFactoryInitialization` 的关键动作与伪代码（含 `preInstantiateSingletons` + `afterSingletonsInstantiated`），明确关键分支与必看变量，对应 Lab：`SpringCoreBeansPreInstantiationLabTest`
- [√] 1.2 在同章补齐 `doGetBean` 的关键分支：`dependsOn`、parent factory fallback、prototype guard（`beforePrototypeCreation/afterPrototypeCreation`），并绑定对应 Lab：`SpringCoreBeansDependsOnLabTest` / `SpringCoreBeansMergedBeanDefinitionLabTest`

## 2. spring-core-beans：bootstrap 章（022）补齐“处理器表 + 时间线”
- [√] 2.1 在 `docs/beans/spring-core-modules/spring-core-beans/part-03-container-internals/01-container-bootstrap-and-infrastructure.md` 新增“注解能力处理器表”：功能→处理器→类型（BDRPP/BFPP/BPP）→关键方法→refresh 阶段，并绑定 Lab：`SpringCoreBeansBootstrapInternalsLabTest`
- [√] 2.2 在同章新增“时机时间线”：定义层注册 → BFPP/BDRPP 执行 → BPP 注册 → bean 创建链路命中；补齐“过早 getBean 的反例”落点与证据链（可引用 registry/BPP ordering 相关 Lab）

## 3. spring-core-beans：深挖指南（011）升级为“症状驱动导航”
- [√] 3.1 在 `docs/beans/spring-core-modules/spring-core-beans/part-00-guide/03-deep-dive-guide.md` 新增“按现象选章节”速查表（现象→章节→断点→LabTest），并显式引用主线章 18 与 bootstrap 章 022

## 4. springboot-web-mvc：主链路（067）补齐 ERROR vs ASYNC dispatch 对照
- [√] 4.1 在 `docs/web-mvc/spring-boot-modules/springboot-web-mvc/part-03-web-mvc-internals/01-dispatcherservlet-call-chain.md` 增补 ERROR dispatch 时间线（DispatcherType.ERROR）与 `/error` 关键落点，并与 ASYNC 时间线并排对照
- [√] 4.2 在同章增加“现象→阶段→关键方法→证据链”速查表（至少覆盖：FilterChain 异常、resolver 未处理回落 /error、async 两次 dispatch），绑定 Lab：`BootWebMvcTraceLabTest` / `BootWebMvcSpringBootLabTest` / `BootWebMvcViewSpringBootLabTest`

## 5. Security Check
- [√] 5.1 安全自检（G9）：确认无敏感信息写入、无危险脚本/命令、无生产环境操作

## 6. Documentation / Knowledge Base Sync
- [√] 6.1 更新 `helloagents/wiki/modules/spring-core-beans.md`：补充本轮“主线+bootstrap+guide 深化”的入口说明与变更记录
- [√] 6.2 更新 `helloagents/wiki/modules/springboot-web-mvc.md`：补充 ERROR vs ASYNC dispatch 对照入口与变更记录
- [√] 6.3 更新 `helloagents/CHANGELOG.md`（Unreleased：补充本轮文档深化点）

## 7. Testing
- [√] 7.1 运行 `mvn -q -pl spring-core-beans test`
- [√] 7.2 运行 `mvn -q -pl springboot-web-mvc test`

## 8. Migration
- [√] 8.1 更新 task 状态并迁移方案包到 `helloagents/history/2026-01/202601182117_beans_bootstrap_guide_webmvc_deepen/`
- [√] 8.2 更新 `helloagents/history/index.md`
