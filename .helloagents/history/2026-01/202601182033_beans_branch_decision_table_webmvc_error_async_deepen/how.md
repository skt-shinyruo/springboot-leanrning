# Technical Design: 深化主线叙事的“可复用排障套路”（Beans 决策表 + Web MVC error/async 证据链）

## Technical Solution

### Core Technologies

- Documentation: Markdown（GitHub Pages / MkDocs 聚合）
- Spring Framework `6.2.15`（由 Spring Boot `3.5.9` 管理版本）
- Verification: 仓库内 `*LabTest`（MockMvc / 容器启动 trace）

### Implementation Key Points

1. `spring-core-beans`：在主线章增加“分支决策表”小节，将常见现象映射到：
   - 所在阶段（refresh 第几幕 / getBean/doCreateBean 哪一段）
   - 关键方法（可下断点）
   - 必看变量（可解释分支走向）
   - 对应 LabTest（可复现、可回归）

2. `springboot-web-mvc`：在 `DispatcherServlet` 主链路章补齐两条“连续叙事”：
   - **异常链**：FilterChain → doDispatch → processDispatchResult/processHandlerException → resolvers 是否处理 → 未处理则抛出 → 进入 Boot error（/error、ErrorAttributes、错误页模板/JSON）
   - **async 链**：REQUEST dispatch（启动异步并提前返回）→ ASYNC dispatch（二次进入 DispatcherServlet 完成写回），用时间线串起来，并提供可断言证据链（Trace Lab 的事件序列）

3. 保持“像书一样”的连续阅读体验：
   - 主线段落尽量用“问题 → 机制 → 断点/变量 → Lab”结构推进
   - 对已在 Part 02 讲过的错误页/内容协商，只在本章给出承接链接，避免重复写一遍

## Security and Performance

- **Security:** 仅文档改动，不涉及生产环境操作、不引入密钥/外部依赖；不新增危险脚本
- **Performance:** 无运行时影响

## Testing and Deployment

- **Testing:**
  - `mvn -q -pl spring-core-beans test`
  - `mvn -q -pl springboot-web-mvc test`
- **Deployment:** 文档由 GitHub Pages/MkDocs 构建发布，按现有流程即可
