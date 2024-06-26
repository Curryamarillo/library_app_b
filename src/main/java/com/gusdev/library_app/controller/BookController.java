package com.gusdev.library_app.controller;

import com.gusdev.library_app.dtoRequest.BookDTO;
import com.gusdev.library_app.entities.Book;
import com.gusdev.library_app.exceptions.BookNotFoundException;
import com.gusdev.library_app.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/create")
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book createdBook = bookService.create(book);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BookDTO>> findAll() {
        List<Book> bookList = bookService.findAll();
        List<BookDTO> bookDTOList = bookList.stream()
                .map(bookService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookDTOList);
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<List<Book>> findByTitle(@PathVariable String title) {
        List<Book> books = bookService.findByTitleIgnoreCase(title);
        return ResponseEntity.ok(books);
    }
    @GetMapping("/title/v2/{title}")
    public ResponseEntity<List<BookDTO>> findByTitleIgnoreCase(@PathVariable String title) {
        List<Book> books = bookService.findByTitleContainingIgnoreCase(title);
        List<BookDTO> bookDTOList = books.stream()
                .map(bookService::convertToDTO)
                .toList();
        return ResponseEntity.ok(bookDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> findById(@PathVariable Long id) {
        BookDTO bookDTO = bookService.findById(id).orElseThrow(() -> new BookNotFoundException("Book not found"));
        return ResponseEntity.ok(bookDTO);
    }

    @GetMapping("/author/{author}")
    public ResponseEntity<List<Book>> findByAuthor(@PathVariable String author) {
        List<Book> books = bookService.findByAuthorIgnoreCase(author);
        return ResponseEntity.ok(books);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBook(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
        try {
            bookService.update(id, bookDTO);
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