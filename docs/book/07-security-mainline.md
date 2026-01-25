# （Redirect）Security 主线（旧入口）

<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：（Redirect）Security 主线（旧入口）
    - 怎么使用：将认证/授权配置为 FilterChain；区分 401/403 与 CSRF 场景；方法级安全依赖代理与拦截器链。
    - 原理：HTTP 请求 → `FilterChainProxy` 选择 SecurityFilterChain → 认证（Authentication）→ 授权（Authorization）→ 异常处理（401/403）→ 继续进入 MVC。
    - 源码入口：`org.springframework.security.web.FilterChainProxy` / `org.springframework.security.web.SecurityFilterChain` / `org.springframework.security.web.access.intercept.AuthorizationFilter`
    - 推荐 Lab：`BootSecurityLabTest`
<!-- CHAPTER-CARD:END -->

## 已迁移
本页为旧入口兼容页，正文已迁移到：[新位置](084-security-mainline.md)。

## 返回
- [模块目录](../README.md)
- [全书目录](/book/)
