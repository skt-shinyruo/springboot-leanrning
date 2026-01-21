package com.learning.springboot.bootdatajpa.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class BootDataJpaExerciseTest {

    @Test
    @Disabled("练习：新增一个 Repository 查询方法（例如 findByAuthor），并用测试证明它能工作")
    void exercise_addQueryMethod() {
        assertThat(true)
                .as("""
                        练习：新增一个 Repository 查询方法（例如 findByAuthor），并用测试证明它能工作。

                        下一步：
                        1) 在 `BookRepository` 增加方法（例如 `Optional<Book> findByAuthor(String author)` 或 `List<Book> ...`）。
                        2) 在测试里先插入 2 条数据，再调用方法并断言返回。

                        参考：
                        - `spring-boot-modules/springboot-data-jpa/src/test/java/com/learning/springboot/bootdatajpa/part01_data_jpa/BootDataJpaLabTest.java`
                        - `docs/data-jpa/springboot-data-jpa/part-01-data-jpa/01-entity-states.md`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：增加一个实体关系（例如 Author -> Books），并复现一次 fetching 坑（N+1）")
    void exercise_relationshipsAndFetching() {
        assertThat(true)
                .as("""
                        练习：增加一个实体关系（例如 Author -> Books），并复现一次 fetching 坑（N+1）。

                        下一步：
                        1) 设计一个最小关系（OneToMany/ManyToOne）。
                        2) 在测试里准备数据，触发一次“查询 + 访问集合”，观察 SQL 数量。
                        3) 记录你观察到的现象，并尝试一种修复（例如 fetch join / EntityGraph）。

                        建议阅读：
                        - `docs/data-jpa/springboot-data-jpa/part-01-data-jpa/05-fetching-and-n-plus-one.md`
                        - `docs/data-jpa/springboot-data-jpa/part-01-data-jpa/07-debug-sql.md`

                        常见坑：
                        - 只看日志容易误判；先用断言固定结论，再用 SQL 日志解释原因
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：演示 @DataJpaTest 的回滚行为（例如 @Commit 或 @Rollback(false)），并记录观察")
    void exercise_rollbackBehavior() {
        assertThat(true)
                .as("""
                        练习：演示 @DataJpaTest 的回滚行为（例如 @Commit 或 @Rollback(false)），并记录观察。

                        下一步：
                        1) 写一个测试：插入数据后结束测试。
                        2) 对比默认行为（回滚）与显式提交（@Commit / @Rollback(false)）。
                        3) 用断言/查询证明“测试结束后数据是否还在”。

                        建议阅读：
                        - `docs/data-jpa/springboot-data-jpa/part-01-data-jpa/06-datajpatest-slice.md`
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：演示 getReferenceById 的 lazy 行为，并记录你观察到的现象")
    void exercise_getReferenceById() {
        assertThat(true)
                .as("""
                        练习：演示 getReferenceById 的 lazy 行为，并记录你观察到的现象。

                        下一步：
                        1) 先保存一条实体并拿到 id。
                        2) 调用 `getReferenceById(id)`，观察返回对象与 SQL 触发时机。
                        3) 对比：直接 findById 与 getReferenceById 在“何时触发 SQL”上的差异。

                        常见坑：
                        - 访问 proxy 属性时才会触发 SQL；越过事务边界可能抛 LazyInitializationException
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：新增一个 JPQL 或 native query，并用测试证明它返回预期结果")
    void exercise_customQuery() {
        assertThat(true)
                .as("""
                        练习：新增一个 JPQL 或 native query，并用测试证明它返回预期结果。

                        下一步：
                        1) 在 repository 里新增 `@Query(...)` 方法（JPQL 或 nativeQuery=true）。
                        2) 准备数据后调用该方法，并对返回内容做断言。

                        参考：
                        - `docs/data-jpa/springboot-data-jpa/appendix/90-common-pitfalls.md`
                        """)
                .isFalse();
    }
}
