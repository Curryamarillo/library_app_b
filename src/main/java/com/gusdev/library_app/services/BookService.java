package com.gusdev.library_app.services;

import com.gusdev.library_app.dtoRequest.BookRequestDTO;
import com.gusdev.library_app.dtoResponse.BookResponseDTO;
import com.gusdev.library_app.entities.Book;
import com.gusdev.library_app.exceptions.BookAlreadyExistsException;
import com.gusdev.library_app.exceptions.BookNotFoundException;
import com.gusdev.library_app.repositories.BookRepository;
import com.gusdev.library_app.utils.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class BookService {

    @Autowired
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book create(Book book) {
        boolean existBook = bookRepository.existsByTitle(book.getTitle());
        if (existBook) {
            throw new BookAlreadyExistsException("Book already exists.");
        }
        return bookRepository.save(book);
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public List<Book> findByTitleIgnoreCase(String title) {
        List<Book> books = bookRepository.findByTitleIgnoreCase(title);
        if (books.isEmpty()) {
            throw new BookNotFoundException("Books not found by title");
        }
        return books;
    }

    public List<Book> findByTitleContainingIgnoreCase(String title) {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(title);
        if (books.isEmpty()) {
            throw new BookNotFoundException("Books not found by title");
        }
        return books;
    }

    public List<Book> findByAuthorIgnoreCase(String author) {
        List<Book> books = bookRepository.findByAuthorIgnoreCase(author);
        if (books.isEmpty()) {
            throw new BookNotFoundException("Books not found by author");
        }
        return books;
    }


    public Optional<BookResponseDTO> findById(Long id) {
        return bookRepository.findById(id)
                .map(BookMapper::toDto);
    }

    public void update(Long id, BookRequestDTO bookRequestDTO) {
        Book existingBook = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException("Book not found"));
        existingBook.setAuthor(bookRequestDTO.author());
        existingBook.setIsbn(bookRequestDTO.isbn());
        existingBook.setTitle(bookRequestDTO.title());
        bookRepository.save(existingBook);
    }

    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException("Book not found"));
        bookRepository.delete(book);
    }
}


