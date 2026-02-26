# Task List: spring-core-beans + spring-core-aop 深挖增强 v2

Directory: `helloagents/plan/202601010845_beans-aop-deep-dive-v2/`

---

## 1. spring-core-aop（文档：源码主线 / 链条 / pointcut / 多代理）
- [√] 1.1 增补“AutoProxyCreator/Advisor/Advice/Pointcut 主线”文档，并把断点导航补齐到 `docs/aop/spring-core-aop/part-00-guide/00-deep-dive-guide.md`
- [√] 1.2 增补“拦截器链执行细节（MethodInvocation#proceed 嵌套）”文档，并补齐断点清单到 `docs/aop/spring-core-aop/part-01-proxy-fundamentals/06-debugging.md`
- [√] 1.3 增补“pointcut 表达式系统”文档（execution/within/this/target/args/@annotation/... + 常见误判）
- [√] 1.4 增补“多代理叠加与顺序”文档（单 proxy 多 advisor vs 多层 proxy + 真实项目排障 checklist）
- [√] 1.5 更新 `spring-core-aop/README.md` 阅读路径与章节索引

## 2. spring-core-aop（Labs：可断言 + 可断点）
- [√] 2.1 新增 Lab：AutoProxyCreator 作为 BPP 的容器主线（能看到 BPP 注册、proxy 产生、advisor 列表）
- [√] 2.2 新增/增强 Lab：advice 链执行的 proceed 嵌套结构（before/after 顺序可断言）
- [√] 2.3 新增 Lab：pointcut 误判最小复现（this vs target 在 JDK/CGLIB 下差异可断言）
- [√] 2.4 新增 Lab：多代理/多 advisor 叠加观察（提供 proxy/advisor introspection 辅助工具）

## 3. spring-core-beans（承接增强：把 AOP 放回容器时间线）
- [√] 3.1 更新 beans 的 BPP/代理章节：显式引入 AutoProxyCreator 作为典型 BPP（注册→排序→介入点：pre/early/after-init）
- [√] 3.2 增强 beans 的“顺序与代理叠加”解释：给出从 `registerBeanPostProcessors` 到 `applyBeanPostProcessorsAfterInitialization` 的短路径断点闭环，并补齐跨模块链接到 aop 对应 Labs

## 4. Security Check
- [√] 4.1 执行安全自检（G9）：不引入明文密钥/外部服务；依赖变更记录版本与影响；避免高风险操作

## 5. Knowledge Base Sync（SSOT）
- [√] 5.1 更新 `helloagents/wiki/modules/spring-core-aop.md` 与 `helloagents/wiki/modules/spring-core-beans.md`
- [√] 5.2 更新 `helloagents/CHANGELOG.md` 与 `helloagents/history/index.md`

## 6. Testing
- [√] 6.1 运行 `mvn -pl spring-core-aop test` 并修复失败
- [√] 6.2 运行 `mvn -pl spring-core-beans test` 并修复失败
