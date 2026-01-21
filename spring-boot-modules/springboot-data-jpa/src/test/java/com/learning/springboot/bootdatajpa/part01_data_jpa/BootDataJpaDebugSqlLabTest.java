package com.learning.springboot.bootdatajpa.part01_data_jpa;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest(properties = {
        "spring.jpa.show-sql=true",
        "spring.jpa.properties.hibernate.format_sql=true"
})
class BootDataJpaDebugSqlLabTest {

    @Autowired
    private BookRepository repository;

    @Test
    void showSqlHelpsExplainPersistenceBehavior_whenRunningTests() {
        repository.save(new Book("Debug SQL", "Learning Team"));

        assertThat(repository.findByTitle("Debug SQL")).isPresent();
    }
}

