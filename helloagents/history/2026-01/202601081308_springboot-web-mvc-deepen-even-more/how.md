# Technical Design: springboot-web-mvc deepen even more（占位补齐 + 工程化边界 Labs）

## Technical Solution

### Core Technologies

- Spring Web MVC（binding/validation/exception handling）
- MockMvc（slice Lab 固定关键分支）

### Implementation Key Points

1) **Docs 占位补齐**
- 导读（Deep Dive Guide）：补齐“主线阶段 → 关键分支 → 证据链（测试/断点）”的导航视图，并明确“源码阅读课 vs 工程落地课”的两条学习路径。
- 自测（Self Check）：把题目变成“可验证清单”，每题给出：
  - 最小可运行 Lab/Test
  - 建议断点（类/方法）
  - 观察点（status code、resolvedException、handler、headers）
- Part01/Part02 坑点：补齐工程级坑点与排障 checklist，强调“分支分类”与“证据链优先”。

2) **Labs：binder 安全边界（mass assignment）**
- 在 binder 路径示例 controller 中新增一个 form DTO（包含危险字段 `admin`）
- 使用现有 `@InitBinder#setAllowedFields("name","email")`，确保 `admin` 不会被绑定
- LabTest 断言响应中的 `admin=false`，形成可回归证据链

3) **Labs：ControllerAdvice 优先级**
- 新增 demo controller（仅用于抛异常）
- 新增两份 `@RestControllerAdvice`，均处理同一异常类型，但 `@Order` 不同
- LabTest 固定：高优先级 advice 生效（响应体 message 明确可区分）

## Security and Performance

- **Security:**
  - 新增端点均为教学用途，限定 `/api/advanced/**`
  - mass assignment 演示强调“白名单”做法，不引入真实权限后门
- **Performance:** 新增逻辑仅用于教学，且 scope 明确，不改变全局默认路径

## Testing and Verification

- `mvn -pl springboot-web-mvc test`

