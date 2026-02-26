# Technical Design: spring-core-beans 全任务收尾（Docs 一致性 + 闭环补齐）

## Technical Solution

### Core Technologies
- Java 17
- Spring Boot（父 POM 管理）
- JUnit 5（`spring-boot-starter-test`）

### Implementation Key Points

1. **Docs 导航自动化**：以“章节编号顺序”作为 SSOT，脚本生成每章的 prev/toc/next，并以相对路径写入/替换
2. **闭环结构标准化**：对缺少“复现入口/推荐断点/观察点/命令”的章节，补齐统一小节（允许引用 `00-deep-dive-guide.md` 的通用模板，避免重复）
3. **JSR-330 Lab**：新增一个最小可运行测试，覆盖 `@Inject` 与 `Provider<T>` 的核心语义，并对比 Spring 的 `@Autowired` / `ObjectProvider<T>`
4. **testsupport 增强**：提升输出结构化程度，让读者能在测试中快速定位：
   - 候选集合（by type）与最终注入对象
   - 依赖边（depends-on / injected-by）与销毁顺序提示
   - BeanDefinition 来源（resourceDescription/source/factory method/originating）

## Security and Performance

- **Security:** 不引入密钥/令牌/生产环境操作；仅在测试与文档范围内增强
- **Performance:** docs 批量改动不影响运行时；新增测试保持最小化与可复现

## Testing and Deployment

- **Testing:**
  - `mvn -pl spring-core-beans test`
  - 方法级抽查（关键 Lab）：
    - `SpringCoreBeansAutowireCandidateSelectionLabTest`
    - `SpringCoreBeansPostProcessorOrderingLabTest`
    - `SpringCoreBeansEarlyReferenceLabTest`
    - `SpringCoreBeansProxyingPhaseLabTest`
    - `SpringCoreBeansValuePlaceholderResolutionLabTest`
- **Deployment:** 无（仅文档/测试增强）

