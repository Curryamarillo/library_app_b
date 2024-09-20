package com.gusdev.library_app.repositories;

import com.gusdev.library_app.entities.Loan;
import jdk.dynalink.linker.LinkerServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    Optional<Loan> findFirstByBookIdAndReturnDateIsNullOrderByLoanDateDesc(Long bookId);

    List<Loan> findByUserId(Long userId);

    Loan findByBookId(Long bookId);

    boolean existsByUserId(Long id);

    boolean existsByUserIdAndReturnDateIsNull(Long userId);

    void deleteByUserId(Long id);

}
