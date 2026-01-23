package com.learning.springboot.bootdatajpa.part01_data_jpa;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LibraryAuthorRepository extends JpaRepository<LibraryAuthor, Long> {

    @EntityGraph(attributePaths = "books")
    @Query("select a from LibraryAuthor a")
    List<LibraryAuthor> findAllWithBooks();
}
