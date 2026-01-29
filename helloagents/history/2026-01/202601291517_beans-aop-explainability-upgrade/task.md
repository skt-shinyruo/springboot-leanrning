# Task List: beans + aop 基础知识可解释性升级（Why Index + 最短证据链）

Directory: `helloagents/plan/202601291517_beans-aop-explainability-upgrade/`

---

> Note：已于 2026-01-29 归档到 `helloagents/history/2026-01/202601291517_beans-aop-explainability-upgrade/`。

## 1. spring-core-beans（基础问题可解释性）

- [√] 1.1 新增 Beans 模块 Why Index（基础问题索引页）到 `spring-core-modules/spring-core-beans/docs/part-00-guide/009-00-why-index.md`，verify why.md#core-scenarios
- [√] 1.2 在 `spring-core-modules/spring-core-beans/docs/README.md` 增加 “Why Index” 入口，并把“三级缓存/early reference/proxy 替换”纳入可检索导航，verify why.md#core-scenarios, depends on task 1.1
- [√] 1.3 在 `spring-core-modules/spring-core-beans/README.md` 增加 “基础问题入口（Why Index）+ 10 分钟最短证据链”指引，verify why.md#core-scenarios

## 2. spring-core-beans（关键章节：补齐答案先行与跨模块回链）

- [√] 2.1 在 `spring-core-modules/spring-core-beans/docs/part-01-ioc-container/09-circular-dependencies.md` 增强“为什么读者看完仍不懂”的桥接段：补“二级 vs 三级”的对比论证入口，并链接 Why Index 与 AOP proxy 心智模型，verify why.md#core-scenarios
- [√] 2.2 在 `spring-core-modules/spring-core-beans/docs/part-03-container-internals/16-early-reference-and-circular.md` 增强“factory 层价值”的最短闭环：补一页式证据链清单（命令/断点/watch list），verify why.md#core-scenarios
- [√] 2.3 在 `spring-core-modules/spring-core-beans/docs/part-04-wiring-and-boundaries/31-proxying-phase-bpp-wraps-bean.md` 增加“最终暴露对象为何会变化”的统一解释与回链（Why Index + AOP），verify why.md#core-scenarios

## 3. spring-core-aop（Proxy 心智模型与 Beans 主线互链）

- [√] 3.1 在 `spring-core-modules/spring-core-aop/docs/README.md` 增加 “Beans 前置：代理替换发生在哪个阶段” 的跳转入口（指向 beans 31/16），verify why.md#core-scenarios
- [√] 3.2 在 `spring-core-modules/spring-core-aop/docs/part-01-proxy-fundamentals/030-01-aop-proxy-mental-model.md` 增加“前置：Bean 最终暴露对象可能被 BPP 替换”的链接与一句话说明，verify why.md#core-scenarios
- [√] 3.3 在 `spring-core-modules/spring-core-aop/docs/part-02-autoproxy-and-pointcuts/036-07-autoproxy-creator-mainline.md` 增强“AutoProxyCreator 为什么是 BPP”的回链（指向 beans 31 与 refresh 主线），verify why.md#core-scenarios

## 4. 可验证入口（最短证据链落地）

- [√] 4.1 盘点并在 Why Index 中绑定现有 Lab（Beans：early reference/raw vs wrapped/circular boundary；AOP：mental model/self-invocation/autoproxy mainline），verify why.md#core-scenarios
- [-] 4.2 （可选）若现有 Lab 命名不利于检索，新增一个“Why 三层缓存/Why early reference”聚合型 LabTest（只做索引与断言，不新增机制），verify why.md#core-scenarios
  > Note: 现有 Lab/Test 命名已足够直观（early reference/raw vs wrapped/self-invocation/autoproxy mainline），且 Why Index 已绑定命令入口，暂不新增聚合型测试以避免回归耗时与维护面增加。

## 5. 知识库与变更记录同步

- [√] 5.1 更新 `helloagents/wiki/modules/spring-core-beans.md`：增加 Why Index 入口与跨模块依赖说明，verify why.md#impact-scope
- [√] 5.2 更新 `helloagents/wiki/modules/spring-core-aop.md`：增加 Beans 前置回链与 Why Index 引用，verify why.md#impact-scope
- [√] 5.3 更新 `helloagents/CHANGELOG.md`：记录“基础问题可解释性升级（Why Index + 最短证据链）”，verify why.md#change-content

## 6. 安全检查与质量验证

- [√] 6.1 安全检查：确认文档与测试不会引导执行危险命令/连接生产环境；确认无明文敏感信息写入（G9），verify how.md#security-and-performance
- [√] 6.2 运行回归：`mvn -pl :spring-core-beans test` 与 `mvn -pl :spring-core-aop test`，verify how.md#testing-and-deployment
