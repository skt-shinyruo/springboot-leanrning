# Technical Design: spring-core-beans AOT/生产排障/面试体系补齐

## Technical Solution

### Core Technologies
- Java 17
- Spring Boot 3.5.x（依赖 Spring Framework 6.2.x）
- JUnit 5（`spring-boot-starter-test`）

### Implementation Key Points

1) **新增 Part 05 文档目录 + 章节**
- 以“可跑的 Lab → 可下断点 → 可复述结论”为统一契约
- 章节内容尽量复用现有主线（refresh/createBean/resolveDependency）与 `11-debugging-and-observability` 的断点地图

2) **新增 Labs（全部可单测运行）**
- AOT/RuntimeHints：在 JVM 测试中执行 AOT 处理流程，断言 `RuntimeHints` 的存在性/缺失
- XML：用 `XmlBeanDefinitionReader` + `ByteArrayResource` 构建最小示例，观察 BeanDefinition 元信息与错误分型
- AutowireCapableBeanFactory：对容器外对象执行注入与初始化回调，观察行为边界
- SpEL：验证 `@Value("#{...}")` 的表达式解析链路（与占位符解析区别）
- 自定义 Qualifier：通过 meta-annotation 建立“自定义限定符”并验证候选收敛

3) **新增 Appendix（面试模板/排障清单）**
- 不重复解释原理，只做“结构化复述 + 入口链接”
- 每个条目都指向：章节 + 对应 Lab/Test + 推荐断点入口

## Architecture Decision ADR

### ADR-001: AOT 学习验证以“生成 RuntimeHints”为主，不在 CI 中构建 native image
**Context:** native image 构建成本高、依赖环境复杂，学习仓库的 CI/本地环境不一定具备完整链路。  
**Decision:** 在 JVM 单测中运行 AOT 处理流程，聚焦验证 RuntimeHints/代理提示等“可证明的中间产物”。  
**Rationale:** 能保持测试快速、可复现，同时覆盖“为什么 AOT 需要 hints”的核心心智模型。  
**Alternatives:** 直接在测试里构建 native image → 拒绝原因：构建耗时与环境依赖过高。  
**Impact:** AOT 章节会明确说明：这些测试验证的是“构建期契约”，不是 native image 的最终运行结果。

## Security and Performance

- **Security:** 仅新增学习型代码与文档，不引入外部服务、密钥、权限变更；避免在示例中出现敏感信息。
- **Performance:** 新增测试均为轻量级上下文（优先 `GenericApplicationContext` / `ApplicationContextRunner`），避免启动完整应用。

## Testing and Deployment

- **Testing:** 以模块维度回归：`mvn -pl spring-core-beans test`
- **Deployment:** 无部署变更

