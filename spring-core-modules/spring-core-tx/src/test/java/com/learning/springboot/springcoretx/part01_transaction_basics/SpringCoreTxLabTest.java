package com.learning.springboot.springcoretx.part01_transaction_basics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@Import(SpringCoreTxLabTest.ExtraTxConfig.class)
class SpringCoreTxLabTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TxPlaygroundService txPlaygroundService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
    }

    @Test
    void commitsOnSuccess() {
        accountService.createTwoAccounts();
        assertThat(accountRepository.count()).isEqualTo(2);
    }

    @Test
    void rollsBackOnRuntimeException() {
        assertThatThrownBy(() -> accountService.createTwoAccountsThenFail())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("boom");

        assertThat(accountRepository.count()).isEqualTo(0);
    }

    @Test
    void transactionalBeansAreProxied() {
        assertThat(AopUtils.isAopProxy(accountService)).isTrue();
    }

    @Test
    void transactionsAreActiveInsideTransactionalMethods() {
        assertThat(txPlaygroundService.isTransactionActive()).isTrue();
    }

    @Test
    void checkedExceptionsDoNotRollbackByDefault() {
        assertThatThrownBy(() -> txPlaygroundService.insertThenThrowChecked())
                .isInstanceOf(BusinessCheckedException.class);

        assertThat(countOwner("checked")).isEqualTo(1);
    }

    @Test
    void rollbackForCheckedExceptionsCanBeConfigured() {
        assertThatThrownBy(() -> txPlaygroundService.insertThenThrowCheckedWithRollback())
                .isInstanceOf(BusinessCheckedException.class);

        assertThat(countOwner("checkedRollback")).isEqualTo(0);
    }

    @Test
    void withoutTransactional_eachStatementIsEffectivelyAutoCommitted() {
        assertThatThrownBy(() -> txPlaygroundService.insertWithoutTxThenFail())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("boom");

        assertThat(countOwner("noTx")).isEqualTo(1);
    }

    @Test
    void requiresNewCanCommitEvenIfOuterTransactionRollsBack() {
        assertThatThrownBy(() -> txPlaygroundService.outerFailsButInnerCommits())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("boom");

        assertThat(countOwner("outer")).isEqualTo(0);
        assertThat(countOwner("inner")).isEqualTo(1);
    }

    @Test
    void requiresNewRollbackDoesNotNecessarilyRollbackOuter_whenCaught() {
        txPlaygroundService.outerCommitsEvenIfInnerRollsBack();

        assertThat(countOwner("outer2")).isEqualTo(1);
        assertThat(countOwner("inner2")).isEqualTo(0);
    }

    @Test
    void transactionTemplateAllowsProgrammaticCommitOrRollback() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        template.executeWithoutResult(status -> {
            jdbcTemplate.update("insert into accounts(owner, balance) values (?, ?)", "template", 1);
        });
        assertThat(countOwner("template")).isEqualTo(1);

        template.executeWithoutResult(status -> {
            jdbcTemplate.update("insert into accounts(owner, balance) values (?, ?)", "templateRollback", 1);
            status.setRollbackOnly();
        });
        assertThat(countOwner("templateRollback")).isEqualTo(0);
    }

    private int countOwner(String owner) {
        Integer count = jdbcTemplate.queryForObject("select count(*) from accounts where owner = ?", Integer.class, owner);
        return count == null ? 0 : count;
    }

    @TestConfiguration
    static class ExtraTxConfig {

        @Bean
        TxPlaygroundService txPlaygroundService(AccountRepository repository, JdbcTemplate jdbcTemplate, InnerRequiresNewService inner) {
            return new TxPlaygroundService(repository, jdbcTemplate, inner);
        }

        @Bean
        InnerRequiresNewService innerRequiresNewService(AccountRepository repository) {
            return new InnerRequiresNewService(repository);
        }
    }

    static class BusinessCheckedException extends Exception {
        BusinessCheckedException(String message) {
            super(message);
        }
    }

    static class TxPlaygroundService {
        private final AccountRepository repository;
        private final JdbcTemplate jdbcTemplate;
        private final InnerRequiresNewService innerRequiresNewService;

        TxPlaygroundService(AccountRepository repository, JdbcTemplate jdbcTemplate, InnerRequiresNewService innerRequiresNewService) {
            this.repository = repository;
            this.jdbcTemplate = jdbcTemplate;
            this.innerRequiresNewService = innerRequiresNewService;
        }

        @Transactional
        boolean isTransactionActive() {
            return TransactionSynchronizationManager.isActualTransactionActive();
        }

        @Transactional
        void insertThenThrowChecked() throws BusinessCheckedException {
            repository.insert("checked", 1);
            throw new BusinessCheckedException("checked boom");
        }

        @Transactional(rollbackFor = BusinessCheckedException.class)
        void insertThenThrowCheckedWithRollback() throws BusinessCheckedException {
            repository.insert("checkedRollback", 1);
            throw new BusinessCheckedException("checked boom");
        }

        void insertWithoutTxThenFail() {
            repository.insert("noTx", 1);
            throw new IllegalStateException("boom");
        }

        @Transactional
        void outerFailsButInnerCommits() {
            repository.insert("outer", 1);
            innerRequiresNewService.insertInNewTx("inner", 1);
            throw new IllegalStateException("boom");
        }

        @Transactional
        void outerCommitsEvenIfInnerRollsBack() {
            repository.insert("outer2", 1);
            try {
                innerRequiresNewService.insertInNewTxThenFail("inner2", 1);
            } catch (IllegalStateException ignored) {
            }
        }
    }

    static class InnerRequiresNewService {
        private final AccountRepository repository;

        InnerRequiresNewService(AccountRepository repository) {
            this.repository = repository;
        }

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void insertInNewTx(String owner, int balance) {
            repository.insert(owner, balance);
        }

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void insertInNewTxThenFail(String owner, int balance) {
            repository.insert(owner, balance);
            throw new IllegalStateException("inner boom");
        }
    }
}

