package com.learning.springboot.springcorevalidation.part02_perf_concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.springcorevalidation.part01_validation_core.CreateUserCommand;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 并发/性能实验：Validator 是线程安全的 —— 并发校验同一输入应产生一致的 violations 集合（不抛异常）。
 */
@SpringBootTest
class SpringCoreValidationValidatorConcurrencyLabTest {

    @Autowired
    private Validator validator;

    @Test
    void validator_isThreadSafe_underConcurrentValidations() throws Exception {
        CreateUserCommand invalid = new CreateUserCommand("", "not-an-email", -1);

        int tasks = 24;
        ExecutorService pool = Executors.newFixedThreadPool(8, namedThreadFactory("validation-perf-"));
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch doneGate = new CountDownLatch(tasks);
        ConcurrentLinkedQueue<String> fingerprints = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

        try {
            for (int i = 0; i < tasks; i++) {
                pool.submit(() -> {
                    try {
                        startGate.await(1, TimeUnit.SECONDS);
                        Set<ConstraintViolation<CreateUserCommand>> violations = validator.validate(invalid);
                        String fp = violations.stream()
                                .map(v -> v.getPropertyPath().toString())
                                .sorted()
                                .toList()
                                .toString();
                        fingerprints.add(fp);
                    } catch (Throwable ex) {
                        errors.add(ex);
                    } finally {
                        doneGate.countDown();
                    }
                });
            }

            startGate.countDown();
            assertThat(doneGate.await(5, TimeUnit.SECONDS)).isTrue();

            assertThat(errors).isEmpty();
            assertThat(fingerprints).hasSize(tasks);
            assertThat(Set.copyOf(fingerprints)).hasSize(1);
            assertThat(fingerprints.iterator().next()).contains("age").contains("email").contains("username");
        } finally {
            pool.shutdownNow();
        }
    }

    private static ThreadFactory namedThreadFactory(String prefix) {
        AtomicInteger seq = new AtomicInteger();
        return r -> {
            Thread t = new Thread(r);
            t.setName(prefix + seq.incrementAndGet());
            t.setDaemon(true);
            return t;
        };
    }
}
