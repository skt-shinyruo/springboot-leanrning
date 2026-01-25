# （Redirect）Testing 主线（旧入口）

<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：（Redirect）Testing 主线（旧入口）
    - 怎么使用：按目标选择测试切片（如 `@WebMvcTest`）或全量上下文（`@SpringBootTest`）；用 mock/替身把外部依赖固定成可断言证据。
    - 原理：测试注解决定上下文装配范围 → TestContext 缓存与复用 → slice/full context 的权衡 → 断言固化机制结论 → 快速定位失败。
    - 源码入口：`org.springframework.boot.test.context.SpringBootTest` / `org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest` / `org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate`
    - 推荐 Lab：`GreetingControllerWebMvcLabTest`
<!-- CHAPTER-CARD:END -->

## 已迁移
本页为旧入口兼容页，正文已迁移到：[新位置](182-testing-mainline.md)。

## 返回
- [模块目录](../README.md)
- [全书目录](/book/)
