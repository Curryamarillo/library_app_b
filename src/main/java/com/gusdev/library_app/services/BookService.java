package com.gusdev.library_app.services;

import com.gusdev.library_app.dtoRequest.BookDTO;
import com.gusdev.library_app.entities.Book;
import com.gusdev.library_app.exceptions.BookAlreadyExistsException;
import com.gusdev.library_app.exceptions.BookNotFoundException;
import com.gusdev.library_app.repositories.BookRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class BookService {

    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public BookService(BookRepository bookRepository, ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
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

    public List<Book> findByAuthorIgnoreCase(String author) {
        List<Book> books = bookRepository.findByAuthorIgnoreCase(author);
        if (books.isEmpty()) {
            throw new BookNotFoundException("Books not found by author");
        }
        return books;
    }

    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    public void update(Long id, BookDTO bookDTO) {
        Book existingBook = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException("Book not found"));
        existingBook.setAuthor(bookDTO.getAuthor());
        existingBook.setIsbn(bookDTO.getIsbn());
        existingBook.setTitle(bookDTO.getTitle());
        bookRepository.save(existingBook);
    }

    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException("Book not found"));
        bookRepository.delete(book);
    }

    public BookDTO convertToDTO(Book book) {
        return modelMapper.map(book, BookDTO.class);
    }
}
