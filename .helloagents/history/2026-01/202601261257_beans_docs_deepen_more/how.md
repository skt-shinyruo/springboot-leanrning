# Technical Design: spring-core-beans 文档与 Labs 深化（证据链 + 边界 Case）

## Technical Solution

### Core Technologies

- Java + Spring Framework（容器/BeanFactory/BeanDefinition）
- JUnit 5（JUnit Platform Suite 聚合入口）
- 现有 testsupport 工具（例如 BeanDefinition 元信息 dump）

### Implementation Key Points

1. **“Lab 先行”强化证据链**
   - 每个边界主题优先落一个“最小可跑实验”（GenericApplicationContext / AnnotationConfigApplicationContext）。
   - 断言以“现象可复述 + 失败形态可观测”为核心，不追求复杂集成。
2. **证据链级文档（粒度 3）**
   - 每章补齐：最短调用链、关键分支条件、关键数据结构变化（watch list）、最小源码片段/伪代码对照、断点入口、常见误解与边界。
3. **将“排障分流”与“实验入口”绑定**
   - 文档中的排障分流表给出：怎么复现（Lab）、去哪下断点（入口点）、看什么（watch list）、如何判断（断言/日志）。
4. **可维护性优先**
   - 避免引入外部依赖与复杂框架。
   - explore 类型实验通过现有开关（如 `springcorebeans.explore=true`）隔离。

## Security and Performance

- **Security:** 不涉及生产环境操作、不引入敏感信息、不落地密钥；不新增危险命令。
- **Performance:** 新增 Labs 保持最小化；尽量使用轻量容器（GenericApplicationContext）避免启动开销；探索性测试走 explore 开关。

## Testing and Deployment

- **Testing:**
  - `mvn -pl :spring-core-beans test`
  - 对新增/修改章节对应的 Lab 进行点跑验证
- **Docs Build:**
  - `python3 -m mkdocs build -f docs-site/mkdocs.yml`
- **Deployment:** 本次不涉及发布流程变更

