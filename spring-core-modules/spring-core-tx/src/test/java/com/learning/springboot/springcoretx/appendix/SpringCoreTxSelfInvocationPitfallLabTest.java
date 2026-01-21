package com.learning.springboot.springcoretx.appendix;

import com.learning.springboot.springcoretx.part01_transaction_basics.AccountRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SpringBootTest
@Import(SpringCoreTxSelfInvocationPitfallLabTest.SelfInvocationConfig.class)
class SpringCoreTxSelfInvocationPitfallLabTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SelfInvocationPitfallService selfInvocationPitfallService;

    @Autowired
    private SplitBeanOuterService splitBeanOuterService;

    @Autowired
    private SplitBeanInnerService splitBeanInnerService;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
    }

    @Test
    void selfInvocationBypassesTransactional_onInnerMethod() {
        assertThatThrownBy(() -> selfInvocationPitfallService.outerCallsInnerTransactionalThenFails())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("boom");

        assertThat(selfInvocationPitfallService.lastInnerTransactionActive())
                .as("self-invocation bypasses proxy => @Transactional is ignored")
                .isFalse();
        assertThat(accountRepository.count())
                .as("no transaction => insert auto-commits => exception cannot rollback")
                .isEqualTo(1);
    }

    @Test
    void splittingBeanRestoresTransactional_interceptorIsApplied() {
        assertThatThrownBy(() -> splitBeanOuterService.outerCallsInnerTransactionalThenFails())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("boom");

        assertThat(splitBeanInnerService.lastTransactionActive())
                .as("call crosses bean boundary => proxy applies @Transactional")
                .isTrue();
        assertThat(accountRepository.count())
                .as("transaction rollback => insert is not persisted")
                .isEqualTo(0);
    }

    @TestConfiguration
    static class SelfInvocationConfig {

        @Bean
        SelfInvocationPitfallService selfInvocationPitfallService(AccountRepository repository) {
            return new SelfInvocationPitfallService(repository);
        }

        @Bean
        SplitBeanInnerService splitBeanInnerService(AccountRepository repository) {
            return new SplitBeanInnerService(repository);
        }

        @Bean
        SplitBeanOuterService splitBeanOuterService(SplitBeanInnerService inner) {
            return new SplitBeanOuterService(inner);
        }
    }

    static class SelfInvocationPitfallService {
        private final AccountRepository repository;
        private final AtomicBoolean lastInnerTransactionActive = new AtomicBoolean(false);

        SelfInvocationPitfallService(AccountRepository repository) {
            this.repository = repository;
        }

        void outerCallsInnerTransactionalThenFails() {
            innerTransactionalThenFail();
        }

        @Transactional
        public void innerTransactionalThenFail() {
            lastInnerTransactionActive.set(TransactionSynchronizationManager.isActualTransactionActive());
            repository.insert("selfInvocation", 1);
            throw new IllegalStateException("boom");
        }

        boolean lastInnerTransactionActive() {
            return lastInnerTransactionActive.get();
        }
    }

    static class SplitBeanOuterService {
        private final SplitBeanInnerService inner;

        SplitBeanOuterService(SplitBeanInnerService inner) {
            this.inner = inner;
        }

        void outerCallsInnerTransactionalThenFails() {
            inner.transactionalInsertThenFail();
        }
    }

    static class SplitBeanInnerService {
        private final AccountRepository repository;
        private final AtomicBoolean lastTransactionActive = new AtomicBoolean(false);

        SplitBeanInnerService(AccountRepository repository) {
            this.repository = repository;
        }

        @Transactional
        public void transactionalInsertThenFail() {
            lastTransactionActive.set(TransactionSynchronizationManager.isActualTransactionActive());
            repository.insert("splitBean", 1);
            throw new IllegalStateException("boom");
        }

        boolean lastTransactionActive() {
            return lastTransactionActive.get();
        }
    }
}

