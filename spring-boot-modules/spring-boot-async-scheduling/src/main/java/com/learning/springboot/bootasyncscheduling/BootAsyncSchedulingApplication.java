package com.learning.springboot.bootasyncscheduling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import com.learning.springboot.bootasyncscheduling.part01_async_scheduling.ThreadLocalTransactionManager;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
public class BootAsyncSchedulingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootAsyncSchedulingApplication.class, args);
    }

    @Bean
    PlatformTransactionManager transactionManager() {
        return new ThreadLocalTransactionManager();
    }

    @Bean
    TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }
}
