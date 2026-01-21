package com.learning.springboot.bootwebmvc.part05_real_world;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.springboot.bootwebmvc.part04_contract.AdvancedApiExceptionHandler;
import com.learning.springboot.bootwebmvc.part08_security_observability.SecurityConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(controllers = {CorsDemoController.class, FileTransferController.class, ApiEtagDemoController.class})
@Import({AdvancedCorsConfig.class, CacheEtagFilterConfig.class, InMemoryFileStore.class, AdvancedApiExceptionHandler.class, SecurityConfig.class})
class BootWebMvcRealWorldHttpLabTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void corsPreflightReturnsAccessControlHeaders() throws Exception {
        mockMvc.perform(options("/api/advanced/cors/ping")
                        .header(HttpHeaders.ORIGIN, "https://example.com")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "X-Request-Id"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "https://example.com"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, containsString("GET")))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, containsString("X-Request-Id")));
    }

    @Test
    void corsActualRequestContainsAccessControlAllowOriginHeader() throws Exception {
        mockMvc.perform(get("/api/advanced/cors/ping")
                        .header(HttpHeaders.ORIGIN, "https://example.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "https://example.com"))
                .andExpect(jsonPath("$.message").value("pong"));
    }

    @Test
    void uploadThenDownloadReturnsSameBytes() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "hello".getBytes(StandardCharsets.UTF_8)
        );

        MvcResult uploadResult = mockMvc.perform(multipart("/api/advanced/files/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.fileName").value("hello.txt"))
                .andExpect(jsonPath("$.size").value(5))
                .andReturn();

        JsonNode json = objectMapper.readTree(uploadResult.getResponse().getContentAsString(StandardCharsets.UTF_8));
        long id = json.get("id").asLong();

        mockMvc.perform(get("/api/advanced/files/{id}", id))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment")))
                .andExpect(content().bytes("hello".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void staticResourceCssIsReachable() throws Exception {
        mockMvc.perform(get("/css/app.css").accept(MediaType.valueOf("text/css")))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, containsString("text/css")))
                .andExpect(content().string(containsString(":root")));
    }

    @Test
    void staticResourceSupportsIfModifiedSince304() throws Exception {
        MvcResult first = mockMvc.perform(get("/css/app.css").accept(MediaType.valueOf("text/css")))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.LAST_MODIFIED))
                .andReturn();

        String lastModified = first.getResponse().getHeader(HttpHeaders.LAST_MODIFIED);

        mockMvc.perform(get("/css/app.css")
                        .header(HttpHeaders.IF_MODIFIED_SINCE, lastModified)
                        .accept(MediaType.valueOf("text/css")))
                .andExpect(status().isNotModified());
    }

    @Test
    void apiEtagSupportsConditionalGet304() throws Exception {
        MvcResult first = mockMvc.perform(get("/api/advanced/cache/etag")
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.ETAG))
                .andReturn();

        String etag = first.getResponse().getHeader(HttpHeaders.ETAG);

        mockMvc.perform(get("/api/advanced/cache/etag")
                        .header(HttpHeaders.IF_NONE_MATCH, etag)
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isNotModified());
    }

    @Test
    void shallowEtagHeaderFilterSupportsConditionalGet304() throws Exception {
        MvcResult first = mockMvc.perform(get("/api/advanced/cache/filter-etag")
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.ETAG))
                .andReturn();

        String etag = first.getResponse().getHeader(HttpHeaders.ETAG);

        mockMvc.perform(get("/api/advanced/cache/filter-etag")
                        .header(HttpHeaders.IF_NONE_MATCH, etag)
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isNotModified());
    }
}
