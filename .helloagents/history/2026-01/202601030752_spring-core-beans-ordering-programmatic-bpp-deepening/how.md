# Technical Design: spring-core-beans Ordering & Programmatic BPP Deepening

## Technical Solution

### Core Technologies
- Spring Framework（重点：`spring-context` / `spring-beans`）
- JUnit 5 + AssertJ（最小可复现实验）

### Implementation Key Points
1. **文档升级到算法级**
   - 在 docs 中以“源码主线 + 关键集合/状态 + 简化伪代码”描述机制，避免粘贴大段 Spring 源码。
   - 用“可复现实验”闭环关键结论；文档引用本仓库 `src/test/java` 的 Lab。
2. **Ordering 维度拆分**
   - BFPP/BDRPP：定义层（改 BeanDefinition）顺序
   - BPP：实例层（影响 bean 创建/包装）顺序
   - 强调：BPP 包裹顺序 ≠ AOP advisor/interceptor 顺序（跨文档引用即可）
3. **programmatic addBeanPostProcessor 解释闭环**
   - 明确 `addBeanPostProcessor` 的语义是“直接操作 beanFactory 的 BPP list”，因此绕过排序。
   - 补充“时机陷阱”：添加得太晚不会影响已实例化 bean，只影响之后创建的 bean。
4. **测试稳定性策略**
   - 断言我们自己定义的处理器相对顺序，不断言容器内置处理器全序列。
   - 对 `@Order`/order 数值的演示，确保只观察目标 bean 的事件，不受其他 BPP 干扰。

## Testing and Verification
- `mvn -q -pl spring-core-beans test`
- 重点关注：
  - ordering 相关 Lab 的事件序列是否符合“分组 + order 值比较”规则
  - programmatic BPP 相关 Lab 的事件序列是否符合“注册顺序 = 执行顺序”

