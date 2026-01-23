package com.learning.springboot.bootbusinesscase.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.springboot.bootbusinesscase.domain.PurchaseOrder;
import com.learning.springboot.bootbusinesscase.domain.PurchaseOrderRepository;
import com.learning.springboot.bootbusinesscase.events.InMemoryAuditLog;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * 参考实现：对齐 BootBusinessCaseExerciseTest 的练习题，提供可运行通过的 Solution（默认参与回归）。
 */
@SpringBootTest
@AutoConfigureMockMvc
class BootBusinessCaseExerciseSolutionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PurchaseOrderRepository repository;

    @Autowired
    private InMemoryAuditLog auditLog;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        auditLog.clear();
        repository.deleteAll();
    }

    @Test
    void solution_getOrderById_returnsOrderResponse() throws Exception {
        PurchaseOrder saved = repository.save(new PurchaseOrder("Alice", "SKU-1", 2));

        MvcResult result = mockMvc.perform(get("/api/orders/{id}", saved.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> body = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                new TypeReference<>() {
                }
        );

        assertThat(((Number) body.get("id")).longValue()).isEqualTo(saved.getId());
        assertThat(body.get("customer")).isEqualTo("Alice");
        assertThat(body.get("sku")).isEqualTo("SKU-1");
        assertThat(((Number) body.get("quantity")).intValue()).isEqualTo(2);
        assertThat(body.get("status")).isEqualTo("CREATED");
    }

    @Test
    void solution_getOrderById_returns404WhenMissing() throws Exception {
        mockMvc.perform(get("/api/orders/{id}", 9999)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void solution_listOrders_returnsJsonArray() throws Exception {
        repository.save(new PurchaseOrder("Alice", "SKU-1", 2));
        repository.save(new PurchaseOrder("Bob", "SKU-2", 1));

        MvcResult result = mockMvc.perform(get("/api/orders").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<Map<String, Object>> body = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                new TypeReference<>() {
                }
        );

        assertThat(body).hasSize(2);
        assertThat(body)
                .extracting(m -> (String) m.get("customer"))
                .containsExactlyInAnyOrder("Alice", "Bob");
    }

    @Test
    void solution_checkedException_defaultDoesNotRollback_transactionCommits() throws Exception {
        mockMvc.perform(post("/api/orders/fail-checked-default")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customer\":\"C1\",\"sku\":\"SKU-1\",\"quantity\":1}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("checked_error"));

        assertThat(repository.count())
                .as("默认规则：checked exception 不触发回滚，事务仍然提交")
                .isEqualTo(1);

        assertThat(auditLog.entries()).anySatisfy(e -> assertThat(e).startsWith("sync:orderPlaced:"));
        assertThat(auditLog.entries()).anySatisfy(e -> assertThat(e).startsWith("afterCommit:orderPlaced:"));
        assertThat(auditLog.entries()).noneSatisfy(e -> assertThat(e).startsWith("afterRollback:orderPlaced:"));
    }

    @Test
    void solution_checkedException_rollbackFor_rollsBack_andAfterRollbackFires() throws Exception {
        mockMvc.perform(post("/api/orders/fail-checked-rollback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customer\":\"C2\",\"sku\":\"SKU-2\",\"quantity\":1}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("checked_error"));

        assertThat(repository.count())
                .as("rollbackFor 生效：checked exception 也触发回滚")
                .isEqualTo(0);

        assertThat(auditLog.entries()).anySatisfy(e -> assertThat(e).startsWith("sync:orderPlaced:"));
        assertThat(auditLog.entries()).noneSatisfy(e -> assertThat(e).startsWith("afterCommit:orderPlaced:"));
        assertThat(auditLog.entries()).anySatisfy(e -> assertThat(e).startsWith("afterRollback:orderPlaced:"));
    }
}

