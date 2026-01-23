package com.learning.springboot.bootwebmvc.part07_testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.learning.springboot.bootwebmvc.part01_web_mvc.BindingDeepDiveController;
import com.learning.springboot.bootwebmvc.part01_web_mvc.GlobalExceptionHandler;
import com.learning.springboot.bootwebmvc.part01_web_mvc.UserController;
import com.learning.springboot.bootwebmvc.part04_contract.AdvancedApiExceptionHandler;
import com.learning.springboot.bootwebmvc.part04_contract.RestContractController;
import com.learning.springboot.bootwebmvc.part04_contract.StrictJsonMessageConverterConfig;
import com.learning.springboot.bootwebmvc.part08_security_observability.SecurityConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@WebMvcTest(controllers = {RestContractController.class, UserController.class, BindingDeepDiveController.class})
@Import({StrictJsonMessageConverterConfig.class, AdvancedApiExceptionHandler.class, GlobalExceptionHandler.class, SecurityConfig.class})
class BootWebMvcErrorBranchMatrixLabTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void branch415_whenContentTypeIsNotSupported() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/advanced/contract/echo")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("hello"))
                .andExpect(status().isUnsupportedMediaType())
                .andReturn();

        assertThat(result.getResolvedException()).isInstanceOf(HttpMediaTypeNotSupportedException.class);
    }

    @Test
    void branch406_whenAcceptIsNotSupported() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/advanced/contract/ping")
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isNotAcceptable())
                .andReturn();

        assertThat(result.getResolvedException()).isInstanceOf(HttpMediaTypeNotAcceptableException.class);
    }

    @Test
    void branch400_whenJsonIsMalformed() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Alice\","))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("malformed_json"))
                .andReturn();

        assertThat(result.getResolvedException()).isInstanceOf(HttpMessageNotReadableException.class);
    }

    @Test
    void branch400_whenValidationFails() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"email\":\"not-an-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("validation_failed"))
                .andReturn();

        assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentNotValidException.class);
    }

    @Test
    void branch400_whenRequestParamTypeMismatch() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/advanced/binding/age")
                        .queryParam("age", "not-a-number")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("type_mismatch"))
                .andExpect(jsonPath("$.fieldErrors.age").exists())
                .andReturn();

        assertThat(result.getResolvedException()).isInstanceOf(MethodArgumentTypeMismatchException.class);
    }
}

