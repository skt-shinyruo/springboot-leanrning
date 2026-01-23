package com.learning.springboot.boottesting.part01_testing;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GreetingController.class)
class GreetingControllerWebMvcLabTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GreetingService greetingService;

    @Test
    void returnsGreetingFromMockedService() throws Exception {
        when(greetingService.greet("Alice")).thenReturn("Hi, Alice");

        mockMvc.perform(get("/api/greeting")
                        .param("name", "Alice")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hi, Alice"));
    }

    @Test
    void usesDefaultRequestParamValueWhenMissing() throws Exception {
        when(greetingService.greet("World")).thenReturn("Hi, World");

        mockMvc.perform(get("/api/greeting").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hi, World"));
    }

    @Test
    void callsServiceWithTheResolvedNameParameter() throws Exception {
        when(greetingService.greet("Bob")).thenReturn("Hi, Bob");

        mockMvc.perform(get("/api/greeting")
                        .param("name", "Bob")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(greetingService).greet("Bob");
    }

    @Test
    void returnsJsonResponseShape() throws Exception {
        when(greetingService.greet("World")).thenReturn("Hi, World");

        mockMvc.perform(get("/api/greeting").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void supportsUnicodeNames() throws Exception {
        when(greetingService.greet("小明")).thenReturn("Hi, 小明");

        mockMvc.perform(get("/api/greeting")
                        .param("name", "小明")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hi, 小明"));
    }
}
