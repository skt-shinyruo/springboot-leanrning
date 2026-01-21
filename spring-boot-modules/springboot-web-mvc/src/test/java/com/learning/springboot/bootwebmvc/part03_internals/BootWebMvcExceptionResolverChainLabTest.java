package com.learning.springboot.bootwebmvc.part03_internals;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.learning.springboot.bootwebmvc.part08_security_observability.SecurityConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;

@WebMvcTest(controllers = ExceptionResolverChainController.class)
@Import(SecurityConfig.class)
class BootWebMvcExceptionResolverChainLabTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void canDebugBindExceptionFromModelAttributeValidationViaResolvedException() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/advanced/exceptions/model"))
                .andReturn();

        System.out.println("OBSERVE: @Valid on @ModelAttribute -> BindException (binder/validation stage), before controller body logic matters.");
        assertThat(result.getResponse().getStatus()).isEqualTo(400);
        assertThat(result.getResolvedException()).isInstanceOf(BindException.class);
    }

    @Test
    void canDebugMethodArgumentNotValidExceptionFromRequestBodyValidationViaResolvedException() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/advanced/exceptions/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andReturn();

        System.out.println("OBSERVE: @Valid on @RequestBody -> MethodArgumentNotValidException (validation stage), still a 400 but different root cause.");
        assertThat(result.getResponse().getStatus()).isEqualTo(400);
        assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class);
    }

    @Test
    void canDebugHttpMessageNotReadableExceptionFromInvalidJsonViaResolvedException() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/advanced/exceptions/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":"))
                .andReturn();

        System.out.println("OBSERVE: malformed JSON -> HttpMessageNotReadableException (converter/read stage), controller is never invoked.");
        assertThat(result.getResponse().getStatus()).isEqualTo(400);
        assertThat(result.getResolvedException()).isInstanceOf(HttpMessageNotReadableException.class);
    }
}

