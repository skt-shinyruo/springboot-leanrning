package com.learning.springboot.bootwebmvc.part07_testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.learning.springboot.bootwebmvc.part04_contract.AdvancedApiExceptionHandler;
import com.learning.springboot.bootwebmvc.part04_contract.RestContractController;
import com.learning.springboot.bootwebmvc.part04_contract.StrictJsonMessageConverterConfig;
import com.learning.springboot.bootwebmvc.part08_security_observability.SecurityConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;

@WebMvcTest(controllers = RestContractController.class)
@Import({StrictJsonMessageConverterConfig.class, AdvancedApiExceptionHandler.class, SecurityConfig.class})
class BootWebMvcTestingDebuggingLabTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void canDebug415ViaResolvedException() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/advanced/contract/echo")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("hello"))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(415);
        assertThat(result.getResolvedException()).isInstanceOf(HttpMediaTypeNotSupportedException.class);
    }

    @Test
    void canDebug406ViaResolvedException() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/advanced/contract/ping")
                        .accept(MediaType.TEXT_PLAIN))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(406);
        assertThat(result.getResolvedException()).isInstanceOf(HttpMediaTypeNotAcceptableException.class);
    }
}
