package com.learning.springboot.bootasyncscheduling.part01_async_scheduling;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class AsyncSchedulingDemoRunner implements ApplicationRunner {

    private final ConfigurableApplicationContext applicationContext;
    private final Environment environment;
    private final ExecutorSelectionAsyncService executorSelectionAsyncService;
    private final TxBoundaryAsyncService txBoundaryAsyncService;
    private final TransactionTemplate transactionTemplate;
    private final SecurityContextAsyncService securityContextAsyncService;
    private final AsyncTaskExecutor asyncTaskExecutor;

    public AsyncSchedulingDemoRunner(
            ConfigurableApplicationContext applicationContext,
            Environment environment,
            ExecutorSelectionAsyncService executorSelectionAsyncService,
            TxBoundaryAsyncService txBoundaryAsyncService,
            TransactionTemplate transactionTemplate,
            SecurityContextAsyncService securityContextAsyncService,
            @Qualifier("applicationTaskExecutor") AsyncTaskExecutor asyncTaskExecutor
    ) {
        this.applicationContext = applicationContext;
        this.environment = environment;
        this.executorSelectionAsyncService = executorSelectionAsyncService;
        this.txBoundaryAsyncService = txBoundaryAsyncService;
        this.transactionTemplate = transactionTemplate;
        this.securityContextAsyncService = securityContextAsyncService;
        this.asyncTaskExecutor = asyncTaskExecutor;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("== spring-boot-async-scheduling ==");
        System.out.println("(DemoRunner 只用于观察边界，跑完会自动退出)");

        printSection("0) 当前环境");
        System.out.println("- active profiles: " + formatActiveProfiles(environment.getActiveProfiles()));
        System.out.println("- main thread: " + Thread.currentThread().getName());
        System.out.println("- TaskExecutors: " + formatBeanNames(applicationContext.getBeansOfType(TaskExecutor.class)));
        System.out.println("- TaskSchedulers: " + formatBeanNames(applicationContext.getBeansOfType(TaskScheduler.class)));
        System.out.println("- spring.task.execution.thread-name-prefix: "
                + formatNullable(environment.getProperty("spring.task.execution.thread-name-prefix")));
        System.out.println("- spring.task.scheduling.thread-name-prefix: "
                + formatNullable(environment.getProperty("spring.task.scheduling.thread-name-prefix")));

        printSection("1) @Async：默认 executor 跑在哪个线程");
        String asyncThreadName = executorSelectionAsyncService.defaultExecutorThreadName().get(1, TimeUnit.SECONDS);
        System.out.println("- async thread: " + asyncThreadName);

        printSection("2) @Async × @Transactional：事务到底在哪个线程");
        TxBoundaryAsyncService.TxSnapshot txInCaller = transactionTemplate.execute(status -> {
            boolean callerTxActive = TransactionSynchronizationManager.isActualTransactionActive();
            String callerThreadName = Thread.currentThread().getName();

            try {
                TxBoundaryAsyncService.TxSnapshot asyncSnapshot = txBoundaryAsyncService.observeTxActive().get(1, TimeUnit.SECONDS);
                System.out.println("- caller thread: " + callerThreadName);
                System.out.println("- caller tx active: " + callerTxActive);
                System.out.println("- async thread: " + asyncSnapshot.threadName());
                System.out.println("- async tx active: " + asyncSnapshot.txActive());
                return asyncSnapshot;
            } catch (Exception ex) {
                throw new IllegalStateException("tx boundary demo failed", ex);
            }
        });
        System.out.println("- tx boundary demo done: " + (txInCaller != null));

        TxBoundaryAsyncService.TxSnapshot txInAsyncTransactional = txBoundaryAsyncService.observeTxActiveInAsyncTransactionalMethod()
                .get(1, TimeUnit.SECONDS);
        System.out.println("- async+transactional thread: " + txInAsyncTransactional.threadName());
        System.out.println("- async+transactional tx active: " + txInAsyncTransactional.txActive());

        printSection("3) SecurityContext：默认丢失 vs Delegating* 传播");
        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("alice", "N/A"));
        try {
            SecurityContextAsyncService.AuthSnapshot defaultAsyncAuth = securityContextAsyncService.observeAuthentication()
                    .get(1, TimeUnit.SECONDS);
            System.out.println("- default async thread: " + defaultAsyncAuth.threadName());
            System.out.println("- default async authentication: " + defaultAsyncAuth.authenticationName());

            DelegatingSecurityContextAsyncTaskExecutor delegating = new DelegatingSecurityContextAsyncTaskExecutor(asyncTaskExecutor);
            Future<String> propagated = delegating.submit(AsyncSchedulingDemoRunner::currentAuthenticationNameOrNull);
            System.out.println("- delegating authentication: " + propagated.get(1, TimeUnit.SECONDS));

            SecurityContextHolder.clearContext();
            Future<String> afterClear = delegating.submit(AsyncSchedulingDemoRunner::currentAuthenticationNameOrNull);
            System.out.println("- delegating after clear (should be null): " + afterClear.get(1, TimeUnit.SECONDS));
        } finally {
            SecurityContextHolder.clearContext();
        }

        System.out.println();
        System.out.println("done.");
        applicationContext.close();
    }

    private static void printSection(String title) {
        System.out.println();
        System.out.println("---- " + title + " ----");
    }

    private static String currentAuthenticationNameOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? null : authentication.getName();
    }

    private static String formatNullable(String value) {
        if (value == null || value.isBlank()) {
            return "(not set)";
        }
        return value;
    }

    private static String formatActiveProfiles(String[] profiles) {
        if (profiles == null || profiles.length == 0) {
            return "(none)";
        }
        return Arrays.stream(profiles)
                .sorted()
                .collect(Collectors.joining(","));
    }

    private static String formatBeanNames(Map<String, ?> beans) {
        if (beans == null || beans.isEmpty()) {
            return "(none)";
        }

        return beans.keySet()
                .stream()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.joining(","));
    }
}
