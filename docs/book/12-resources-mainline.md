# （Redirect）Resources 主线（旧入口）

<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：（Redirect）Resources 主线（旧入口）
    - 怎么使用：通过 `ResourceLoader`/`ApplicationContext` 获取 `Resource`；读取优先走 `getInputStream()`；pattern 扫描使用 `PathMatchingResourcePatternResolver`。
    - 原理：定位（路径/模式）→ 解析为 `Resource`（file/classpath/jar/url）→ 校验（exists/readable）→ 读取（流/编码）；jar 场景下 `getFile()` 不可靠。
    - 源码入口：`org.springframework.core.io.Resource` / `org.springframework.core.io.ResourceLoader` / `org.springframework.core.io.support.PathMatchingResourcePatternResolver`
    - 推荐 Lab：`SpringCoreResourcesLabTest`
<!-- CHAPTER-CARD:END -->

## 已迁移
本页为旧入口兼容页，正文已迁移到：[新位置](138-resources-mainline.md)。

## 返回
- [模块目录](../README.md)
- [全书目录](/book/)
