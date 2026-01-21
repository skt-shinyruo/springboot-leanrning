# 第 3 章：主线时间线：Spring Boot Basics（已迁移到“主线之书”）

<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：主线时间线：Spring Boot Basics（已迁移到“主线之书”）
    - 怎么使用：建议先跑本章推荐 Lab，把现象固化为断言，再对照正文理解机制；真实项目里常用方式：通过 `application.yml`/环境变量/命令行等配置进入 `Environment`，用 `@ConfigurationProperties` 做类型安全绑定，并将最终值用于条件装配或业务逻辑。
    - 原理：配置源（PropertySource）→ `Environment` 聚合与覆盖 → Binder 绑定 → Profile/优先级分流 → 影响条件装配与运行期行为。
    - 源码入口：`org.springframework.core.env.ConfigurableEnvironment` / `org.springframework.core.env.PropertySource` / `org.springframework.boot.context.properties.bind.Binder` / `org.springframework.boot.context.properties.ConfigurationPropertiesBinder`
    - 推荐 Lab：`BootBasicsOverrideLabTest`
<!-- CHAPTER-CARD:END -->

## 已迁移
本页为旧入口兼容页，正文已迁移到：[新位置](../../../book/002-boot-basics-mainline.md)。

## 可跑入口（保留以通过教学闸门）

- Lab：`BootBasicsOverrideLabTest`

## 返回
- [模块目录](../README.md)
- [全书目录](../../../book/index.md)
