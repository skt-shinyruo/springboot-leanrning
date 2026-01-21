package com.learning.springboot.bootdatajpa.part01_data_jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryBookRepository extends JpaRepository<LibraryBook, Long> {
}
