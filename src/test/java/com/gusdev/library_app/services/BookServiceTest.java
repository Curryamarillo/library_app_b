package com.gusdev.library_app.services;

import com.gusdev.library_app.dtoRequest.BookRequestDTO;
import com.gusdev.library_app.entities.Book;
import com.gusdev.library_app.entities.Loan;
import com.gusdev.library_app.exceptions.BookAlreadyExistsException;
import com.gusdev.library_app.exceptions.BookNotFoundException;
import com.gusdev.library_app.repositories.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book1;
    private Book book2;
    private BookRequestDTO bookRequestDTO1;
    private BookRequestDTO bookRequestDTO2;
    @BeforeEach
    void setUp() {
        book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book One");
        book1.setAuthor("Author One");
        book1.setIsAvailable(true);
        book1.setIsbn("12345");
        book1.setLoans(Set.of(new Loan()));

        book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book Two");
        book2.setAuthor("Author Two");
        book2.setIsAvailable(false);
        book2.setIsbn("67890");
        book2.setLoans(Set.of(new Loan()));

        bookRequestDTO1 = new BookRequestDTO(1L, "Book One", "Author One", "12345", true);
        bookRequestDTO2 = new BookRequestDTO(2L, "Book Two", "Author Two", "67890", false);
    }
    @Test
    void createBookTest() {
        // given
        given(bookRepository.existsByTitle(book1.getTitle())).willReturn(false);
        given(bookRepository.save(book1)).willReturn(book1);

        // when
        Book createdBook = bookService.create(book1);

        // then
        assertNotNull(createdBook);
        assertEquals(book1.getTitle(), createdBook.getTitle());
        verify(bookRepository).existsByTitle(book1.getTitle());
        verify(bookRepository).save(book1);
    }

    @Test
    void createBookAlreadyExistsTest() {
        // given
        given(bookRepository.existsByTitle(book1.getTitle())).willReturn(true);

        // when & then
        assertThrows(BookAlreadyExistsException.class, () -> bookService.create(book1));
        verify(bookRepository).existsByTitle(book1.getTitle());
        verify(bookRepository, never()).save(book1);
    }

    @Test
    void findAllBooksTest() {
        // given
        List<Book> books = Collections.singletonList(book1);
        given(bookRepository.findAll()).willReturn(books);

        // when
        List<Book> foundBooks = bookService.findAll();

        // then
        assertNotNull(foundBooks);
        assertEquals(1, foundBooks.size());
        assertEquals(book1.getTitle(), foundBooks.get(0).getTitle());
        verify(bookRepository).findAll();
    }

    @Test
    void findByTitleIgnoreCaseTest() {
        // given
        String title = "Book One";
        given(bookRepository.findByTitleIgnoreCase(title)).willReturn(Collections.singletonList(book1));

        // when
        List<Book> foundBooks = bookService.findByTitleIgnoreCase(title);

        // then
        assertNotNull(foundBooks);
        assertEquals(1, foundBooks.size());
        assertEquals(book1.getTitle(), foundBooks.get(0).getTitle());
        verify(bookRepository).findByTitleIgnoreCase(title);
    }
    @Test
    void findByTitleContainingIgnoreCase() {
        // given
        String title = "book one";
        given(bookRepository.findByTitleContainingIgnoreCase(title)).willReturn(Collections.singletonList(book1));
        // when
        List<Book> foundBooks = bookService.findByTitleContainingIgnoreCase(title);

        // then
        assertNotNull(foundBooks);
        assertEquals(1, foundBooks.size());
        assertEquals(book1.getTitle(), foundBooks.get(0).getTitle());
        verify(bookRepository).findByTitleContainingIgnoreCase(title);
    }

    @Test
    void notFindByTitleContainingIgnoreCase() {
       when(bookRepository.findByTitleContainingIgnoreCase("NoBook"))
               .thenReturn(Collections.emptyList());

       assertThrows(BookNotFoundException.class, () -> {
           bookService.findByTitleContainingIgnoreCase("NoBook");
       });
        verify(bookRepository).findByTitleContainingIgnoreCase("NoBook");
    }

    @Test
    void findByTitleIgnoreCaseNotFoundTest() {
        // given
        String title = "Non-existent Book";
        given(bookRepository.findByTitleIgnoreCase(title)).willReturn(Collections.emptyList());

        // when & then
        assertThrows(BookNotFoundException.class, () -> bookService.findByTitleIgnoreCase(title));
        verify(bookRepository).findByTitleIgnoreCase(title);
    }

    @Test
    void findByAuthorIgnoreCaseTest() {
        // given
        String author = "Author One";
        given(bookRepository.findByAuthorIgnoreCase(author)).willReturn(Collections.singletonList(book1));

        // when
        List<Book> foundBooks = bookService.findByAuthorIgnoreCase(author);

        // then
        assertNotNull(foundBooks);
        assertEquals(1, foundBooks.size());
        assertEquals(book1.getAuthor(), foundBooks.get(0).getAuthor());
        verify(bookRepository).findByAuthorIgnoreCase(author);
    }

    @Test
    void findByAuthorIgnoreCaseNotFoundTest() {
        // given
        String author = "Non-existent Author";
        given(bookRepository.findByAuthorIgnoreCase(author)).willReturn(Collections.emptyList());

        // when & then
        assertThrows(BookNotFoundException.class, () -> bookService.findByAuthorIgnoreCase(author));
        verify(bookRepository).findByAuthorIgnoreCase(author);
    }

    @Test
    void findByIdTest() {
        // given
        given(bookRepository.findById(book1.getId())).willReturn(Optional.of(book1));

        // when
        Optional<BookRequestDTO> foundBook = bookService.findById(book1.getId());

        // then
        assertTrue(foundBook.isPresent());
        assertEquals(bookRequestDTO1.title(), foundBook.get().title());
        verify(bookRepository).findById(book1.getId());
    }

    @Test
    void findByIdNotFoundTest() {
        // given
        Long nonExistingId = 999L;
        given(bookRepository.findById(nonExistingId)).willReturn(Optional.empty());

        // when
        Optional<BookRequestDTO> foundBook = bookService.findById(nonExistingId);

        // then
        assertFalse(foundBook.isPresent());
        verify(bookRepository).findById(nonExistingId);
    }

    @Test
    void updateBookTest() {
        // given
        given(bookRepository.findById(book1.getId())).willReturn(Optional.of(book1));


        // when
        bookService.update(book1.getId(), bookRequestDTO1);

        // then
        verify(bookRepository).findById(book1.getId());
        verify(bookRepository).save(book1);
    }

    @Test
    void updateBookNotFoundTest() {
        // given
        Long nonExistingId = 999L;
        given(bookRepository.findById(nonExistingId)).willReturn(Optional.empty());

        // when & then
        assertThrows(BookNotFoundException.class, () -> bookService.update(nonExistingId, bookRequestDTO1));
        verify(bookRepository).findById(nonExistingId);
        verify(bookRepository, never()).save(any());
    }

    @Test
    void deleteBookTest() {
        // given
        given(bookRepository.findById(book1.getId())).willReturn(Optional.of(book1));

        // when
        bookService.deleteBook(book1.getId());

        // then
        verify(bookRepository).findById(book1.getId());
        verify(bookRepository).delete(book1);
    }

    @Test
    void deleteBookNotFoundTest() {
        // given
        Long nonExistingId = 999L;
        given(bookRepository.findById(nonExistingId)).willReturn(Optional.empty());

        // when & then
        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(nonExistingId));
        verify(bookRepository).findById(nonExistingId);
        verify(bookRepository, never()).delete(any());
    }
}