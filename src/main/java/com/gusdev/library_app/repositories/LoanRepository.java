package com.gusdev.library_app.repositories;

import com.gusdev.library_app.entities.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    Optional<Loan> findFirstByBookIdAndReturnDateIsNullOrderByLoanDateDesc(Long bookId);
}
