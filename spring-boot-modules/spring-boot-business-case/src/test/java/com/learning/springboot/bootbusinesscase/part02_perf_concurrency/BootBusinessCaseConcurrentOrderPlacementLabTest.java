package com.learning.springboot.bootbusinesscase.part02_perf_concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.bootbusinesscase.app.OrderService;
import com.learning.springboot.bootbusinesscase.app.PlaceOrderCommand;
import com.learning.springboot.bootbusinesscase.domain.PurchaseOrder;
import com.learning.springboot.bootbusinesscase.domain.PurchaseOrderRepository;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 并发/性能实验：并发下调用事务服务方法，验证“每次调用都是独立事务 + 独立写入结果”。
 *
 * <p>断言策略：用“数据库记录数 / 主键唯一性”等可观测事实，不用耗时阈值。
 */
@SpringBootTest
class BootBusinessCaseConcurrentOrderPlacementLabTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PurchaseOrderRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void concurrentPlaceOrder_createsDistinctOrdersAndCommitsAll() throws Exception {
        int tasks = 10;
        ExecutorService pool = Executors.newFixedThreadPool(5, namedThreadFactory("businesscase-perf-"));
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch doneGate = new CountDownLatch(tasks);

        ConcurrentLinkedQueue<Long> ids = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

        try {
            for (int i = 0; i < tasks; i++) {
                int idx = i;
                pool.submit(() -> {
                    try {
                        startGate.await(1, TimeUnit.SECONDS);
                        PurchaseOrder saved = orderService.placeOrder(new PlaceOrderCommand("C" + idx, "SKU-CC", 1));
                        ids.add(saved.getId());
                    } catch (Throwable ex) {
                        errors.add(ex);
                    } finally {
                        doneGate.countDown();
                    }
                });
            }

            startGate.countDown();
            assertThat(doneGate.await(10, TimeUnit.SECONDS)).isTrue();

            assertThat(errors).isEmpty();
            assertThat(ids).hasSize(tasks);
            assertThat(ids).doesNotHaveDuplicates();
            assertThat(repository.count()).isEqualTo(tasks);
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

