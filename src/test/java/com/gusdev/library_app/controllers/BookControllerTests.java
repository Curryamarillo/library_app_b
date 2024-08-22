package com.gusdev.library_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gusdev.library_app.config.security.JwtUtils;
import com.gusdev.library_app.dtoRequest.BookRequestDTO;
import com.gusdev.library_app.entities.Book;
import com.gusdev.library_app.exceptions.BookNotFoundException;
import com.gusdev.library_app.services.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.doThrow;
import static org.mockito.BDDMockito.given;
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

    @Autowired
    private JwtUtils jwtUtils;

    private List<Book> bookList;
    private List<BookRequestDTO> bookRequestDTOList;
    private Book book1;
    private BookRequestDTO bookRequestDTO1;
    private BookRequestDTO bookRequestDTO2;
    private BookRequestDTO bookRequestDTO3;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        // Instanciar varios libros
        book1 = Book.builder()
                .id(1L)
                .title("Book One")
                .author("Author One")
                .isbn("12345")
                .isAvailable(true)
                .createdDate(LocalDateTime.now())
                .build();

        Book book2 = Book.builder()
                .id(2L)
                .title("Book Two")
                .author("Author Two")
                .isbn("67890")
                .isAvailable(true)
                .createdDate(LocalDateTime.now())
                .build();

        Book book3 = Book.builder()
                .id(3L)
                .title("Book Three")
                .author("Author Three")
                .isbn("54321")
                .isAvailable(false)
                .createdDate(LocalDateTime.now())
                .build();

        // Agregar los libros a la lista
        bookList = new ArrayList<>();
        bookList.add(book1);
        bookList.add(book2);
        bookList.add(book3);

        // Instanciar los DTOs correspondientes
        bookRequestDTO1 = new BookRequestDTO(1L, "Book One", "Author One", "12345", true);
        bookRequestDTO2 = new BookRequestDTO(2L, "Book Two", "Author Two", "67890", true);
        bookRequestDTO3 = new BookRequestDTO(3L, "Book Three", "Author Three", "54321", false);

        bookRequestDTOList = new ArrayList<>();
        bookRequestDTOList.add(bookRequestDTO1);
        bookRequestDTOList.add(bookRequestDTO2);
        bookRequestDTOList.add(bookRequestDTO3);

        // Generar el token JWT
        jwtToken = generateJwtToken();
    }

    private String generateJwtToken() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("emailOne@test.com", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        return jwtUtils.createToken(auth); // Generar el token sin el prefijo "Bearer"
    }

    @Test
    void createBookSuccessTest() throws Exception {
        given(bookService.create(any(Book.class))).willReturn(book1);

        ResultActions result = mockMvc.perform(post("/books/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book1))
                .header("Authorization", "Bearer " + jwtToken)); // Usar el token JWT

        result.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookRequestDTO1.id()))
                .andExpect(jsonPath("$.title").value(bookRequestDTO1.title()))
                .andExpect(jsonPath("$.author").value(bookRequestDTO1.author()))
                .andExpect(jsonPath("$.isbn").value(bookRequestDTO1.isbn()))
                .andExpect(jsonPath("$.isAvailable").value(bookRequestDTO1.isAvailable()));
    }

    @Test
    void findAllBooksTest() throws Exception {
        given(bookService.findAll()).willReturn(bookList);

        ResultActions result = mockMvc.perform(get("/books")
                .header("Authorization", "Bearer " + jwtToken)); // Usar el token JWT

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(bookRequestDTO1.id()))
                .andExpect(jsonPath("$[0].title").value(bookRequestDTO1.title()))
                .andExpect(jsonPath("$[0].author").value(bookRequestDTO1.author()))
                .andExpect(jsonPath("$[0].isbn").value(bookRequestDTO1.isbn()))
                .andExpect(jsonPath("$[0].isAvailable").value(bookRequestDTO1.isAvailable()))
                .andExpect(jsonPath("$[1].id").value(bookRequestDTO2.id()))
                .andExpect(jsonPath("$[1].title").value(bookRequestDTO2.title()))
                .andExpect(jsonPath("$[1].author").value(bookRequestDTO2.author()))
                .andExpect(jsonPath("$[1].isbn").value(bookRequestDTO2.isbn()))
                .andExpect(jsonPath("$[1].isAvailable").value(bookRequestDTO2.isAvailable()))
                .andExpect(jsonPath("$[2].id").value(bookRequestDTO3.id()))
                .andExpect(jsonPath("$[2].title").value(bookRequestDTO3.title()))
                .andExpect(jsonPath("$[2].author").value(bookRequestDTO3.author()))
                .andExpect(jsonPath("$[2].isbn").value(bookRequestDTO3.isbn()))
                .andExpect(jsonPath("$[2].isAvailable").value(bookRequestDTO3.isAvailable()));
    }

    @Test
    void findBookById() throws Exception {
        given(bookService.findById(1L)).willReturn(Optional.of(bookRequestDTO1));

        ResultActions result = mockMvc.perform(get("/books/{id}", 1L)
                .header("Authorization", "Bearer " + jwtToken)); // Usar el token JWT

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(bookRequestDTO1.id()))
                .andExpect(jsonPath("$.title").value(bookRequestDTO1.title()))
                .andExpect(jsonPath("$.author").value(bookRequestDTO1.author()))
                .andExpect(jsonPath("$.isbn").value(bookRequestDTO1.isbn()))
                .andExpect(jsonPath("$.isAvailable").value(bookRequestDTO1.isAvailable()));
    }

    @Test
    void findByIdBookNotFound() throws Exception {
        given(bookService.findById(2L)).willThrow(new BookNotFoundException("Book not found."));

        ResultActions result = mockMvc.perform(get("/books/{id}", 2L)
                .header("Authorization", "Bearer " + jwtToken)); // Usar el token JWT

        result.andExpect(status().isNotFound());
    }

    @Test
    void findByTitleIgnoreCaseTest() throws Exception {
        List<Book> filteredList = new ArrayList<>();
        filteredList.add(book1);

        given(bookService.findByTitleIgnoreCase("Book One")).willReturn(filteredList);

        ResultActions result = mockMvc.perform(get("/books/title/{title}", "Book One")
                .header("Authorization", "Bearer " + jwtToken)); // Usar el token JWT

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(book1.getId()))
                .andExpect(jsonPath("$[0].title").value(book1.getTitle()))
                .andExpect(jsonPath("$[0].author").value(book1.getAuthor()))
                .andExpect(jsonPath("$[0].isbn").value(book1.getIsbn()))
                .andExpect(jsonPath("$[0].isAvailable").value(book1.getIsAvailable()));
    }

    @Test
    void deleteBookTest() throws Exception {
        mockMvc.perform(delete("/books/{id}", 1L)
                        .header("Authorization", "Bearer " + jwtToken)) // Usar el token JWT
                .andExpect(status().isNoContent());

        // Verificar que el servicio de eliminación se llame
        doThrow(new BookNotFoundException("Book not found.")).when(bookService).deleteBook(2L);
    }

    @Test
    void deleteBookNotFoundTest() throws Exception {
        doThrow(new BookNotFoundException("Book not found.")).when(bookService).deleteBook(2L);

        ResultActions result = mockMvc.perform(delete("/books/{id}", 2L)
                .header("Authorization", "Bearer " + jwtToken)); // Usar el token JWT

        result.andExpect(status().isNotFound());
    }
}
