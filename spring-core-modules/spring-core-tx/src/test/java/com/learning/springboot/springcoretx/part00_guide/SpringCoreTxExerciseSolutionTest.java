package com.learning.springboot.springcoretx.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.learning.springboot.springcoretx.SpringCoreTxApplication;
import com.learning.springboot.springcoretx.part01_transaction_basics.AccountRepository;
import com.learning.springboot.springcoretx.part02_template_and_debugging.TxIntrospectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 参考实现：对齐 SpringCoreTxExerciseTest 的练习题，提供可运行通过的 Solution（默认参与回归）。
 *
 * <p>约束：
 * <ul>
 *   <li>不修改 {@code *ExerciseTest}（练习仍保持 @Disabled）。</li>
 *   <li>不依赖耗时阈值：用事务边界/回滚结果等“可观测事实”做断言。</li>
 * </ul>
 */
@SpringBootTest(classes = { SpringCoreTxApplication.class, SpringCoreTxExerciseSolutionTest.SolutionConfig.class })
class SpringCoreTxExerciseSolutionTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RequiresNewPlaygroundService requiresNewPlaygroundService;

    @Autowired
    private SelfInvocationPlaygroundService selfInvocationPlaygroundService;

    @Autowired
    private CheckedExceptionRollbackPlaygroundService checkedExceptionRollbackPlaygroundService;

    @Autowired
    private TxIntrospectionService txIntrospectionService;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
    }

    @Test
    void solution_requiresNew_innerTransactionCanCommitEvenIfOuterRollsBack() {
        assertThatThrownBy(() -> requiresNewPlaygroundService.outerFailsButInnerCommits())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("boom");

        assertThat(countOwner("outer")).isEqualTo(0);
        assertThat(countOwner("inner")).isEqualTo(1);
    }

    @Test
    void solution_selfInvocation_transactionalOnInnerIsIgnoredWhenCalledViaThis() {
        assertThatThrownBy(() -> selfInvocationPlaygroundService.outerCallsInnerViaThis_thenThrows())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("boom");

        assertThat(countOwner("selfInvocation")).isEqualTo(1);
    }

    @Test
    void solution_selfInvocation_canBeAvoidedByCallingViaProxyFromTheContainer() {
        assertThatThrownBy(() -> selfInvocationPlaygroundService.outerCallsInnerViaContainerProxy_thenThrows())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("boom");

        assertThat(countOwner("selfInvocationProxy")).isEqualTo(0);
    }

    @Test
    void solution_checkedException_doesNotRollbackByDefault_butRollbackForCanChangeIt() {
        assertThatThrownBy(() -> checkedExceptionRollbackPlaygroundService.insertThenThrowChecked())
                .isInstanceOf(BusinessCheckedException.class);
        assertThat(countOwner("checked")).isEqualTo(1);

        assertThatThrownBy(() -> checkedExceptionRollbackPlaygroundService.insertThenThrowCheckedWithRollback())
                .isInstanceOf(BusinessCheckedException.class);
        assertThat(countOwner("checkedRollback")).isEqualTo(0);
    }

    @Test
    void solution_transactionIntrospection_canProveTransactionBoundary() {
        assertThat(TransactionSynchronizationManager.isActualTransactionActive()).isFalse();
        assertThat(txIntrospectionService.isTransactionActive()).isTrue();
    }

    private int countOwner(String owner) {
        Integer count = jdbcTemplate.queryForObject("select count(*) from accounts where owner = ?", Integer.class, owner);
        return count == null ? 0 : count;
    }

    @TestConfiguration
    static class SolutionConfig {

        @Bean
        RequiresNewPlaygroundService requiresNewPlaygroundService(AccountRepository repository, InnerRequiresNewService innerRequiresNewService) {
            return new RequiresNewPlaygroundService(repository, innerRequiresNewService);
        }

        @Bean
        InnerRequiresNewService innerRequiresNewService(AccountRepository repository) {
            return new InnerRequiresNewService(repository);
        }

        @Bean
        SelfInvocationPlaygroundService selfInvocationPlaygroundService(AccountRepository repository,
                ObjectProvider<SelfInvocationPlaygroundService> selfProvider) {
            return new SelfInvocationPlaygroundService(repository, selfProvider);
        }

        @Bean
        CheckedExceptionRollbackPlaygroundService checkedExceptionRollbackPlaygroundService(AccountRepository repository) {
            return new CheckedExceptionRollbackPlaygroundService(repository);
        }
    }

    static class RequiresNewPlaygroundService {
        private final AccountRepository repository;
        private final InnerRequiresNewService innerRequiresNewService;

        RequiresNewPlaygroundService(AccountRepository repository, InnerRequiresNewService innerRequiresNewService) {
            this.repository = repository;
            this.innerRequiresNewService = innerRequiresNewService;
        }

        @Transactional
        void outerFailsButInnerCommits() {
            repository.insert("outer", 1);
            innerRequiresNewService.insertInNewTx("inner", 1);
            throw new IllegalStateException("boom");
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
    }

    static class SelfInvocationPlaygroundService {
        private final AccountRepository repository;
        private final ObjectProvider<SelfInvocationPlaygroundService> selfProvider;

        SelfInvocationPlaygroundService(AccountRepository repository, ObjectProvider<SelfInvocationPlaygroundService> selfProvider) {
            this.repository = repository;
            this.selfProvider = selfProvider;
        }

        void outerCallsInnerViaThis_thenThrows() {
            this.innerTransactional_thenInsertThenThrow("selfInvocation");
        }

        void outerCallsInnerViaContainerProxy_thenThrows() {
            selfProvider.getObject().innerTransactional_thenInsertThenThrow("selfInvocationProxy");
        }

        @Transactional
        void innerTransactional_thenInsertThenThrow(String owner) {
            repository.insert(owner, 1);
            throw new IllegalStateException("boom");
        }
    }

    static class BusinessCheckedException extends Exception {
        BusinessCheckedException(String message) {
            super(message);
        }
    }

    static class CheckedExceptionRollbackPlaygroundService {
        private final AccountRepository repository;

        CheckedExceptionRollbackPlaygroundService(AccountRepository repository) {
            this.repository = repository;
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
    }

}
