package com.learning.springboot.bootbusinesscase.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.springboot.bootbusinesscase.domain.PurchaseOrder;
import com.learning.springboot.bootbusinesscase.domain.PurchaseOrderRepository;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class BootBusinessCaseExerciseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PurchaseOrderRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @Disabled("练习：实现 GET /api/orders/{id}（按 id 查询订单），并让本测试通过")
    void exercise_getOrderById() throws Exception {
        PurchaseOrder saved = repository.save(new PurchaseOrder("Alice", "SKU-1", 2));

        MvcResult result = mockMvc.perform(get("/api/orders/" + saved.getId()))
                .andReturn();

        assertThat(result.getResponse().getStatus())
                .as("""
                        练习：实现 GET /api/orders/{id}（按 id 查询订单）。

                        下一步：
                        1) 在 `OrderController` 增加 `@GetMapping(\"/{id}\")`。
                        2) 用 `PurchaseOrderRepository#findById(id)` 查询并返回 `OrderResponse.from(...)`。
                        3) 本测试通过后，再补一个“找不到 id 返回 404”的负向测试。

                        参考：
                        - `spring-boot-modules/spring-boot-business-case/src/main/java/com/learning/springboot/bootbusinesscase/api/OrderController.java`
                        - `spring-boot-modules/spring-boot-business-case/src/test/java/com/learning/springboot/bootbusinesscase/part01_business_case/BootBusinessCaseLabTest.java`
                        """)
                .isEqualTo(200);

        Map<String, Object> body = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                new TypeReference<Map<String, Object>>() {
                }
        );

        assertThat(((Number) body.get("id")).longValue()).isEqualTo(saved.getId());
        assertThat(body.get("customer")).isEqualTo("Alice");
        assertThat(body.get("sku")).isEqualTo("SKU-1");
        assertThat(((Number) body.get("quantity")).intValue()).isEqualTo(2);
        assertThat(body.get("status")).isEqualTo("CREATED");
    }

    @Test
    @Disabled("练习：实现 GET /api/orders（订单列表），并让本测试通过")
    void exercise_listOrders() throws Exception {
        repository.save(new PurchaseOrder("Alice", "SKU-1", 2));
        repository.save(new PurchaseOrder("Bob", "SKU-2", 1));

        MvcResult result = mockMvc.perform(get("/api/orders"))
                .andReturn();

        assertThat(result.getResponse().getStatus())
                .as("""
                        练习：实现 GET /api/orders（订单列表）。

                        期望：
                        - 返回一个 JSON 数组（元素形状与 POST /api/orders 的响应一致）。

                        下一步：
                        1) 在 `OrderController` 增加 `@GetMapping`。
                        2) 用 `PurchaseOrderRepository#findAll()` 获取列表并映射为 `List<OrderResponse>`。

                        常见坑：
                        - 返回的顺序可能不稳定；如果你要稳定顺序，考虑按 id 排序（仓库层或 controller 层）。
                        """)
                .isEqualTo(200);

        List<Map<String, Object>> body = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                new TypeReference<List<Map<String, Object>>>() {
                }
        );

        assertThat(body).hasSize(2);
        assertThat(body)
                .extracting(m -> (String) m.get("customer"))
                .containsExactlyInAnyOrder("Alice", "Bob");
    }

    @Test
    @Disabled("练习：让 checked exception 也触发事务回滚（增加一个新 endpoint + 测试演示）")
    void exercise_checkedExceptionRollbackRule() {
        assertThat(true)
                .as("""
                        练习：让 checked exception 也触发事务回滚（增加一个新 endpoint + 测试演示）。

                        下一步：
                        1) 设计一个新流程：在 `@Transactional` 内抛出一个 checked exception。
                        2) 先观察默认行为：checked exception 默认不回滚（需要验证数据是否落库）。
                        3) 用 `rollbackFor=...`（或等价方式）修改规则，再用测试证明回滚生效。

                        建议阅读：
                        - `spring-core-modules/spring-core-tx/docs/transaction-basics-rollback-rules.md`
                        - `spring-core-modules/spring-core-tx/docs/transaction-basics-transaction-boundary.md`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：复现 self-invocation 陷阱（Tx/AOP），并写下 1 个规避方式")
    void exercise_selfInvocationPitfall() {
        assertThat(true)
                .as("""
                        练习：复现 self-invocation 陷阱（Tx/AOP），并写下 1 个规避方式。

                        下一步：
                        1) 写一个“同类内部 this.xxx() 调用”的场景，让事务/切面不生效。
                        2) 用测试证明“不生效”的现象。
                        3) 实现一种规避方式（拆分 bean / 注入自身代理 / exposeProxy 等）。

                        建议阅读：
                        - `spring-core-modules/spring-core-aop/docs/proxy-fundamentals-self-invocation.md`
                        - `spring-core-modules/spring-core-tx/docs/transaction-basics-transactional-proxy.md`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：新增一个 AFTER_ROLLBACK 的 transactional event listener，并断言它只在回滚时触发")
    void exercise_transactionalEventAfterRollback() {
        assertThat(true)
                .as("""
                        练习：新增一个 AFTER_ROLLBACK 的 transactional event listener，并断言它只在回滚时触发。

                        下一步：
                        1) 在事件监听器上使用 `@TransactionalEventListener(phase = AFTER_ROLLBACK)`。
                        2) 对比两条链路：成功提交 vs 触发回滚。
                        3) 用测试断言：only rollback 时 AFTER_ROLLBACK 会触发。

                        建议阅读：
                        - `spring-core-modules/spring-core-events/docs/async-and-transactional-transactional-event-listener.md`
                        """)
                .isFalse();
    }
}
