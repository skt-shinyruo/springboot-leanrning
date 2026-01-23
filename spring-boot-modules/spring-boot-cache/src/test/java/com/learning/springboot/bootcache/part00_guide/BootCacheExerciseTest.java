package com.learning.springboot.bootcache.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class BootCacheExerciseTest {

    @Test
    @Disabled("练习：增加一个多参数方法，观察默认 key（SimpleKey）语义，并写测试固化结论")
    void exercise_defaultKeyGeneration() {
        assertThat(true)
                .as("""
                        练习：增加一个多参数方法，观察默认 key（SimpleKey）语义，并写测试固化结论。

                        目标：
                        - 你能解释：不指定 key 时，Spring Cache 用什么做 key（单参 vs 多参）。
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：增加一个自定义 key（SpEL），并证明它会改变缓存命中行为")
    void exercise_customKey() {
        assertThat(true)
                .as("""
                        练习：增加一个自定义 key（SpEL），并证明它会改变缓存命中行为。

                        示例：
                        - key = \"#name.toLowerCase()\"
                        - 让 \"Alice\" 与 \"alice\" 命中同一缓存
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：为 @CacheEvict 增加 allEntries=true 的例子，并写测试证明它会清空整个 cache")
    void exercise_evictAllEntries() {
        assertThat(true)
                .as("""
                        练习：为 @CacheEvict 增加 allEntries=true 的例子，并写测试证明它会清空整个 cache。
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：增加一个 cache stampede 场景（更多并发线程），并把观察写进 docs/90")
    void exercise_stampedeNotes() {
        assertThat(true)
                .as("""
                        练习：增加一个 cache stampede 场景（更多并发线程），并把观察写进 docs/90。

                        提示：
                        - 对比 sync=true 与 sync=false 的差异。
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：为缓存增加一个“负缓存（negative cache）”例子：缓存不存在/空结果，并写测试")
    void exercise_negativeCache() {
        assertThat(true)
                .as("""
                        练习：为缓存增加一个“负缓存（negative cache）”例子：缓存不存在/空结果，并写测试。
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：增加一个“缓存一致性”示例：更新数据库后如何做 evict/put？把策略写进 docs/90")
    void exercise_consistency() {
        assertThat(true)
                .as("""
                        练习：增加一个“缓存一致性”示例：更新数据库后如何做 evict/put？把策略写进 docs/90。
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：把 TTL 改成 expireAfterAccess，并用 ManualTicker 写一个可断言实验")
    void exercise_expireAfterAccess() {
        assertThat(true)
                .as("""
                        练习：把 TTL 改成 expireAfterAccess，并用 ManualTicker 写一个可断言实验。
                        """)
                .isFalse();
    }

    @Test
    @Disabled("练习：增加一个 CacheManager 多 cache 配置（不同 TTL），并写测试验证每个 cache 的行为差异")
    void exercise_multipleCachesDifferentTtl() {
        assertThat(true)
                .as("""
                        练习：增加一个 CacheManager 多 cache 配置（不同 TTL），并写测试验证每个 cache 的行为差异。
                        """)
                .isFalse();
    }
}

