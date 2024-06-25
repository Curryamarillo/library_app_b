package com.gusdev.library_app.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.gusdev.library_app.dtoRequest.BookDTO;
import com.gusdev.library_app.entities.Book;
import com.gusdev.library_app.exceptions.BookAlreadyExistsException;
import com.gusdev.library_app.exceptions.BookNotFoundException;
import com.gusdev.library_app.services.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;


    private Book book1;
    private BookDTO bookDTO1;

    @BeforeEach
    void setUp() {

        book1 = Book.builder()
                .id(1L)
                .title("Book One")
                .author("Author One")
                .isbn("12345")
                .isAvailable(true)
                .createdDate(LocalDateTime.now())
                .build();

        bookDTO1 = BookDTO.builder()
                .id(1L)
                .title("Book One")
                .author("Author One")
                .isbn("12345")
                .isAvailable(true)
                .build();
    }
    @Test
    void createBookSuccessTest() throws Exception {
        given(bookService.create(any(Book.class))).willReturn(book1);
        given(bookService.convertToDTO(book1)).willReturn(bookDTO1);

        ResultActions result = mockMvc.perform(post("/books/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book1)));

        result.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookDTO1.getId()))
                .andExpect(jsonPath("$.title").value(bookDTO1.getTitle()))
                .andExpect(jsonPath("$.author").value(bookDTO1.getAuthor()))
                .andExpect(jsonPath("$.isbn").value(bookDTO1.getIsbn()))
                .andExpect(jsonPath("$.isAvailable").value(bookDTO1.getIsAvailable()));

    }
    @Test
    void createExistingBookTest() throws Exception {
        given(bookService.create(any(Book.class))).willThrow(new BookAlreadyExistsException("Book already exists."));

        ResultActions result = mockMvc.perform(post("/books/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book1)));

        result.andExpect(status().isConflict());
    }
    @Test
    void findAllBooksTest() throws Exception{
        List<Book> bookList = new ArrayList<>();
        bookList.add(book1);

        given(bookService.findAll()).willReturn(bookList);
        given(bookService.convertToDTO(book1)).willReturn(bookDTO1);

        ResultActions result = mockMvc.perform(get("/books"));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(bookDTO1.getId()))
                .andExpect(jsonPath("$[0].title").value(bookDTO1.getTitle()))
                .andExpect(jsonPath("$[0].author").value(bookDTO1.getAuthor()))
                .andExpect(jsonPath("$[0].isbn").value(bookDTO1.getIsbn()))
                .andExpect(jsonPath("$[0].isAvailable").value(bookDTO1.getIsAvailable()));
        verify(bookService, times(1)).findAll();
        verify(bookService, times(1)).convertToDTO(book1);
    }

    @Test
    void findBookById() throws Exception {
        given(bookService.findById(1L)).willReturn(Optional.ofNullable(bookDTO1));

        ResultActions result = mockMvc.perform(get("/books/{id}", 1L));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookDTO1.getId()))
                .andExpect(jsonPath("$.title").value(bookDTO1.getTitle()))
                .andExpect(jsonPath("$.author").value(bookDTO1.getAuthor()))
                .andExpect(jsonPath("$.isbn").value(bookDTO1.getIsbn()))
                .andExpect(jsonPath("$.isAvailable").value(bookDTO1.getIsAvailable()));
    }
    @Test
    void findByIdBookNotFound() throws Exception {
        given(bookService.findById(2L)).willThrow(new BookNotFoundException("Book not found."));

        ResultActions result = mockMvc.perform(get("/books/{id}", 2L));

        result.andExpect(status().isNotFound());
    }
    @Test
    void findByTitleIgnoreCaseTest() throws Exception {
        List<Book> bookList = new ArrayList<>();
        bookList.add(book1);

        given(bookService.findByTitleIgnoreCase("Book One")).willReturn(bookList);
        given(bookService.findByTitleIgnoreCase("Book One")).willReturn(bookList);
        given(bookService.findByTitleIgnoreCase("book one")).willReturn(bookList);
        given(bookService.findByTitleIgnoreCase("BOOK ONE")).willReturn(bookList);

        ResultActions result1 = mockMvc.perform(get("/books/title/{title}", "Book One")
                .contentType(MediaType.APPLICATION_JSON));

        ResultActions result2 = mockMvc.perform(get("/books/title/{title}", "book one")
                .contentType(MediaType.APPLICATION_JSON));

        ResultActions result3 = mockMvc.perform(get("/books/title/{title}", "BOOK ONE")
                .contentType(MediaType.APPLICATION_JSON));

        result1.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(book1.getId()))
                .andExpect(jsonPath("$[0].title").value(book1.getTitle()))
                .andExpect(jsonPath("$[0].author").value(book1.getAuthor()))
                .andExpect(jsonPath("$[0].isbn").value(book1.getIsbn()))
                .andExpect(jsonPath("$[0].isAvailable").value(book1.getIsAvailable()));

        result2.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(book1.getId()))
                .andExpect(jsonPath("$[0].title").value(book1.getTitle()))
                .andExpect(jsonPath("$[0].author").value(book1.getAuthor()))
                .andExpect(jsonPath("$[0].isbn").value(book1.getIsbn()))
                .andExpect(jsonPath("$[0].isAvailable").value(book1.getIsAvailable()));

        result3.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(book1.getId()))
                .andExpect(jsonPath("$[0].title").value(book1.getTitle()))
                .andExpect(jsonPath("$[0].author").value(book1.getAuthor()))
                .andExpect(jsonPath("$[0].isbn").value(book1.getIsbn()))
                .andExpect(jsonPath("$[0].isAvailable").value(book1.getIsAvailable()));
    }

    @Test
    void findByAuthorIgnoreCase() throws Exception {

        List<Book> bookList = new ArrayList<>();
        bookList.add(book1);


        given(bookService.findByAuthorIgnoreCase("Author One")).willReturn(bookList);
        given(bookService.findByAuthorIgnoreCase("author one")).willReturn(bookList);
        given(bookService.findByAuthorIgnoreCase("AUTHOR ONE")).willReturn(bookList);


        ResultActions result1 = mockMvc.perform(get("/books/author/{author}", "Author One")
                .contentType(MediaType.APPLICATION_JSON));

        ResultActions result2 = mockMvc.perform(get("/books/author/{author}", "author one")
                .contentType(MediaType.APPLICATION_JSON));

        ResultActions result3 = mockMvc.perform(get("/books/author/{author}", "AUTHOR ONE")
                .contentType(MediaType.APPLICATION_JSON));


        result1.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(book1.getId()))
                .andExpect(jsonPath("$[0].title").value(book1.getTitle()))
                .andExpect(jsonPath("$[0].author").value(book1.getAuthor()))
                .andExpect(jsonPath("$[0].isbn").value(book1.getIsbn()))
                .andExpect(jsonPath("$[0].isAvailable").value(book1.getIsAvailable()));

        result2.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(book1.getId()))
                .andExpect(jsonPath("$[0].title").value(book1.getTitle()))
                .andExpect(jsonPath("$[0].author").value(book1.getAuthor()))
                .andExpect(jsonPath("$[0].isbn").value(book1.getIsbn()))
                .andExpect(jsonPath("$[0].isAvailable").value(book1.getIsAvailable()));

        result3.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(book1.getId()))
                .andExpect(jsonPath("$[0].title").value(book1.getTitle()))
                .andExpect(jsonPath("$[0].author").value(book1.getAuthor()))
                .andExpect(jsonPath("$[0].isbn").value(book1.getIsbn()))
                .andExpect(jsonPath("$[0].isAvailable").value(book1.getIsAvailable()));
    }
    @Test
    void updateBookSuccessTest() throws Exception {
        doNothing().when(bookService).update(eq(1L), any(BookDTO.class));

        ResultActions result = mockMvc.perform(put("/books/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookDTO1)));

        result.andExpect(status().isOk());
    }
    @Test
    void updateBookNotFoundTest() throws Exception {
        doThrow(new BookNotFoundException("Book not found")).when(bookService).update(eq(2L), any(BookDTO.class));

        ResultActions result = mockMvc.perform(put("/books/{id}", 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookDTO1)));

        result.andExpect(status().isNotFound());
    }
    @Test
    void deleteBookSuccessTest() throws Exception {
        doNothing().when(bookService).deleteBook(eq(1L));

        ResultActions result = mockMvc.perform(delete("/books/{id}", 1L));

        result.andExpect(status().isNoContent());
    }
}
