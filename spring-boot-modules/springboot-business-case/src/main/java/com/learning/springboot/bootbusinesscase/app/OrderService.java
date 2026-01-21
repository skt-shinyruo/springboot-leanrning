package com.learning.springboot.bootbusinesscase.app;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.learning.springboot.bootbusinesscase.domain.PurchaseOrder;
import com.learning.springboot.bootbusinesscase.domain.PurchaseOrderRepository;
import com.learning.springboot.bootbusinesscase.events.OrderPlacedEvent;
import com.learning.springboot.bootbusinesscase.tracing.TracedOperation;

@Service
public class OrderService {

    private final PurchaseOrderRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(PurchaseOrderRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @TracedOperation
    @Transactional
    public PurchaseOrder placeOrder(PlaceOrderCommand command) {
        PurchaseOrder saved = repository.save(new PurchaseOrder(command.customer(), command.sku(), command.quantity()));
        repository.flush();
        eventPublisher.publishEvent(new OrderPlacedEvent(saved.getId(), saved.getCustomer()));
        return saved;
    }

    @TracedOperation
    @Transactional
    public PurchaseOrder placeOrderThenFail(PlaceOrderCommand command) {
        PurchaseOrder saved = repository.save(new PurchaseOrder(command.customer(), command.sku(), command.quantity()));
        repository.flush();
        eventPublisher.publishEvent(new OrderPlacedEvent(saved.getId(), saved.getCustomer()));
        throw new IllegalStateException("boom");
    }
}

