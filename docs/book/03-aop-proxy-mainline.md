# （Redirect）AOP/代理主线（旧入口）

<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：（Redirect）AOP/代理主线（旧入口）
    - 怎么使用：通过切点表达式与通知声明横切意图；在 Spring 中多数能力（Tx/Cache/Validation/Method Security）都以代理方式织入。
    - 原理：目标 Bean → `AbstractAutoProxyCreator` 判断 → 生成代理（JDK/CGLIB）→ advisor/interceptor 链 → `proceed()` 形成嵌套调用。
    - 源码入口：`org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization` / `org.springframework.aop.framework.ProxyFactory` / `org.springframework.aop.framework.ReflectiveMethodInvocation#proceed`
    - 推荐 Lab：`SpringCoreAopLabTest`
<!-- CHAPTER-CARD:END -->

## 已迁移
本页为旧入口兼容页，正文已迁移到：[新位置](027-aop-proxy-mainline.md)。

## 返回
- [模块目录](../README.md)
- [全书目录](/book/)
