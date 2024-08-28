package com.gusdev.library_app.controller;

import com.gusdev.library_app.dtoRequest.BookRequestDTO;
import com.gusdev.library_app.dtoResponse.BookResponseDTO;
import com.gusdev.library_app.entities.Book;
import com.gusdev.library_app.exceptions.BookNotFoundException;
import com.gusdev.library_app.services.BookService;
import com.gusdev.library_app.utils.BookMapper;
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
    public ResponseEntity<List<BookResponseDTO>> findAll() {
        List<Book> bookList = bookService.findAll();
        List<BookResponseDTO> bookResponseDTOList = bookList.stream()
                .map(BookMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookResponseDTOList);
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<List<Book>> findByTitle(@PathVariable String title) {
        List<Book> books = bookService.findByTitleIgnoreCase(title);
        return ResponseEntity.ok(books);
    }
    @GetMapping("/title/v2/{title}")
    public ResponseEntity<List<BookResponseDTO>> findByTitleIgnoreCase(@PathVariable String title) {
        List<Book> books = bookService.findByTitleContainingIgnoreCase(title);
        List<BookResponseDTO> bookResponseDTOList = books.stream()
                .map(BookMapper::toDto)
                .toList();
        return ResponseEntity.ok(bookResponseDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> findById(@PathVariable Long id) {
        BookResponseDTO bookRequestDTO = bookService.findById(id).orElseThrow(() -> new BookNotFoundException("Book not found"));
        return ResponseEntity.ok(bookRequestDTO);
    }

    @GetMapping("/author/{author}")
    public ResponseEntity<List<Book>> findByAuthor(@PathVariable String author) {
        List<Book> books = bookService.findByAuthorIgnoreCase(author);
        return ResponseEntity.ok(books);
    }


    @PutMapping("/{id}")
    public ResponseEntity<BookRequestDTO> updateBook(@PathVariable Long id, @RequestBody BookRequestDTO bookRequestDTO) {
        try {
            bookService.update(id, bookRequestDTO);
            return ResponseEntity.ok(bookRequestDTO);
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