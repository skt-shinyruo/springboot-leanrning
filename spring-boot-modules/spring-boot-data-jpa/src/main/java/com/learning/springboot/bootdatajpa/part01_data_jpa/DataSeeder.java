package com.learning.springboot.bootdatajpa.part01_data_jpa;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements ApplicationRunner {

    private final BookRepository repository;

    public DataSeeder(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (repository.count() > 0) {
            return;
        }

        Book saved = repository.save(new Book("Spring Boot 入门", "Learning Team"));
        repository.save(new Book("Spring Data JPA", "Learning Team"));

        System.out.println("== springboot-data-jpa ==");
        System.out.println("savedBookId=" + saved.getId());
        System.out.println("totalBooks=" + repository.count());
        repository.findByTitle("Spring Boot 入门")
                .ifPresent(book -> System.out.println("foundByTitle=" + book.getTitle() + "," + book.getAuthor()));
    }
}
