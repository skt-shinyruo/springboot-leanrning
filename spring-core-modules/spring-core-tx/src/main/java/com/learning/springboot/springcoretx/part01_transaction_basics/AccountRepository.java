package com.learning.springboot.springcoretx.part01_transaction_basics;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepository {

    private final JdbcTemplate jdbcTemplate;

    public AccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void deleteAll() {
        jdbcTemplate.update("delete from accounts");
    }

    public void insert(String owner, int balance) {
        jdbcTemplate.update("insert into accounts(owner, balance) values (?, ?)", owner, balance);
    }

    public int count() {
        Integer count = jdbcTemplate.queryForObject("select count(*) from accounts", Integer.class);
        return count == null ? 0 : count;
    }
}

