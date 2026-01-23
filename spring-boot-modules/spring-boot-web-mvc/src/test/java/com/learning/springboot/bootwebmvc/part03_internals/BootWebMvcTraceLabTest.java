package com.learning.springboot.bootwebmvc.part03_internals;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.learning.springboot.bootwebmvc.part08_security_observability.SecurityConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(controllers = WebMvcTraceController.class)
@Import({WebMvcTraceConfig.class, SecurityConfig.class})
class BootWebMvcTraceLabTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void syncTraceRecordsFilterAndInterceptorOrder() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/advanced/trace/sync").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        List<String> events = (List<String>) result.getRequest().getAttribute(WebMvcTraceSupport.EVENTS_ATTR);

        assertThat(events).containsExactly(
                "filter:before[REQUEST]",
                "interceptor:preHandle[REQUEST]",
                "handler:sync[REQUEST]",
                "interceptor:postHandle[REQUEST]",
                "interceptor:afterCompletion[REQUEST]",
                "filter:after[REQUEST]"
        );
    }

    @Test
    void asyncTraceRecordsAfterConcurrentHandlingStartedAndAsyncDispatchCallbacks() throws Exception {
        MvcResult initial = mockMvc.perform(get("/api/advanced/trace/async").accept(MediaType.APPLICATION_JSON))
                .andExpect(request().asyncStarted())
                .andReturn();

        MvcResult dispatched = mockMvc.perform(asyncDispatch(initial))
                .andExpect(status().isOk())
                .andReturn();

        @SuppressWarnings("unchecked")
        List<String> events = (List<String>) dispatched.getRequest().getAttribute(WebMvcTraceSupport.EVENTS_ATTR);

        assertThat(events).containsSequence(
                "filter:before[REQUEST]",
                "interceptor:preHandle[REQUEST]",
                "handler:async[REQUEST]",
                "interceptor:afterConcurrentHandlingStarted[REQUEST]",
                "filter:after[REQUEST]"
        );
        assertThat(events).contains(
                "interceptor:preHandle[ASYNC]",
                "interceptor:postHandle[ASYNC]",
                "interceptor:afterCompletion[ASYNC]"
        );
    }
}

