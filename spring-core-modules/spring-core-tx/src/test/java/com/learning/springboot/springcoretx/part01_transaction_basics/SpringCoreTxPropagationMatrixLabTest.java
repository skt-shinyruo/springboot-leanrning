package com.learning.springboot.springcoretx.part01_transaction_basics;

// 本测试用于补齐传播行为的“进阶分支”对照：MANDATORY/NEVER/NESTED，并用查表行数作为可回归证据链（避免只看日志/异常）。

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(SpringCoreTxPropagationMatrixLabTest.ExtraTxConfig.class)
class SpringCoreTxPropagationMatrixLabTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private OuterTxService outerTxService;

    @Autowired
    private InnerTxService innerTxService;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
    }

    @Test
    void mandatoryThrowsWhenNoExistingTransaction() {
        assertThatThrownBy(() -> innerTxService.mandatoryInsert("mandatory", 1))
                .isInstanceOf(IllegalTransactionStateException.class);

        assertThat(countOwner("mandatory")).isEqualTo(0);
    }

    @Test
    void neverThrowsWhenTransactionExists() {
        assertThatThrownBy(() -> outerTxService.outerCallsNeverThenFails())
                .isInstanceOf(IllegalTransactionStateException.class);

        assertThat(countOwner("outerNever")).isEqualTo(0);
        assertThat(countOwner("never")).isEqualTo(0);
    }

    @Test
    void nestedRollsBackOnlyInnerWhenOuterCatchesException() {
        outerTxService.outerCommitsEvenIfNestedFails();

        assertThat(countOwner("outerNested")).isEqualTo(1);
        assertThat(countOwner("nested")).isEqualTo(0);
    }

    private int countOwner(String owner) {
        Integer count = jdbcTemplate.queryForObject("select count(*) from accounts where owner = ?", Integer.class, owner);
        return count == null ? 0 : count;
    }

    static class OuterTxService {

        private final AccountRepository repository;
        private final InnerTxService innerTxService;

        OuterTxService(AccountRepository repository, InnerTxService innerTxService) {
            this.repository = repository;
            this.innerTxService = innerTxService;
        }

        @Transactional
        void outerCallsNeverThenFails() {
            repository.insert("outerNever", 1);
            innerTxService.neverInsert("never", 1);
        }

        @Transactional
        void outerCommitsEvenIfNestedFails() {
            repository.insert("outerNested", 1);
            try {
                innerTxService.nestedInsertThenFail("nested", 1);
            } catch (IllegalStateException ignored) {
            }
        }
    }

    static class InnerTxService {

        private final AccountRepository repository;

        InnerTxService(AccountRepository repository) {
            this.repository = repository;
        }

        @Transactional(propagation = Propagation.MANDATORY)
        void mandatoryInsert(String owner, int balance) {
            repository.insert(owner, balance);
        }

        @Transactional(propagation = Propagation.NEVER)
        void neverInsert(String owner, int balance) {
            repository.insert(owner, balance);
        }

        @Transactional(propagation = Propagation.NESTED)
        void nestedInsertThenFail(String owner, int balance) {
            repository.insert(owner, balance);
            throw new IllegalStateException("nested boom");
        }
    }

    @TestConfiguration
    static class ExtraTxConfig {

        @Bean
        OuterTxService outerTxService(AccountRepository repository, InnerTxService innerTxService) {
            return new OuterTxService(repository, innerTxService);
        }

        @Bean
        InnerTxService innerTxService(AccountRepository repository) {
            return new InnerTxService(repository);
        }
    }
}

