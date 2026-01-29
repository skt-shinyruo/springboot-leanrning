package com.learning.springboot.springcoretx.part00_guide;

import com.learning.springboot.springcoretx.part01_transaction_basics.AccountRepository;
import com.learning.springboot.springcoretx.part01_transaction_basics.AccountService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringCoreTxExerciseTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
    }

    @Test
    @Disabled("练习：新增一个 Propagation.REQUIRES_NEW 的方法，并用断言证明它的行为")
    void exercise_requiresNew() {
        assertThat(true)
                .as("""
                        练习：新增一个 Propagation.REQUIRES_NEW 的方法，并用断言证明它的行为。

                        下一步：
                        1) 参考 `SpringCoreTxLabTest#requiresNewCanCommitEvenIfOuterTransactionRollsBack` 的实验。
                        2) 写一个外层事务会失败的场景（抛异常回滚）。
                        3) 在内层用 REQUIRES_NEW 插入一条“独立事务”记录，证明它能独立提交。

                        建议阅读：
                        - `spring-core-modules/spring-core-tx/docs/part-01-transaction-basics/057-04-propagation.md`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：复现 @Transactional 的自调用陷阱，并展示一种规避方式")
    void exercise_selfInvocation() {
        assertThat(true)
                .as("""
                        练习：复现 @Transactional 的自调用陷阱，并展示一种规避方式。

                        下一步：
                        1) 写一个同类内部 `this.inner()` 的调用链，让 inner 上的 @Transactional 不生效。
                        2) 用测试证明：是否真的有事务（或数据是否回滚）。
                        3) 选择一种规避方式：拆分 bean / 注入自身代理 / exposeProxy。

                        建议阅读：
                        - `spring-core-modules/spring-core-tx/docs/part-01-transaction-basics/055-02-transactional-proxy.md`
                        - `spring-core-modules/spring-core-aop/docs/part-01-proxy-fundamentals/032-03-self-invocation.md`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：为 checked exception 增加回滚规则，并更新预期")
    void exercise_checkedRollbackRule() {
        assertThat(true)
                .as("""
                        练习：为 checked exception 增加回滚规则，并更新预期。

                        下一步：
                        1) 写一个会抛 checked exception 的事务方法。
                        2) 先观察默认行为（通常不会回滚）。
                        3) 再用 `rollbackFor = ...` 修改规则，并用断言证明回滚发生。

                        建议阅读：
                        - `spring-core-modules/spring-core-tx/docs/part-01-transaction-basics/056-03-rollback-rules.md`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：用 TransactionSynchronizationManager 断言事务边界（证明当前是否真的在事务内）")
    void exercise_transactionIntrospection() {
        assertThat(true)
                .as("""
                        练习：用 TransactionSynchronizationManager 断言事务边界（证明当前是否真的在事务内）。

                        下一步：
                        1) 在一个 @Transactional 方法里断言 `isActualTransactionActive()` 为 true。
                        2) 在非事务方法里断言它为 false。
                        3) 把结论写成可重复的测试（比看日志可靠）。

                        建议阅读：
                        - `spring-core-modules/spring-core-tx/docs/part-02-template-and-debugging/059-06-debugging.md`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：写一个负向验证：移除 createTwoAccountsThenFail 上的 @Transactional，观察回滚失效会发生什么")
    void exercise_removeTransactional() {
        assertThatThrownBy(() -> accountService.createTwoAccountsThenFail())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("boom");

        assertThat(accountRepository.count())
                .as("""
                        练习：写一个负向验证：移除 `AccountService#createTwoAccountsThenFail` 上的 @Transactional。

                        说明：
                        - 你现在看到的失败是“正常的”：因为当前方法有 @Transactional，异常会触发回滚，所以 count=0。
                        - 当你移除 @Transactional 后，每次 insert 可能会 auto-commit，异常抛出也无法回滚，所以 count 会变成 2。

                        下一步：
                        1) 打开 `spring-core-modules/spring-core-tx/src/main/java/.../AccountService.java`。
                        2) 暂时移除 `createTwoAccountsThenFail` 上的 `@Transactional`。
                        3) 重新运行本测试，观察断言变化。

                        建议阅读：
                        - `spring-core-modules/spring-core-tx/docs/part-01-transaction-basics/054-01-transaction-boundary.md`
                        """)
                .isEqualTo(2);
    }
}
