package com.learning.springboot.springcoretx.part01_transaction_basics;

// 本测试用于把“回滚规则”固化为断言：RuntimeException 默认回滚、CheckedException 默认提交；
// 以及 rollbackFor/noRollbackFor 如何覆盖默认规则（避免只靠口述记忆）。

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(SpringCoreTxRollbackRulesLabTest.RollbackRulesTestConfig.class)
class SpringCoreTxRollbackRulesLabTest {

    @Autowired
    private AccountRepository repository;

    @Autowired
    private RollbackRulesService rollbackRulesService;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void runtimeExceptionRollsBackByDefault() {
        assertThatThrownBy(() -> rollbackRulesService.runtimeExceptionRollsBack())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("runtime boom");

        assertThat(repository.count()).isEqualTo(0);
    }

    @Test
    void checkedExceptionCommitsByDefault() {
        assertThatThrownBy(() -> rollbackRulesService.checkedExceptionCommitsByDefault())
                .isInstanceOf(CustomCheckedException.class)
                .hasMessage("checked boom");

        System.out.println("OBSERVE: checked exception does NOT trigger rollback by default (transaction commits, then exception is rethrown).");
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void checkedExceptionRollsBackWhenRollbackForIsSpecified() {
        assertThatThrownBy(() -> rollbackRulesService.checkedExceptionRollsBackWhenRollbackForIsSpecified())
                .isInstanceOf(CustomCheckedException.class)
                .hasMessage("checked boom");

        assertThat(repository.count()).isEqualTo(0);
    }

    @Test
    void runtimeExceptionCommitsWhenNoRollbackForIsSpecified() {
        assertThatThrownBy(() -> rollbackRulesService.runtimeExceptionCommitsWhenNoRollbackForIsSpecified())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("runtime boom");

        System.out.println("OBSERVE: noRollbackFor can make a RuntimeException commit (exception is still rethrown).");
        assertThat(repository.count()).isEqualTo(1);
    }

    static class CustomCheckedException extends Exception {
        CustomCheckedException(String message) {
            super(message);
        }
    }

    static class RollbackRulesService {

        private final AccountRepository repository;

        RollbackRulesService(AccountRepository repository) {
            this.repository = repository;
        }

        @Transactional
        void runtimeExceptionRollsBack() {
            repository.insert("runtimeDefaultRollback", 1);
            throw new IllegalStateException("runtime boom");
        }

        @Transactional
        void checkedExceptionCommitsByDefault() throws CustomCheckedException {
            repository.insert("checkedDefaultCommit", 1);
            throw new CustomCheckedException("checked boom");
        }

        @Transactional(rollbackFor = CustomCheckedException.class)
        void checkedExceptionRollsBackWhenRollbackForIsSpecified() throws CustomCheckedException {
            repository.insert("checkedRollbackFor", 1);
            throw new CustomCheckedException("checked boom");
        }

        @Transactional(noRollbackFor = IllegalStateException.class)
        void runtimeExceptionCommitsWhenNoRollbackForIsSpecified() {
            repository.insert("runtimeNoRollbackFor", 1);
            throw new IllegalStateException("runtime boom");
        }
    }

    @TestConfiguration
    static class RollbackRulesTestConfig {

        @Bean
        RollbackRulesService rollbackRulesService(AccountRepository repository) {
            return new RollbackRulesService(repository);
        }
    }
}

