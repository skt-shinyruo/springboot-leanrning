package com.learning.springboot.springcoretx.part02_template_and_debugging;

import com.learning.springboot.springcoretx.part01_transaction_basics.AccountRepository;
import com.learning.springboot.springcoretx.part01_transaction_basics.AccountService;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class TxDemoRunner implements ApplicationRunner {

    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final TxIntrospectionService txIntrospectionService;

    public TxDemoRunner(
            AccountRepository accountRepository,
            AccountService accountService,
            TxIntrospectionService txIntrospectionService
    ) {
        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.txIntrospectionService = txIntrospectionService;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("== spring-core-tx ==");

        System.out.println("TX:transactionActive.outside=" + TransactionSynchronizationManager.isActualTransactionActive());
        System.out.println("TX:transactionActive.insideTransactionalMethod=" + txIntrospectionService.isTransactionActive());

        accountRepository.deleteAll();

        try {
            accountService.createTwoAccountsThenFail();
        } catch (IllegalStateException ex) {
            System.out.println("TX:rollback.expectedException=" + ex.getMessage());
        }
        System.out.println("TX:afterRollback.accountCount=" + accountRepository.count());

        accountService.createTwoAccounts();
        System.out.println("TX:afterCommit.accountCount=" + accountRepository.count());
    }
}
