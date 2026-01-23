package com.learning.springboot.bootwebmvc.part10_advice_matching;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.learning.springboot.bootwebmvc.part10_advice_matching.alpha.AdviceMatchingBasePackageOnlyController;
import com.learning.springboot.bootwebmvc.part10_advice_matching.alpha.AdviceMatchingCompositeController;
import com.learning.springboot.bootwebmvc.part10_advice_matching.controllers.AdviceMatchingAnnotationsOnlyController;
import com.learning.springboot.bootwebmvc.part10_advice_matching.controllers.AdviceMatchingAssignableOnlyController;
import com.learning.springboot.bootwebmvc.part10_advice_matching.controllers.AdviceMatchingPlainController;
import com.learning.springboot.bootwebmvc.part08_security_observability.SecurityConfig;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        AdviceMatchingAnnotationsOnlyController.class,
        AdviceMatchingAssignableOnlyController.class,
        AdviceMatchingPlainController.class,
        AdviceMatchingBasePackageOnlyController.class,
        AdviceMatchingCompositeController.class
})
@Import({
        AdviceMatchingGlobalAdvice.class,
        AdviceMatchingAlphaPackageAdvice.class,
        AdviceMatchingAnnotationsAdvice.class,
        AdviceMatchingAssignableAdvice.class,
        SecurityConfig.class
})
class BootWebMvcAdviceMatchingLabTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void annotationsSelectorAppliesToTaggedController() throws Exception {
        mockMvc.perform(get("/api/advanced/advice-matching/annotations-only")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("advice_annotations"))
                .andExpect(jsonPath("$.fieldErrors.selector").value("annotations"));
    }

    @Test
    void assignableTypesSelectorAppliesToMarkerControllers() throws Exception {
        mockMvc.perform(get("/api/advanced/advice-matching/assignable-only")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("advice_assignable"))
                .andExpect(jsonPath("$.fieldErrors.selector").value("assignableTypes"));
    }

    @Test
    void basePackagesSelectorAppliesToAlphaPackageControllers() throws Exception {
        mockMvc.perform(get("/api/advanced/advice-matching/base-package-only")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("advice_base_packages"))
                .andExpect(jsonPath("$.fieldErrors.selector").value("basePackages(alpha)"));
    }

    @Test
    void fallsBackToGlobalAdviceWhenNoSpecificSelectorMatches() throws Exception {
        mockMvc.perform(get("/api/advanced/advice-matching/plain")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("advice_global"))
                .andExpect(jsonPath("$.fieldErrors.selector").value("basePackages(part)"));
    }

    @Test
    void whenMultipleAdvicesMatchOrderStillDecidesWhichOneWins() throws Exception {
        mockMvc.perform(get("/api/advanced/advice-matching/composite")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("advice_base_packages"))
                .andExpect(jsonPath("$.fieldErrors.selector").value("basePackages(alpha)"));
    }
}
