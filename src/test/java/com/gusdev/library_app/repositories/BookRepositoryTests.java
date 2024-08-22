package com.gusdev.library_app.repositories;

import com.gusdev.library_app.entities.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BookRepositoryTests {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Book book1;
    private Book book2;

    @BeforeEach
    public void setUpTest() {
        book1 = Book.builder()
                .title("Book One")
                .author("Author One")
                .isbn("1234")
                .isAvailable(true)
                .createdDate(LocalDateTime.now())
                .build();

        book2 = Book.builder()
                .title("Book Two")
                .author("Author Two")
                .isbn("5678")
                .isAvailable(false)
                .createdDate(LocalDateTime.now())
                .build();

        // Persistir las entidades
        entityManager.persist(book1);
        entityManager.persist(book2);
        entityManager.flush();
    }

    @Test
    public void saveBookTest() {
        // given
        Book book3 = Book.builder()
                .title("Book Three")
                .author("Author Three")
                .isbn("91011")
                .isAvailable(true)
                .createdDate(LocalDateTime.now())
                .build();

        // when
        Book savedBook = entityManager.persistAndFlush(book3);

        // then
        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getId()).isGreaterThan(0);
    }

    @Test
    public void existsByTitleTest() {
        // when
        boolean exists = bookRepository.existsByTitle("Book One");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    public void findByTitleIgnoreCaseTest() {
        // when
        Iterable<Book> books = bookRepository.findByTitleIgnoreCase("book one");

        // then
        assertThat(books).hasSize(1);
        assertThat(books.iterator().next().getTitle()).isEqualTo("Book One");
    }

    @Test
    public void findByTitleContainingIgnoreCaseTest() {
        // when
        Iterable<Book> books = bookRepository.findByTitleContainingIgnoreCase("bOok One");

        // then
        assertThat(books).hasSize(1);
        assertThat(books.iterator().next().getTitle()).isEqualTo("Book One");
    }

    @Test
    public void findByAuthorIgnoreCaseTest() {
        // when
        Iterable<Book> books = bookRepository.findByAuthorIgnoreCase("author two");

        // then
        assertThat(books).hasSize(1);
        assertThat(books.iterator().next().getAuthor()).isEqualTo("Author Two");
    }

    @Test
    public void testCreateReadUpdateDelete() {
        // Create
        Book newBook = Book.builder()
                .title("New Book")
                .author("New Author")
                .isbn("111213")
                .isAvailable(true)
                .createdDate(LocalDateTime.now())
                .build();
        entityManager.persistAndFlush(newBook);
        assertThat(bookRepository.existsByTitle("New Book")).isTrue();

        // Read
        Book foundBook = entityManager.find(Book.class, newBook.getId());
        assertThat(foundBook).isNotNull();
        assertThat(foundBook.getTitle()).isEqualTo("New Book");

        // Update
        foundBook.setTitle("Updated Book");
        entityManager.merge(foundBook);
        entityManager.flush();

        Book updatedBook = entityManager.find(Book.class, foundBook.getId());
        assertThat(updatedBook).isNotNull();
        assertThat(updatedBook.getTitle()).isEqualTo("Updated Book");

        // Delete
        entityManager.remove(updatedBook);
        entityManager.flush();
        assertThat(bookRepository.existsByTitle("Updated Book")).isFalse();
    }
}
