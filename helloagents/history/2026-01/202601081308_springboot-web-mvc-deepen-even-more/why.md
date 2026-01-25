# Change Proposal: springboot-web-mvc deepen even more（补齐占位章节 + 工程化边界 Labs）

## Requirement Background

上一轮已补齐：

- MVC 内核：ExceptionResolvers 主线
- Filter/Interceptor：sync + async lifecycle 可断言证据链（Trace）
- 条件请求：静态资源 If-Modified-Since/304 + ShallowEtagHeaderFilter
- Async：DeferredResult（含可控 fallback）+ tests 闭环

但在教学化“更深一层”的目标下，还存在两类明显缺口：

1) **文档完整性缺口（占位/兜底章节）**
- `part-00-guide/00-deep-dive-guide.md` 的主线仍是兜底描述
- `appendix/99-self-check.md` 仍是“兜底入口”，缺少“题目 → 证据链（测试/断点）”映射
- Part 01/02 仍存在 “坑点待补齐” 占位，缺少工程级排障清单与对照入口

2) **工程落地边界缺口（缺少更典型的“风险/边界”可复现 Labs）**
- binder 路径的安全边界（mass assignment）需要用可断言用例固化（避免只停留在概念）
- `@ControllerAdvice` 的匹配/优先级（basePackages、`@Order`、异常选择策略）需要可复现证据链，否则排障会长期靠猜

## Change Content

1. 补齐 docs 的占位/兜底内容：导读主线、自测题证据链映射、以及 Part01/Part02 的坑点章节。
2. 新增两组“工程落地边界”Labs：
   - binder 的 `@InitBinder#setAllowedFields` 防 mass assignment
   - `@ControllerAdvice` 优先级（`@Order`）与匹配规则可断言

## Impact Scope

- **Modules:** `springboot-web-mvc`、知识库 `helloagents/*`
- **Files:** docs 增强 + 新增/扩展 LabTest + 少量 demo controller/DTO
- **APIs:** 仅新增教学端点（限定在 `/api/advanced/**`），不破坏既有契约
- **Data:** 无

## Core Scenarios

### Requirement: 补齐占位章节，形成更可执行的学习闭环
**Module:** springboot-web-mvc
把导读与自测从“文字”升级成“题目 → 证据链（测试/断点/观察点）”，并补齐 Part01/Part02 的坑点段落。

#### Scenario: 读者能按“现象→证据→断点→修复”闭环推进
- 每个坑点至少能指向一个可运行 Lab/Test（或明确说明使用哪个既有 Lab）
- 自测题能映射到具体断点与实验入口

### Requirement: binder 安全边界（mass assignment）可复现
**Module:** springboot-web-mvc
通过 `@InitBinder#setAllowedFields` 固定“字段白名单”行为，避免“多传字段被绑定”的风险。

#### Scenario: 通过测试断言危险字段不会被绑定
- form 提交携带 `admin=true`，但响应中 `admin=false`（被 allowedFields 阻断）

### Requirement: ControllerAdvice 匹配/优先级可复现
**Module:** springboot-web-mvc
补齐 `@ControllerAdvice/@ExceptionHandler` 的匹配规则与 `@Order` 优先级证据链。

#### Scenario: 两个 advice 同时匹配同一异常时，高优先级生效
- 通过测试断言：响应体来自 `@Order` 更高的 advice

## Risk Assessment

- **Risk:** 新增 demo 组件影响既有行为
  - **Mitigation:** 新增端点与 advice 采用独立 package + 限定 basePackages/路径；并用回归测试验证不影响既有 Labs。
- **Risk:** 文档链接断裂/导航错误

