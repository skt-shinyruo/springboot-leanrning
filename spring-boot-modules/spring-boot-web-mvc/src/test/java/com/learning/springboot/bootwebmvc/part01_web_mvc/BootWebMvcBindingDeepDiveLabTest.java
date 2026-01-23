package com.learning.springboot.bootwebmvc.part01_web_mvc;

// 本测试用于固定 binder 路径的关键分支：类型不匹配、@ModelAttribute 校验失败等。

import static org.hamcrest.Matchers.hasItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.learning.springboot.bootwebmvc.part08_security_observability.SecurityConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {BindingDeepDiveController.class, MethodValidationController.class})
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class BootWebMvcBindingDeepDiveLabTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsTypeMismatchWhenRequestParamCannotConvert() throws Exception {
        mockMvc.perform(get("/api/advanced/binding/age")
                        .param("age", "not-a-number")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("type_mismatch"))
                .andExpect(jsonPath("$.fieldErrors.age").exists());
    }

    @Test
    void returnsMethodValidationFailedWhenRequestParamViolatesConstraint() throws Exception {
        mockMvc.perform(get("/api/advanced/binding/age-validated")
                        .param("age", "-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("method_validation_failed"))
                .andExpect(jsonPath("$.fieldErrors.age").exists());
    }

    @Test
    void returnsValidationFailedWhenModelAttributeIsInvalid() throws Exception {
        mockMvc.perform(post("/api/advanced/binding/form")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("name", "")
                        .param("email", "not-an-email"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(org.springframework.validation.BindException.class))
                .andExpect(jsonPath("$.message").value("validation_failed"))
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    @Test
    void bindingResultCanShortCircuitExceptionFlowWhenHandledManually() throws Exception {
        mockMvc.perform(post("/api/advanced/binding/form-manual")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("name", "")
                        .param("email", "not-an-email"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException()).isNull())
                .andExpect(jsonPath("$.message").value("validation_failed_manual"))
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    @Test
    void returnsOkWhenModelAttributeIsValid() throws Exception {
        mockMvc.perform(post("/api/advanced/binding/form")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("name", "Alice")
                        .param("email", "alice@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void preventsMassAssignmentViaInitBinderAllowedFields() throws Exception {
        mockMvc.perform(post("/api/advanced/binding/mass-assignment")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("name", "Alice")
                        .param("admin", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.admin").value(false));
    }

    @Test
    void exposesSuppressedFieldsAsEvidenceWhenBindingBlockedFields() throws Exception {
        mockMvc.perform(post("/api/advanced/binding/mass-assignment-debug")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("name", "Alice")
                        .param("admin", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.admin").value(false))
                .andExpect(jsonPath("$.suppressedFields").isArray())
                .andExpect(jsonPath("$.suppressedFields", hasItem("admin")));
    }
}
