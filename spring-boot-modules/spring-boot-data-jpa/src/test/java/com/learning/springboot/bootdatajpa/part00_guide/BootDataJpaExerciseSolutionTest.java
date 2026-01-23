package com.learning.springboot.bootdatajpa.part00_guide;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.springboot.bootdatajpa.part01_data_jpa.Book;
import com.learning.springboot.bootdatajpa.part01_data_jpa.BookRepository;
import com.learning.springboot.bootdatajpa.part01_data_jpa.LibraryAuthor;
import com.learning.springboot.bootdatajpa.part01_data_jpa.LibraryAuthorRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.transaction.TestTransaction;

/**
 * 参考实现：对齐 BootDataJpaExerciseTest 的练习题，提供可运行通过的 Solution（默认参与回归）。
 */
@DataJpaTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
class BootDataJpaExerciseSolutionTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LibraryAuthorRepository libraryAuthorRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    void solution_addQueryMethod_findByAuthor() {
        bookRepository.saveAll(List.of(
                new Book("T1", "Author-A"),
                new Book("T2", "Author-B"),
                new Book("T3", "Author-A")
        ));

        List<Book> books = bookRepository.findByAuthor("Author-A");
        assertThat(books).hasSize(2);
        assertThat(books).extracting(Book::getTitle).containsExactlyInAnyOrder("T1", "T3");
    }

    @Test
    void solution_customQuery_findByAuthorJpql() {
        bookRepository.saveAll(List.of(
                new Book("Q1", "Query-A"),
                new Book("Q2", "Query-B"),
                new Book("Q3", "Query-A")
        ));

        List<Book> books = bookRepository.findByAuthorJpql("Query-A");
        assertThat(books).hasSize(2);
        assertThat(books).extracting(Book::getTitle).containsExactlyInAnyOrder("Q1", "Q3");
    }

    @Test
    void solution_relationshipsAndFetching_nPlusOneEvidence() {
        LibraryAuthor a1 = new LibraryAuthor("A1");
        a1.addBook("A1-B1");
        a1.addBook("A1-B2");

        LibraryAuthor a2 = new LibraryAuthor("A2");
        a2.addBook("A2-B1");
        a2.addBook("A2-B2");

        libraryAuthorRepository.saveAll(List.of(a1, a2));
        entityManager.flush();
        entityManager.clear();

        Statistics statistics = stats();
        statistics.clear();

        List<LibraryAuthor> authors = libraryAuthorRepository.findAll();
        assertThat(authors).hasSize(2);
        long afterSelectingAuthors = statistics.getPrepareStatementCount();

        authors.forEach(a -> assertThat(a.getBooks()).hasSize(2));

        long afterAccessingBooks = statistics.getPrepareStatementCount();
        assertThat(afterAccessingBooks)
                .as("访问 LAZY 集合通常会触发 N+1：每个 author 额外 select books")
                .isGreaterThanOrEqualTo(afterSelectingAuthors + authors.size());
    }

    @Test
    void solution_rollbackBehavior_defaultRollbackVsCommit_withTestTransaction() {
        assertThat(bookRepository.count()).isEqualTo(0);

        bookRepository.save(new Book("RollbackMe", "TX"));
        entityManager.flush();

        TestTransaction.flagForRollback();
        TestTransaction.end();

        TestTransaction.start();
        assertThat(bookRepository.count()).isEqualTo(0);

        bookRepository.save(new Book("CommitMe", "TX"));
        entityManager.flush();

        TestTransaction.flagForCommit();
        TestTransaction.end();

        TestTransaction.start();
        assertThat(bookRepository.count()).isEqualTo(1);

        bookRepository.deleteAll();
        entityManager.flush();

        TestTransaction.flagForCommit();
        TestTransaction.end();

        TestTransaction.start();
        assertThat(bookRepository.count()).isEqualTo(0);
    }

    @Test
    void solution_getReferenceById_isLazyUntilPropertyAccess() {
        LibraryAuthor saved = libraryAuthorRepository.save(new LibraryAuthor("Lazy-Author"));
        entityManager.flush();
        entityManager.clear();

        Statistics statistics = stats();
        statistics.clear();

        LibraryAuthor ref = libraryAuthorRepository.getReferenceById(saved.getId());
        assertThat(Hibernate.isInitialized(ref)).isFalse();
        assertThat(statistics.getPrepareStatementCount()).isEqualTo(0);

        assertThat(ref.getId()).isEqualTo(saved.getId());
        assertThat(Hibernate.isInitialized(ref)).isFalse();

        assertThat(ref.getName()).isEqualTo("Lazy-Author");
        assertThat(Hibernate.isInitialized(ref)).isTrue();
        assertThat(statistics.getPrepareStatementCount()).isGreaterThanOrEqualTo(1);
    }

    private Statistics stats() {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        return statistics;
    }
}

