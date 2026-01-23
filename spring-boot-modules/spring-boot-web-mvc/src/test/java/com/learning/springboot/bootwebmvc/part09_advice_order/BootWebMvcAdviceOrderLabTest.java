package com.learning.springboot.bootwebmvc.part09_advice_order;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.learning.springboot.bootwebmvc.part08_security_observability.SecurityConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AdviceOrderController.class)
@Import({HighPriorityAdvice.class, LowPriorityAdvice.class, SecurityConfig.class})
class BootWebMvcAdviceOrderLabTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void higherOrderControllerAdviceWinsWhenBothMatchSameException() throws Exception {
        mockMvc.perform(get("/api/advanced/advice-order/boom")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("high_priority_advice"))
                .andExpect(jsonPath("$.fieldErrors.source").value("high"));
    }
}

