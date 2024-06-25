package com.gusdev.library_app.repositories;

import com.gusdev.library_app.entities.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class BookRepositoryTests {

    @Autowired
    private BookRepository bookRepository;

    private Book book1;
    private Book book2;

    @BeforeEach
    public void setUpTest() {
        book1 = Book.builder()
                .id(1L)
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

        bookRepository.save(book1);
        bookRepository.save(book2);
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
        Book savedBook = bookRepository.save(book3);

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
        bookRepository.save(newBook);
        assertThat(bookRepository.existsByTitle("New Book")).isTrue();

        // Read
        Book foundBook = bookRepository.findById(newBook.getId()).orElse(null);
        assertThat(foundBook).isNotNull();
        assertThat(foundBook.getTitle()).isEqualTo("New Book");

        // Update
        foundBook.setTitle("Updated Book");
        bookRepository.save(foundBook);
        Book updatedBook = bookRepository.findById(foundBook.getId()).orElse(null);
        assertThat(updatedBook.getTitle()).isEqualTo("Updated Book");

        // Delete
        bookRepository.delete(updatedBook);
        assertThat(bookRepository.existsByTitle("Updated Book")).isFalse();
    }
}