package com.learning.springboot.bootbusinesscase.events;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

@Component
public class OrderEventListeners {

    private final InMemoryAuditLog auditLog;

    public OrderEventListeners(InMemoryAuditLog auditLog) {
        this.auditLog = auditLog;
    }

    @EventListener
    public void onOrderPlacedSync(OrderPlacedEvent event) {
        auditLog.add("sync:orderPlaced:" + event.orderId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderPlacedAfterCommit(OrderPlacedEvent event) {
        auditLog.add("afterCommit:orderPlaced:" + event.orderId());
    }
}

