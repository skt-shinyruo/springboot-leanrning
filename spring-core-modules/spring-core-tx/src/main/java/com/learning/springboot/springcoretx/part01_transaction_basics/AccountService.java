package com.learning.springboot.springcoretx.part01_transaction_basics;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void createTwoAccounts() {
        accountRepository.insert("Alice", 100);
        accountRepository.insert("Bob", 200);
    }

    @Transactional
    public void createTwoAccountsThenFail() {
        accountRepository.insert("Carol", 300);
        accountRepository.insert("Dave", 400);
        throw new IllegalStateException("boom");
    }
}

