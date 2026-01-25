# （Redirect）Actuator/Observability 主线（旧入口）

<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：（Redirect）Actuator/Observability 主线（旧入口）
    - 怎么使用：通过 Actuator endpoints 暴露健康检查/信息/指标；用 exposure 控制可见范围，并在生产环境结合鉴权与安全边界。
    - 原理：引入 Actuator → 端点注册与 discover → exposure 决定暴露 → Web 层映射为 HTTP 端点 → 结合安全策略与可观测信号使用。
    - 源码入口：`org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration` / `org.springframework.boot.actuate.endpoint.annotation.Endpoint` / `org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties`
    - 推荐 Lab：`BootActuatorLabTest`
<!-- CHAPTER-CARD:END -->

## 已迁移
本页为旧入口兼容页，正文已迁移到：[新位置](166-actuator-observability-mainline.md)。

## 返回
- [模块目录](../README.md)
- [全书目录](/book/)
