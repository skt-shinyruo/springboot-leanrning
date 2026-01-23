package com.learning.springboot.bootwebmvc.part04_contract;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.springboot.bootwebmvc.part08_security_observability.SecurityConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = RestContractController.class)
@Import({StrictJsonMessageConverterConfig.class, AdvancedApiExceptionHandler.class, SecurityConfig.class})
class BootWebMvcContractJacksonLabTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void returns415WhenContentTypeIsNotSupported() throws Exception {
        mockMvc.perform(post("/api/advanced/contract/echo")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("hello"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void returns406WhenAcceptIsNotSupported() throws Exception {
        mockMvc.perform(get("/api/advanced/contract/ping")
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void strictMediaTypeRejectsUnknownFields() throws Exception {
        String body = """
                {
                  "message": "hello",
                  "createdAt": "2026-01-07T16:35:00Z",
                  "extra": "should-fail"
                }
                """;

        mockMvc.perform(post("/api/advanced/contract/strict-echo")
                        .contentType(StrictJsonMessageConverterConfig.STRICT_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("malformed_json"));
    }

    @Test
    void echoesInstantInIso8601StringForm() throws Exception {
        ContractEchoRequest request = new ContractEchoRequest("hello", Instant.parse("2026-01-07T16:35:00Z"));

        mockMvc.perform(post("/api/advanced/contract/echo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("hello"))
                .andExpect(jsonPath("$.createdAt").value("2026-01-07T16:35:00Z"));
    }
}
