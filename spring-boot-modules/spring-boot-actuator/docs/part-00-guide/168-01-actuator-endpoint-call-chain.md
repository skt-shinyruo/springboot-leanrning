# 第 168 章：01：Actuator 调用链（端点发现 → 映射 → 执行）
<!-- CHAPTER-CARD:START -->
!!! summary "章节学习卡片（五问闭环）"

    - 知识点：01：Actuator 调用链（端点发现 → 映射 → 执行）
    - 怎么使用：先跑 `BootActuatorLabTest`，把“端点存在/暴露/可访问”固化为断言，再按本文从 endpoint discoverer 走到 handler mapping。
    - 原理：Actuator 的端点不是“写了就能访问”，中间有两层决策：端点被发现（discover）与端点被暴露（exposure/安全）。
    - 源码入口：`org.springframework.boot.actuate.endpoint.web.annotation.WebEndpointDiscoverer` / `org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping` / `org.springframework.boot.actuate.endpoint.invoke.reflect.ReflectiveOperationInvoker`
    - 推荐 Lab：`BootActuatorLabTest`
<!-- CHAPTER-CARD:END -->

<!-- GLOBAL-BOOK-NAV:START -->
上一章：[第 168 章：00. 深挖导读](168-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[第 168 章：02：断点地图](168-02-breakpoint-map.md)
<!-- GLOBAL-BOOK-NAV:END -->

## 最短调用链（你要能复述）

1. **发现端点（discover）**
   - Actuator 扫描并收集 endpoint（web/servlet/jmx 等变体）
2. **建立映射（mapping）**
   - 将 endpoint 的 operation 映射成可被 Web 请求访问的 handler
3. **执行端点（invoke）**
   - 请求到来后，找到对应 operation，完成参数解析并调用 invoker
4. **暴露策略与安全边界（exposure/security）**
   - 端点是否可见、是否可访问，取决于 exposure 与安全配置

证据链入口：

- `BootActuatorLabTest`

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootActuatorLabTest`

上一章：[part-00-guide/00-deep-dive-guide.md](168-00-deep-dive-guide.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-00-guide/02-breakpoint-map.md](168-02-breakpoint-map.md)

<!-- BOOKIFY:END -->
