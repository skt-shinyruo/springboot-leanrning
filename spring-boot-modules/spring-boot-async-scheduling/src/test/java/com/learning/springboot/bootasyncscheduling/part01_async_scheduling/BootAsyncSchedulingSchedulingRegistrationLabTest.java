package com.learning.springboot.bootasyncscheduling.part01_async_scheduling;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.FixedDelayTask;
import org.springframework.scheduling.config.FixedRateTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskHolder;

class BootAsyncSchedulingSchedulingRegistrationLabTest {

    @Test
    void scheduledTasksAreRegisteredAsDifferentTaskTypes() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withUserConfiguration(SchedulingRegistrationConfig.class);

        runner.run(context -> {
            ScheduledTaskHolder holder = context.getBean(ScheduledTaskHolder.class);
            Set<ScheduledTask> tasks = holder.getScheduledTasks();

            assertThat(tasks).isNotEmpty();
            assertThat(tasks).anySatisfy(task -> assertThat(task.getTask()).isInstanceOf(FixedRateTask.class));
            assertThat(tasks).anySatisfy(task -> assertThat(task.getTask()).isInstanceOf(FixedDelayTask.class));
            assertThat(tasks).anySatisfy(task -> assertThat(task.getTask()).isInstanceOf(CronTask.class));
        });
    }

    @EnableScheduling
    @Configuration
    static class SchedulingRegistrationConfig {

        @Bean
        ThreadPoolTaskScheduler taskScheduler() {
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setPoolSize(1);
            scheduler.setThreadNamePrefix("sched-");
            scheduler.initialize();
            return scheduler;
        }

        @Bean
        SchedulingProbe schedulingProbe() {
            return new SchedulingProbe();
        }
    }

    static class SchedulingProbe {

        @Scheduled(fixedRate = 1000)
        void fixedRateTask() {
        }

        @Scheduled(fixedDelay = 1000)
        void fixedDelayTask() {
        }

        @Scheduled(cron = "*/5 * * * * *")
        void cronTask() {
        }
    }
}

