package com.gusdev.library_app.services;

import com.gusdev.library_app.dtoResponse.LoanDTO;
import com.gusdev.library_app.entities.Book;
import com.gusdev.library_app.entities.Loan;
import com.gusdev.library_app.entities.User;
import com.gusdev.library_app.exceptions.LoanAlreadyExistsException;
import com.gusdev.library_app.exceptions.LoanNotFoundException;
import com.gusdev.library_app.repositories.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private Loan loan2;
    private LoanDTO loanDTO1;
    private LoanDTO loanDTO2;

    private User user1;
    private User user2;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        // Instancia de entidades User
        user1 = User.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email("johndoe@example.com")
                .password("password")
                .isAdmin(false)
                .loans(new HashSet<>())
                .build();

        user2 = User.builder()
                .id(2L)
                .name("Jane")
                .surname("Doe")
                .email("janedoe@example.com")
                .password("password")
                .isAdmin(false)
                .loans(new HashSet<>())
                .build();

        // Instancia de entidades Book
        book1 = Book.builder()
                .id(1L)
                .title("Effective Java")
                .author("Joshua Bloch")
                .isbn("9780134685991")
                .isAvailable(true)
                .createdDate(LocalDateTime.now())
                .loans(new HashSet<>())
                .build();

        book2 = Book.builder()
                .id(2L)
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("9780132350884")
                .isAvailable(true)
                .createdDate(LocalDateTime.now())
                .loans(new HashSet<>())
                .build();

        // Instancia de entidades Loan
        loan1 = Loan.builder()
                .id(1L)
                .user(user1)
                .book(book1)
                .loanDate(LocalDateTime.now())
                .returnDate(LocalDateTime.now().plusDays(14))
                .build();

        loan2 = Loan.builder()
                .id(2L)
                .user(user2)
                .book(book2)
                .loanDate(LocalDateTime.now())
                .returnDate(LocalDateTime.now().plusDays(14))
                .build();

        // Instancia de entidades LoanDTO
        loanDTO1 = new LoanDTO(1l, user1.getId(), book1.getId(), loan1.getLoanDate(),loan1.getReturnDate());

        loanDTO2 = new LoanDTO(2L, user2.getId(), book2.getId(), loan2.getLoanDate(), loan2.getReturnDate());

    }

    @Test
    void createLoan_Successful() {
        // Given
        loan1.getBook().setIsAvailable(true);
        given(loanRepository.save(any(Loan.class))).willReturn(loan1);

        // When
        Loan createdLoan = loanService.create(loan1);

        // Then
        assertNotNull(createdLoan);
        assertEquals(loan1.getId(), createdLoan.getId());
        assertFalse(loan1.getBook().getIsAvailable(), "Book should not be available after loan creation");
    }

    @Test
    void createLoan_FailureLoanAlreadyExists() {
        // Given
        loan1.getBook().setIsAvailable(false);

        // When & Then
        assertThrows(LoanAlreadyExistsException.class, () -> loanService.create(loan1));
    }

    @Test
    void findAllLoans() {
        // Given
        List<Loan> loans = List.of(loan1, loan2);
        given(loanRepository.findAll()).willReturn(loans);

        // When
        List<LoanDTO> loanDTOList = loanService.findAll();

        // Then
        assertEquals(2, loanDTOList.size());
        assertNotNull(loanDTOList);

        LoanDTO fetchedLoanDTO1 = loanDTOList.get(0);
        assertEquals(loan1.getId(), fetchedLoanDTO1.id());
        assertEquals(loan1.getUser().getId(), fetchedLoanDTO1.userId());
        assertEquals(loan1.getBook().getId(), fetchedLoanDTO1.bookId());
        assertEquals(loan1.getLoanDate(), fetchedLoanDTO1.loanDate());
        assertEquals(loan1.getReturnDate(), fetchedLoanDTO1.returnDate());

        LoanDTO fetchedLoanDTO2 = loanDTOList.get(1);
        assertEquals(loan2.getId(), fetchedLoanDTO2.id());
        assertEquals(loan2.getUser().getId(), fetchedLoanDTO2.userId());
        assertEquals(loan2.getBook().getId(), fetchedLoanDTO2.bookId());
        assertEquals(loan2.getLoanDate(), fetchedLoanDTO2.loanDate());
        assertEquals(loan2.getReturnDate(), fetchedLoanDTO2.returnDate());
    }

    @Test
    void findLoanById() {
        // Given
        given(loanRepository.findById(anyLong())).willReturn(Optional.of(loan1));

        // When
        LoanDTO foundLoanDTO = loanService.findById(1L);

        // Then
        assertNotNull(foundLoanDTO);
        assertEquals(loanDTO1.id(), foundLoanDTO.id());
        assertEquals(loanDTO1.userId(), foundLoanDTO.userId());
        assertEquals(loanDTO1.bookId(), foundLoanDTO.bookId());
        assertEquals(loanDTO1.loanDate(), foundLoanDTO.loanDate());
        assertEquals(loanDTO1.returnDate(), foundLoanDTO.returnDate());
    }

    @Test
    void findLoanById_NotFound() {
        // Given
        given(loanRepository.findById(anyLong())).willReturn(Optional.empty());

        // When & Then
        assertThrows(LoanNotFoundException.class, () -> loanService.findById(4L));
    }

    @Test
    void deleteLoanById() {
        // When
        loanService.deleteById(1L);

        // Then
        verify(loanRepository, times(1)).deleteById(1L);
    }
}
