package com.learning.springboot.bootasyncscheduling.part01_async_scheduling;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

class BootAsyncSchedulingSchedulingLabTest {

    @Test
    void schedulingRequiresEnableScheduling() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(ScheduledProbeConfigWithoutEnable.class);

        runner.run(context -> {
            ScheduledProbe probe = context.getBean(ScheduledProbe.class);
            assertThat(probe.await(200, TimeUnit.MILLISECONDS)).isFalse();
        });
    }

    @Test
    void schedulingTriggersTaskWhenEnableSchedulingPresent() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(ScheduledProbeConfig.class);

        runner.run(context -> {
            ScheduledProbe probe = context.getBean(ScheduledProbe.class);
            assertThat(probe.await(1, TimeUnit.SECONDS)).isTrue();
            assertThat(probe.invocationCount()).isGreaterThanOrEqualTo(1);
        });
    }

    @Configuration
    static class ScheduledProbeConfigWithoutEnable {

        @Bean
        ScheduledProbe scheduledProbe() {
            return new ScheduledProbe();
        }
    }

    @EnableScheduling
    @Configuration
    static class ScheduledProbeConfig {

        @Bean
        ScheduledProbe scheduledProbe() {
            return new ScheduledProbe();
        }
    }

    static class ScheduledProbe {

        private final CountDownLatch latch = new CountDownLatch(1);
        private volatile int count;

        @Scheduled(fixedDelay = 10)
        void tick() {
            count += 1;
            latch.countDown();
        }

        boolean await(long timeout, TimeUnit unit) throws InterruptedException {
            return latch.await(timeout, unit);
        }

        int invocationCount() {
            return count;
        }
    }
}

