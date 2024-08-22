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
import com.gusdev.library_app.utils.BookMapper;
import com.gusdev.library_app.utils.LoanMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;


@Service
public class LoanService {


    private final LoanRepository loanRepository;

    private final UserRepository userRepository;

    private final BookRepository bookRepository;

    public LoanService(LoanRepository loanRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public Loan create(LoanRequestDTO loanRequestDTO) {
        Book bookToLoan = bookRepository.findById(loanRequestDTO.bookId()).orElseThrow(() -> new BookNotFoundException("Book not found"));

        User userLoan = userRepository.findById(loanRequestDTO.userId()).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!bookToLoan.getIsAvailable()) {
            throw  new LoanAlreadyExistsException("The book is not available");
            }
        Loan loan = new Loan();
        loan.setBook(bookToLoan);
        loan.setUser(userLoan);
        loan.setLoanDate(LocalDateTime.now());

        bookToLoan.setIsAvailable(false);
        bookRepository.save(bookToLoan);
        return loanRepository.save(loan);
    }
    public List<LoanResponseDTO> findAll() {
        List<Loan> loans = loanRepository.findAll();
        return LoanMapper.toDTOList(loans);
    }
    public LoanResponseDTO findById(Long id) {
        Loan loan = loanRepository.findById(id).orElseThrow(() -> new LoanNotFoundException("Book not found"));
        return LoanMapper.toDTO(loan);
    }
    public void deleteById(Long id) {
        loanRepository.deleteById(id);
    }

}
