package com.learning.springboot.bootbusinesscase.part01_business_case;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.learning.springboot.bootbusinesscase.app.OrderService;
import com.learning.springboot.bootbusinesscase.app.PlaceOrderCommand;
import com.learning.springboot.bootbusinesscase.domain.PurchaseOrder;
import com.learning.springboot.bootbusinesscase.domain.PurchaseOrderRepository;
import com.learning.springboot.bootbusinesscase.events.InMemoryAuditLog;
import com.learning.springboot.bootbusinesscase.tracing.InvocationLog;

@SpringBootTest
class BootBusinessCaseServiceLabTest {

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
    void servicePublishesSyncAndAfterCommitEventsOnSuccess() {
        PurchaseOrder saved = orderService.placeOrder(new PlaceOrderCommand("Alice", "SKU-1", 2));

        assertThat(saved.getId()).isNotNull();
        assertThat(repository.count()).isEqualTo(1);

        assertThat(auditLog.entries()).anySatisfy(e -> assertThat(e).startsWith("sync:orderPlaced:"));
        assertThat(auditLog.entries()).anySatisfy(e -> assertThat(e).startsWith("afterCommit:orderPlaced:"));
        assertThat(invocationLog.count()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void rollbackPreventsAfterCommitListenerButSyncListenerStillRuns() {
        assertThatThrownBy(() -> orderService.placeOrderThenFail(new PlaceOrderCommand("Fail", "SKU-X", 1)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("boom");

        assertThat(repository.count()).isEqualTo(0);
        assertThat(auditLog.entries()).anySatisfy(e -> assertThat(e).startsWith("sync:orderPlaced:"));
        assertThat(auditLog.entries()).noneSatisfy(e -> assertThat(e).startsWith("afterCommit:orderPlaced:"));
    }
}

