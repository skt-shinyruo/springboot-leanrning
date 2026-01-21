package com.learning.springboot.bootwebmvc.part06_async_sse;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;

import com.learning.springboot.bootwebmvc.part04_contract.AdvancedApiExceptionHandler;
import com.learning.springboot.bootwebmvc.part08_security_observability.SecurityConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(controllers = {AsyncDemoController.class, SseDemoController.class})
@Import({AdvancedApiExceptionHandler.class, SecurityConfig.class})
class BootWebMvcAsyncSseLabTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void asyncPingUsesAsyncDispatch() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/advanced/async/ping")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("pong"))
                .andExpect(jsonPath("$.thread").isString());
    }

    @Test
    void deferredResultPingUsesAsyncDispatch() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/advanced/async/deferred")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("pong"))
                .andExpect(jsonPath("$.thread").isString());
    }

    @Test
    void deferredResultTimeoutFallsBackToDefaultValue() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/advanced/async/deferred-timeout")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("timeout"));
    }

    @Test
    void ssePingReturnsTextEventStream() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/advanced/sse/ping")
                        .accept(MediaType.TEXT_EVENT_STREAM))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM))
                .andExpect(content().string(containsString("data:")))
                .andExpect(content().string(containsString("ping-1")));
    }
}
