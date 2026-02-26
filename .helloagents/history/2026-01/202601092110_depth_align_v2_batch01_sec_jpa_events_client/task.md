# Task List: 全模块更深一层对标（V2 / Batch 01）

Directory: `helloagents/plan/202601092110_depth_align_v2_batch01_sec_jpa_events_client/`

> Batch 01 目标模块：`springboot-security` / `springboot-data-jpa` / `spring-core-events` / `springboot-web-client`

---

## 0. 本批次验收标准（执行期 SSOT）

> 说明：本文件的目标是把“补齐/新增”改成“改什么/改到什么程度/证据链与断点锚点是什么”。执行时以这里为准。

- [√] 0.1 每个目标模块新增 ≥1 个默认 `*LabTest`（不 `@Disabled`），断言稳定（不 sleep、不依赖真实网络/真实时间），并在模块 Guide 的关键分支中绑定该 Lab 的类/方法名（证据链）。
- [√] 0.2 每个目标模块的 `docs/part-00-guide/00-deep-dive-guide.md`：
  - 每条“关键分支”都包含：call chain sketch（6–12 行，可复述）+ 断点锚点（class#method）+ 对应默认 Lab/Test（类#方法）。
  - 新增 Debug Playbook 5–8 条：现象 → 分支判定 → 断点锚点（1–3 个）→ 可跑入口（默认 Lab/Test）→ 修复建议。
- [√] 0.3 每个章节 doc 新增 ≥1 个“可断言坑点/边界”（结构固定：Symptom / Root Cause / Verification / Breakpoints / Fix），Verification 必须引用默认 Lab/Test 的方法名（可回归）。

---

## 1. springboot-security

### 1.1 Guide 源码级深化（关键分支 → call chain + 断点锚点 + playbook）

- [√] 1.1 在 `docs/security/springboot-security/part-00-guide/00-deep-dive-guide.md` 增补以下内容（以“测试断言 → 调用链 → 断点锚点”反推）：
  - 关键分支矩阵（建议放在 `C.3 本模块的关键分支` 后）：每条分支都补齐 `Call Chain Sketch` + `Breakpoints` + `Evidence (Lab/Test#method)`。
  - 分支 S1（新增）：**多 `SecurityFilterChain` 命中与顺序（securityMatcher + @Order）**
    - Evidence：`BootSecurityMultiFilterChainOrderLabTest#jwtPathMatchesJwtChain_andApiPathMatchesBasicChain`
    - Breakpoints（至少 1 个仓库内锚点 + 2 个框架锚点）：
      - repo：`com.learning.springboot.bootsecurity.part01_security.SecurityConfig#jwtApiChain` / `SecurityConfig#apiChain`
      - spring：`org.springframework.security.web.FilterChainProxy#doFilterInternal`
      - spring：`org.springframework.security.web.DefaultSecurityFilterChain#matches`
    - Call chain sketch（示例节点）：`FilterChainProxy#doFilterInternal → DefaultSecurityFilterChain#matches → (pick chain) → filters...`
  - 分支 S2（补齐）：**401 vs 403 分流（AuthenticationEntryPoint vs AccessDeniedHandler）**
    - Evidence：`BootSecurityLabTest#secureEndpointReturns401WhenAnonymous` / `BootSecurityLabTest#adminEndpointReturns403ForNonAdminUser`
    - Breakpoints：
      - repo：`JsonAuthenticationEntryPoint#commence`（401 塑形）
      - repo：`JsonAccessDeniedHandler#handle`（403 塑形，含 csrf_failed 分支）
      - spring：`org.springframework.security.web.access.ExceptionTranslationFilter#doFilter`
    - Call chain sketch（示例节点）：`... → ExceptionTranslationFilter#doFilter → (unauthenticated?)->AuthenticationEntryPoint / (access denied?)->AccessDeniedHandler`
  - 分支 S3（补齐）：**CSRF：Basic 链路默认开启，JWT 链路明确关闭（边界：禁用只对“匹配到的链”生效）**
    - Evidence：`BootSecurityLabTest#csrfBlocksPostEvenWhenAuthenticated` / `BootSecurityLabTest#csrfTokenAllowsPostWhenAuthenticated` / `BootSecurityLabTest#jwtPostDoesNotRequireCsrf`
    - Breakpoints：
      - spring：`org.springframework.security.web.csrf.CsrfFilter#doFilterInternal`
      - repo：`JsonAccessDeniedHandler#handle`（Missing/Invalid CSRF → csrf_failed）
      - repo：`SecurityConfig#jwtApiChain`（csrf.disable）
  - 分支 S4（补齐）：**JWT：scope → `SCOPE_xxx` authority 映射（401/403 与 scope 缺失）**
    - Evidence：`BootSecurityLabTest#jwtSecureEndpointReturns401WhenMissingBearerToken` / `BootSecurityLabTest#jwtAdminEndpointReturns403WhenScopeMissing`
    - Breakpoints：
      - repo：`JwtTokenService#issueToken`（scope claim 形态：空格分隔）
      - repo：`JsonAuthenticationEntryPoint#commence`（无 token → 401）
      - repo：`JsonAccessDeniedHandler#handle`（scope 不足 → 403）
  - 分支 S5（补齐）：**Method Security：代理边界（self-invocation 绕过）**
    - Evidence：`BootSecurityLabTest#selfInvocationBypassesMethodSecurityAsAPitfall`
    - Breakpoints：
      - repo：`SelfInvocationPitfallService#outerCallsAdminOnly`
      - repo：`SelfInvocationPitfallService#adminOnly`
      - repo：`AdminOnlyService#adminOnlyAction`
  - Debug Playbook（新增 5–8 条，必须绑定默认可跑入口 + 断点锚点）：
    - P1：现象“返回 401 unauthorized” → 分支判定（缺认证 vs token 格式）→ 断点：`JsonAuthenticationEntryPoint#commence` → 入口：`BootSecurityLabTest#secureEndpointReturns401WhenAnonymous`
    - P2：现象“返回 403 forbidden” → 分支判定（权限不足 vs csrf_failed）→ 断点：`JsonAccessDeniedHandler#handle` → 入口：`BootSecurityLabTest#adminEndpointReturns403ForNonAdminUser`
    - P3：现象“POST 403 csrf_failed” → 分支判定（命中 Basic 链路/CSRF 开启）→ 断点：`CsrfFilter#doFilterInternal` → 入口：`BootSecurityLabTest#csrfBlocksPostEvenWhenAuthenticated`
    - P4：现象“JWT API 行为像 Basic/CSRF（疑似命中错链）” → 分支判定（chain matcher/order）→ 断点：`FilterChainProxy#doFilterInternal` / `DefaultSecurityFilterChain#matches` → 入口：`BootSecurityMultiFilterChainOrderLabTest#jwtPathMatchesJwtChain_andApiPathMatchesBasicChain`
    - P5：现象“@PreAuthorize 没生效” → 分支判定（是否 self-invocation）→ 断点：`SelfInvocationPitfallService#outerCallsAdminOnly` → 入口：`BootSecurityLabTest#selfInvocationBypassesMethodSecurityAsAPitfall`

### 1.2 新增默认 Lab：多链路 matcher/order 分流（证据链比“猜行为”更稳）

- [√] 1.2 在 `springboot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityMultiFilterChainOrderLabTest.java` 新增默认 Lab（建议直接 `@SpringBootTest` + 注入 `FilterChainProxy`）：
  - 方法 `jwtPathMatchesJwtChain_andApiPathMatchesBasicChain`：
    - 构造 `MockHttpServletRequest`：`/api/jwt/secure/ping` 与 `/api/secure/ping`
    - 用 `FilterChainProxy#getFilters(request)` 获取 filters
    - 断言 JWT path 的 filters 中包含 `BearerTokenAuthenticationFilter`（或其实现类名），且不包含 `BasicAuthenticationFilter`
    - 断言 Basic path 的 filters 中包含 `BasicAuthenticationFilter`，且不包含 `BearerTokenAuthenticationFilter`
  - 方法 `traceIdFilterIsPresentInBothChains_asCrossCuttingConcern`：
    - 断言两条链都包含 `TraceIdFilter`（证明“跨链路的自定义 Filter”确实都装载）
  - 为该 Lab 在 Guide 分支 S1/S3 中提供断点锚点：`FilterChainProxy#doFilterInternal` / `DefaultSecurityFilterChain#matches`。

### 1.3 补齐“对照坑点/边界”的可断言证据链（新增测试入口）

- [√] 1.3 在 `springboot-security/src/test/java/com/learning/springboot/bootsecurity/part01_security/BootSecurityLabTest.java` 补齐以下“更深一层边界”的可断言入口（避免仅靠文档描述）：
  - 新增方法 `adminEndpointReturns403WhenAuthorityAdminButMissingRolePrefix_asPitfall`：
    - `@WithMockUser(authorities = "ADMIN")` 请求 `/api/admin/ping`
    - 断言 403（解释 `hasRole("ADMIN")` 需要 `ROLE_ADMIN` 前缀）
  - 新增方法 `jwtSecureEndpointReturns401WhenBearerPrefixMissing_asPitfall`：
    - 用 `JwtTokenService#issueToken` 发 token，但 header 写成 `Authorization: <token>`（缺少 `Bearer ` 前缀）
    - 断言 401（并绑定到 JWT 章节坑点）

### 1.4 章节对照坑点/边界：01-basic-auth-and-authorization（新增 ≥1）

- [√] 1.4 在 `docs/security/springboot-security/part-01-security/01-basic-auth-and-authorization.md` 新增 **坑点 2**（可断言）：
  - 主题：`hasRole("ADMIN")` 与 `authorities("ADMIN")` 的前缀边界（ROLE_）
  - Verification：`BootSecurityLabTest#adminEndpointReturns403WhenAuthorityAdminButMissingRolePrefix_asPitfall`
  - Breakpoints：`SecurityConfig#apiChain`（规则定义）/ `JsonAccessDeniedHandler#handle`（403 塑形）

### 1.5 章节对照坑点/边界：02-csrf（新增 ≥1）

- [√] 1.5 在 `docs/security/springboot-security/part-01-security/02-csrf.md` 新增 **坑点 2**（可断言）：
  - 主题：**“我禁用了 CSRF 但还是 403”** 的常见原因：禁用发生在另一条 `SecurityFilterChain`（请求命中错链）
  - Verification：`BootSecurityLabTest#csrfBlocksPostEvenWhenAuthenticated` + `BootSecurityLabTest#jwtPostDoesNotRequireCsrf` + `BootSecurityMultiFilterChainOrderLabTest#jwtPathMatchesJwtChain_andApiPathMatchesBasicChain`
  - Breakpoints：`FilterChainProxy#doFilterInternal` / `DefaultSecurityFilterChain#matches` / `CsrfFilter#doFilterInternal`

### 1.6 章节对照坑点/边界：03-method-security-and-proxy（新增 ≥1）

- [√] 1.6 在 `docs/security/springboot-security/part-01-security/03-method-security-and-proxy.md` 做两件事：
  - 把现有“散落描述”整理成标准坑点结构（坑点 1：self-invocation 绕过）
    - Verification：`BootSecurityLabTest#selfInvocationBypassesMethodSecurityAsAPitfall`
    - Breakpoints：`SelfInvocationPitfallService#outerCallsAdminOnly` / `SelfInvocationPitfallService#adminOnly`
  - 新增 **坑点 2**（可断言）：roles vs authorities 前缀差异会让 `@PreAuthorize("hasRole('ADMIN')")` 误判
    - Verification：新增一个 method-security 入口（可复用 1.3 的思路，或新增 `@WithMockUser(authorities="ADMIN")` 调用 `AdminOnlyService#adminOnlyAction` 断言 `AccessDeniedException`）
    - Breakpoints：`AdminOnlyService#adminOnlyAction`

### 1.7 章节对照坑点/边界：04-filter-chain-and-order（新增 ≥1）

- [√] 1.7 在 `docs/security/springboot-security/part-01-security/04-filter-chain-and-order.md` 新增 **坑点 2**（可断言）：
  - 主题：只凭“响应码/行为”判断命中哪条链路容易误判，推荐用 `FilterChainProxy#getFilters` 建立证据链
  - Verification：`BootSecurityMultiFilterChainOrderLabTest#jwtPathMatchesJwtChain_andApiPathMatchesBasicChain`
  - Breakpoints：`FilterChainProxy#doFilterInternal` / `DefaultSecurityFilterChain#matches`

### 1.8 章节对照坑点/边界：05-jwt-stateless（新增 ≥1）

- [√] 1.8 在 `docs/security/springboot-security/part-01-security/05-jwt-stateless.md` 补齐 `F. 常见坑与边界`（至少 2 条，保证有 ≥1 新增且可断言）：
  - 坑点 1（新增，可断言）：Authorization header 缺少 `Bearer ` 前缀 → 401
    - Verification：`BootSecurityLabTest#jwtSecureEndpointReturns401WhenBearerPrefixMissing_asPitfall`
    - Breakpoints：`JsonAuthenticationEntryPoint#commence`
  - 坑点 2（复述并绑定证据链）：scope → `SCOPE_xxx`，写错 authority 会导致 403
    - Verification：`BootSecurityLabTest#jwtAdminEndpointReturns403WhenScopeMissing`
    - Breakpoints：`SecurityConfig#jwtApiChain`

### 1.9 Appendix：90-common-pitfalls（新增 ≥1，可断言）

- [√] 1.9 在 `docs/security/springboot-security/appendix/90-common-pitfalls.md`：
  - 把“清单式条目”至少补齐 1 个为标准结构坑点（Symptom/Root Cause/Verification/Breakpoints/Fix）
  - 推荐新增条目：多 `SecurityFilterChain` 命中错链导致 CSRF/认证行为异常
    - Verification：`BootSecurityMultiFilterChainOrderLabTest#jwtPathMatchesJwtChain_andApiPathMatchesBasicChain`
    - Breakpoints：`FilterChainProxy#doFilterInternal` / `DefaultSecurityFilterChain#matches`

### 1.10 Self-check：99-self-check（新增 ≥1 自测题 + ≥1 对照坑点）

- [√] 1.10 在 `docs/security/springboot-security/appendix/99-self-check.md`：
  - 自测题新增 ≥1：要求读者能给出“断点锚点 + 对应可跑入口”（例如：如何证明命中哪条 filter chain？）
  - 新增 **坑点 2**（可断言）：链路分流没验证导致排障走错方向
    - Verification：`BootSecurityMultiFilterChainOrderLabTest#jwtPathMatchesJwtChain_andApiPathMatchesBasicChain`

### 1.11 模块回归

- [√] 1.11 运行模块测试：`mvn -pl springboot-security test`。

---

## 2. springboot-data-jpa

### 2.1 Guide 源码级深化（关键分支 → call chain + 断点锚点 + playbook）

- [√] 2.1 在 `docs/data-jpa/springboot-data-jpa/part-00-guide/00-deep-dive-guide.md` 增补以下内容：
  - 关键分支矩阵：每条分支补齐 `Call Chain Sketch` + `Breakpoints` + `Evidence (Lab/Test#method)`。
  - 分支 J1（补齐）：**save 的入口语义：persist vs merge（新旧实体分流）**
    - Evidence：新增 `BootDataJpaMergeAndDetachLabTest#mergeReturnsManagedCopy_andOriginalStaysDetached`
    - Breakpoints：
      - spring-data：`org.springframework.data.jpa.repository.support.SimpleJpaRepository#save`
      - jpa：`jakarta.persistence.EntityManager#persist` / `EntityManager#merge`
  - 分支 J2（补齐）：**managed/detached 边界：clear/detach 之后 dirty checking 不再发生**
    - Evidence：`BootDataJpaLabTest#entityManagerClearDetachesEntities` / `BootDataJpaLabTest#dirtyCheckingPersistsChangesOnFlush`
    - Breakpoints：`EntityManager#clear` / `EntityManager#contains` / `EntityManager#flush`
  - 分支 J3（补齐）：**flush vs commit：flush 让 SQL 执行但不代表对外可见**
    - Evidence：`BootDataJpaLabTest#flushMakesRowsVisibleToJdbcTemplateWithinSameTransaction`
    - Breakpoints：`EntityManager#flush`
  - 分支 J4（补齐）：**getReferenceById：lazy proxy 的“何时触发 SQL”**
    - Evidence：`BootDataJpaLabTest#getReferenceByIdReturnsALazyProxy_andInitializesOnPropertyAccess`
    - Breakpoints：`LibraryAuthorRepository#getReferenceById`（入口）+（可选）Hibernate 初始化点（以测试断言为主）
  - 分支 J5（补齐）：**N+1 证据链：用 Hibernate Statistics 把 SQL 数量变成断言**
    - Evidence：`BootDataJpaLabTest#nPlusOneHappensWhenAccessingLazyCollections` / `BootDataJpaLabTest#entityGraphCanAvoidNPlusOne_whenFetchingCollections`
    - Breakpoints：`org.hibernate.stat.Statistics#getPrepareStatementCount`（证据链）
  - Debug Playbook（新增 5–8 条）：
    - P1：现象“改了对象但没落库” → 分支判定（detached vs managed）→ 断点：`EntityManager#contains` / `EntityManager#clear` → 入口：`BootDataJpaLabTest#entityManagerClearDetachesEntities`
    - P2：现象“merge 后改了对象没生效/改错对象” → 分支判定（merge 返回 managed copy）→ 断点：`EntityManager#merge` → 入口：`BootDataJpaMergeAndDetachLabTest#mergeReturnsManagedCopy_andOriginalStaysDetached`
    - P3：现象“JDBC 查不到/查到了但误以为已提交” → 分支判定（flush vs commit）→ 断点：`EntityManager#flush` → 入口：`BootDataJpaLabTest#flushMakesRowsVisibleToJdbcTemplateWithinSameTransaction`
    - P4：现象“SQL 数量暴涨” → 分支判定（N+1）→ 断点：Statistics 计数 → 入口：`BootDataJpaLabTest#nPlusOneHappensWhenAccessingLazyCollections`
    - P5：现象“LazyInitializationException” → 分支判定（事务/Session 已结束）→ 入口：新增 `BootDataJpaLabTest#lazyInitializationExceptionWhenAccessingLazyOutsidePersistenceContext_asPitfall`

### 2.2 新增默认 Lab：merge/detached/managed 语义对照（证据链）

- [√] 2.2 在 `springboot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaMergeAndDetachLabTest.java` 新增默认 Lab（建议 `@DataJpaTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")`）：
  - 方法 `mergeReturnsManagedCopy_andOriginalStaysDetached`：
    - 准备：save 一本书 → flush + clear → 得到 detached instance（重新查/或保存引用后 clear）
    - 执行：`Book merged = entityManager.merge(detached)`
    - 断言：`entityManager.contains(merged) == true`；`merged != detached`（引用不相同）
    - 断言：修改 `detached` 不会落库；修改 `merged` + flush 才会落库（最终 clear 后重新查询验证）
  - 方法 `mergeIsNotMagic_needsFlushToPersistChanges_asBoundary`（可选但推荐）：
    - 断言 merge 后未 flush 前，“你以为写入”其实仍停留在 persistence context（用 clear+reload 或 JDBC count 对照）

### 2.3 章节对照坑点/边界：01-entity-states（新增 ≥1）

- [√] 2.3 在 `docs/data-jpa/springboot-data-jpa/part-01-data-jpa/01-entity-states.md` 新增 **坑点 2**（可断言）：
  - 主题：merge 返回的是 managed copy，原对象仍 detached（改错对象导致“看似没生效”）
  - Verification：`BootDataJpaMergeAndDetachLabTest#mergeReturnsManagedCopy_andOriginalStaysDetached`
  - Breakpoints：`EntityManager#merge` / `EntityManager#contains`

### 2.4 章节对照坑点/边界：02-persistence-context（新增 ≥1）

- [√] 2.4 在 `docs/data-jpa/springboot-data-jpa/part-01-data-jpa/02-persistence-context.md` 新增 **坑点 2**（可断言）：
  - 主题：“同一事务里两次 find 得到的是同一个对象”导致你误以为是 DB 的最新事实（identity map 误导）
  - Verification：新增 `BootDataJpaLabTest#sameEntityLoadedTwiceIsSameInstanceWithinPersistenceContext_asPitfall`（断言 `ref1 == ref2`，clear 后 `ref1 != ref3`）
  - Breakpoints：`EntityManager#find` / `EntityManager#clear`

### 2.5 章节对照坑点/边界：03-flush-and-visibility（新增 ≥1）

- [√] 2.5 在 `docs/data-jpa/springboot-data-jpa/part-01-data-jpa/03-flush-and-visibility.md` 新增 **坑点 2**（可断言）：
  - 主题：FlushMode.AUTO：某些查询会触发自动 flush，导致你以为“只是读”但出现写 SQL
  - Verification：新增 `BootDataJpaLabTest#queryMayTriggerAutoFlush_asPitfall`（用 Hibernate Statistics 断言查询前后 statement count 变化）
  - Breakpoints：`EntityManager#flush`（观察谁触发）

### 2.6 章节对照坑点/边界：04-dirty-checking（新增 ≥1）

- [√] 2.6 在 `docs/data-jpa/springboot-data-jpa/part-01-data-jpa/04-dirty-checking.md` 新增 **坑点 2**（可断言）：
  - 主题：你以为“只读查询”，但在同一 persistence context 里对 managed entity 的改动会在 flush/commit 时自动写回（无 save 也会更新）
  - Verification：复用 `BootDataJpaLabTest#dirtyCheckingPersistsChangesOnFlush`，并新增一个对照测试（可选）：`BootDataJpaLabTest#accidentalDirtyChangeGetsPersisted_asPitfall`
  - Breakpoints：`EntityManager#flush`

### 2.7 章节对照坑点/边界：05-fetching-and-n-plus-one（新增 ≥1）

- [√] 2.7 在 `docs/data-jpa/springboot-data-jpa/part-01-data-jpa/05-fetching-and-n-plus-one.md` 新增 **坑点 2**（可断言）：
  - 主题：事务外访问 lazy 关联 → `LazyInitializationException`
  - Verification：新增 `BootDataJpaLabTest#lazyInitializationExceptionWhenAccessingLazyOutsidePersistenceContext_asPitfall`
    - 建议实现：保存 author+books → flush+clear → 获取 entity → 结束事务/清掉上下文后访问 lazy 集合并断言异常
  - Breakpoints：lazy 初始化点（以异常与断言为主，避免过度依赖 Hibernate 内部类名）

### 2.8 章节对照坑点/边界：06-datajpatest-slice（新增 ≥1）

- [√] 2.8 在 `docs/data-jpa/springboot-data-jpa/part-01-data-jpa/06-datajpatest-slice.md` 新增 **坑点 2**（可断言）：
  - 主题：`@DataJpaTest` 的数据库/上下文边界与生产不一致（常见：H2 dialect/行为差异）
  - Verification：新增 `BootDataJpaLabTest#dataJpaTestUsesH2DatabaseByDefault_asBoundary`（断言 DataSource JDBC URL 包含 `jdbc:h2:`）
  - Breakpoints：无强制（以断言为证据链）

### 2.9 章节对照坑点/边界：07-debug-sql（新增 ≥1）

- [√] 2.9 在 `docs/data-jpa/springboot-data-jpa/part-01-data-jpa/07-debug-sql.md` 补齐 `F. 常见坑与边界`（新增 ≥1，可断言）：
  - 推荐坑点：只看 show-sql 容易误判，建议用 Hibernate Statistics 固化“SQL 数量”证据链（N+1/自动 flush）
  - Verification：`BootDataJpaLabTest#nPlusOneHappensWhenAccessingLazyCollections` / `BootDataJpaLabTest#entityGraphCanAvoidNPlusOne_whenFetchingCollections` / `BootDataJpaLabTest#queryMayTriggerAutoFlush_asPitfall`

### 2.10 Appendix：90-common-pitfalls（新增 ≥1，可断言）

- [√] 2.10 在 `docs/data-jpa/springboot-data-jpa/appendix/90-common-pitfalls.md`：
  - 至少新增 1 个“标准结构坑点”（推荐：merge 返回新对象/改错对象）
  - 绑定 Verification：`BootDataJpaMergeAndDetachLabTest#mergeReturnsManagedCopy_andOriginalStaysDetached`

### 2.11 Self-check：99-self-check（新增 ≥1 自测题 + ≥1 对照坑点）

- [√] 2.11 在 `docs/data-jpa/springboot-data-jpa/appendix/99-self-check.md`：
  - 自测题新增 ≥1：要求回答时必须给出“断点锚点 + 可跑入口”（例如：如何证明 query 触发 auto flush？）
  - 新增 **坑点 2**（可断言）：merge 的对象语义（改错对象）
    - Verification：`BootDataJpaMergeAndDetachLabTest#mergeReturnsManagedCopy_andOriginalStaysDetached`

### 2.12 模块回归

- [√] 2.12 运行模块测试：`mvn -pl springboot-data-jpa test`。

---

## 3. spring-core-events

### 3.1 Guide 源码级深化（关键分支 → call chain + 断点锚点 + playbook）

- [√] 3.1 在 `docs/events/spring-core-events/part-00-guide/00-deep-dive-guide.md`：
  - 为现有 5 条关键分支逐条补齐：call chain sketch + 断点锚点（class#method）+ Evidence（Lab/Test#method）。
  - 新增关键分支 E6（新增）：**listener filtering：supportsEventType/sourceType 导致“为什么没触发”**
    - Evidence：新增 `SpringCoreEventsListenerFilteringLabTest#smartListenerFiltersByEventTypeAndSourceType`
    - Breakpoints：
      - spring：`org.springframework.context.event.SimpleApplicationEventMulticaster#multicastEvent`
      - spring：`org.springframework.context.event.AbstractApplicationEventMulticaster#getApplicationListeners`
      - spring：`org.springframework.context.event.GenericApplicationListenerAdapter#supportsEventType`（或等价过滤入口，按实际 class 存在性调整）
  - Debug Playbook（新增 5–8 条）：
    - P1：现象“listener 没触发” → 分支判定（类型不匹配/condition 过滤/supportsEventType 过滤）→ 断点：`getApplicationListeners` → 入口：`SpringCoreEventsListenerFilteringLabTest#smartListenerFiltersByEventTypeAndSourceType`
    - P2：现象“顺序不对” → 分支判定（@Order 是否声明/是否同一事件类型）→ 入口：`SpringCoreEventsLabTest#orderedListenersFollowOrderAnnotation`
    - P3：现象“异常炸回发布方” → 入口：`SpringCoreEventsMechanicsLabTest#listenerExceptionsPropagateToPublisher_byDefault` → 断点：`SimpleApplicationEventMulticaster#multicastEvent`
    - P4：现象“@Async 不异步” → 入口：`SpringCoreEventsMechanicsLabTest#asyncAnnotationIsIgnored_withoutEnableAsync`
    - P5：现象“afterCommit 没触发/回滚仍有副作用” → 入口：`SpringCoreEventsTransactionalEventLabTest#afterCommitDoesNotRunOnRollback_butAfterRollbackDoes`

### 3.2 新增默认 Lab：listener filtering（supportsEventType/sourceType）

- [√] 3.2 在 `spring-core-events/src/test/java/com/learning/springboot/springcoreevents/part01_event_basics/SpringCoreEventsListenerFilteringLabTest.java` 新增默认 Lab（建议使用 `AnnotationConfigApplicationContext`，避免依赖完整 Boot 上下文）：
  - 方法 `smartListenerFiltersByEventTypeAndSourceType`：
    - 准备一个 `SmartApplicationListener`（或 `GenericApplicationListener`）实现：
      - `supportsEventType` 只接受 `UserRegisteredEvent`（或自定义 `ApplicationEvent`）
      - `supportsSourceType` 只接受某个 source 类型（例如 `String.class`）
    - publish 3 次：
      - eventType 匹配 + source 匹配 → 触发
      - eventType 匹配 + source 不匹配 → 不触发
      - eventType 不匹配 → 不触发
    - 断言触发计数/记录列表（CopyOnWriteArrayList）
  - Breakpoints：`AbstractApplicationEventMulticaster#getApplicationListeners`（观察过滤发生点）

### 3.3 章节对照坑点/边界：01-event-mental-model（新增 ≥1）

- [√] 3.3 在 `docs/events/spring-core-events/part-01-event-basics/01-event-mental-model.md` 新增 **坑点 2**（可断言）：
  - 主题：事件对象可变会导致多个 listener 互相污染（建议 immutable record）
  - Verification：新增一个最小测试（推荐放在 `SpringCoreEventsMechanicsLabTest`）：
    - `mutableEventCanBeMutatedAcrossListeners_asPitfall`（listener1 改字段，listener2 观察到变化）
  - Breakpoints：监听器方法本身（以数据变化作为证据）

### 3.4 章节对照坑点/边界：02-multiple-listeners-and-order（新增 ≥1）

- [√] 3.4 在 `docs/events/spring-core-events/part-01-event-basics/02-multiple-listeners-and-order.md` 新增 **坑点 2**（可断言）：
  - 主题：`@Order` 值越小越先执行（常见误解：以为越大越先）
  - Verification：`SpringCoreEventsLabTest#orderedListenersFollowOrderAnnotation`

### 3.5 章节对照坑点/边界：03-condition-and-payload（新增 ≥1）

- [√] 3.5 在 `docs/events/spring-core-events/part-01-event-basics/03-condition-and-payload.md` 新增 **坑点 3**（可断言）：
  - 主题：listener 没触发可能不是没注册，而是被 condition 过滤（不会报错）
  - Verification：`SpringCoreEventsLabTest#conditionalEventListenerOnlyRunsWhenConditionMatches`
  - Breakpoints：`ApplicationListenerMethodAdapter#shouldHandle`（或等价判断点，按版本存在性调整）

### 3.6 章节对照坑点/边界：04-sync-and-exceptions（新增 ≥1）

- [√] 3.6 在 `docs/events/spring-core-events/part-01-event-basics/04-sync-and-exceptions.md` 新增 **坑点 2**（可断言）：
  - 主题：一个 listener 抛异常会中断后续 listener（默认行为：异常传播 + dispatch 终止）
  - Verification：新增 `SpringCoreEventsMechanicsLabTest#exceptionStopsDispatch_andLaterListenersNotInvoked_asPitfall`

### 3.7 章节对照坑点/边界：05-async-listener（新增 ≥1）

- [√] 3.7 在 `docs/events/spring-core-events/part-02-async-and-transactional/05-async-listener.md` 补齐 `F. 常见坑与边界`（至少 1 条可断言）：
  - 坑点 1：没 `@EnableAsync` 时 `@Async` 被忽略（“看起来写了但没生效”）
    - Verification：`SpringCoreEventsMechanicsLabTest#asyncAnnotationIsIgnored_withoutEnableAsync`
  - 坑点 2（可选但推荐）：异步 listener 切线程导致 ThreadLocal/事务上下文不可用
    - Verification：新增 `SpringCoreEventsMechanicsLabTest#threadLocalIsNotPropagated_toAsyncListener_asPitfall`

### 3.8 章节对照坑点/边界：06-async-multicaster（新增 ≥1）

- [√] 3.8 在 `docs/events/spring-core-events/part-02-async-and-transactional/06-async-multicaster.md` 新增 **坑点 2**（可断言）：
  - 主题：async multicaster 下 listener 异常不会炸回发布方（需要 ErrorHandler 承接）
  - Verification：新增 `SpringCoreEventsAsyncMulticasterLabTest#asyncMulticasterDoesNotPropagateListenerException_toPublisher_asPitfall`：
    - multicaster.setTaskExecutor(...) + setErrorHandler(...) 捕获异常
    - publishEvent 不抛异常，但 errorHandler 收到异常（用 latch/AtomicReference 断言）

### 3.9 章节对照坑点/边界：07-transactional-event-listener（新增 ≥1）

- [√] 3.9 在 `docs/events/spring-core-events/part-02-async-and-transactional/07-transactional-event-listener.md` 补齐 `F. 常见坑与边界`（至少 1 条可断言）：
  - 坑点 1：普通 `@EventListener` 不理解事务边界，回滚也已经产生副作用
    - Verification：新增 `SpringCoreEventsTransactionalEventLabTest#syncListenerRunsEvenWhenRollback_butAfterCommitDoesNot_asPitfall`（对照 `@TransactionalEventListener(AFTER_COMMIT)`）
  - Breakpoints：`TransactionalApplicationListenerMethodAdapter#onApplicationEvent`

### 3.10 Appendix：90-common-pitfalls（新增 ≥1，可断言）

- [√] 3.10 在 `docs/events/spring-core-events/appendix/90-common-pitfalls.md` 新增 ≥1 条标准结构坑点（推荐：listener filtering 导致没触发）：
  - Verification：`SpringCoreEventsListenerFilteringLabTest#smartListenerFiltersByEventTypeAndSourceType`
  - Breakpoints：`AbstractApplicationEventMulticaster#getApplicationListeners`

### 3.11 Self-check：99-self-check（新增 ≥1 自测题 + ≥1 对照坑点）

- [√] 3.11 在 `docs/events/spring-core-events/appendix/99-self-check.md`：
  - 自测题新增 ≥1：要求“给出断点锚点 + 对应可跑入口”
  - 新增 **坑点 2**（可断言）：listener filtering/condition 过滤导致“没触发”的分流排障
    - Verification：`SpringCoreEventsListenerFilteringLabTest#smartListenerFiltersByEventTypeAndSourceType` + `SpringCoreEventsLabTest#conditionalEventListenerOnlyRunsWhenConditionMatches`

### 3.12 模块回归

- [√] 3.12 运行模块测试：`mvn -pl spring-core-events test`。

---

## 4. springboot-web-client

### 4.1 Guide 源码级深化（关键分支 → call chain + 断点锚点 + playbook）

- [√] 4.1 在 `docs/web-client/springboot-web-client/part-00-guide/00-deep-dive-guide.md`：
  - 为现有 4 条关键分支逐条补齐：call chain sketch + 断点锚点（class#method）+ Evidence（Lab/Test#method）。
  - 新增关键分支 W5（新增）：**WebClient filter 链顺序 + “必须调用 next.exchange”边界**
    - Evidence：新增 `BootWebClientWebClientFilterOrderLabTest#filtersWrapInOrder_andNextExchangeIsRequired`
    - Breakpoints：`org.springframework.web.reactive.function.client.ExchangeFilterFunction#filter` / `org.springframework.web.reactive.function.client.DefaultWebClient#exchange`
  - Debug Playbook（新增 5–8 条）：
    - P1：现象“请求没发出去（server 没收到）” → 分支判定（Mono 未订阅 / filter 未调用 next.exchange）→ 入口：`BootWebClientWebClientFilterOrderLabTest#filterThatDoesNotCallNext_shortCircuitsRequest_asPitfall`
    - P2：现象“4xx/5xx 没映射成领域异常” → 入口：`BootWebClientWebClientLabTest#webClientMaps400ToDomainException`
    - P3：现象“timeout 不生效/测试 flaky” → 入口：`BootWebClientWebClientLabTest#webClientResponseTimeoutFailsFast`
    - P4：现象“retry 次数不对/把 4xx 也重试” → 入口：`BootWebClientWebClientLabTest#webClientRetriesOn5xxAndEventuallySucceeds`
    - P5：现象“header 没注入/顺序不对” → 入口：`BootWebClientWebClientLabTest#webClientSendsExpectedPathAndHeaders` + `BootWebClientWebClientFilterOrderLabTest#filtersWrapInOrder_andNextExchangeIsRequired`

### 4.2 新增默认 Lab：WebClient filter 链顺序 + next.exchange 边界（纯内存，不依赖网络）

- [√] 4.2 在 `springboot-web-client/src/test/java/com/learning/springboot/bootwebclient/part01_web_client/BootWebClientWebClientFilterOrderLabTest.java` 新增默认 Lab（建议用 `ExchangeFunction` stub，而不是 MockWebServer）：
  - 方法 `filtersWrapInOrder_andNextExchangeIsRequired`：
    - 构造 `List<String> events` 记录顺序
    - 定义 2 个 filters：`A` 与 `B`，都在调用 `next.exchange` 前后写入 events（before/after）
    - ExchangeFunction stub：记录 `exchange` 被调用，并返回固定的 200 JSON 响应
    - 断言顺序：`A:before → B:before → exchange → B:after → A:after`
  - 方法 `filterThatDoesNotCallNext_shortCircuitsRequest_asPitfall`：
    - 定义一个 filter 直接 `return Mono.error(...)`（不调用 next.exchange）
    - 断言 exchange 次数为 0（证明请求根本没发出去）

### 4.3 章节对照坑点/边界：01-restclient-basics（新增 ≥1）

- [√] 4.3 在 `docs/web-client/springboot-web-client/part-01-web-client/01-restclient-basics.md` 新增 **坑点 2**（可断言）：
  - 主题：只配置/只关注 happy path，没把 timeout 做成确定性实验（导致线上挂死/测试 flaky）
  - Verification：`BootWebClientRestClientLabTest#restClientReadTimeoutFailsFast`
  - Breakpoints：`RestClientGreetingClient` 的 timeout 配置入口（按实际类/方法补齐到 class#method）

### 4.4 章节对照坑点/边界：02-webclient-basics（新增 ≥1）

- [√] 4.4 在 `docs/web-client/springboot-web-client/part-01-web-client/02-webclient-basics.md` 新增 **坑点 2**（可断言）：
  - 主题：`Mono` 是 lazy 的，不订阅就不会发请求（“看起来调用了 client，其实什么都没发生”）
  - Verification：新增 `BootWebClientWebClientLabTest#webClientDoesNothingUntilSubscribed_asPitfall`：
    - 调用 `client.getGreeting("Alice")` 但不订阅
    - 断言 `server.getRequestCount() == 0`
  - Breakpoints：`WebClientGreetingClient#getGreeting`（构造链路）

### 4.5 章节对照坑点/边界：03-error-handling（新增 ≥1）

- [√] 4.5 在 `docs/web-client/springboot-web-client/part-01-web-client/03-error-handling.md` 新增 **坑点 2**（可断言）：
  - 主题：`onStatus` 只覆盖 HTTP status，timeout/连接失败走的是异常分支（需要独立处理或至少在测试里覆盖）
  - Verification：`BootWebClientWebClientLabTest#webClientResponseTimeoutFailsFast`（对照 4xx/5xx mapping）

### 4.6 章节对照坑点/边界：04-timeout-and-retry（新增 ≥1）

- [√] 4.6 在 `docs/web-client/springboot-web-client/part-01-web-client/04-timeout-and-retry.md` 新增 **坑点 2**（可断言）：
  - 主题：retry 的“次数语义”容易 off-by-one（maxAttempts vs retry 次数）
  - Verification：`BootWebClientWebClientLabTest#webClientRetriesOn5xxAndEventuallySucceeds`（必须提及 request count=2 的证据链）

### 4.7 章节对照坑点/边界：05-testing-with-mockwebserver（新增 ≥1）

- [√] 4.7 在 `docs/web-client/springboot-web-client/part-01-web-client/05-testing-with-mockwebserver.md` 把 `F. 常见坑与边界` 补齐为标准结构坑点（至少 1 条可断言）：
  - 推荐坑点：只断言响应不读取/断言 `RecordedRequest`，容易让“请求契约”悄悄变坏
  - Verification：`BootWebClientRestClientLabTest#restClientSendsExpectedPathAndHeaders` / `BootWebClientWebClientLabTest#webClientSendsExpectedPathAndHeaders`

### 4.8 Appendix：90-common-pitfalls（新增 ≥1，可断言）

- [√] 4.8 在 `docs/web-client/springboot-web-client/appendix/90-common-pitfalls.md`：
  - 新增 ≥1 条标准结构坑点（推荐：Mono 未订阅/Filter 未调用 next.exchange 导致“请求没发出去”）
  - Verification：`BootWebClientWebClientFilterOrderLabTest#filterThatDoesNotCallNext_shortCircuitsRequest_asPitfall` + `BootWebClientWebClientLabTest#webClientDoesNothingUntilSubscribed_asPitfall`

### 4.9 Self-check：99-self-check（新增 ≥1 自测题 + ≥1 对照坑点）

- [√] 4.9 在 `docs/web-client/springboot-web-client/appendix/99-self-check.md`：
  - 自测题新增 ≥1：要求给出“断点锚点 + 可跑入口”（例如：如何证明 filter 链顺序？）
  - 新增 **坑点 2**（可断言）：filter short-circuit/Mono 未订阅导致误判为网络问题
    - Verification：`BootWebClientWebClientFilterOrderLabTest#filterThatDoesNotCallNext_shortCircuitsRequest_asPitfall`

### 4.10 模块回归

- [√] 4.10 运行模块测试：`mvn -pl springboot-web-client test`。

---

## 5. Knowledge Base Sync（helloagents/wiki）

- [√] 5.1 同步模块知识库：`helloagents/wiki/modules/springboot-security.md`、`helloagents/wiki/modules/springboot-data-jpa.md`
  - 明确写入本批次新增的“关键分支列表 + 断点锚点（class#method） + 默认 Lab/Test 入口（类#方法）”
- [√] 5.2 同步模块知识库：`helloagents/wiki/modules/spring-core-events.md`、`helloagents/wiki/modules/springboot-web-client.md`
  - 明确写入本批次新增的“关键分支列表 + 断点锚点（class#method） + 默认 Lab/Test 入口（类#方法）”

## 6. Security Check

- [√] 6.1 执行安全检查（按 G9）：
  - 敏感信息/密钥：确保文档示例与测试日志不输出 token/Authorization 全量
  - 权限边界：Security 模块新增用例不引入生产级账号/密钥

## 7. Testing & Gates

- [√] 7.1 运行全仓库测试：`mvn test`。

## 8. Changelog & Migration

- [√] 8.1 更新 `helloagents/CHANGELOG.md`：记录本批次新增默认 Lab、Guide 深挖点（按模块列举）。
- [√] 8.2 迁移方案包到 `helloagents/history/2026-01/` 并更新 `helloagents/history/index.md`（按 develop 技能规则）。
