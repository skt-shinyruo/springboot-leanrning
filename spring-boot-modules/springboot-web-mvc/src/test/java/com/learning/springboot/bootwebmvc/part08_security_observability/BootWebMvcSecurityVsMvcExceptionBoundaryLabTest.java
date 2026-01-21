package com.learning.springboot.bootwebmvc.part08_security_observability;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.learning.springboot.bootwebmvc.part03_internals.ExceptionResolverChainController;

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

@WebMvcTest(controllers = {
        SecureDemoController.class,
        ExceptionResolverChainController.class
})
@Import(SecurityConfig.class)
class BootWebMvcSecurityVsMvcExceptionBoundaryLabTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void security401_happensInFilterChain_beforeDispatcherServlet() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/advanced/secure/ping")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(401);
        assertThat(result.getHandler()).as("401 usually stops before handler mapping").isNull();
        assertThat(result.getResolvedException()).as("401 is handled by Security filters, not MVC resolvers").isNull();

        System.out.println("OBSERVE: 401 (unauthenticated) happens in Security FilterChain, before DispatcherServlet/HandlerMethod.");
    }

    @Test
    void security403_happensInFilterChain_beforeDispatcherServlet_whenRoleDenied() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/advanced/secure/admin/ping")
                        .with(httpBasic("user", "password"))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(403);
        assertThat(result.getHandler()).as("403 from access denied usually stops before handler mapping").isNull();
        assertThat(result.getResolvedException()).as("403 is translated by Security filters, not MVC resolvers").isNull();

        System.out.println("OBSERVE: 403 (access denied) happens in Security FilterChain, before DispatcherServlet/HandlerMethod.");
    }

    @Test
    void security403_happensInFilterChain_beforeDispatcherServlet_whenCsrfMissing() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/advanced/secure/update")
                        .with(httpBasic("user", "password"))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(403);
        assertThat(result.getHandler()).as("403 from CSRF usually stops before handler mapping").isNull();
        assertThat(result.getResolvedException()).as("CSRF 403 is handled by filters, not MVC resolvers").isNull();

        System.out.println("OBSERVE: 403 (CSRF) happens in Security FilterChain, before DispatcherServlet/HandlerMethod.");
    }

    @Test
    void mvc400_happensInDispatcherServlet_andHasResolvedException_bindException() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/advanced/exceptions/model")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(400);
        assertThat(result.getHandler()).as("MVC exceptions happen after handler mapping").isNotNull();
        assertThat(result.getResolvedException()).isInstanceOf(BindException.class);

        System.out.println("OBSERVE: 400 (BindException) happens inside MVC (binder/validation), not in Security FilterChain.");
    }

    @Test
    void mvc400_happensInDispatcherServlet_andHasResolvedException_methodArgumentNotValidException() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/advanced/exceptions/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(400);
        assertThat(result.getHandler()).as("MVC exceptions happen after handler mapping").isNotNull();
        assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class);

        System.out.println("OBSERVE: 400 (MethodArgumentNotValidException) happens inside MVC (RequestBody validation).");
    }

    @Test
    void mvc400_happensInDispatcherServlet_andHasResolvedException_httpMessageNotReadableException() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/advanced/exceptions/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"name\":"))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(400);
        assertThat(result.getHandler()).as("MVC exceptions happen after handler mapping").isNotNull();
        assertThat(result.getResolvedException()).isInstanceOf(HttpMessageNotReadableException.class);

        System.out.println("OBSERVE: 400 (HttpMessageNotReadableException) happens inside MVC (converter/read stage).");
    }

    @Test
    void security200_canReachController_whenAuthenticated_andCsrfProvidedForPost() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/advanced/secure/update")
                        .with(httpBasic("user", "password"))
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getHandler()).as("when allowed, request reaches HandlerMethod").isNotNull();
        assertThat(result.getResolvedException()).isNull();

        System.out.println("OBSERVE: once allowed by Security filters, request enters MVC and gets a handler.");
    }
}

