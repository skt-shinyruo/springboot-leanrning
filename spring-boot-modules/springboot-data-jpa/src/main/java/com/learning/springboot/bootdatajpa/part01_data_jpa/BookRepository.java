package com.learning.springboot.bootdatajpa.part01_data_jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByTitle(String title);

    List<Book> findByAuthor(String author);

    @Query("select b from Book b where b.author = :author")
    List<Book> findByAuthorJpql(@Param("author") String author);
}
