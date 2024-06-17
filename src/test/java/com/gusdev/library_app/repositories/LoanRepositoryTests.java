package com.gusdev.library_app.repositories;

import com.gusdev.library_app.entities.Book;
import com.gusdev.library_app.entities.Loan;
import com.gusdev.library_app.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class LoanRepositoryTests {


    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    private User user1;
    private Book book1;
    private Loan loan1;

    @BeforeEach
    public void setUpTest() {
        loanRepository.deleteAll();
        userRepository.deleteAll();
        bookRepository.deleteAll();

        user1 = User.builder()
                .name("John")
                .surname("Doe")
                .email("john.doe4@example.com")
                .isAdmin(false)
                .password("password123")
                .build();

        book1 = Book.builder()
                .title("Book one")
                .author("Author one")
                .isbn("1234")
                .isAvailable(true)
                .createdDate(LocalDateTime.now())
                .build();

        userRepository.save(user1);
        bookRepository.save(book1);

        loan1 = Loan.builder()
                .user(user1)
                .book(book1)
                .loanDate(LocalDateTime.now())
                .returnDate(null)
                .build();

        loanRepository.save(loan1);
    }

    @Test
    public void saveLoanTest() {
        // given
        Loan loan2 = Loan.builder()
                .user(user1)
                .book(book1)
                .loanDate(LocalDateTime.now())
                .returnDate(null)
                .build();

        // when
        Loan savedLoan = loanRepository.save(loan2);

        // then
        assertThat(savedLoan).isNotNull();
        assertThat(savedLoan.getId()).isGreaterThan(0);
    }

    @Test
    public void findFirstByBookIdAndReturnDateIsNullOrderByLoanDateDescTest() {
        // when
        Optional<Loan> foundLoan = loanRepository.findFirstByBookIdAndReturnDateIsNullOrderByLoanDateDesc(book1.getId());

        // then
        assertThat(foundLoan).isPresent();
        assertThat(foundLoan.get().getBook().getId()).isEqualTo(book1.getId());
        assertThat(foundLoan.get().getReturnDate()).isNull();
    }

    @Test
    public void deleteLoanTest() {
        // when
        loanRepository.delete(loan1);
        Optional<Loan> foundLoan = loanRepository.findById(loan1.getId());

        // then
        assertThat(foundLoan).isNotPresent();
    }

    @Test
    public void findAllLoansTest() {
        // given
        Loan loan2 = Loan.builder()
                .user(user1)
                .book(book1)
                .loanDate(LocalDateTime.now().plusDays(1))
                .returnDate(null)
                .build();

        loanRepository.save(loan2);

        // when
        Iterable<Loan> loans = loanRepository.findAll();

        // then
        assertThat(loans).isNotNull();
        assertThat(loans).hasSize(2); // ya que hemos guardado dos préstamos en total
    }

    @Test
    public void findByIdTest() {
        // when
        Optional<Loan> foundLoan = loanRepository.findById(loan1.getId());

        // then
        assertThat(foundLoan).isPresent();
        assertThat(foundLoan.get().getId()).isEqualTo(loan1.getId());
    }

    @Test
    public void updateLoanTest() {
        // given
        loan1.setReturnDate(LocalDateTime.now());

        // when
        loanRepository.save(loan1);
        Optional<Loan> updatedLoan = loanRepository.findById(loan1.getId());

        // then
        assertThat(updatedLoan).isPresent();
        assertThat(updatedLoan.get().getReturnDate()).isNotNull();
    }
}
