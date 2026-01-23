package com.learning.springboot.bootwebmvc.part03_internals;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.learning.springboot.bootwebmvc.part04_contract.StrictJsonMessageConverterConfig;
import com.learning.springboot.bootwebmvc.part08_security_observability.SecurityConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MessageConverterTraceController.class)
@Import({MessageConverterTraceAdvice.class, StrictJsonMessageConverterConfig.class, SecurityConfig.class})
class BootWebMvcMessageConverterTraceLabTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void recordsStringHttpMessageConverterSelection() throws Exception {
        mockMvc.perform(get("/api/advanced/message-converters/string")
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        MessageConverterTraceAdvice.HEADER_SELECTED_CONVERTER,
                        "StringHttpMessageConverter"
                ))
                .andExpect(header().string(
                        MessageConverterTraceAdvice.HEADER_SELECTED_CONTENT_TYPE,
                        startsWith(MediaType.TEXT_PLAIN_VALUE)
                ));
    }

    @Test
    void recordsJacksonHttpMessageConverterSelection() throws Exception {
        mockMvc.perform(get("/api/advanced/message-converters/json")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        MessageConverterTraceAdvice.HEADER_SELECTED_CONVERTER,
                        "MappingJackson2HttpMessageConverter"
                ))
                .andExpect(header().string(
                        MessageConverterTraceAdvice.HEADER_SELECTED_CONTENT_TYPE,
                        startsWith(MediaType.APPLICATION_JSON_VALUE)
                ));
    }

    @Test
    void recordsByteArrayHttpMessageConverterSelection() throws Exception {
        mockMvc.perform(get("/api/advanced/message-converters/bytes")
                        .accept(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        MessageConverterTraceAdvice.HEADER_SELECTED_CONVERTER,
                        "ByteArrayHttpMessageConverter"
                ))
                .andExpect(header().string(
                        MessageConverterTraceAdvice.HEADER_SELECTED_CONTENT_TYPE,
                        startsWith(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                ));
    }

    @Test
    void recordsCustomMediaTypeConverterSelection() throws Exception {
        mockMvc.perform(get("/api/advanced/message-converters/strict-json")
                        .accept(StrictJsonMessageConverterConfig.STRICT_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        MessageConverterTraceAdvice.HEADER_SELECTED_CONVERTER,
                        StrictJsonMessageConverterConfig.StrictJsonMessageConverter.class.getSimpleName()
                ))
                .andExpect(header().string(
                        MessageConverterTraceAdvice.HEADER_SELECTED_CONTENT_TYPE,
                        startsWith(StrictJsonMessageConverterConfig.STRICT_JSON_VALUE)
                ));
    }
}

