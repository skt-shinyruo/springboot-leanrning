# Task List: Tutorials 风格结构重排与深度化改造

Directory: `helloagents/plan/202601242148_tutorials_style_deepen_all/`

---

## 1. 目录与模块结构重排
- [ ] 1.1 定义主题分组与迁移映射表在 `docs/migration/module-layout-map.md`，验证 why.md#requirement-目录与模块结构重排-scenario-构建入口保持可用
- [ ] 1.2 调整聚合入口 `pom.xml` 与 `spring-boot-modules/pom.xml`，验证 why.md#requirement-目录与模块结构重排-scenario-构建入口保持可用
- [ ] 1.3 调整 `spring-core-modules/pom.xml` 与 `docs/SUMMARY.md`，验证 why.md#requirement-目录与模块结构重排-scenario-构建入口保持可用

## 2. 文档导航对齐与索引统一
- [ ] 2.1 对齐 `docs/README.md` 与 `docs/topics/index.md` 的主题入口，验证 why.md#requirement-文档导航对齐与索引统一-scenario-站点导航稳定
- [ ] 2.2 维护 `docs/SUMMARY.md` 的模块入口层级与排序，验证 why.md#requirement-文档导航对齐与索引统一-scenario-站点导航稳定

## 3. 内容深度化（优先模块）
- [ ] 3.1 深化 `docs/basics/spring-boot-basics/README.md` 与 `docs/basics/spring-boot-basics/part-00-guide/03-springapplication-run-call-chain.md`，验证 why.md#requirement-内容深度化规范与首批模块落地-scenario-证据链完整
- [ ] 3.2 深化 `docs/web-mvc/spring-boot-web-mvc/README.md` 与 `docs/web-mvc/spring-boot-web-mvc/part-00-guide/024-01-request-mapping-call-chain.md`，验证 why.md#requirement-内容深度化规范与首批模块落地-scenario-证据链完整
- [ ] 3.3 深化 `docs/beans/spring-core-beans/README.md` 与 `docs/beans/spring-core-beans/part-00-guide/008-01-bean-creation-call-chain.md`，验证 why.md#requirement-内容深度化规范与首批模块落地-scenario-证据链完整
- [ ] 3.4 深化 `docs/aop/spring-core-aop/README.md` 与 `docs/aop/spring-core-aop/part-00-guide/026-01-proxy-creation-call-chain.md`，验证 why.md#requirement-内容深度化规范与首批模块落地-scenario-证据链完整
- [ ] 3.5 深化 `docs/tx/spring-core-tx/README.md` 与 `docs/tx/spring-core-tx/part-00-guide/03-transaction-interceptor-call-chain.md`，验证 why.md#requirement-内容深度化规范与首批模块落地-scenario-证据链完整

## 4. 迁移兼容与验证
- [ ] 4.1 添加迁移说明 `docs/migration/README.md` 与 `docs/migration/redirect-rules.md`，验证 why.md#requirement-迁移兼容与验证-scenario-链接不失效
- [ ] 4.2 更新根 `README.md` 与 `docs/index.md` 的入口指引，验证 why.md#requirement-迁移兼容与验证-scenario-链接不失效

## 5. 安全检查
- [ ] 5.1 执行安全检查（输入校验、敏感信息处理、权限控制、EHRB 风险规避）

## 6. 文档同步
- [ ] 6.1 更新 `helloagents/wiki/overview.md` 与 `helloagents/project.md`

## 7. 测试
