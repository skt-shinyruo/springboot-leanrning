package com.learning.springboot.bootasyncscheduling.part01_async_scheduling;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 一个“只用于演示/测试事务边界”的最小事务管理器实现：
 * - 事务上下文绑定在当前线程（通过 TransactionSynchronizationManager）
 * - 不依赖 DataSource/JPA 等资源
 *
 * 注意：它不试图完整实现生产级传播/挂起/恢复语义。
 */
public class ThreadLocalTransactionManager implements PlatformTransactionManager {

    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
        boolean existing = TransactionSynchronizationManager.isActualTransactionActive();
        if (existing) {
            return new SimpleTransactionStatus(false);
        }

        TransactionSynchronizationManager.setActualTransactionActive(true);
        TransactionSynchronizationManager.initSynchronization();
        return new SimpleTransactionStatus(true);
    }

    @Override
    public void commit(TransactionStatus status) throws TransactionException {
        cleanupIfNewTransaction(status);
    }

    @Override
    public void rollback(TransactionStatus status) throws TransactionException {
        cleanupIfNewTransaction(status);
    }

    private static void cleanupIfNewTransaction(TransactionStatus status) {
        if (!status.isNewTransaction()) {
            return;
        }

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
        TransactionSynchronizationManager.clear();
    }
}

