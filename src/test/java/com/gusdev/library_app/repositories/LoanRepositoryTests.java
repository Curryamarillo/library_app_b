package com.gusdev.library_app.repositories;

import com.gusdev.library_app.entities.Book;
import com.gusdev.library_app.entities.Loan;
import com.gusdev.library_app.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
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
    private Book book2;
    private Loan loan1;

    @BeforeEach
    public void setUpTest() {
        user1 = userRepository.save(User.builder()
                .name("John")
                .surname("Doe")
                .email("john.doe4@example.com")
                .isAdmin(false)
                .password("password123")
                .build());


        book1 = bookRepository.save(Book.builder()
                .title("Book one")
                .author("Author one")
                .isbn("1234")
                .isAvailable(true)
                .createdDate(LocalDateTime.now())
                .build());

        book2 = bookRepository.save(Book.builder()
                .title("Book two")
                .author("Author two")
                .isbn("5678")
                .isAvailable(true)
                .createdDate(LocalDateTime.now())
                .build());

        loan1 = loanRepository.save(Loan.builder()
                .user(user1)
                .book(book1)
                .loanDate(LocalDateTime.now())
                .returnDate(null)
                .build());
    }

    @Test
    public void saveLoanTest() {
        // when
        Loan savedLoan = loanRepository.save(Loan.builder()
                .user(user1)
                .book(book2)
                .loanDate(LocalDateTime.now())
                .returnDate(null)
                .build());

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

        List<Loan> loanList = loanRepository.findAll();
        System.out.println("La cantidad de objetos en la lista es: " + loanList.size());
        assertThat(loanList).isNotNull();
        assertThat(loanList).hasSize(5);
    }

    @Test
    public void findByIdTest() {

        Optional<Loan> foundLoan = loanRepository.findById(loan1.getId());


        assertThat(foundLoan).isPresent();
        assertThat(foundLoan.get().getId()).isEqualTo(loan1.getId());
    }

    @Test
    public void updateLoanTest() {

        loan1.setReturnDate(LocalDateTime.now());


        loanRepository.save(loan1);
        Optional<Loan> updatedLoan = loanRepository.findById(loan1.getId());


        assertThat(updatedLoan).isPresent();
        assertThat(updatedLoan.get().getReturnDate()).isNotNull();
    }
}