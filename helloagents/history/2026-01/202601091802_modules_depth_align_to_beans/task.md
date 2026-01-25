# Task List: 全模块深挖对齐（对标 spring-core-beans）

Directory: `helloagents/plan/202601091802_modules_depth_align_to_beans/`

---

## 1. springboot-basics
- [√] 1.1 补齐/增强 Guide 机制主线（时间线/关键参与者/断点/关键分支 2–5 条）在 `docs/basics/springboot-basics/part-00-guide/00-deep-dive-guide.md`，verify why.md#requirement-module-guide-mainline-alignment-scenario-guide-is-a-navigation-map
- [√] 1.2 补齐坑点占位（至少 1 条可断言坑点）在 `docs/basics/springboot-basics/appendix/99-self-check.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 1.3 校验默认 Labs 是否覆盖关键分支；必要时补强用例在 `springboot-basics/src/test/java/com/learning/springboot/bootbasics/part01_boot_basics/BootBasicsOverrideLabTest.java`，verify why.md#requirement-key-branch-default-labs-scenario-key-branches-are-covered-by-default-labs
- [√] 1.4 运行模块测试：`mvn -pl springboot-basics test`

## 2. springboot-actuator
- [√] 2.1 重写 Guide 机制主线（端点注册 vs 暴露 vs 安全边界 vs 排障入口）在 `docs/actuator/springboot-actuator/part-00-guide/00-deep-dive-guide.md`，verify why.md#requirement-module-guide-mainline-alignment-scenario-guide-is-a-navigation-map
- [√] 2.2 补齐坑点占位（至少 1 条可断言坑点）在 `docs/actuator/springboot-actuator/appendix/99-self-check.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 2.3 为关键分支补齐默认 Lab（至少覆盖：暴露策略覆盖/退让、env 404 vs expose 后 200、health/info 结构契约）在 `springboot-actuator/src/test/java/com/learning/springboot/bootactuator/part01_actuator/BootActuatorLabTest.java`，verify why.md#requirement-key-branch-default-labs-scenario-key-branches-are-covered-by-default-labs
- [√] 2.4 补齐章节坑点（至少 1 条可断言坑点）在 `docs/actuator/springboot-actuator/part-01-actuator/01-actuator-basics.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 2.5 运行模块测试：`mvn -pl springboot-actuator test`

## 3. springboot-testing
- [√] 3.1 补齐 Guide 机制主线（slice vs full、@MockBean 替换边界、排障分流）在 `docs/testing/springboot-testing/part-00-guide/00-deep-dive-guide.md`，verify why.md#requirement-module-guide-mainline-alignment-scenario-guide-is-a-navigation-map
- [√] 3.2 补齐章节坑点（至少 1 条可断言坑点）在 `docs/testing/springboot-testing/part-01-testing/01-slice-and-mocking.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 3.3 补齐自测坑点占位（至少 1 条可断言坑点）在 `docs/testing/springboot-testing/appendix/99-self-check.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 3.4 为关键分支补齐默认 Lab（至少覆盖：@WebMvcTest 的 bean 图边界、@SpringBootTest 的真实启动边界、@MockBean 的替换行为与误用风险）在 `springboot-testing/src/test/java/com/learning/springboot/boottesting/part01_testing/BootTestingMockBeanLabTest.java`，verify why.md#requirement-key-branch-default-labs-scenario-key-branches-are-covered-by-default-labs
- [√] 3.5 运行模块测试：`mvn -pl springboot-testing test`

## 4. springboot-business-case
- [√] 4.1 重写 Guide 机制主线（请求→校验→事务→事件→AOP→异常塑形→回滚边界）在 `docs/business-case/springboot-business-case/part-00-guide/00-deep-dive-guide.md`，verify why.md#requirement-module-guide-mainline-alignment-scenario-guide-is-a-navigation-map
- [√] 4.2 补齐章节坑点占位（至少 1 条可断言坑点）在 `docs/business-case/springboot-business-case/part-01-business-case/01-architecture-and-flow.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 4.3 为关键分支补齐默认 Lab（至少覆盖：回滚阻止落库、sync listener vs afterCommit listener、AOP 记录调用链）在 `springboot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part01_business_case/BootBusinessCaseLabTest.java`，verify why.md#requirement-key-branch-default-labs-scenario-key-branches-are-covered-by-default-labs
- [√] 4.4 运行模块测试：`mvn -pl springboot-business-case test`

## 5. springboot-security
- [√] 5.1 重写 Guide 机制主线（FilterChain→Authentication→Authorization；CSRF 威胁模型；JWT 无状态边界；method security 代理）在 `docs/security/springboot-security/part-00-guide/00-deep-dive-guide.md`，verify why.md#requirement-module-guide-mainline-alignment-scenario-guide-is-a-navigation-map
- [√] 5.2 补齐章节坑点（至少 1 条可断言坑点）在 `docs/security/springboot-security/part-01-security/01-basic-auth-and-authorization.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 5.3 补齐章节坑点（至少 1 条可断言坑点）在 `docs/security/springboot-security/part-01-security/02-csrf.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 5.4 补齐章节坑点（至少 1 条可断言坑点）在 `docs/security/springboot-security/part-01-security/04-filter-chain-and-order.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 5.5 补齐自测坑点占位（至少 1 条可断言坑点）在 `docs/security/springboot-security/appendix/99-self-check.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 5.6 为关键分支补齐默认 Lab（至少覆盖：401 vs 403 分流、CSRF 对 session 与 JWT 的差异、self-invocation 绕过 method security）在 `springboot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityLabTest.java`，verify why.md#requirement-key-branch-default-labs-scenario-key-branches-are-covered-by-default-labs
- [√] 5.7 运行模块测试：`mvn -pl springboot-security test`

## 6. springboot-web-client
- [√] 6.1 重写 Guide 机制主线（RestClient vs WebClient；错误映射；超时/重试；可测性：MockWebServer）在 `docs/web-client/springboot-web-client/part-00-guide/00-deep-dive-guide.md`，verify why.md#requirement-module-guide-mainline-alignment-scenario-guide-is-a-navigation-map
- [√] 6.2 补齐章节坑点（至少 1 条可断言坑点）在 `docs/web-client/springboot-web-client/part-01-web-client/01-restclient-basics.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 6.3 补齐章节坑点（至少 1 条可断言坑点）在 `docs/web-client/springboot-web-client/part-01-web-client/02-webclient-basics.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 6.4 补齐章节坑点（至少 1 条可断言坑点）在 `docs/web-client/springboot-web-client/part-01-web-client/03-error-handling.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 6.5 补齐章节坑点（至少 1 条可断言坑点）在 `docs/web-client/springboot-web-client/part-01-web-client/04-timeout-and-retry.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 6.6 补齐自测坑点占位（至少 1 条可断言坑点）在 `docs/web-client/springboot-web-client/appendix/99-self-check.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 6.7 为关键分支补齐默认 Lab（至少覆盖：错误码映射、超时失败快、5xx 重试可回归、请求头/路径固定）在 `springboot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientWebClientLabTest.java`，verify why.md#requirement-key-branch-default-labs-scenario-key-branches-are-covered-by-default-labs
- [√] 6.8 运行模块测试：`mvn -pl springboot-web-client test`

## 7. springboot-async-scheduling
- [√] 7.1 重写 Guide 机制主线（@Async 代理前提、自调用绕过、异常传播、@Scheduled 触发边界）在 `docs/async-scheduling/springboot-async-scheduling/part-00-guide/00-deep-dive-guide.md`，verify why.md#requirement-module-guide-mainline-alignment-scenario-guide-is-a-navigation-map
- [√] 7.2 补齐章节坑点（至少 1 条可断言坑点）在 `docs/async-scheduling/springboot-async-scheduling/part-01-async-scheduling/01-async-proxy-mental-model.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 7.3 补齐章节坑点（至少 1 条可断言坑点）在 `docs/async-scheduling/springboot-async-scheduling/part-01-async-scheduling/02-executor-and-threading.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 7.4 补齐章节坑点（至少 1 条可断言坑点）在 `docs/async-scheduling/springboot-async-scheduling/part-01-async-scheduling/03-exceptions.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 7.5 补齐章节坑点（至少 1 条可断言坑点）在 `docs/async-scheduling/springboot-async-scheduling/part-01-async-scheduling/05-scheduling-basics.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 7.6 补齐自测坑点占位（至少 1 条可断言坑点）在 `docs/async-scheduling/springboot-async-scheduling/appendix/99-self-check.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 7.7 为关键分支补齐默认 Lab（至少覆盖：无 EnableAsync 不生效、异常传播/uncaught handler、自调用绕过、EnableScheduling 触发）在 `springboot-async-scheduling/src/test/java/com/learning/springboot/bootasyncscheduling/part01_async_scheduling/BootAsyncSchedulingLabTest.java`，verify why.md#requirement-key-branch-default-labs-scenario-key-branches-are-covered-by-default-labs
- [√] 7.8 运行模块测试：`mvn -pl springboot-async-scheduling test`

## 8. springboot-cache
- [√] 8.1 重写 Guide 机制主线（Cacheable/Put/Evict、key/condition/unless、sync 防击穿、过期可测）在 `docs/cache/springboot-cache/part-00-guide/00-deep-dive-guide.md`，verify why.md#requirement-module-guide-mainline-alignment-scenario-guide-is-a-navigation-map
- [√] 8.2 补齐章节坑点（至少 1 条可断言坑点）在 `docs/cache/springboot-cache/part-01-cache/01-cacheable-basics.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 8.3 补齐章节坑点（至少 1 条可断言坑点）在 `docs/cache/springboot-cache/part-01-cache/02-cacheput-and-evict.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 8.4 补齐章节坑点（至少 1 条可断言坑点）在 `docs/cache/springboot-cache/part-01-cache/03-key-condition-unless.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 8.5 补齐章节坑点（至少 1 条可断言坑点）在 `docs/cache/springboot-cache/part-01-cache/04-sync-stampede.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 8.6 补齐章节坑点（至少 1 条可断言坑点）在 `docs/cache/springboot-cache/part-01-cache/05-expiry-with-ticker.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 8.7 补齐自测坑点占位（至少 1 条可断言坑点）在 `docs/cache/springboot-cache/appendix/99-self-check.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 8.8 为关键分支补齐默认 Lab（至少覆盖：sync 收敛计算、手动 ticker 过期可测、condition/unless 不缓存分支）在 `springboot-cache/src/test/java/com/learning/springboot/bootcache/part01_cache/BootCacheLabTest.java`，verify why.md#requirement-key-branch-default-labs-scenario-key-branches-are-covered-by-default-labs
- [√] 8.9 运行模块测试：`mvn -pl springboot-cache test`

## 9. springboot-data-jpa
- [√] 9.1 重写 Guide 机制主线（实体状态机 + persistence context + flush/脏检查 + fetching/N+1 + 排障路径）在 `docs/data-jpa/springboot-data-jpa/part-00-guide/00-deep-dive-guide.md`，verify why.md#requirement-module-guide-mainline-alignment-scenario-guide-is-a-navigation-map
- [√] 9.2 补齐章节坑点（至少 1 条可断言坑点）在 `docs/data-jpa/springboot-data-jpa/part-01-data-jpa/01-entity-states.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 9.3 补齐章节坑点（至少 1 条可断言坑点）在 `docs/data-jpa/springboot-data-jpa/part-01-data-jpa/02-persistence-context.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 9.4 补齐章节坑点（至少 1 条可断言坑点）在 `docs/data-jpa/springboot-data-jpa/part-01-data-jpa/03-flush-and-visibility.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 9.5 补齐章节坑点（至少 1 条可断言坑点）在 `docs/data-jpa/springboot-data-jpa/part-01-data-jpa/04-dirty-checking.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 9.6 补齐章节坑点（至少 1 条可断言坑点）在 `docs/data-jpa/springboot-data-jpa/part-01-data-jpa/05-fetching-and-n-plus-one.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 9.7 补齐章节坑点（至少 1 条可断言坑点）在 `docs/data-jpa/springboot-data-jpa/part-01-data-jpa/06-datajpatest-slice.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 9.8 补齐自测坑点占位（至少 1 条可断言坑点）在 `docs/data-jpa/springboot-data-jpa/appendix/99-self-check.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 9.9 为关键分支补齐默认 Lab（补齐关系映射与 N+1 的默认可回归证据链；并补齐 getReferenceById/lazy 场景）在 `springboot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaLabTest.java`，verify why.md#requirement-key-branch-default-labs-scenario-key-branches-are-covered-by-default-labs
- [√] 9.10 运行模块测试：`mvn -pl springboot-data-jpa test`

## 10. spring-core-aop
- [√] 10.1 补齐章节坑点（至少 1 条可断言坑点）在 `docs/aop/spring-core-aop/part-01-proxy-fundamentals/05-expose-proxy.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 10.2 补齐章节坑点（至少 1 条可断言坑点）在 `docs/aop/spring-core-aop/part-01-proxy-fundamentals/06-debugging.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 10.3 补齐章节坑点（至少 1 条可断言坑点）在 `docs/aop/spring-core-aop/part-02-autoproxy-and-pointcuts/08-pointcut-expression-system.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 10.4 补齐章节坑点（至少 1 条可断言坑点）在 `docs/aop/spring-core-aop/part-03-proxy-stacking/09-multi-proxy-stacking.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 10.5 补齐自测坑点占位（至少 1 条可断言坑点）在 `docs/aop/spring-core-aop/appendix/99-self-check.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 10.6 校验关键分支默认 Labs 映射（this vs target、JDK vs CGLIB、自调用、顺序、套娃）并在需要时补强在 `spring-core-aop/src/test/java/com/learning/springboot/springcoreaop/part02_autoproxy_and_pointcuts/SpringCoreAopAutoProxyCreatorInternalsLabTest.java`，verify why.md#requirement-key-branch-default-labs-scenario-key-branches-are-covered-by-default-labs
- [√] 10.7 运行模块测试：`mvn -pl spring-core-aop test`

## 11. spring-core-aop-weaving
- [√] 11.1 重写 Guide 机制主线（proxy vs weaving 分流；LTW/CTW 生效前提与排障路径）在 `docs/aop/spring-core-aop-weaving/part-00-guide/00-deep-dive-guide.md`，verify why.md#requirement-module-guide-mainline-alignment-scenario-guide-is-a-navigation-map
- [√] 11.2 补齐 LTW 章节坑点（至少 1 条可断言坑点 + 补齐 aop.xml/agent 关键路径信息）在 `docs/aop/spring-core-aop-weaving/part-02-ltw/02-ltw-basics.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 11.3 补齐 CTW 章节坑点（至少 1 条可断言坑点）在 `docs/aop/spring-core-aop-weaving/part-03-ctw/03-ctw-basics.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 11.4 补齐自测坑点占位（至少 1 条可断言坑点）在 `docs/aop/spring-core-aop-weaving/appendix/99-self-check.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 11.5 校验关键分支默认 Labs 覆盖（javaagent、call vs execution、constructor/field/withincode/cflow、CTW 不依赖 agent）并在需要时补强在 `spring-core-aop-weaving/src/test/java/com/learning/springboot/springcoreaopweaving/part02_ltw_fundamentals/AspectjLtwLabTest.java`，verify why.md#requirement-key-branch-default-labs-scenario-key-branches-are-covered-by-default-labs
- [√] 11.6 运行模块测试：`mvn -pl spring-core-aop-weaving test`

## 12. spring-core-events
- [√] 12.1 补齐 Guide 机制主线（同步/异常传播/顺序/条件；异步 multicaster；事务事件）在 `docs/events/spring-core-events/part-00-guide/00-deep-dive-guide.md`，verify why.md#requirement-module-guide-mainline-alignment-scenario-guide-is-a-navigation-map
- [√] 12.2 补齐章节坑点（至少 1 条可断言坑点）在 `docs/events/spring-core-events/part-01-event-basics/01-event-mental-model.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 12.3 补齐章节坑点（至少 1 条可断言坑点）在 `docs/events/spring-core-events/part-01-event-basics/02-multiple-listeners-and-order.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 12.4 补齐章节坑点（至少 1 条可断言坑点）在 `docs/events/spring-core-events/part-01-event-basics/03-condition-and-payload.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 12.5 补齐章节坑点（至少 1 条可断言坑点）在 `docs/events/spring-core-events/part-01-event-basics/04-sync-and-exceptions.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 12.6 补齐章节坑点（至少 1 条可断言坑点）在 `docs/events/spring-core-events/part-02-async-and-transactional/06-async-multicaster.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 12.7 补齐自测坑点占位（至少 1 条可断言坑点）在 `docs/events/spring-core-events/appendix/99-self-check.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 12.8 校验关键分支默认 Labs 覆盖（异常传播、@Async、afterCommit/afterRollback）并在需要时补强在 `spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part02_async_and_transactional/SpringCoreEventsTransactionalEventLabTest.java`，verify why.md#requirement-key-branch-default-labs-scenario-key-branches-are-covered-by-default-labs
- [√] 12.9 运行模块测试：`mvn -pl spring-core-events test`

## 13. spring-core-validation
- [√] 13.1 补齐 Guide 机制主线（constraint/violation、programmatic、method validation 代理、groups、自定义约束、排障）在 `docs/validation/spring-core-validation/part-00-guide/00-deep-dive-guide.md`，verify why.md#requirement-module-guide-mainline-alignment-scenario-guide-is-a-navigation-map
- [√] 13.2 补齐章节坑点（至少 1 条可断言坑点）在 `docs/validation/spring-core-validation/part-01-validation-core/01-constraint-mental-model.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 13.3 补齐章节坑点（至少 1 条可断言坑点）在 `docs/validation/spring-core-validation/part-01-validation-core/02-programmatic-validator.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 13.4 补齐章节坑点（至少 1 条可断言坑点）在 `docs/validation/spring-core-validation/part-01-validation-core/03-method-validation-proxy.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 13.5 补齐章节坑点（至少 1 条可断言坑点）在 `docs/validation/spring-core-validation/part-01-validation-core/05-custom-constraint.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 13.6 补齐章节坑点（至少 1 条可断言坑点）在 `docs/validation/spring-core-validation/part-01-validation-core/06-debugging.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 13.7 补齐自测坑点占位（至少 1 条可断言坑点）在 `docs/validation/spring-core-validation/appendix/99-self-check.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 13.8 校验关键分支默认 Labs 覆盖（无代理则无 method validation、groups、生效边界）并在需要时补强在 `spring-core-validation/src/test/java/com/learning/springboot/springcorevalidation/part01_validation_core/SpringCoreValidationMechanicsLabTest.java`，verify why.md#requirement-key-branch-default-labs-scenario-key-branches-are-covered-by-default-labs
- [√] 13.9 运行模块测试：`mvn -pl spring-core-validation test`

## 14. spring-core-resources
- [√] 14.1 重写 Guide 机制主线（Resource 抽象、classpath/classpath*、pattern、exists vs handle、jar 差异、编码）在 `docs/resources/spring-core-resources/part-00-guide/00-deep-dive-guide.md`，verify why.md#requirement-module-guide-mainline-alignment-scenario-guide-is-a-navigation-map
- [√] 14.2 补齐章节坑点（至少 1 条可断言坑点）在 `docs/resources/spring-core-resources/part-01-resource-abstraction/03-classpath-star-and-pattern.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 14.3 补齐章节坑点（至少 1 条可断言坑点）在 `docs/resources/spring-core-resources/part-01-resource-abstraction/04-exists-and-handles.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 14.4 补齐章节坑点（至少 1 条可断言坑点）在 `docs/resources/spring-core-resources/part-01-resource-abstraction/05-reading-and-encoding.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 14.5 补齐自测坑点占位（至少 1 条可断言坑点）在 `docs/resources/spring-core-resources/appendix/99-self-check.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 14.6 校验关键分支默认 Labs 覆盖（classpath* pattern、handle vs exists、描述用于排障、jar/file 边界）并在需要时补强在 `spring-core-resources/src/test/java/com/learning/springboot/springcoreresources/part01_resource_abstraction/SpringCoreResourcesMechanicsLabTest.java`，verify why.md#requirement-key-branch-default-labs-scenario-key-branches-are-covered-by-default-labs
- [√] 14.7 运行模块测试：`mvn -pl spring-core-resources test`

## 15. spring-core-tx
- [√] 15.1 重写 Guide 机制主线（事务边界、代理、回滚规则、传播、Template、调试观察点）在 `docs/tx/spring-core-tx/part-00-guide/00-deep-dive-guide.md`，verify why.md#requirement-module-guide-mainline-alignment-scenario-guide-is-a-navigation-map
- [√] 15.2 补齐章节坑点（至少 1 条可断言坑点）在 `docs/tx/spring-core-tx/part-01-transaction-basics/03-rollback-rules.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 15.3 补齐章节坑点（至少 1 条可断言坑点）在 `docs/tx/spring-core-tx/part-02-template-and-debugging/06-debugging.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 15.4 补齐自测坑点占位（至少 1 条可断言坑点）在 `docs/tx/spring-core-tx/appendix/99-self-check.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 15.5 校验关键分支默认 Labs 覆盖（checked 不回滚、rollbackFor、REQUIRES_NEW、MANDATORY/NEVER/NESTED、模板事务）并在需要时补强在 `spring-core-tx/src/test/java/com/learning/springboot/springcoretx/part01_transaction_basics/SpringCoreTxLabTest.java`，verify why.md#requirement-key-branch-default-labs-scenario-key-branches-are-covered-by-default-labs
- [√] 15.6 运行模块测试：`mvn -pl spring-core-tx test`

## 16. spring-core-profiles
- [√] 16.1 重写 Guide 机制主线（Profile 激活来源、@Profile 语义、默认/active/negation、排障）在 `docs/profiles/spring-core-profiles/part-00-guide/00-deep-dive-guide.md`，verify why.md#requirement-module-guide-mainline-alignment-scenario-guide-is-a-navigation-map
- [√] 16.2 补齐章节坑点（至少 1 条可断言坑点）在 `docs/profiles/spring-core-profiles/part-01-profiles/01-profile-activation-and-bean-selection.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 16.3 补齐自测坑点占位（至少 1 条可断言坑点）在 `docs/profiles/spring-core-profiles/appendix/99-self-check.md`，verify why.md#requirement-chapter-pitfall-evidence-linkage-scenario-each-chapter-has-one-verifiable-pitfall
- [√] 16.4 校验关键分支默认 Labs 覆盖（active vs default、多个 profile、negation、优先级）并在需要时补强在 `spring-core-profiles/src/test/java/com/learning/springboot/springcoreprofiles/part01_profiles/SpringCoreProfilesProfilePrecedenceLabTest.java`，verify why.md#requirement-key-branch-default-labs-scenario-key-branches-are-covered-by-default-labs
- [√] 16.5 运行模块测试：`mvn -pl spring-core-profiles test`

## 17. Knowledge Base Sync（helloagents/wiki）
- [√] 17.1 更新模块知识库（分批，每批 ≤3 文件）：`helloagents/wiki/modules/springboot-actuator.md`、`helloagents/wiki/modules/springboot-testing.md`、`helloagents/wiki/modules/springboot-business-case.md`
- [√] 17.2 更新模块知识库（分批，每批 ≤3 文件）：`helloagents/wiki/modules/springboot-security.md`、`helloagents/wiki/modules/springboot-web-client.md`、`helloagents/wiki/modules/springboot-async-scheduling.md`
- [√] 17.3 更新模块知识库（分批，每批 ≤3 文件）：`helloagents/wiki/modules/springboot-cache.md`、`helloagents/wiki/modules/springboot-data-jpa.md`、`helloagents/wiki/modules/springboot-basics.md`
- [√] 17.4 更新模块知识库（分批，每批 ≤3 文件）：`helloagents/wiki/modules/spring-core-aop.md`、`helloagents/wiki/modules/spring-core-aop-weaving.md`、`helloagents/wiki/modules/spring-core-events.md`
- [√] 17.5 更新模块知识库（分批，每批 ≤3 文件）：`helloagents/wiki/modules/spring-core-validation.md`、`helloagents/wiki/modules/spring-core-resources.md`、`helloagents/wiki/modules/spring-core-tx.md`
- [√] 17.6 更新模块知识库（分批，每批 ≤1 文件）：`helloagents/wiki/modules/spring-core-profiles.md`

## 18. Security Check
- [√] 18.1 执行安全检查（按 G9）：输入校验、敏感信息输出、权限边界、避免 EHRB 风险

## 19. Testing & Gates
- [√] 19.1 运行全仓库测试：`mvn test`
- [√] 19.3 更新 `helloagents/CHANGELOG.md` 记录本次“全模块深挖对齐（对标 spring-core-beans）”

