package com.gusdev.library_app.services;

import com.gusdev.library_app.dtoRequest.BookDTO;
import com.gusdev.library_app.entities.Book;
import com.gusdev.library_app.exceptions.BookAlreadyExistsException;
import com.gusdev.library_app.exceptions.BookNotFoundException;
import com.gusdev.library_app.repositories.BookRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;



@Service
public class BookService {

    private final BookRepository bookRepository;

    private ModelMapper modelMapper;

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

    public Iterable<Book> findAll() {
        return bookRepository.findAll();
    }

    public Iterable<Book> findByTitleIgnoreCase(String title) {
        Iterable<Book> books = bookRepository.findByTitleIgnoreCase(title);
        if (!books.iterator().hasNext()) {
            throw new BookNotFoundException("Books not found by title");
        }        return books;
    }

    public Iterable<Book> findByAuthorIgnoreCase(String author) {
        Iterable<Book> books = bookRepository.findByAuthorIgnoreCase(author);
        if (!books.iterator().hasNext()) {
            throw new BookNotFoundException("Books not found by author");
        }
        return books;
    }

    public Book findById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public void update(Long id, BookDTO book) {
        Book existsBook = bookRepository.findById(id).orElse(null);
        if (existsBook != null) {
            existsBook.setAuthor(book.getAuthor());
            existsBook.setIsbn(book.getIsbn());
            existsBook.setTitle(book.getTitle());
            bookRepository.save(existsBook);
        } else {
            throw new BookNotFoundException("Book not found");
        }
    }
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id).orElse(null);
    }

    public BookDTO convertToDTO(Book book) { return modelMapper.map(book, BookDTO.class);}
}
