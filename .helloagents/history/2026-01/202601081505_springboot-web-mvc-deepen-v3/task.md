# Task List: springboot-web-mvc deepen v3（Advice 匹配 + Binder suppressedFields + Converter 可观测）

Directory: `helloagents/plan/202601081505_springboot-web-mvc-deepen-v3/`

---

## 1. Labs：ControllerAdvice 匹配规则（annotations/assignableTypes/basePackages + order）
- [√] 1.1 新增 demo controller(s) + 自定义异常类型，限定 `/api/advanced/advice-matching/**`
- [√] 1.2 新增多份 `@RestControllerAdvice`（不同 selector + @Order），且仅作用于该 demo controllers 包
- [√] 1.3 新增 LabTest 固定：不同 controller 命中不同 advice（并解释 why：匹配集合 + order）

## 2. Labs：binder suppressedFields 可视化（mass assignment 证据链升级）
- [√] 2.1 在 `BindingDeepDiveController` 新增 debug endpoint 输出 `suppressedFields`
- [√] 2.2 扩展 `BootWebMvcBindingDeepDiveLabTest`：断言 `suppressedFields` 包含 `admin`
- [√] 2.3 docs 补充：suppressedFields 的意义/断点/排障用法

## 3. Labs：HttpMessageConverter 选择可观测（selectedConverterType/selectedContentType）
- [√] 3.1 新增 `ResponseBodyAdvice`：把选择结果写入响应头（限定 `/api/advanced/message-converters/**`）
- [√] 3.2 新增 demo controller endpoints：String/JSON/bytes/strict media type
- [√] 3.3 必要时调整 strict converter 为可识别子类，便于观察与断言
- [√] 3.4 新增 LabTest 固定：每个端点的 converter + content-type 选择结果

## 4. Docs：新增章节 + 调用链/关键分支解释 + 排障清单
- [√] 4.1 新增 docs 章节：ControllerAdvice 的匹配与优先级（applicable vs order）
- [√] 4.2 强化 MessageConverter 章节：增加“可观测证据链”与 watchpoints
- [√] 4.3 更新常见坑/排障工具箱：suppressedFields、advice 匹配、converter 选择证据
- [√] 4.4 更新 docs TOC 与 Labs 入口列表，确保断链=0

## 5. Security Check
- [√] 5.1 安全检查（G9）：新增端点仅教学用途、无敏感信息、限定影响面（basePackages/URI 前缀）

## 6. Knowledge Base Update
- [√] 6.1 更新 `helloagents/wiki/modules/springboot-web-mvc.md`（新增章节/Labs 与变更记录）
- [√] 6.2 更新 `helloagents/CHANGELOG.md`（Unreleased - Added）
- [√] 6.3 更新 `helloagents/history/index.md`（新增本次方案包索引）

## 7. Testing
- [√] 7.1 运行：`mvn -pl springboot-web-mvc test`
