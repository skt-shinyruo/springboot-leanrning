# 02. Multipart 上传（multipart/form-data：与 JSON 完全不同的边界）

## 导读

本章围绕「02：Multipart 上传（multipart/form-data：与 JSON 完全不同的边界）」展开，目标是把机制边界写成可回归的事实（可运行入口与关键观察点会在文中给出）。
建议优先运行 `BootWebMvcRealWorldHttpLabTest`（或文末“对应 Lab/Test”中的最小入口），再回到正文逐段对照分支与原因。

!!! example "本章配套实验（先跑再读）"

    - Lab：`BootWebMvcRealWorldHttpLabTest`

## 机制主线

- 本章用 `BootWebMvcRealWorldHttpLabTest` 覆盖上传 → 返回 id → 再下载验证的闭环。

## 源码与断点

建议断点：
- `org.springframework.web.multipart.support.StandardMultipartHttpServletRequest#parseRequest`
- `org.springframework.web.method.annotation.RequestParamMethodArgumentResolver#resolveName`

## 最小可运行实验（Lab）

- Lab：`BootWebMvcRealWorldHttpLabTest`

## 常见坑与边界

- 只在 Postman 里手工点成功不算完成：必须用测试固定“文件解析 + 返回字段”。

## 小结与下一章

- 下一章进入下载与 header：Content-Disposition/Content-Type 关系。

<!-- BOOKIFY:START -->

### 对应 Lab/Test

- Lab：`BootWebMvcRealWorldHttpLabTest`

上一章：[01. CORS 与预检（OPTIONS：浏览器为什么要先问一句）](real-world-http-cors-preflight.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[03. 下载与 Header（Content-Disposition / Content-Type / bytes）](real-world-http-download-and-streaming.md)
<!-- BOOKIFY:END -->
