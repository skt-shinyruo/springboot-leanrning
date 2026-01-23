package com.learning.springboot.bootwebmvc.part02_view_mvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.learning.springboot.bootwebmvc.part08_security_observability.SecurityConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {MvcErrorDemoController.class, MvcUserController.class})
@Import({MvcExceptionHandler.class, SecurityConfig.class})
class BootWebMvcErrorViewLabTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void renders404HtmlPageWhenControllerThrowsNotFound() throws Exception {
        mockMvc.perform(get("/pages/users/999").accept(MediaType.TEXT_HTML))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("data-testid=\"error-404\"")));
    }

    @Test
    void renders5xxHtmlPageWhenControllerThrows() throws Exception {
        mockMvc.perform(get("/pages/error-demo").accept(MediaType.TEXT_HTML))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("error/5xx"))
                .andExpect(content().string(containsString("data-testid=\"error-5xx\"")));
    }

    @Test
    void returnsJsonWhenAcceptIsJson() throws Exception {
        mockMvc.perform(get("/pages/error-demo").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("page_error"));
    }
}
