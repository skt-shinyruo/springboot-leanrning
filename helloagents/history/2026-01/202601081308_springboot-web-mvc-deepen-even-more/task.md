# Task List: springboot-web-mvc deepen even more（占位补齐 + 工程化边界 Labs）

Directory: `helloagents/plan/202601081308_springboot-web-mvc-deepen-even-more/`

---

## 1. Docs（补齐占位与“证据链”映射）
- [√] 1.1 补齐导读主线（机制主线 + 两条课程路径 + 推荐 Labs 顺序），update `docs/web-mvc/springboot-web-mvc/part-00-guide/00-deep-dive-guide.md`
- [√] 1.2 强化自测题：题目 → Lab/Test → 断点/观察点映射，update `docs/web-mvc/springboot-web-mvc/appendix/99-self-check.md`
- [√] 1.3 补齐异常处理章节坑点（坏输入分类 + advice 匹配/优先级），update `docs/web-mvc/springboot-web-mvc/part-01-web-mvc/02-exception-handling.md`
- [√] 1.4 补齐表单提交章节坑点（BindingResult/PRG/Flash/回显），update `docs/web-mvc/springboot-web-mvc/part-02-view-mvc/02-form-binding-validation-prg.md`
- [√] 1.5 补齐错误页与内容协商坑点（HTML vs JSON、Accept、错误页兜底），update `docs/web-mvc/springboot-web-mvc/part-02-view-mvc/03-error-pages-and-content-negotiation.md`
- [√] 1.6 必要时更新 docs TOC / 对应 Lab 引用（避免断链），update `docs/web-mvc/springboot-web-mvc/README.md`

## 2. Labs（binder：mass assignment 防护）
- [√] 2.1 新增 binder 演示 DTO + endpoint（携带危险字段 `admin`），update `springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BindingDeepDiveController.java`
- [√] 2.2 新增/扩展 LabTest 固定 `admin` 不被绑定，update `springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part01_web_mvc/BootWebMvcBindingDeepDiveLabTest.java`
- [√] 2.3 docs 补充说明（mass assignment + allowedFields），update `docs/web-mvc/springboot-web-mvc/part-01-web-mvc/03-binding-and-converters.md`

## 3. Labs（ControllerAdvice 优先级）
- [√] 3.1 新增 demo controller + 2 份 advice（@Order 不同），files in `springboot-web-mvc/src/main/java/com/learning/springboot/bootwebmvc/part09_advice_order/*`
- [√] 3.2 新增 LabTest 固定高优先级 advice 生效，file `springboot-web-mvc/src/test/java/com/learning/springboot/bootwebmvc/part09_advice_order/BootWebMvcAdviceOrderLabTest.java`
- [√] 3.3 docs 章节补充 advice 匹配/优先级证据链，update `docs/web-mvc/springboot-web-mvc/part-01-web-mvc/02-exception-handling.md`

## 4. Security Check
- [√] 4.1 安全检查（G9）：确保新增端点为教学用途、无敏感信息、无权限绕过、限定影响面

## 5. Knowledge Base Update
- [√] 5.1 更新 `helloagents/wiki/modules/springboot-web-mvc.md`（新增 Labs/章节入口）
- [√] 5.2 更新 `helloagents/CHANGELOG.md`（Unreleased - Added）
- [√] 5.3 更新 `helloagents/history/index.md`（新增本次方案包索引）

## 6. Testing
- [√] 6.1 运行：`mvn -pl springboot-web-mvc test`
