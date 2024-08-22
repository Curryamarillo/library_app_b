package com.gusdev.library_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gusdev.library_app.config.security.JwtUtils;
import com.gusdev.library_app.dtoRequest.LoanRequestDTO;
import com.gusdev.library_app.dtoResponse.LoanResponseDTO;
import com.gusdev.library_app.entities.Book;
import com.gusdev.library_app.entities.Loan;
import com.gusdev.library_app.entities.User;
import com.gusdev.library_app.exceptions.LoanNotFoundException;
import com.gusdev.library_app.repositories.LoanRepository;
import com.gusdev.library_app.services.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class LoanControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanService loanService;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtils jwtUtils;

    private Loan loan1;
    private LoanResponseDTO loanResponseDTO1;
    private LoanResponseDTO loanResponseDTO2;
    private final LocalDateTime loanDate = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
    private final LocalDateTime returnDate = (LocalDateTime.of(2024, 01, 01, 12, 00));
    private String jwtToken;

    @BeforeEach
    void setUp() {
        loan1 = new Loan();
        loan1.setId(1L);
        loan1.setUser(new User());
        loan1.setBook(new Book());
        loan1.setLoanDate(loanDate);
        loan1.setReturnDate(returnDate);

        loanResponseDTO1 = new LoanResponseDTO(1L, 1L, 1L, loanDate, returnDate);
        loanResponseDTO2 = new LoanResponseDTO(2L, 2L, 2L, loanDate, returnDate);
        jwtToken = generateJwtToken();
    }
    private String generateJwtToken() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("emailOne@test.com", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        return jwtUtils.createToken(auth);
    }
    @WithMockUser(roles = "USER_ADMIN") // Simula un usuario autenticado con rol USER_ADMIN
    @Test
    void createLoanTest() throws Exception {

        given(loanService.create(any(LoanRequestDTO.class))).willReturn(loan1);


        String loanDTOJson = objectMapper.writeValueAsString(loanResponseDTO1);


        ResultActions result = mockMvc.perform(post("/loans/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(loanResponseDTO1)));


        result.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(loanResponseDTO1.id()))
                .andExpect(jsonPath("$.userId").value(loanResponseDTO1.userId()))
                .andExpect(jsonPath("$.bookId").value(loanResponseDTO1.bookId()))
                .andExpect(jsonPath("$.loanDate[0]").value(2023)) // Year
                .andExpect(jsonPath("$.loanDate[1]").value(1))    // Month
                .andExpect(jsonPath("$.loanDate[2]").value(1))    // Day
                .andExpect(jsonPath("$.loanDate[3]").value(0))    // Hour
                .andExpect(jsonPath("$.loanDate[4]").value(0))    // Minute
                .andExpect(jsonPath("$.returnDate[0]").value(2024)) // Year
                .andExpect(jsonPath("$.returnDate[1]").value(1))    // Month
                .andExpect(jsonPath("$.returnDate[2]").value(1))    // Day
                .andExpect(jsonPath("$.returnDate[3]").value(12))   // Hour
                .andExpect(jsonPath("$.returnDate[4]").value(0));   // Minute
    }

    @Test
    void findAllLoansTest() throws Exception {

        List<LoanResponseDTO> loanResponseDTOList = Arrays.asList(loanResponseDTO1, loanResponseDTO2);
        given(loanService.findAll()).willReturn(loanResponseDTOList);

        mockMvc.perform(get("/loans").header("Authorization","Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(loanResponseDTO1.id()))
                .andExpect(jsonPath("$[0].userId").value(loanResponseDTO1.userId()))
                .andExpect(jsonPath("$[0].bookId").value(loanResponseDTO1.bookId()))
                .andExpect(jsonPath("$[1].id").value(loanResponseDTO2.id()))
                .andExpect(jsonPath("$[1].userId").value(loanResponseDTO2.userId()))
                .andExpect(jsonPath("$[1].bookId").value(loanResponseDTO2.bookId()));
    }

    @WithMockUser(roles = "USER_ADMIN")
    @Test
    void findLoanByIdTest() throws Exception {
        given(loanService.findById(1L)).willReturn(loanResponseDTO1);

        mockMvc.perform(get("/loans/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(loanResponseDTO1.id()))
                .andExpect(jsonPath("$.userId").value(loanResponseDTO1.userId()))
                .andExpect(jsonPath("$.bookId").value(loanResponseDTO1.bookId()));
    }
    @Test
    void findLoanByIdNotFoundTest() throws Exception {
        Long loanId = 2L;
        given(loanService.findById(loanId)).willThrow(new LoanNotFoundException("Loan not found"));

        mockMvc.perform(get("/loans/{id}", loanId).header("Authorization","Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }
    @Test
    void deleteLoanByIdTest() throws Exception {
        mockMvc.perform(delete("/loans/{id}", 1L).header("Authorization","Bearer " + jwtToken))
                .andExpect(status().isNoContent());

        verify(loanService, times(1)).deleteById(1L);
    }
}
