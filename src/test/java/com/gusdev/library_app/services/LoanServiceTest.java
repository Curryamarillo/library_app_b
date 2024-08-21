package com.gusdev.library_app.services;

import com.gusdev.library_app.LibraryAppApplication;
import com.gusdev.library_app.dtoResponse.LoanDTO;
import com.gusdev.library_app.entities.Book;
import com.gusdev.library_app.entities.Loan;
import com.gusdev.library_app.entities.User;
import com.gusdev.library_app.exceptions.LoanAlreadyExistsException;
import com.gusdev.library_app.exceptions.LoanNotFoundException;
import com.gusdev.library_app.repositories.LoanRepository;
import com.gusdev.library_app.utils.LoanMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {
    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanService loanService;

    private Loan loan1;
    private LoanDTO loanDTO1;
    private LoanDTO loanDTO2;
    private User user1;
    private User user2;
    private Book book1;
    private Book book2;
    private final LocalDateTime loanDate = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
    private final LocalDateTime returnDate = (LocalDateTime.of(2024, 01, 01, 12, 00));

    @BeforeEach
    void setUp() {
        loan1.setId(1L);
        loan1.setUser(new User());
        loan1.setBook(new Book());
        loan1.setLoanDate(loanDate);
        loan1.setReturnDate(returnDate);

        user1.setId(1L);
        user1.setName("Name one");
        user1.setSurname("Surname one");
        user1

        loanDTO1 = new LoanDTO(1L, 1L, 1L, loanDate, returnDate);
        loanDTO2 = new LoanDTO(2L, 2L, 2L, loanDate, returnDate);
    }

    @Test
    void createLoan_Successful() {

        loan1.getBook().setIsAvailable(true);


        given(loanRepository.save(any(Loan.class))).willReturn(loan1);


        Loan createdLoan = loanService.create(loan1);


        assertNotNull(createdLoan);
        assertEquals(loan1.getId(), createdLoan.getId());
    }

    @Test
    void createLoan_FailureLoanAlreadyExists() {

        loan1.getBook().setIsAvailable(false);


        assertThrows(LoanAlreadyExistsException.class, () -> loanService.create(loan1));
    }

    @Test
    void findAllLoans() {

        List<Loan> loans = new ArrayList<>();
        loans.add(loan1);
        given(loanRepository.findAll()).willReturn(loans);


        given(LoanMapper.toDTOList(loans), eq(LoanDTO.class))).willReturn(loanDTO1);


        List<LoanDTO> loanDTOList = loanService.findAll();


        assertEquals(1, loanDTOList.size());
        LoanDTO fetchedLoanDTO = loanDTOList.get(0); // Aquí puede ocurrir el NullPointerException
        assertEquals(loan1.getId(), fetchedLoanDTO.getId());
        assertEquals(loan1.getUser().getId(), fetchedLoanDTO.getUserId());
        assertEquals(loan1.getBook().getId(), fetchedLoanDTO.getBookId());
        assertEquals(loan1.getLoanDate(), fetchedLoanDTO.getLoanDate());
        assertEquals(loan1.getReturnDate(), fetchedLoanDTO.getReturnDate());
    }

    @Test
    void findLoanById() {

        given(loanRepository.findById(anyLong())).willReturn(Optional.of(loan1));
        given(modelMapper.map(loan1, LoanDTO.class)).willReturn(loanDTO1);


        LoanDTO foundLoanDTO = loanService.findById(1L);


        assertNotNull(foundLoanDTO);
        assertEquals(loanDTO1.getId(), foundLoanDTO.getId());
        assertEquals(loanDTO1.getUserId(), foundLoanDTO.getUserId());
        assertEquals(loanDTO1.getBookId(), foundLoanDTO.getBookId());
        assertEquals(loanDTO1.getLoanDate(), foundLoanDTO.getLoanDate());
        assertEquals(loanDTO1.getReturnDate(), foundLoanDTO.getReturnDate());
    }

    @Test
    void findLoanById_NotFound() {

        given(loanRepository.findById(anyLong())).willReturn(Optional.empty());


        assertThrows(LoanNotFoundException.class, () -> loanService.findById(4L));
    }

    @Test
    void deleteLoanById() {


        loanService.deleteById(1L);


        verify(loanRepository, times(1)).deleteById(1L);
    }

    @Test
    void convertToDTOLoan() {

        given(modelMapper.map(loan1, LoanDTO.class)).willReturn(loanDTO1);


        LoanDTO loanDTO = loanService.convertToDTO(loan1);


        assertNotNull(loanDTO);
        assertEquals(loanDTO1.getId(), loanDTO.getId());
        assertEquals(loanDTO1.getUserId(), loanDTO.getUserId());
        assertEquals(loanDTO1.getBookId(), loanDTO.getBookId());
        assertEquals(loanDTO1.getLoanDate(), loanDTO.getLoanDate());
        assertEquals(loanDTO1.getReturnDate(), loanDTO.getReturnDate());
    }
}