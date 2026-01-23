package com.learning.springboot.bootwebmvc.part02_view_mvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.learning.springboot.bootwebmvc.part08_security_observability.SecurityConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(controllers = {MvcPingController.class, MvcUserController.class})
@Import({MvcExceptionHandler.class, SecurityConfig.class})
class BootWebMvcViewLabTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rendersPingPage() throws Exception {
        mockMvc.perform(get("/pages/ping").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/ping"))
                .andExpect(model().attribute("message", "pong"))
                .andExpect(content().string(containsString("data-testid=\"ping-message\"")))
                .andExpect(content().string(containsString("pong")));
    }

    @Test
    void rendersUserFormPage() throws Exception {
        mockMvc.perform(get("/pages/users/new").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/user-form"))
                .andExpect(model().attributeExists("form"))
                .andExpect(content().string(containsString("data-testid=\"user-create-form\"")));
    }

    @Test
    void reRendersFormWhenPostIsInvalid() throws Exception {
        mockMvc.perform(post("/pages/users")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.TEXT_HTML)
                        .param("name", "")
                        .param("email", "not-an-email"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/user-form"))
                .andExpect(model().attributeHasFieldErrors("form", "name", "email"))
                .andExpect(content().string(containsString("data-testid=\"error-name\"")))
                .andExpect(content().string(containsString("data-testid=\"error-email\"")));
    }

    @Test
    void redirectsWhenPostIsValid() throws Exception {
        MvcResult result = mockMvc.perform(post("/pages/users")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.TEXT_HTML)
                        .param("name", "Alice")
                        .param("email", "alice@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", startsWith("/pages/users/")))
                .andExpect(flash().attributeExists("flashMessage"))
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        mockMvc.perform(get(location)
                        .accept(MediaType.TEXT_HTML)
                        .flashAttrs(result.getFlashMap()))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/user-detail"))
                .andExpect(content().string(containsString("data-testid=\"flash-message\"")))
                .andExpect(content().string(containsString("Alice")))
                .andExpect(content().string(containsString("alice@example.com")));
    }
}
