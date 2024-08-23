package com.gusdev.library_app.services;

import com.gusdev.library_app.dtoRequest.LoanRequestDTO;
import com.gusdev.library_app.dtoResponse.LoanResponseDTO;
import com.gusdev.library_app.entities.Book;
import com.gusdev.library_app.entities.Loan;
import com.gusdev.library_app.entities.User;
import com.gusdev.library_app.exceptions.BookNotFoundException;
import com.gusdev.library_app.exceptions.LoanAlreadyExistsException;
import com.gusdev.library_app.exceptions.LoanNotFoundException;
import com.gusdev.library_app.exceptions.UserNotFoundException;
import com.gusdev.library_app.repositories.BookRepository;
import com.gusdev.library_app.repositories.LoanRepository;
import com.gusdev.library_app.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

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

    @Mock
    private BookRepository bookRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    private LoanService loanService;

    private Loan loan1;
    private Loan loan2;
    private LoanRequestDTO loanRequestDTO1;
    private LoanRequestDTO loanRequestDTO2;
    private LoanResponseDTO loanResponseDTO1;
    private LoanResponseDTO loanResponseDTO2;


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

        loanRequestDTO1 = new LoanRequestDTO(user1.getId(), book1.getId());
        loanRequestDTO2 = new LoanRequestDTO(user2.getId(), user2.getId());

        loanResponseDTO1 = new LoanResponseDTO(1l, user1.getId(), book1.getId(), loan1.getLoanDate(),loan1.getReturnDate());
        loanResponseDTO2 = new LoanResponseDTO(2L, user2.getId(), book2.getId(), loan2.getLoanDate(), loan2.getReturnDate());


    }

    @Test
    void createLoan_Successful() {
        // Given
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));
        given(bookRepository.findById(book1.getId())).willReturn(Optional.of(book1));
        given(loanRepository.save(any(Loan.class))).willReturn(loan1);

        // When
        Loan createdLoan = loanService.create(loanRequestDTO1);

        // Then
        assertNotNull(createdLoan);
        assertEquals(loan1.getId(), createdLoan.getId());
        assertEquals(book1.getId(), createdLoan.getBook().getId());
        assertEquals(user1.getId(), createdLoan.getUser().getId());
        assertFalse(loan1.getBook().getIsAvailable(), "Book should not be available after loan creation");

    }

    @Test
    void createLoan_BookNotFound() {
        given(bookRepository.findById(book1.getId())).willReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> loanService.create(loanRequestDTO1));
    }

    @Test
    void createLoan_UserNotFound() {
        // Given
        given(bookRepository.findById(book1.getId())).willReturn(Optional.of(book1));
        given(userRepository.findById(user1.getId())).willReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> loanService.create(loanRequestDTO1));
    }
    @Test
    void createLoan_BookNotAvailable() {
        // Given
        book1.setIsAvailable(false);
        given(bookRepository.findById(book1.getId())).willReturn(Optional.of(book1));
        given(userRepository.findById(user1.getId())).willReturn(Optional.of(user1));

        // When & Then
        assertThrows(LoanAlreadyExistsException.class, () -> loanService.create(loanRequestDTO1));
    }
    @Test
    void findAllLoans() {
        // Given
        List<Loan> loans = List.of(loan1, loan2);
        given(loanRepository.findAll()).willReturn(loans);

        // When
        List<LoanResponseDTO> loanResponseDTOList = loanService.findAll();

        // Then
        assertEquals(2, loanResponseDTOList.size());
        assertNotNull(loanResponseDTOList);

        LoanResponseDTO fetchedLoanResponseDTO1 = loanResponseDTOList.get(0);
        assertEquals(loan1.getId(), fetchedLoanResponseDTO1.id());
        assertEquals(loan1.getUser().getId(), fetchedLoanResponseDTO1.userId());
        assertEquals(loan1.getBook().getId(), fetchedLoanResponseDTO1.bookId());
        assertEquals(loan1.getLoanDate(), fetchedLoanResponseDTO1.loanDate());
        assertEquals(loan1.getReturnDate(), fetchedLoanResponseDTO1.returnDate());

        LoanResponseDTO fetchedLoanResponseDTO2 = loanResponseDTOList.get(1);
        assertEquals(loan2.getId(), fetchedLoanResponseDTO2.id());
        assertEquals(loan2.getUser().getId(), fetchedLoanResponseDTO2.userId());
        assertEquals(loan2.getBook().getId(), fetchedLoanResponseDTO2.bookId());
        assertEquals(loan2.getLoanDate(), fetchedLoanResponseDTO2.loanDate());
        assertEquals(loan2.getReturnDate(), fetchedLoanResponseDTO2.returnDate());
    }

    @Test
    void findLoanById() {
        // Given
        given(loanRepository.findById(anyLong())).willReturn(Optional.of(loan1));

        // When
        LoanResponseDTO foundLoanResponseDTO = loanService.findById(1L);

        // Then
        assertNotNull(foundLoanResponseDTO);
        assertEquals(loanResponseDTO1.id(), foundLoanResponseDTO.id());
        assertEquals(loanResponseDTO1.userId(), foundLoanResponseDTO.userId());
        assertEquals(loanResponseDTO1.bookId(), foundLoanResponseDTO.bookId());
        assertEquals(loanResponseDTO1.loanDate(), foundLoanResponseDTO.loanDate());
        assertEquals(loanResponseDTO1.returnDate(), foundLoanResponseDTO.returnDate());
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
