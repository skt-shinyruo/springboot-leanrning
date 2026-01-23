package com.learning.springboot.bootdatajpa.part01_data_jpa;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
class BootDataJpaMergeAndDetachLabTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private LibraryAuthorRepository authorRepository;

    @Test
    void detached_changesWithoutMerge_shouldNotBePersisted() {
        LibraryAuthor author = new LibraryAuthor("Alice");
        LibraryAuthor saved = authorRepository.save(author);

        entityManager.flush();
        entityManager.clear();

        LibraryAuthor managed = authorRepository.findById(saved.getId()).orElseThrow();
        assertThat(entityManager.contains(managed)).isTrue();

        entityManager.detach(managed);
        assertThat(entityManager.contains(managed)).isFalse();

        managed.changeName("Alice-2-not-persisted");

        entityManager.flush();
        entityManager.clear();

        LibraryAuthor reloaded = authorRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getName()).isEqualTo("Alice");
    }

    @Test
    void merge_shouldPersistDetachedChangesIntoManagedCopy() {
        LibraryAuthor author = new LibraryAuthor("Bob");
        LibraryAuthor saved = authorRepository.save(author);

        entityManager.flush();
        entityManager.clear();

        LibraryAuthor detached = authorRepository.findById(saved.getId()).orElseThrow();
        assertThat(entityManager.contains(detached)).isTrue();

        entityManager.detach(detached);
        assertThat(entityManager.contains(detached)).isFalse();

        detached.changeName("Bob-2");

        LibraryAuthor merged = entityManager.merge(detached);
        assertThat(merged).isNotSameAs(detached);
        assertThat(entityManager.contains(merged)).isTrue();
        assertThat(entityManager.contains(detached)).isFalse();

        entityManager.flush();
        entityManager.clear();

        LibraryAuthor reloaded = authorRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getName()).isEqualTo("Bob-2");
    }
}
