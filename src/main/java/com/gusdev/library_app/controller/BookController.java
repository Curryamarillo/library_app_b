package com.gusdev.library_app.controller;

import com.gusdev.library_app.dtoRequest.BookDTO;
import com.gusdev.library_app.entities.Book;
import com.gusdev.library_app.exceptions.BookNotFoundException;
import com.gusdev.library_app.services.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final ModelMapper modelMapper;

    @Autowired
    public BookController(BookService bookService, ModelMapper modelMapper) {
        this.bookService = bookService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/create")
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book createdBook = bookService.create(book);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Iterable<BookDTO>> findAll() {
        Iterable<Book> bookList = bookService.findAll();

        List<BookDTO> bookDTOList = new ArrayList<>();
        for (Book book : bookList) {
            bookDTOList.add(modelMapper.map(book, BookDTO.class));
        }

        return ResponseEntity.ok(bookDTOList);
    }
    @GetMapping("/{title}")
    public ResponseEntity<Iterable<Book>> findByTitle(@PathVariable String title) {
        try {
            Iterable<Book> books = bookService.findByTitleIgnoreCase(title);
            return ResponseEntity.ok(books);
        } catch (BookNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while searching for books", e);
        }
    }
    @GetMapping("/author/{author}")
    public ResponseEntity<Iterable<Book>> findByAuthor(@PathVariable String author) {
        Iterable<Book> books = bookService.findByAuthorIgnoreCase(author);
        return ResponseEntity.ok(books);
    }
    @PutMapping()
    public ResponseEntity<Void> updateBook(@PathVariable Long id, @RequestBody BookDTO book) {
        try {
            bookService.update(id, book);
            return ResponseEntity.ok().build();
        } catch (BookNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
