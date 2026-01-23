package com.learning.springboot.bootwebmvc.part04_problemdetail;

// 本类用于演示 ProblemDetail：一种“更标准化”的错误响应载体（RFC 7807 风格）。

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/advanced/problem")
public class ProblemDetailDemoController {

    @GetMapping(value = "/bad-request", produces = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    public ResponseEntity<ProblemDetail> explicitBadRequest() {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("bad_request");
        problem.setDetail("这是一个显式返回的 ProblemDetail（不依赖异常映射）。");
        problem.setProperty("errorCode", "bad_request_explicit");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @GetMapping(value = "/throw", produces = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    public void throwBadRequest() {
        throw new IllegalArgumentException("这是一个通过 @ExceptionHandler 映射成 ProblemDetail 的异常。");
    }
}

