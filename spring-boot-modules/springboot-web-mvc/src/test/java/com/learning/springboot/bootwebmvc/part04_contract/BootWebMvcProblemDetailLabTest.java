package com.learning.springboot.bootwebmvc.part04_contract;

// 本测试用于对照 ProblemDetail 与自定义错误体（ApiError）的差异：形状、语义与落地方式。

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.learning.springboot.bootwebmvc.part04_problemdetail.ProblemDetailDemoController;
import com.learning.springboot.bootwebmvc.part04_problemdetail.ProblemDetailDemoExceptionHandler;
import com.learning.springboot.bootwebmvc.part08_security_observability.SecurityConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ProblemDetailDemoController.class)
@Import({ProblemDetailDemoExceptionHandler.class, SecurityConfig.class})
class BootWebMvcProblemDetailLabTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void explicitProblemDetailResponseUsesProblemJsonContentType() throws Exception {
        mockMvc.perform(get("/api/advanced/problem/bad-request").accept(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("bad_request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("bad_request_explicit"));
    }

    @Test
    void exceptionMappedProblemDetailKeepsEvidenceInFields() throws Exception {
        mockMvc.perform(get("/api/advanced/problem/throw").accept(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("bad_request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.instance").value("/api/advanced/problem/throw"))
                .andExpect(jsonPath("$.errorCode").value("bad_request_exception"));
    }
}
