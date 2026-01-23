# 02：Multipart 上传（multipart/form-data：与 JSON 完全不同的边界）

## 导读

- 本章主题：**02：Multipart 上传（multipart/form-data：与 JSON 完全不同的边界）**
- 目标：用 MockMvc 的 multipart 测试稳定复现上传解析，并讲清 `@RequestParam MultipartFile` 的边界。

!!! summary "本章要点"

    - multipart 是“一个请求里有多个 part”，不是 JSON body；常见写法是 `@RequestParam("file") MultipartFile file`。
    - 上传接口要明确：文件名、content-type、大小、返回的 id；否则很难测试/排障。


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

上一章：[part-05-real-world-http/01-cors-preflight.md](078-01-cors-preflight.md) ｜ 目录：[Docs TOC](../README.md) ｜ 下一章：[part-05-real-world-http/03-download-and-streaming.md](03-download-and-streaming.md)

<!-- BOOKIFY:END -->
