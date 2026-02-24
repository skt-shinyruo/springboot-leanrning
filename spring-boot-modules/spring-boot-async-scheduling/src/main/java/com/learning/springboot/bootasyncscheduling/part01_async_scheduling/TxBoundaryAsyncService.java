package com.learning.springboot.bootasyncscheduling.part01_async_scheduling;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class TxBoundaryAsyncService {

    @Async
    public CompletableFuture<TxSnapshot> observeTxActive() {
        return CompletableFuture.completedFuture(new TxSnapshot(
                Thread.currentThread().getName(),
                TransactionSynchronizationManager.isActualTransactionActive()));
    }

    @Async
    @Transactional
    public CompletableFuture<TxSnapshot> observeTxActiveInAsyncTransactionalMethod() {
        return CompletableFuture.completedFuture(new TxSnapshot(
                Thread.currentThread().getName(),
                TransactionSynchronizationManager.isActualTransactionActive()));
    }

    public record TxSnapshot(String threadName, boolean txActive) {}
}

