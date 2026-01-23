package com.learning.springboot.bootdatajpa.part01_data_jpa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "library_authors")
public class LibraryAuthor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<LibraryBook> books = new ArrayList<>();

    protected LibraryAuthor() {
    }

    public LibraryAuthor(String name) {
        this.name = name;
    }

    public LibraryBook addBook(String title) {
        LibraryBook book = new LibraryBook(title, this);
        this.books.add(book);
        return book;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public List<LibraryBook> getBooks() {
        return books;
    }
}
