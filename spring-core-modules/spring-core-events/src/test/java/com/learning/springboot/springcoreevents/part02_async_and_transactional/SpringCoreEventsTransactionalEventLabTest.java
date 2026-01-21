package com.learning.springboot.springcoreevents.part02_async_and_transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.learning.springboot.springcoreevents.part01_event_basics.UserRegisteredEvent;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

class SpringCoreEventsTransactionalEventLabTest {

    @Test
    void afterCommitListenerRunsOnlyAfterCommit() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TxEventsConfig.class)) {
            TransactionTemplate transactionTemplate = context.getBean(TransactionTemplate.class);
            TxEventLog log = context.getBean(TxEventLog.class);

            log.clear();

            transactionTemplate.executeWithoutResult(status -> {
                context.publishEvent(new UserRegisteredEvent("Alice"));
                assertThat(log.afterCommitEntries()).isEmpty();
                assertThat(log.afterRollbackEntries()).isEmpty();
            });

            assertThat(log.afterCommitEntries()).containsExactly("afterCommit:Alice");
            assertThat(log.afterRollbackEntries()).isEmpty();
        }
    }

    @Test
    void afterCommitDoesNotRunOnRollback_butAfterRollbackDoes() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TxEventsConfig.class)) {
            TransactionTemplate transactionTemplate = context.getBean(TransactionTemplate.class);
            TxEventLog log = context.getBean(TxEventLog.class);

            log.clear();

            assertThatThrownBy(() -> transactionTemplate.executeWithoutResult(status -> {
                context.publishEvent(new UserRegisteredEvent("Bob"));
                throw new IllegalStateException("boom");
            })).isInstanceOf(IllegalStateException.class);

            assertThat(log.afterCommitEntries()).isEmpty();
            assertThat(log.afterRollbackEntries()).containsExactly("afterRollback:Bob");
        }
    }

    @Configuration
    @EnableTransactionManagement
    static class TxEventsConfig {

        @Bean
        PlatformTransactionManager transactionManager() {
            return new InMemoryTransactionManager();
        }

        @Bean
        TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
            return new TransactionTemplate(transactionManager);
        }

        @Bean
        TxEventLog txEventLog() {
            return new TxEventLog();
        }

        @Bean
        AfterCommitListener afterCommitListener(TxEventLog log) {
            return new AfterCommitListener(log);
        }

        @Bean
        AfterRollbackListener afterRollbackListener(TxEventLog log) {
            return new AfterRollbackListener(log);
        }
    }

    static class TxEventLog {
        private final List<String> afterCommitEntries = new CopyOnWriteArrayList<>();
        private final List<String> afterRollbackEntries = new CopyOnWriteArrayList<>();

        void addAfterCommit(String entry) {
            afterCommitEntries.add(entry);
        }

        void addAfterRollback(String entry) {
            afterRollbackEntries.add(entry);
        }

        List<String> afterCommitEntries() {
            return List.copyOf(afterCommitEntries);
        }

        List<String> afterRollbackEntries() {
            return List.copyOf(afterRollbackEntries);
        }

        void clear() {
            afterCommitEntries.clear();
            afterRollbackEntries.clear();
        }
    }

    static class AfterCommitListener {
        private final TxEventLog log;

        AfterCommitListener(TxEventLog log) {
            this.log = log;
        }

        @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
        public void on(UserRegisteredEvent event) {
            log.addAfterCommit("afterCommit:" + event.username());
        }
    }

    static class AfterRollbackListener {
        private final TxEventLog log;

        AfterRollbackListener(TxEventLog log) {
            this.log = log;
        }

        @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
        public void on(UserRegisteredEvent event) {
            log.addAfterRollback("afterRollback:" + event.username());
        }
    }

    static class InMemoryTransactionManager extends AbstractPlatformTransactionManager {

        @Override
        protected Object doGetTransaction() {
            return new Object();
        }

        @Override
        protected void doBegin(Object transaction, TransactionDefinition definition) {
        }

        @Override
        protected void doCommit(DefaultTransactionStatus status) {
        }

        @Override
        protected void doRollback(DefaultTransactionStatus status) {
        }
    }
}
