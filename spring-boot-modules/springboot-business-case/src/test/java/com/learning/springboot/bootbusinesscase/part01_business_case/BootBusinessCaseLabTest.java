package com.learning.springboot.bootbusinesscase.part01_business_case;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.learning.springboot.bootbusinesscase.app.OrderService;
import com.learning.springboot.bootbusinesscase.domain.PurchaseOrderRepository;
import com.learning.springboot.bootbusinesscase.events.InMemoryAuditLog;
import com.learning.springboot.bootbusinesscase.tracing.InvocationLog;

@SpringBootTest
@AutoConfigureMockMvc
class BootBusinessCaseLabTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PurchaseOrderRepository repository;

    @Autowired
    private InMemoryAuditLog auditLog;

    @Autowired
    private InvocationLog invocationLog;

    @Autowired
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        invocationLog.reset();
        auditLog.clear();
        repository.deleteAll();
    }

    @Test
    void createsOrderWhenRequestIsValid() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customer\":\"Alice\",\"sku\":\"SKU-1\",\"quantity\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.customer").value("Alice"))
                .andExpect(jsonPath("$.sku").value("SKU-1"))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.status").value("CREATED"));

        assertThat(repository.count()).isEqualTo(1);
        assertThat(auditLog.entries()).anySatisfy(e -> assertThat(e).startsWith("sync:orderPlaced:"));
        assertThat(auditLog.entries()).anySatisfy(e -> assertThat(e).startsWith("afterCommit:orderPlaced:"));
    }

    @Test
    void returnsValidationErrorWhenRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customer\":\"\",\"sku\":\"\",\"quantity\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("validation_failed"))
                .andExpect(jsonPath("$.fieldErrors.customer").exists())
                .andExpect(jsonPath("$.fieldErrors.sku").exists())
                .andExpect(jsonPath("$.fieldErrors.quantity").exists());

        assertThat(repository.count()).isEqualTo(0);
        assertThat(auditLog.entries()).isEmpty();
    }

    @Test
    void serviceBeanIsAnAopProxy() {
        assertThat(AopUtils.isAopProxy(orderService)).isTrue();
    }

    @Test
    void aspectRecordsInvocationForTracedOperation() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customer\":\"Bob\",\"sku\":\"SKU-2\",\"quantity\":1}"))
                .andExpect(status().isOk());

        assertThat(invocationLog.count()).isGreaterThanOrEqualTo(1);
        assertThat(invocationLog.lastMethod()).contains("placeOrder");
    }

    @Test
    void rollbackPreventsPersistenceOnFailure() throws Exception {
        mockMvc.perform(post("/api/orders/fail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customer\":\"Fail\",\"sku\":\"SKU-X\",\"quantity\":1}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("internal_error"));

        assertThat(repository.count()).isEqualTo(0);
    }

    @Test
    void syncListenerRunsEvenWhenTransactionRollsBack_butAfterCommitDoesNot() throws Exception {
        mockMvc.perform(post("/api/orders/fail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customer\":\"Fail\",\"sku\":\"SKU-X\",\"quantity\":1}"))
                .andExpect(status().isInternalServerError());

        assertThat(auditLog.entries()).anySatisfy(e -> assertThat(e).startsWith("sync:orderPlaced:"));
        assertThat(auditLog.entries()).noneSatisfy(e -> assertThat(e).startsWith("afterCommit:orderPlaced:"));
    }

    @Test
    void afterCommitListenerRunsOnSuccess() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customer\":\"Carol\",\"sku\":\"SKU-3\",\"quantity\":3}"))
                .andExpect(status().isOk());

        assertThat(auditLog.entries()).anySatisfy(e -> assertThat(e).startsWith("afterCommit:orderPlaced:"));
    }

    @Test
    void validationRejectsNegativeQuantity() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customer\":\"Alice\",\"sku\":\"SKU-1\",\"quantity\":-1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validationRejectsMissingFields() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrderIsIdempotentAtDatabaseLevel_perRequestOnly() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customer\":\"Dan\",\"sku\":\"SKU-4\",\"quantity\":1}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customer\":\"Dan\",\"sku\":\"SKU-4\",\"quantity\":1}"))
                .andExpect(status().isOk());

        assertThat(repository.count()).isEqualTo(2);
    }
}

