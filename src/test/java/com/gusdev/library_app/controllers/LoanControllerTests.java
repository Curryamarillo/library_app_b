package com.gusdev.library_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gusdev.library_app.dtoResponse.LoanDTO;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
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

    private Loan loan1;
    private LoanDTO loanDTO1;

    @BeforeEach
    void setUp() {
        loan1 = new Loan();
        loan1.setId(1L);
        loan1.setUser(new User());
        loan1.setBook(new Book());
        loan1.setLoanDate(LocalDateTime.of(2023, 1, 1, 0, 0, 0));
        loan1.setReturnDate(LocalDateTime.of(2024, 01, 01, 12, 00));

        loanDTO1 = new LoanDTO();
        loanDTO1.setId(1L);
        loanDTO1.setUserId(1L);
        loanDTO1.setBookId(1L);
        loanDTO1.setLoanDate(LocalDateTime.of(2023, 1, 1, 0, 0, 0));
        loanDTO1.setReturnDate(LocalDateTime.of(2024, 01, 01, 12, 00));
    }

    @Test
    void createLoanTest() throws Exception{
        given(loanService.create(any(Loan.class))).willReturn(loan1);
        given(loanService.convertToDTO(loan1)).willReturn(loanDTO1);

        ResultActions result = mockMvc.perform(post("/loans/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loan1)));

        result.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(loanDTO1.getId()))
                .andExpect(jsonPath("$.userId").value(loanDTO1.getUserId()))
                .andExpect(jsonPath("$.bookId").value(loanDTO1.getBookId()))
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
        LoanDTO loanDTO2 = new LoanDTO();
        loanDTO2.setId(2L);
        loanDTO2.setUserId(2L);
        loanDTO2.setBookId(2L);
        loanDTO2.setLoanDate(LocalDateTime.of(2023, 2, 1, 0, 0, 0));
        loanDTO2.setReturnDate(LocalDateTime.of(2024, 02, 01, 12, 0));

        List<LoanDTO> loanDTOList = Arrays.asList(loanDTO1, loanDTO2);
        given(loanService.findAll()).willReturn(loanDTOList);

        mockMvc.perform(get("/loans"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(loanDTO1.getId()))
                .andExpect(jsonPath("$[0].userId").value(loanDTO1.getUserId()))
                .andExpect(jsonPath("$[0].bookId").value(loanDTO1.getBookId()))
                .andExpect(jsonPath("$[1].id").value(loanDTO2.getId()))
                .andExpect(jsonPath("$[1].userId").value(loanDTO2.getUserId()))
                .andExpect(jsonPath("$[1].bookId").value(loanDTO2.getBookId()));
    }

    @Test
    void findLoanByIdTest() throws Exception {
        given(loanService.findById(1L)).willReturn(loanDTO1);

        mockMvc.perform(get("/loans/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(loanDTO1.getId()))
                .andExpect(jsonPath("$.userId").value(loanDTO1.getUserId()))
                .andExpect(jsonPath("$.bookId").value(loanDTO1.getBookId()));
    }
    @Test
    void findLoanByIdNotFoundTest() throws Exception {
        Long loanId = 2L;
        given(loanService.findById(loanId)).willThrow(new LoanNotFoundException("Loan not found"));

        mockMvc.perform(get("/loans/{id}", loanId))
                .andExpect(status().isNotFound());
    }
    @Test
    void deleteLoanByIdTest() throws Exception {
        mockMvc.perform(delete("/loans/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(loanService, times(1)).deleteById(1L);
    }
}
