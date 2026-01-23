package com.learning.springboot.bootdatajpa.part01_data_jpa;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@DataJpaTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
class BootDataJpaLabTest {

    @Autowired
    private BookRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private LibraryAuthorRepository libraryAuthorRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void savesAndFindsByTitle() {
        Book saved = repository.save(new Book("Domain-Driven Design", "Eric Evans"));

        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findByTitle("Domain-Driven Design"))
                .isPresent()
                .get()
                .extracting(Book::getAuthor)
                .isEqualTo("Eric Evans");
    }

    @Test
    void findByTitleReturnsEmptyWhenMissing() {
        assertThat(repository.findByTitle("missing")).isEmpty();
    }

    @Test
    void saveAssignsId() {
        Book saved = repository.save(new Book("Spring Data JPA", "Learning Team"));
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void entityIsManagedAfterSaveInSamePersistenceContext() {
        Book saved = repository.save(new Book("Managed", "Author"));
        assertThat(entityManager.contains(saved)).isTrue();
    }

    @Test
    void entityManagerClearDetachesEntities() {
        Book saved = repository.save(new Book("Detach", "Author"));
        assertThat(entityManager.contains(saved)).isTrue();

        entityManager.clear();

        assertThat(entityManager.contains(saved)).isFalse();
    }

    @Test
    void dirtyCheckingPersistsChangesOnFlush() {
        Book saved = repository.save(new Book("Original", "A"));
        saved.changeAuthor("B");

        entityManager.flush();
        entityManager.clear();

        Book reloaded = repository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getAuthor()).isEqualTo("B");
    }

    @Test
    void flushMakesRowsVisibleToJdbcTemplateWithinSameTransaction() {
        repository.save(new Book("Flush", "A"));
        entityManager.flush();

        Integer count = jdbcTemplate.queryForObject("select count(*) from books", Integer.class);
        assertThat(count).isNotNull();
        assertThat(count).isGreaterThanOrEqualTo(1);
    }

    @Test
    void deleteRemovesRowAfterFlush() {
        Book saved = repository.save(new Book("ToDelete", "A"));
        entityManager.flush();

        repository.deleteById(saved.getId());
        entityManager.flush();

        assertThat(repository.findById(saved.getId())).isEmpty();
    }

    @Test
    void repositoryCountReflectsInserts() {
        repository.save(new Book("A", "A"));
        repository.save(new Book("B", "B"));
        assertThat(repository.count()).isEqualTo(2);
    }

    @Test
    void dataJpaTestRunsInsideATransaction() {
        assertThat(TransactionSynchronizationManager.isActualTransactionActive()).isTrue();
    }

    @Test
    void getReferenceByIdReturnsALazyProxy_andInitializesOnPropertyAccess() {
        LibraryAuthor saved = libraryAuthorRepository.save(new LibraryAuthor("Author-1"));
        entityManager.flush();
        entityManager.clear();

        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();

        LibraryAuthor ref = libraryAuthorRepository.getReferenceById(saved.getId());
        assertThat(Hibernate.isInitialized(ref)).isFalse();
        assertThat(statistics.getPrepareStatementCount()).isEqualTo(0);

        assertThat(ref.getId()).isEqualTo(saved.getId());
        assertThat(Hibernate.isInitialized(ref)).isFalse();
        assertThat(statistics.getPrepareStatementCount()).isEqualTo(0);

        assertThat(ref.getName()).isEqualTo("Author-1");
        assertThat(Hibernate.isInitialized(ref)).isTrue();
        assertThat(statistics.getPrepareStatementCount()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void nPlusOneHappensWhenAccessingLazyCollections() {
        LibraryAuthor a1 = new LibraryAuthor("A1");
        a1.addBook("A1-B1");
        a1.addBook("A1-B2");

        LibraryAuthor a2 = new LibraryAuthor("A2");
        a2.addBook("A2-B1");
        a2.addBook("A2-B2");

        LibraryAuthor a3 = new LibraryAuthor("A3");
        a3.addBook("A3-B1");
        a3.addBook("A3-B2");

        libraryAuthorRepository.saveAll(List.of(a1, a2, a3));
        entityManager.flush();
        entityManager.clear();

        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();

        List<LibraryAuthor> authors = libraryAuthorRepository.findAll();
        assertThat(authors).hasSize(3);

        long afterSelectingAuthors = statistics.getPrepareStatementCount();
        assertThat(afterSelectingAuthors).isGreaterThanOrEqualTo(1);

        authors.forEach(a -> assertThat(a.getBooks()).hasSize(2));

        long afterAccessingBooks = statistics.getPrepareStatementCount();
        assertThat(afterAccessingBooks)
                .as("N+1 should trigger extra selects for each author's books")
                .isGreaterThanOrEqualTo(afterSelectingAuthors + authors.size());
    }

    @Test
    void entityGraphCanAvoidNPlusOne_whenFetchingCollections() {
        LibraryAuthor a1 = new LibraryAuthor("G1");
        a1.addBook("G1-B1");
        a1.addBook("G1-B2");

        LibraryAuthor a2 = new LibraryAuthor("G2");
        a2.addBook("G2-B1");
        a2.addBook("G2-B2");

        LibraryAuthor a3 = new LibraryAuthor("G3");
        a3.addBook("G3-B1");
        a3.addBook("G3-B2");

        libraryAuthorRepository.saveAll(List.of(a1, a2, a3));
        entityManager.flush();
        entityManager.clear();

        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();

        List<LibraryAuthor> authors = libraryAuthorRepository.findAllWithBooks();
        assertThat(authors).hasSize(3);

        long afterQuery = statistics.getPrepareStatementCount();
        assertThat(afterQuery).isGreaterThanOrEqualTo(1);

        authors.forEach(a -> assertThat(a.getBooks()).hasSize(2));

        long afterAccessingBooks = statistics.getPrepareStatementCount();
        assertThat(afterAccessingBooks)
                .as("books should already be loaded via EntityGraph, so no N additional selects")
                .isEqualTo(afterQuery);
    }
}
